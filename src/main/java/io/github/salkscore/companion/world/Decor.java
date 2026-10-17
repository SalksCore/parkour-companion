package io.github.salkscore.companion.world;

import io.github.salkscore.companion.net.CourseInfo;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;

// Socles de départ et tableaux des temps de tous les parcours du monde courant
public final class Decor {

    private final List<Piece> pieces = new ArrayList<>();
    private List<CourseInfo> courses = List.of();
    private ClientLevel level;
    private int tick;

    public void update(List<CourseInfo> courses) {
        this.courses = courses;
        rebuild(Minecraft.getInstance().level);
    }

    public CourseInfo course(String id) {
        for (CourseInfo course : courses) {
            if (course.id().equals(id)) return course;
        }
        return null;
    }

    public void tick(Minecraft client) {
        if (client.level != level) {
            rebuild(client.level);
            return;
        }
        if (level == null) return;

        tick++;
        for (Piece piece : pieces) {
            piece.tick(tick);
            if (tick % 20 == 0 && piece.missing()
                    && level.hasChunkAt(BlockPos.containing(piece.anchor()))) {
                piece.remove();
                piece.spawn(level);
            }
        }
    }

    public void clear() {
        courses = List.of();
        rebuild(null);
    }

    private void rebuild(ClientLevel level) {
        pieces.forEach(Piece::remove);
        pieces.clear();
        this.level = level;
        if (level == null) return;

        String dimension = level.dimension().identifier().toString();
        for (CourseInfo course : courses) {
            if (!course.dimension().equals(dimension)) continue;
            pieces.add(new StartPiece(course));
            if (course.board() != null) pieces.add(new BoardPiece(course));
        }
        pieces.forEach(piece -> piece.spawn(level));
    }
}
