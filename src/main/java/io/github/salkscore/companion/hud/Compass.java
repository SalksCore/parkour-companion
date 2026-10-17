package io.github.salkscore.companion.hud;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

final class Compass {

    private Compass() {
    }

    // angle en degrés entre le regard du joueur et la cible, positif vers la droite
    static float angle(Player player, Vec3 target) {
        double dx = target.x - player.getX();
        double dz = target.z - player.getZ();
        float yaw = (float) Math.toDegrees(Math.atan2(dz, dx)) - 90f;
        return Mth.wrapDegrees(yaw - player.getYRot());
    }

    static boolean close(Player player, Vec3 target) {
        double dx = target.x - player.getX();
        double dz = target.z - player.getZ();
        return dx * dx + dz * dz < 1.5;
    }
}
