package io.github.salkscore.companion.world;

import io.github.salkscore.companion.net.CourseInfo;
import io.github.salkscore.companion.util.TimeFormat;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.Display;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

// Tableau des meilleurs temps : un panneau texturé avec le texte posé devant
final class BoardPiece implements Piece {

    private static final int ROWS = 8;
    private static final float WIDTH = 3.0f;
    private static final float HEIGHT = WIDTH * 80 / 64;
    private static final double CENTER_Y = 1.6;
    private static final float TEXT_SCALE = 0.75f;
    // une ligne de text display fait 10 pixels de 1/40 de bloc
    private static final float LINE = 10 * 0.025f * TEXT_SCALE;

    private final CourseInfo course;
    private Display.ItemDisplay panel;
    private Display.TextDisplay text;

    BoardPiece(CourseInfo course) {
        this.course = course;
    }

    @Override
    public void spawn(ClientLevel level) {
        Vec3 center = course.board().add(0, CENTER_Y, 0);
        panel = ClientEntities.item(level, center, "board_panel",
                ClientEntities.transform(0, 0, 0, new Vector3f(WIDTH, HEIGHT, 1f)));
        panel.setBillboardConstraints(Display.BillboardConstraints.VERTICAL);

        int lines = 5 + Math.max(1, Math.min(ROWS, course.top().size()));
        float y = HEIGHT / 2 - 0.12f - lines * LINE;
        text = ClientEntities.text(level, center, content(), 150,
                ClientEntities.transform(0, y, 0.04f, new Vector3f(TEXT_SCALE)));
        text.setBillboardConstraints(Display.BillboardConstraints.VERTICAL);
    }

    private Component content() {
        MutableComponent out = Component.empty()
                .append(Glyphs.of(Glyphs.TROPHY))
                .append(" ")
                .append(course.name().copy().withStyle(style -> style.withBold(true)))
                .append("\n")
                .append(Component.translatable("decor.parkour-companion.board")
                        .withColor(Palette.MUTED))
                .append("\n\n");

        List<CourseInfo.Entry> top = course.top();
        if (top.isEmpty()) {
            out.append(Component.translatable("decor.parkour-companion.empty")
                    .withColor(Palette.MUTED));
        }
        String self = Minecraft.getInstance().getUser().getName();
        for (int i = 0; i < Math.min(ROWS, top.size()); i++) {
            CourseInfo.Entry entry = top.get(i);
            int color = entry.name().equalsIgnoreCase(self) ? Palette.TEAL : Palette.WHITE;
            out.append(Glyphs.medal(i + 1))
                    .append(" ")
                    .append(Component.literal(entry.name()).withColor(color))
                    .append(Component.literal("  " + TimeFormat.format(entry.time()))
                            .withColor(Palette.GOLD))
                    .append("\n");
        }

        out.append("\n").append(Glyphs.of(Glyphs.STAR)).append(" ");
        if (course.hasBest()) {
            out.append(Component.translatable("decor.parkour-companion.best",
                    TimeFormat.format(course.best()), course.rank()).withColor(Palette.TEAL));
        } else {
            out.append(Component.translatable("decor.parkour-companion.no-best")
                    .withColor(Palette.MUTED));
        }
        return out;
    }

    @Override
    public void remove() {
        ClientEntities.remove(panel);
        ClientEntities.remove(text);
    }

    @Override
    public boolean missing() {
        return ClientEntities.gone(panel) || ClientEntities.gone(text);
    }

    @Override
    public Vec3 anchor() {
        return course.board();
    }
}
