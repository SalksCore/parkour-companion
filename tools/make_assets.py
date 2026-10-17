"""Génère les textures de base du mod. Elles sont faites pour être retouchées à la main :
relancer ce script écrase les PNG, donc ne le relance plus une fois tes versions en place.

    python tools/make_assets.py
"""
import json
import math
from pathlib import Path

from PIL import Image, ImageDraw

ROOT = Path(__file__).resolve().parent.parent / "src/main/resources/assets/parkour-companion"
SPRITES = ROOT / "textures/gui/sprites/hud"
ITEMS = ROOT / "textures/item"
FONT = ROOT / "textures/font"

TEAL = (45, 212, 191)
BLUE = (59, 130, 246)
GOLD = (250, 204, 21)
GOLD_DARK = (180, 130, 10)
SILVER = (203, 213, 225)
BRONZE = (217, 119, 6)
INK = (12, 22, 30)
INK_2 = (22, 36, 46)
WHITE = (240, 248, 250)
GREEN = (34, 197, 94)
RED = (239, 68, 68)


def save(img, path, nine_slice=None):
    path.parent.mkdir(parents=True, exist_ok=True)
    img.save(path)
    if nine_slice:
        meta = {"gui": {"scaling": {"type": "nine_slice", "width": img.width,
                                     "height": img.height, "border": nine_slice}}}
        path.with_suffix(".png.mcmeta").write_text(json.dumps(meta, indent=2))


def lerp(a, b, t):
    return tuple(round(x + (y - x) * t) for x, y in zip(a, b))


def panel(edge, size=32):
    img = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    last = size - 1
    d.rectangle([1, 0, last - 1, last], fill=INK + (225,))
    d.rectangle([0, 1, last, last - 1], fill=INK + (225,))
    for x in range(1, last):
        d.point((x, 0), fill=edge + (255,))
        d.point((x, 1), fill=edge + (120,))
    for y in range(2, last):
        d.point((0, y), fill=INK_2 + (255,))
        d.point((last, y), fill=INK_2 + (255,))
    d.line([1, last, last - 1, last], fill=(0, 0, 0, 255))
    return img


