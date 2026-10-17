package io.github.salkscore.companion.net;

import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec3;

public record CourseInfo(String id, Component name, String dimension, Vec3 start, Vec3 board,
        List<Entry> top, long best, int rank) {

    public record Entry(String name, long time) {
    }

    public boolean hasBest() {
        return best >= 0;
    }
}
