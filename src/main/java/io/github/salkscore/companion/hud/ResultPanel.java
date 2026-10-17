package io.github.salkscore.companion.hud;

import io.github.salkscore.companion.run.RunTracker;
import io.github.salkscore.companion.util.TimeFormat;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

final class ResultPanel {

    private static final int WIDTH = 168;
    private static final int HEIGHT = 50;

    private ResultPanel() {
    }

    static void draw(GuiGraphics g, Font font, RunTracker.Result result) {
        int x = (g.guiWidth() - WIDTH) / 2;
        int y = 6;
        int center = x + WIDTH / 2;
        int accent = result.record() ? RunHud.GOLD : RunHud.ACCENT;

        g.fill(x, y, x + WIDTH, y + HEIGHT, RunHud.PANEL);
        g.fill(x, y, x + WIDTH, y + 1, accent);
        Component title = Component.translatable("hud.parkour-companion.done");
        g.drawCenteredString(font, title, center, y + 5, accent);
        RunHud.big(g, font, TimeFormat.format(result.time()), center, y + 17);
        g.drawCenteredString(font, details(result), center, y + 36, RunHud.GRAY);
    }

    private static Component details(RunTracker.Result result) {
        Component rank = Component.translatable("hud.parkour-companion.rank", result.rank());
        if (result.previous() == null) return rank;

        long diff = result.time() - result.previous();
        String key = diff < 0 ? "hud.parkour-companion.new-best" : "hud.parkour-companion.no-best";
        return Component.empty()
                .append(rank)
                .append("  ")
                .append(Component.translatable(key, TimeFormat.delta(diff)));
    }
}
