package io.github.salkscore.companion.hud;

import io.github.salkscore.companion.Config;
import io.github.salkscore.companion.run.Run;
import io.github.salkscore.companion.run.RunTracker;
import io.github.salkscore.companion.util.TimeFormat;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.minecraft.ChatFormatting;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public final class RunHud implements HudElement {

    private static final int TOP = 4;
    private static final int BACKGROUND = 0x80000000;
    private static final int PROGRESS = 0xFF55FFFF;
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
            draw(g, client.font, line(client.player, run), run.progress());
        } else if (result != null && RunTracker.now() - result.shownAt() < RESULT_MS) {
            draw(g, client.font, result(result), 1f);
        }
    }

    private void draw(GuiGraphics g, Font font, Component text, float progress) {
        int width = font.width(text);
        int x = (g.guiWidth() - width) / 2;
        g.fill(x - 4, TOP - 2, x + width + 4, TOP + 10, BACKGROUND);
        g.fill(x - 4, TOP + 10, x - 4 + Math.round((width + 8) * progress), TOP + 11, PROGRESS);
        g.drawString(font, text, x, TOP, -1, true);
    }

    private MutableComponent line(LocalPlayer player, Run run) {
        MutableComponent out = Component.literal(TimeFormat.format(run.elapsed()))
                .withStyle(ChatFormatting.WHITE);

        RunTracker.Split split = tracker.split();
        if (split != null && RunTracker.now() - split.shownAt() < SPLIT_MS) {
            ChatFormatting color = split.delta() <= 0 ? ChatFormatting.GREEN : ChatFormatting.RED;
            out.append(Component.literal(" " + TimeFormat.delta(split.delta())).withStyle(color));
        }

        Component step = run.targetsFinish()
                ? Component.translatable("hud.parkour-companion.finish")
                : Component.translatable("hud.parkour-companion.checkpoint",
                        run.reached() + 1, run.total());
        int distance = (int) Math.round(player.position().distanceTo(run.target()));
        out.append(separator())
                .append(step.copy().withStyle(ChatFormatting.GRAY))
                .append(Component.literal(" " + Compass.arrow(player, run.target()) + " "
                        + distance + "m").withStyle(ChatFormatting.AQUA));

        Long best = tracker.bestTime();
        if (best != null) {
            out.append(separator())
                    .append(Component.translatable("hud.parkour-companion.best",
                            TimeFormat.format(best)).withStyle(ChatFormatting.GOLD));
        }
        return out;
    }

    private static MutableComponent result(RunTracker.Result result) {
        ChatFormatting color = result.record() ? ChatFormatting.GOLD : ChatFormatting.WHITE;
        MutableComponent out = Component.translatable("hud.parkour-companion.done")
                .withStyle(color)
                .append(" " + TimeFormat.format(result.time()))
                .append(separator())
                .append(Component.translatable("hud.parkour-companion.rank", result.rank())
                        .withStyle(ChatFormatting.GRAY));
        if (result.previous() == null) return out;

        long diff = result.time() - result.previous();
        String key = diff < 0 ? "hud.parkour-companion.new-best" : "hud.parkour-companion.no-best";
        ChatFormatting diffColor = diff < 0 ? ChatFormatting.GREEN : ChatFormatting.RED;
        return out.append(separator())
                .append(Component.translatable(key, TimeFormat.delta(diff)).withStyle(diffColor));
    }

    private static Component separator() {
        return Component.literal("  |  ").withStyle(ChatFormatting.DARK_GRAY);
    }
}
