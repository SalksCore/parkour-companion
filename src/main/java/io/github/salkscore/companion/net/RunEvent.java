package io.github.salkscore.companion.net;

import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec3;

public sealed interface RunEvent {

    // points : départ, checkpoints dans l'ordre, puis arrivée
    record Start(String course, Component name, String dimension, List<Vec3> points)
            implements RunEvent {
    }

    record Checkpoint(int index, long elapsed) implements RunEvent {
    }

    record Finish(long time, int rank) implements RunEvent {
    }

    record Stop() implements RunEvent {
    }
}
