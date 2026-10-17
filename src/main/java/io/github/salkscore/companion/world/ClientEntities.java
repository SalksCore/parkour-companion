package io.github.salkscore.companion.world;

import com.mojang.math.Transformation;
import io.github.salkscore.companion.CompanionMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Brightness;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;

// Entités qui n'existent que chez le client, avec des id négatifs pour ne jamais
// croiser celles du serveur
final class ClientEntities {

    private static int nextId = -0x5A2000;

    private ClientEntities() {
    }

    static Display.ItemDisplay item(ClientLevel level, Vec3 pos, String model,
            Transformation transform) {
        Display.ItemDisplay display = new Display.ItemDisplay(EntityType.ITEM_DISPLAY, level);
        ItemStack stack = new ItemStack(Items.PAPER);
        stack.set(DataComponents.ITEM_MODEL,
                Identifier.fromNamespaceAndPath(CompanionMod.ID, model));
        display.setItemStack(stack);
        display.setItemTransform(ItemDisplayContext.NONE);
        return add(level, display, pos, transform);
    }

    static Display.TextDisplay text(ClientLevel level, Vec3 pos, Component text, int width,
            Transformation transform) {
        Display.TextDisplay display = new Display.TextDisplay(EntityType.TEXT_DISPLAY, level);
        display.setText(text);
        display.setLineWidth(width);
        display.setBackgroundColor(0);
        display.setFlags(Display.TextDisplay.FLAG_SHADOW);
        return add(level, display, pos, transform);
    }

    private static <T extends Display> T add(ClientLevel level, T display, Vec3 pos,
            Transformation transform) {
        display.setId(nextId--);
        display.snapTo(pos.x, pos.y, pos.z, 0f, 0f);
        display.setTransformation(transform);
        display.setBrightnessOverride(Brightness.FULL_BRIGHT);
        display.setViewRange(2f);
        level.addEntity(display);
        return display;
    }

    static Transformation transform(float x, float y, float z, Vector3f scale) {
        return new Transformation(new Vector3f(x, y, z), new Quaternionf(), scale,
                new Quaternionf());
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
