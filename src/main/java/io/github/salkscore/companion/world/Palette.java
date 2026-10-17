package io.github.salkscore.companion.world;

// Mêmes teintes que les textures de tools/make_assets.py
public final class Palette {

    public static final int TEAL = 0x2DD4BF;
    public static final int GOLD = 0xFACC15;
    public static final int WHITE = 0xF0F8FA;
    public static final int MUTED = 0x93A5B0;
    public static final int GREEN = 0x4ADE80;
    public static final int RED = 0xF87171;

    private Palette() {
    }

    public static int opaque(int rgb) {
        return 0xFF000000 | rgb;
    }
}
