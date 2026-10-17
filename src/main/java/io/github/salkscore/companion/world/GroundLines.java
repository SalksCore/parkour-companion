package io.github.salkscore.companion.world;

import com.mojang.math.Transformation;
import java.util.List;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Display;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;

// Traits fins posés au sol, faits avec des blocs vanilla aplatis
final class GroundLines {

    private static final float HEIGHT = 0.02f;
    private static final float THICK = 0.07f;

    private GroundLines() {
    }

    static List<Display.BlockDisplay> square(ClientLevel level, Vec3 center, float size,
            BlockState state) {
        float half = size / 2;
        float inner = size - 2 * THICK;
        return List.of(
                bar(level, center, state, 0f, new Vector3f(-half, 0, -half), size, THICK),
                bar(level, center, state, 0f, new Vector3f(-half, 0, half - THICK), size, THICK),
                bar(level, center, state, 0f, new Vector3f(-half, 0, -half + THICK), THICK, inner),
                bar(level, center, state, 0f, new Vector3f(half - THICK, 0, -half + THICK),
                        THICK, inner));
    }

    // trait perpendiculaire au regard enregistré avec le point
    static Display.BlockDisplay across(ClientLevel level, Vec3 center, float yaw, float length,
            BlockState state) {
        float angle = (float) Math.toRadians(-yaw);
        Vector3f corner = new Vector3f(-length / 2, 0, -THICK);
        return bar(level, center, state, angle, corner, length, THICK * 2);
    }

    private static Display.BlockDisplay bar(ClientLevel level, Vec3 center, BlockState state,
            float angle, Vector3f corner, float x, float z) {
        Quaternionf rotation = new Quaternionf().rotateY(angle);
        Vector3f offset = rotation.transform(corner);
        Transformation transform = new Transformation(offset, rotation,
                new Vector3f(x, HEIGHT, z), new Quaternionf());
        return ClientEntities.block(level, center.add(0, 0.005, 0), state, transform);
    }
}
