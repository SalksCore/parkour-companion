package io.github.salkscore.companion.run;

import io.github.salkscore.companion.net.RunEvent;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec3;

public final class Run {

    private final RunEvent.Start info;
    private final long startedAt = System.nanoTime();
    private int reached;
    private int ticks;

    Run(RunEvent.Start info) {
        this.info = info;
    }

    public long elapsed() {
        return (System.nanoTime() - startedAt) / 1_000_000L;
    }

    public Vec3 target() {
        List<Vec3> points = info.points();
        return points.get(Math.min(reached + 1, points.size() - 1));
    }

    public boolean targetsFinish() {
        return reached >= total();
    }

    public int total() {
        return info.points().size() - 2;
    }

    public float progress() {
        return (float) reached / (total() + 1);
    }

    void reach(int index) {
        reached = index;
    }

    int nextTick() {
        return ticks++;
    }

    public int reached() {
        return reached;
    }

    public int ticks() {
        return ticks;
    }

    public String course() {
        return info.course();
    }

    public Component name() {
        return info.name();
    }

    public String dimension() {
        return info.dimension();
    }

    public List<Vec3> points() {
        return info.points();
    }
}
