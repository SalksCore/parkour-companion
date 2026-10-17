package io.github.salkscore.companion.world;

import io.github.salkscore.companion.net.CourseInfo;
import io.github.salkscore.companion.util.TimeFormat;
import net.minecraft.ChatFormatting;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Display;
import net.minecraft.world.phys.Vec3;

final class StartLabel implements Piece {

    private final CourseInfo course;
    private Display.TextDisplay text;

    StartLabel(CourseInfo course) {
        this.course = course;
    }

    @Override
    public void spawn(ClientLevel level) {
        text = ClientEntities.text(level, course.start().add(0, 1.2, 0), label(), 0.6f);
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
        ClientEntities.remove(text);
    }

    @Override
    public boolean missing() {
        return ClientEntities.gone(text);
    }

    @Override
    public Vec3 anchor() {
        return course.start();
    }
}
