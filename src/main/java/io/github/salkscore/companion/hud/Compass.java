package io.github.salkscore.companion.hud;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

final class Compass {

    private static final String[] ARROWS = {"↑", "↗", "→", "↘", "↓", "↙", "←", "↖"};

    private Compass() {
    }

    static String arrow(Player player, Vec3 target) {
        double dx = target.x - player.getX();
        double dz = target.z - player.getZ();
        if (dx * dx + dz * dz < 1.5) return "•";

        float yaw = (float) Math.toDegrees(Math.atan2(dz, dx)) - 90f;
        float diff = Mth.wrapDegrees(yaw - player.getYRot());
        return ARROWS[Math.floorMod(Math.round(diff / 45f), ARROWS.length)];
    }
}
