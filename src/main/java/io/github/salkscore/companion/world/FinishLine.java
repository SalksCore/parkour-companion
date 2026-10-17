package io.github.salkscore.companion.world;

import net.minecraft.ChatFormatting;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Display;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

// Trait rouge au sol en travers de l'arrivée
final class FinishLine implements Piece {

    private final Vec3 pos;
    private final float yaw;
    private final int order;
    private Display.BlockDisplay line;
    private Display.TextDisplay label;

    FinishLine(Vec3 pos, float yaw, int order) {
        this.pos = pos;
        this.yaw = yaw;
        this.order = order;
    }

    @Override
    public void spawn(ClientLevel level) {
        line = GroundLines.across(level, pos, yaw, 3f, Blocks.RED_CONCRETE.defaultBlockState());
        Component text = Component.translatable("decor.parkour-companion.finish")
                .withStyle(ChatFormatting.RED);
        label = ClientEntities.text(level, pos.add(0, 0.8, 0), text, 0.5f);
    }

    @Override
    public void highlight(boolean on) {
        if (line == null) return;
        line.setGlowColorOverride(Palette.RED);
        line.setGlowingTag(on);
    }

    @Override
    public void remove() {
        ClientEntities.remove(line);
        ClientEntities.remove(label);
    }

    @Override
    public boolean missing() {
        return ClientEntities.gone(line) || ClientEntities.gone(label);
    }

    @Override
    public Vec3 anchor() {
        return pos;
    }

    @Override
    public int order() {
        return order;
    }
}
