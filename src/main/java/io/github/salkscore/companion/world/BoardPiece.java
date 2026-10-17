package io.github.salkscore.companion.world;

import io.github.salkscore.companion.net.CourseInfo;
import io.github.salkscore.companion.util.TimeFormat;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.Display;
import net.minecraft.world.phys.Vec3;

final class BoardPiece implements Piece {

    private static final int ROWS = 10;
    private static final ChatFormatting[] PODIUM =
            {ChatFormatting.GOLD, ChatFormatting.WHITE, ChatFormatting.RED};

    private final CourseInfo course;
    private Display.TextDisplay text;

    BoardPiece(CourseInfo course) {
        this.course = course;
    }

    @Override
    public void spawn(ClientLevel level) {
        text = ClientEntities.text(level, course.board(), content(), 0.8f);
    }

    private Component content() {
        MutableComponent out = Component.empty()
                .append(course.name())
                .append("\n")
                .append(Component.translatable("decor.parkour-companion.board")
                        .withStyle(ChatFormatting.GRAY))
                .append("\n");

        List<CourseInfo.Entry> top = course.top();
        if (top.isEmpty()) {
            out.append("\n").append(Component.translatable("decor.parkour-companion.empty")
                    .withStyle(ChatFormatting.DARK_GRAY));
        }
        String self = Minecraft.getInstance().getUser().getName();
        for (int i = 0; i < Math.min(ROWS, top.size()); i++) {
            CourseInfo.Entry entry = top.get(i);
            ChatFormatting rank = i < PODIUM.length ? PODIUM[i] : ChatFormatting.DARK_GRAY;
            ChatFormatting name = entry.name().equalsIgnoreCase(self)
                    ? ChatFormatting.AQUA
                    : ChatFormatting.WHITE;
            out.append("\n")
                    .append(Component.literal("#" + (i + 1) + " ").withStyle(rank))
                    .append(Component.literal(entry.name()).withStyle(name))
                    .append(Component.literal("  " + TimeFormat.format(entry.time()))
                            .withStyle(ChatFormatting.YELLOW));
        }

        out.append("\n\n");
        if (course.hasBest()) {
            out.append(Component.translatable("decor.parkour-companion.best",
                    TimeFormat.format(course.best()), course.rank())
                    .withStyle(ChatFormatting.AQUA));
        } else {
            out.append(Component.translatable("decor.parkour-companion.no-best")
                    .withStyle(ChatFormatting.GRAY));
        }
        return out;
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
        return course.board();
    }
}