def bar(fill, size=(16, 6)):
    w, h = size
    img = Image.new("RGBA", size, (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    if fill:
        for x in range(w):
            d.line([x, 1, x, h - 2], fill=lerp(TEAL, BLUE, x / (w - 1)) + (255,))
        d.line([0, 1, w - 1, 1], fill=(255, 255, 255, 90))
    else:
        d.rectangle([0, 1, w - 1, h - 2], fill=(255, 255, 255, 40))
        d.line([0, h - 1, w - 1, h - 1], fill=(0, 0, 0, 120))
    return img


def pill(color):
    img = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    d.rounded_rectangle([0, 0, 15, 15], radius=4, fill=INK + (230,))
    d.rounded_rectangle([0, 0, 15, 15], radius=4, outline=color + (255,))
    d.line([3, 14, 12, 14], fill=color + (110,))
    return img


def icon(draw_fn):
    img = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
    draw_fn(ImageDraw.Draw(img))
    return img


def clock(d):
    d.ellipse([1, 1, 14, 14], fill=INK_2 + (255,), outline=TEAL + (255,))
    d.line([7, 4, 7, 8], fill=WHITE + (255,))
    d.line([7, 8, 10, 10], fill=WHITE + (255,))
    d.rectangle([6, 0, 9, 0], fill=TEAL + (255,))


def flag(d):
    d.line([3, 1, 3, 15], fill=WHITE + (255,))
    for y in range(2, 10, 2):
        for x in range(4, 14, 2):
            on = ((x // 2) + (y // 2)) % 2 == 0
            d.rectangle([x, y, x + 1, y + 1], fill=(WHITE if on else INK) + (255,))
    d.rectangle([4, 2, 13, 9], outline=TEAL + (255,))


def checkpoint(d):
    d.polygon([(8, 1), (14, 8), (8, 15), (2, 8)], fill=GOLD + (255,))
    d.polygon([(8, 4), (11, 8), (8, 12), (5, 8)], fill=GOLD_DARK + (255,))


def trophy(d):
    d.rectangle([4, 2, 11, 7], fill=GOLD + (255,))
    d.ellipse([4, 4, 11, 10], fill=GOLD + (255,))
    d.rectangle([1, 3, 3, 4], fill=GOLD + (255,))
    d.rectangle([12, 3, 14, 4], fill=GOLD + (255,))
    d.point((2, 5), fill=GOLD + (255,))
    d.point((13, 5), fill=GOLD + (255,))
    d.rectangle([7, 10, 8, 12], fill=GOLD_DARK + (255,))
    d.rectangle([4, 13, 11, 14], fill=GOLD_DARK + (255,))
    d.line([6, 3, 6, 6], fill=(255, 245, 200, 255))


def arrow(d):
    d.polygon([(8, 0), (15, 9), (10, 9), (10, 15), (6, 15), (6, 9), (1, 9)],
              fill=TEAL + (255,))
    d.polygon([(8, 2), (12, 8), (8, 8)], fill=(170, 255, 240, 255))


def disc(size, draw_fn, scale=4):
    big = Image.new("RGBA", (size * scale, size * scale), (0, 0, 0, 0))
    draw_fn(ImageDraw.Draw(big), size * scale)
    return big.resize((size, size), Image.LANCZOS)


def start_pad(d, s):
    c = s / 2
    d.ellipse([0, 0, s - 1, s - 1], fill=TEAL + (70,))
    d.ellipse([s * .06, s * .06, s * .94, s * .94], fill=INK + (235,))
    ring = s * .045
    d.ellipse([s * .1, s * .1, s * .9, s * .9], outline=TEAL + (255,), width=round(ring))
    for i in range(24):
        a = i / 24 * math.tau
        r1, r2 = s * .3, s * .36 if i % 2 else s * .38
        d.line([c + math.cos(a) * r1, c + math.sin(a) * r1,
                c + math.cos(a) * r2, c + math.sin(a) * r2],
               fill=(BLUE if i % 2 else TEAL) + (255,), width=round(s * .012))
    tri = [(c - s * .08, c - s * .12), (c - s * .08, c + s * .12), (c + s * .13, c)]
    d.polygon(tri, fill=WHITE + (255,))


def board_panel():
    w, h = 64, 80
    img = Image.new("RGBA", (w, h), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    d.rectangle([0, 0, w - 1, h - 1], fill=INK + (235,))
    d.rectangle([0, 0, w - 1, h - 1], outline=INK_2 + (255,))
    for x in range(w):
        d.line([x, 0, x, 1], fill=lerp(TEAL, BLUE, x / (w - 1)) + (255,))
    d.rectangle([2, 3, w - 3, 12], fill=INK_2 + (255,))
    for y in range(16, h - 6, 6):
        if (y // 6) % 2 == 0:
            d.rectangle([3, y, w - 4, y + 5], fill=(255, 255, 255, 10))
    d.line([3, h - 9, w - 4, h - 9], fill=TEAL + (90,))
    for corner in [(1, 1), (w - 4, 1), (1, h - 4), (w - 4, h - 4)]:
        x, y = corner
        d.rectangle([x, y, x + 2, y + 2], outline=TEAL + (200,))
    return img


def label_panel():
    w, h = 64, 24
    img = Image.new("RGBA", (w, h), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    d.rounded_rectangle([0, 0, w - 1, h - 1], radius=3, fill=INK + (225,))
    for x in range(4, w - 4):
        d.point((x, 0), fill=lerp(TEAL, BLUE, x / (w - 1)) + (255,))
    d.rounded_rectangle([0, 0, w - 1, h - 1], radius=3, outline=INK_2 + (255,))
    return img


def glyphs():
    names = ["medal_gold", "medal_silver", "medal_bronze", "clock", "star", "flag",
             "trophy", "play"]
    img = Image.new("RGBA", (8 * len(names), 8), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)

    def medal(ox, color):
        d.polygon([(ox + 1, 0), (ox + 3, 0), (ox + 4, 2), (ox + 5, 0), (ox + 7, 0),
                   (ox + 5, 3), (ox + 3, 3)], fill=BLUE + (255,))
        d.ellipse([ox + 1, 2, ox + 6, 7], fill=color + (255,))
        d.point((ox + 3, 4), fill=(255, 255, 255, 200))

    medal(0, GOLD)
    medal(8, SILVER)
    medal(16, BRONZE)
    d.ellipse([24, 0, 31, 7], outline=TEAL + (255,))
    d.line([27, 2, 27, 4], fill=WHITE + (255,))
    d.line([27, 4, 29, 5], fill=WHITE + (255,))
    star = [(36, 0), (37, 3), (40, 3), (38, 5), (39, 7), (36, 6), (33, 7), (34, 5), (32, 3),
            (35, 3)]
    d.polygon(star, fill=TEAL + (255,))
    d.line([41, 0, 41, 7], fill=WHITE + (255,))
    d.rectangle([42, 0, 46, 4], fill=WHITE + (255,))
    d.rectangle([43, 1, 43, 1], fill=INK + (255,))
    d.rectangle([45, 1, 45, 1], fill=INK + (255,))
    d.rectangle([44, 2, 44, 2], fill=INK + (255,))
    d.rectangle([43, 3, 43, 3], fill=INK + (255,))
    d.rectangle([45, 3, 45, 3], fill=INK + (255,))
    d.rectangle([49, 0, 54, 3], fill=GOLD + (255,))
    d.rectangle([50, 4, 53, 4], fill=GOLD + (255,))
    d.rectangle([51, 5, 52, 6], fill=GOLD_DARK + (255,))
    d.rectangle([49, 7, 54, 7], fill=GOLD_DARK + (255,))
    d.polygon([(57, 0), (62, 4), (57, 7)], fill=WHITE + (255,))
    return img, names


def main():
    save(panel(TEAL), SPRITES / "panel.png", nine_slice=5)
    save(panel(GOLD), SPRITES / "panel_gold.png", nine_slice=5)
    save(bar(False), SPRITES / "bar_bg.png", nine_slice=2)
    save(bar(True), SPRITES / "bar_fill.png")
    save(pill(GREEN), SPRITES / "split_ahead.png", nine_slice=5)
    save(pill(RED), SPRITES / "split_behind.png", nine_slice=5)
    for name, fn in [("clock", clock), ("flag", flag), ("checkpoint", checkpoint),
                     ("trophy", trophy), ("arrow", arrow)]:
        save(icon(fn), SPRITES / f"{name}.png")

    save(disc(64, start_pad), ITEMS / "start_pad.png")
    save(board_panel(), ITEMS / "board_panel.png")
    save(label_panel(), ITEMS / "label_panel.png")

    sheet, names = glyphs()
    save(sheet, FONT / "icons.png")
    chars = "".join(chr(0xE100 + i) for i in range(len(names)))
    provider = {"providers": [{"type": "bitmap", "file": "parkour-companion:font/icons.png",
                               "ascent": 7, "height": 8, "chars": [chars]}]}
    (ROOT / "font").mkdir(parents=True, exist_ok=True)
    (ROOT / "font/icons.json").write_text(json.dumps(provider, indent=2))


if __name__ == "__main__":
    main()
