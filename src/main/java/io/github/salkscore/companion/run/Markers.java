package io.github.salkscore.companion.run;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.phys.Vec3;

final class Markers {

    void tick(Run run, int tick) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null || tick % 3 != 0) return;

        Vec3 pos = run.target();
        double x = pos.x + (level.random.nextDouble() - 0.5) * 0.3;
        double z = pos.z + (level.random.nextDouble() - 0.5) * 0.3;
        level.addParticle(ParticleTypes.END_ROD, true, true, x, pos.y + 0.3, z, 0, 0.1, 0);
    }
}
