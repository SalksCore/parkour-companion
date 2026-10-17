package io.github.salkscore.companion.net;

import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec3;

// board et finish peuvent être null, best vaut -1 quand le joueur n'a pas encore fini
public record CourseInfo(String id, Component name, String dimension, Vec3 start, float yaw,
        Vec3 board, Vec3 finish, float finishYaw, List<Vec3> checkpoints, List<Entry> top,
        long best, int rank) {

    public record Entry(String name, long time) {
    }

    public boolean hasBest() {
        return best >= 0;
    }
}
