package io.github.salkscore.companion.hud;

import io.github.salkscore.companion.Config;
import io.github.salkscore.companion.ghost.GhostRecording;
import io.github.salkscore.companion.run.Run;
import io.github.salkscore.companion.run.RunTracker;
import io.github.salkscore.companion.util.TimeFormat;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;

public final class RunHud implements HudElement {

    static final int ACCENT = 0xFF5EEAD4;
    static final int WHITE = 0xFFFFFFFF;
    static final int GRAY = 0xFFA1A1AA;
    static final int GREEN = 0xFF4ADE80;
    static final int RED = 0xFFF87171;
    static final int GOLD = 0xFFFACC15;
    static final int PANEL = 0xA0101014;

    private static final int WIDTH = 168;
    private static final int HEIGHT = 50;
    private static final long SPLIT_MS = 3000;
    private static final long RESULT_MS = 6000;

    private final Config config;
    private final RunTracker tracker;

    public RunHud(Config config, RunTracker tracker) {
        this.config = config;
        this.tracker = tracker;
    }

    @Override
    public void render(GuiGraphics g, DeltaTracker delta) {
        Minecraft client = Minecraft.getInstance();
        if (!config.hud || client.options.hideGui || client.player == null) return;

        Run run = tracker.run();
        RunTracker.Result result = tracker.result();
        if (run != null) {
            drawRun(g, client, run);
        } else if (result != null && RunTracker.now() - result.shownAt() < RESULT_MS) {
            ResultPanel.draw(g, client.font, result);
        }
    }

    private void drawRun(GuiGraphics g, Minecraft client, Run run) {
        Font font = client.font;
        int x = (g.guiWidth() - WIDTH) / 2;
        int y = 6;
        int center = x + WIDTH / 2;

        g.fill(x, y, x + WIDTH, y + HEIGHT, PANEL);
        g.fill(x, y, x + WIDTH, y + 1, ACCENT);
        g.drawCenteredString(font, run.name(), center, y + 5, WHITE);
        big(g, font, TimeFormat.format(run.elapsed()), center, y + 17);
        g.drawCenteredString(font, status(client.player, run), center, y + 36, GRAY);

        int filled = Math.round(WIDTH * run.progress());
        g.fill(x, y + HEIGHT - 2, x + WIDTH, y + HEIGHT, 0x40FFFFFF);
        g.fill(x, y + HEIGHT - 2, x + filled, y + HEIGHT, ACCENT);

        GhostRecording best = tracker.best();
        if (best != null) {
            String pb = Component.translatable("hud.parkour-companion.best",
                    TimeFormat.format(best.time())).getString();
            g.drawString(font, pb, x + WIDTH - font.width(pb) - 4, y + 5, GOLD, false);
        }
        drawSplit(g, font, center, y + HEIGHT + 4);
    }

    private void drawSplit(GuiGraphics g, Font font, int center, int y) {
        RunTracker.Split split = tracker.split();
        if (split == null || RunTracker.now() - split.shownAt() > SPLIT_MS) return;

        String text = TimeFormat.delta(split.delta());
        int width = font.width(text) * 2 + 12;
        g.fill(center - width / 2, y, center + width / 2, y + 22, PANEL);
        int color = split.delta() <= 0 ? GREEN : RED;
        g.pose().pushMatrix();
        g.pose().translate(center - font.width(text), y + 3);
        g.pose().scale(2f, 2f);
        g.drawString(font, text, 0, 0, color, true);
        g.pose().popMatrix();
    }

    private static Component status(LocalPlayer player, Run run) {
        Component step = run.targetsFinish()
                ? Component.translatable("hud.parkour-companion.finish")
                : Component.translatable("hud.parkour-companion.checkpoint",
                        run.reached() + 1, run.total());
        String direction = Compass.arrow(player, run.target());
        int distance = (int) Math.round(player.position().distanceTo(run.target()));
        return Component.empty().append(step).append("  " + direction + " " + distance + " m");
    }

    static void big(GuiGraphics g, Font font, String text, int center, int y) {
        float scale = 1.75f;
        g.pose().pushMatrix();
        g.pose().translate(center - font.width(text) * scale / 2, y);
        g.pose().scale(scale, scale);
        g.drawString(font, text, 0, 0, WHITE, true);
        g.pose().popMatrix();
    }
}
