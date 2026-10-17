package io.github.salkscore.companion.hud;

import io.github.salkscore.companion.Config;
import io.github.salkscore.companion.run.Run;
import io.github.salkscore.companion.run.RunTracker;
import io.github.salkscore.companion.util.TimeFormat;
import io.github.salkscore.companion.world.Palette;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;

public final class RunHud implements HudElement {

    static final int WIDTH = 200;
    static final int HEIGHT = 62;
    static final int TOP = 6;

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
            drawRun(g, client.font, client.player, run);
        } else if (result != null && RunTracker.now() - result.shownAt() < RESULT_MS) {
            ResultPanel.draw(g, client.font, result);
        }
    }

    private void drawRun(GuiGraphics g, Font font, LocalPlayer player, Run run) {
        int x = (g.guiWidth() - WIDTH) / 2;
        int y = TOP;
        int center = x + WIDTH / 2;
        Sprites.draw(g, Sprites.PANEL, x, y, WIDTH, HEIGHT);

        Sprites.draw(g, Sprites.FLAG, x + 7, y + 6, 10, 10);
        g.drawString(font, run.name(), x + 21, y + 7, Palette.opaque(Palette.WHITE), true);
        Long best = tracker.bestTime();
        if (best != null) {
            String pb = TimeFormat.format(best);
            int right = x + WIDTH - 8;
            g.drawString(font, pb, right - font.width(pb), y + 7, Palette.opaque(Palette.GOLD));
            Sprites.draw(g, Sprites.TROPHY, right - font.width(pb) - 12, y + 6, 10, 10);
        }

        String time = TimeFormat.format(run.elapsed());
        int timeWidth = Math.round(font.width(time) * 2f);
        Sprites.draw(g, Sprites.CLOCK, center - timeWidth / 2 - 16, y + 21, 12, 12);
        Text.big(g, font, time, center + 6, y + 20, 2f, Palette.opaque(Palette.WHITE));

        drawStatus(g, font, player, run, x, y + 41);

        int barX = x + 7;
        int barW = WIDTH - 14;
        Sprites.draw(g, Sprites.BAR_BG, barX, y + HEIGHT - 9, barW, 4);
        int filled = Math.round(barW * run.progress());
        if (filled > 0) Sprites.draw(g, Sprites.BAR_FILL, barX, y + HEIGHT - 9, filled, 4);

        drawSplit(g, font, center, y + HEIGHT + 4);
    }

    private void drawStatus(GuiGraphics g, Font font, LocalPlayer player, Run run, int x, int y) {
        Component step = run.targetsFinish()
                ? Component.translatable("hud.parkour-companion.finish")
                : Component.translatable("hud.parkour-companion.checkpoint",
                        run.reached() + 1, run.total());
        Sprites.draw(g, Sprites.CHECKPOINT, x + 8, y, 9, 9);
        g.drawString(font, step, x + 21, y + 1, Palette.opaque(Palette.MUTED));

        int distance = (int) Math.round(player.position().distanceTo(run.target()));
        String meters = distance + " m";
        int right = x + WIDTH - 8;
        g.drawString(font, meters, right - font.width(meters), y + 1,
                Palette.opaque(Palette.WHITE));

        int arrowX = right - font.width(meters) - 9;
        int arrowY = y + 4;
        if (Compass.close(player, run.target())) {
            g.fill(arrowX - 2, arrowY - 2, arrowX + 2, arrowY + 2, Palette.opaque(Palette.TEAL));
            return;
        }
        g.pose().pushMatrix();
        g.pose().translate(arrowX, arrowY);
        g.pose().rotate((float) Math.toRadians(Compass.angle(player, run.target())));
        Sprites.draw(g, Sprites.ARROW, -5, -5, 10, 10);
        g.pose().popMatrix();
    }

    private void drawSplit(GuiGraphics g, Font font, int center, int y) {
        RunTracker.Split split = tracker.split();
        if (split == null || RunTracker.now() - split.shownAt() > SPLIT_MS) return;

        boolean ahead = split.delta() <= 0;
        String text = TimeFormat.delta(split.delta());
        int width = font.width(text) * 2 + 20;
        Sprites.draw(g, ahead ? Sprites.AHEAD : Sprites.BEHIND, center - width / 2, y, width, 26);
        int color = Palette.opaque(ahead ? Palette.GREEN : Palette.RED);
        Text.big(g, font, text, center, y + 6, 2f, color);
    }
}
