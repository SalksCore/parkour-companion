package io.github.salkscore.companion.world;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Display;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

// Petit carré jaune au sol, qui brille à travers les murs quand c'est le prochain
final class CheckpointMark implements Piece {

    private final Vec3 pos;
    private final int index;
    private final List<Display.BlockDisplay> lines = new ArrayList<>();

    CheckpointMark(Vec3 pos, int index) {
        this.pos = pos;
        this.index = index;
    }

    @Override
    public void spawn(ClientLevel level) {
        lines.addAll(GroundLines.square(level, pos, 1.2f,
                Blocks.YELLOW_CONCRETE.defaultBlockState()));
    }

    @Override
    public void highlight(boolean on) {
        for (Display.BlockDisplay line : lines) {
            line.setGlowColorOverride(Palette.GOLD);
            line.setGlowingTag(on);
        }
    }

    @Override
    public void remove() {
        lines.forEach(ClientEntities::remove);
        lines.clear();
    }

    @Override
    public boolean missing() {
        return lines.isEmpty() || lines.stream().anyMatch(ClientEntities::gone);
    }

    @Override
    public Vec3 anchor() {
        return pos;
    }

    @Override
    public int order() {
        return index;
    }
}
