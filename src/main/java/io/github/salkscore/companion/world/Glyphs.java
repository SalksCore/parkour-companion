package io.github.salkscore.companion.world;

import io.github.salkscore.companion.CompanionMod;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FontDescription;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;

// Icônes de la police assets/parkour-companion/font/icons.json
public final class Glyphs {

    public static final char MEDAL_GOLD = '';
    public static final char MEDAL_SILVER = '';
    public static final char MEDAL_BRONZE = '';
    public static final char CLOCK = '';
    public static final char STAR = '';
    public static final char FLAG = '';
    public static final char TROPHY = '';
    public static final char PLAY = '';

    private static final FontDescription FONT =
            new FontDescription.Resource(Identifier.fromNamespaceAndPath(CompanionMod.ID, "icons"));

    private Glyphs() {
    }

    public static MutableComponent of(char glyph) {
        return Component.literal(String.valueOf(glyph))
                .withStyle(style -> style.withFont(FONT).withColor(0xFFFFFF));
    }

    public static MutableComponent medal(int rank) {
        return switch (rank) {
            case 1 -> of(MEDAL_GOLD);
            case 2 -> of(MEDAL_SILVER);
            case 3 -> of(MEDAL_BRONZE);
            default -> Component.literal("#" + rank).withColor(Palette.MUTED);
        };
    }
}
