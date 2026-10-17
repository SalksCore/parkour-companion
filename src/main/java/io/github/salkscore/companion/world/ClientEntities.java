package io.github.salkscore.companion.world;

import com.mojang.math.Transformation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Brightness;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;

// Entités qui n'existent que chez le client, avec des id négatifs pour ne jamais
// croiser celles du serveur
final class ClientEntities {

    private static int nextId = -0x5A2000;

    private ClientEntities() {
    }

    static Display.BlockDisplay block(ClientLevel level, Vec3 pos, BlockState state,
            Transformation transform) {
        Display.BlockDisplay display = new Display.BlockDisplay(EntityType.BLOCK_DISPLAY, level);
        display.setBlockState(state);
        display.setBrightnessOverride(Brightness.FULL_BRIGHT);
        return add(level, display, pos, transform);
    }

    static Display.TextDisplay text(ClientLevel level, Vec3 pos, Component text, float scale) {
        Display.TextDisplay display = new Display.TextDisplay(EntityType.TEXT_DISPLAY, level);
        display.setText(text);
        display.setLineWidth(200);
        display.setFlags(Display.TextDisplay.FLAG_USE_DEFAULT_BACKGROUND);
        display.setBillboardConstraints(Display.BillboardConstraints.CENTER);
        Transformation transform = new Transformation(new Vector3f(), new Quaternionf(),
                new Vector3f(scale), new Quaternionf());
        return add(level, display, pos, transform);
    }

    private static <T extends Display> T add(ClientLevel level, T display, Vec3 pos,
            Transformation transform) {
        display.setId(nextId--);
        display.snapTo(pos.x, pos.y, pos.z, 0f, 0f);
        display.setTransformation(transform);
        display.setViewRange(2f);
        level.addEntity(display);
        return display;
    }

    static boolean gone(Entity entity) {
        return entity == null || entity.isRemoved();
    }

    static void remove(Entity entity) {
        ClientLevel level = Minecraft.getInstance().level;
        if (entity != null && level != null && !entity.isRemoved()) {
            level.removeEntity(entity.getId(), Entity.RemovalReason.DISCARDED);
        }
    }
}
