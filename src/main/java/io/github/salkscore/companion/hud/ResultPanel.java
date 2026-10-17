package io.github.salkscore.companion.hud;

import io.github.salkscore.companion.run.RunTracker;
import io.github.salkscore.companion.util.TimeFormat;
import io.github.salkscore.companion.world.Palette;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

final class ResultPanel {

    private ResultPanel() {
    }

    static void draw(GuiGraphics g, Font font, RunTracker.Result result) {
        int width = RunHud.WIDTH;
        int x = (g.guiWidth() - width) / 2;
        int y = RunHud.TOP;
        int center = x + width / 2;
        boolean record = result.record();
        int accent = Palette.opaque(record ? Palette.GOLD : Palette.TEAL);

        Sprites.draw(g, record ? Sprites.PANEL_GOLD : Sprites.PANEL, x, y, width, RunHud.HEIGHT);
        Component title = Component.translatable("hud.parkour-companion.done");
        int titleWidth = font.width(title);
        Sprites.draw(g, Sprites.TROPHY, center - titleWidth / 2 - 14, y + 5, 11, 11);
        g.drawString(font, title, center - titleWidth / 2, y + 7, accent, true);

        Text.big(g, font, TimeFormat.format(result.time()), center, y + 21, 2f,
                Palette.opaque(Palette.WHITE));

        Component details = details(result);
        g.drawCenteredString(font, details, center, y + RunHud.HEIGHT - 16,
                Palette.opaque(Palette.MUTED));
    }

    private static Component details(RunTracker.Result result) {
        Component rank = Component.translatable("hud.parkour-companion.rank", result.rank());
        if (result.previous() == null) return rank;

        long diff = result.time() - result.previous();
        String key = diff < 0 ? "hud.parkour-companion.new-best" : "hud.parkour-companion.no-best";
        int color = diff < 0 ? Palette.GREEN : Palette.RED;
        return Component.empty()
                .append(rank)
                .append("   ")
                .append(Component.translatable(key, TimeFormat.delta(diff)).withColor(color));
    }
}
