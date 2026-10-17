package io.github.salkscore.companion.hud;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

final class Text {

    private Text() {
    }

    static void big(GuiGraphics g, Font font, String text, int center, int y, float scale,
            int color) {
        g.pose().pushMatrix();
        g.pose().translate(center - font.width(text) * scale / 2, y);
        g.pose().scale(scale, scale);
        g.drawString(font, text, 0, 0, color, true);
        g.pose().popMatrix();
    }
}
