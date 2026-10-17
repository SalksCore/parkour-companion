package io.github.salkscore.companion.ghost;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public record GhostFrame(double x, double y, double z, float yaw, float pitch, float head,
        boolean crouching) {

    static GhostFrame of(Player player) {
        return new GhostFrame(player.getX(), player.getY(), player.getZ(), player.getYRot(),
                player.getXRot(), player.getYHeadRot(), player.isCrouching());
    }

    Vec3 position() {
        return new Vec3(x, y, z);
    }
}
