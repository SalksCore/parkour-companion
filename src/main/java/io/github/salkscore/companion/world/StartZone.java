package io.github.salkscore.companion.world;

import io.github.salkscore.companion.net.CourseInfo;
import io.github.salkscore.companion.util.TimeFormat;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Display;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

// Carré vert au sol autour de la plaque, avec le nom du parcours et ton record
final class StartZone implements Piece {

    private final CourseInfo course;
    private final List<Display> entities = new ArrayList<>();

    StartZone(CourseInfo course) {
        this.course = course;
    }

    @Override
    public void spawn(ClientLevel level) {
        Vec3 start = course.start();
        entities.addAll(GroundLines.square(level, start, 1.4f,
                Blocks.LIME_CONCRETE.defaultBlockState()));
        entities.add(ClientEntities.text(level, start.add(0, 1.1, 0), label(), 0.6f));
    }

    private Component label() {
        Component record = course.hasBest()
                ? Component.translatable("decor.parkour-companion.best",
                        TimeFormat.format(course.best()), course.rank())
                        .withStyle(ChatFormatting.GOLD)
                : Component.translatable("decor.parkour-companion.no-best")
                        .withStyle(ChatFormatting.GRAY);
        return Component.empty()
                .append(course.name())
                .append("\n")
                .append(Component.translatable("decor.parkour-companion.start")
                        .withStyle(ChatFormatting.GREEN))
                .append("\n")
                .append(record);
    }

    @Override
    public void remove() {
        entities.forEach(ClientEntities::remove);
        entities.clear();
    }

    @Override
    public boolean missing() {
        return entities.isEmpty() || entities.stream().anyMatch(ClientEntities::gone);
    }

    @Override
    public Vec3 anchor() {
        return course.start();
    }

    @Override
    public int order() {
        return 0;
    }
}
