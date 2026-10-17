package io.github.salkscore.companion.hud;

import io.github.salkscore.companion.CompanionMod;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;

// Textures du HUD dans assets/parkour-companion/textures/gui/sprites/hud
final class Sprites {

    static final Identifier PANEL = id("panel");
    static final Identifier PANEL_GOLD = id("panel_gold");
    static final Identifier BAR_BG = id("bar_bg");
    static final Identifier BAR_FILL = id("bar_fill");
    static final Identifier AHEAD = id("split_ahead");
    static final Identifier BEHIND = id("split_behind");
    static final Identifier CLOCK = id("clock");
    static final Identifier FLAG = id("flag");
    static final Identifier CHECKPOINT = id("checkpoint");
    static final Identifier TROPHY = id("trophy");
    static final Identifier ARROW = id("arrow");

    private Sprites() {
    }

    static void draw(GuiGraphics g, Identifier sprite, int x, int y, int width, int height) {
        g.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, x, y, width, height);
    }

    private static Identifier id(String name) {
        return Identifier.fromNamespaceAndPath(CompanionMod.ID, "hud/" + name);
    }
}
