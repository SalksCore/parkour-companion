package io.github.salkscore.companion.run;

import io.github.salkscore.companion.Config;
import io.github.salkscore.companion.ghost.GhostPlayer;
import io.github.salkscore.companion.ghost.GhostRecorder;
import io.github.salkscore.companion.ghost.GhostRecording;
import io.github.salkscore.companion.ghost.GhostStore;
import io.github.salkscore.companion.net.CourseInfo;
import io.github.salkscore.companion.net.RunEvent;
import io.github.salkscore.companion.world.Decor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.player.LocalPlayer;

public final class RunTracker {

    public record Split(long delta, long shownAt) {
    }

    public record Result(long time, int rank, Long previous, long shownAt) {

        public boolean record() {
            return previous == null || time < previous;
        }
    }

    private final Config config;
    private final GhostStore store = new GhostStore();
    private final GhostRecorder recorder = new GhostRecorder();
    private final GhostPlayer ghost = new GhostPlayer();
    private final Markers markers = new Markers();
    private final Decor decor = new Decor();

    private Run run;
    private String server;
    private GhostRecording best;
    private Split split;
    private Result result;

    public RunTracker(Config config) {
        this.config = config;
    }

    public void handle(RunEvent event) {
        switch (event) {
            case RunEvent.Start start -> start(start);
            case RunEvent.Checkpoint checkpoint -> checkpoint(checkpoint);
            case RunEvent.Finish finish -> finish(finish);
            case RunEvent.Stop stop -> stop();
            case RunEvent.Courses courses -> decor.update(courses.courses());
        }
    }

    private void start(RunEvent.Start start) {
        stop();
        result = null;
        run = new Run(start);
        server = serverKey();
        best = store.load(server, run.course());
        if (config.ghost) ghost.start(best, 0);
    }

    private void checkpoint(RunEvent.Checkpoint checkpoint) {
        if (run == null) return;
        run.reach(checkpoint.index());
        recorder.checkpoint(checkpoint.index(), checkpoint.elapsed());

        Long reference = best == null ? null : best.checkpoint(checkpoint.index());
        if (reference != null) {
            split = new Split(checkpoint.elapsed() - reference, now());
        }
    }

    private void finish(RunEvent.Finish finish) {
        if (run == null) return;
        Long previous = best == null ? null : best.time();
        if (previous == null || finish.time() < previous) {
            store.save(server, run.course(), recorder.finish(finish.time()));
        }
        Result done = new Result(finish.time(), finish.rank(), previous, now());
        stop();
        result = done;
    }

    public void stop() {
        run = null;
        best = null;
        split = null;
        recorder.reset();
        ghost.stop();
    }

    public void disconnect() {
        stop();
        result = null;
        decor.clear();
    }

    public void tick(Minecraft client) {
        decor.tick(client);
        if (run == null) return;
        LocalPlayer player = client.player;
        if (player == null || client.level == null) {
            stop();
            return;
        }
        if (!client.level.dimension().identifier().toString().equals(run.dimension())) return;

        int tick = run.nextTick();
        recorder.record(player);
        ghost.tick(tick);
        if (config.markers) markers.tick(run, tick);
    }

    public void setGhost(boolean enabled) {
        config.ghost = enabled;
        if (!enabled) {
            ghost.stop();
        } else if (run != null && !ghost.active()) {
            ghost.start(best, run.ticks());
        }
    }

    public Run run() {
        return run;
    }

    public Long bestTime() {
        if (best != null) return best.time();
        CourseInfo info = run == null ? null : decor.course(run.course());
        return info != null && info.hasBest() ? info.best() : null;
    }

    public Split split() {
        return split;
    }

    public Result result() {
        return result;
    }

    private static String serverKey() {
        Minecraft client = Minecraft.getInstance();
        ServerData data = client.getCurrentServer();
        if (data != null) return data.ip;
        return client.isSingleplayer() ? "local" : "inconnu";
    }

    public static long now() {
        return System.currentTimeMillis();
    }
}
