package io.github.salkscore.companion.ghost;

import java.util.List;

public record GhostRecording(long time, List<Long> checkpoints, List<GhostFrame> frames) {

    public Long checkpoint(int index) {
        return index >= 1 && index <= checkpoints.size() ? checkpoints.get(index - 1) : null;
    }

    GhostFrame frame(int tick) {
        return frames.get(Math.clamp(tick, 0, frames.size() - 1));
    }
}
