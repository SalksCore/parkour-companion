package io.github.salkscore.companion.ghost;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.entity.player.Player;

public final class GhostRecorder {

    // une heure à 20 ticks par seconde, au-delà on arrête d'enregistrer
    private static final int MAX_FRAMES = 72_000;

    private final List<GhostFrame> frames = new ArrayList<>();
    private final List<Long> checkpoints = new ArrayList<>();

    public void reset() {
        frames.clear();
        checkpoints.clear();
    }

    public void record(Player player) {
        if (frames.size() < MAX_FRAMES) frames.add(GhostFrame.of(player));
    }

    public void checkpoint(int index, long elapsed) {
        if (index == checkpoints.size() + 1) checkpoints.add(elapsed);
    }

    public GhostRecording finish(long time) {
        return new GhostRecording(time, List.copyOf(checkpoints), List.copyOf(frames));
    }
}
