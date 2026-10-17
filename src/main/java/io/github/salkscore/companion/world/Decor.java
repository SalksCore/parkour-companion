package io.github.salkscore.companion.world;

import io.github.salkscore.companion.net.CourseInfo;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;

// Zones de départ, checkpoints, arrivées et tableaux de tous les parcours du monde courant
public final class Decor {

    private final List<Placed> pieces = new ArrayList<>();
    private List<CourseInfo> courses = List.of();
    private ClientLevel level;
    private String litCourse;
    private int litOrder = -1;
    private int tick;

    private record Placed(String course, Piece piece) {
    }

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

    // fait briller le prochain point à atteindre, null pour tout éteindre
    public void highlight(String course, int order) {
        litCourse = course;
        litOrder = order;
        pieces.forEach(this::applyHighlight);
    }

    private void applyHighlight(Placed placed) {
        boolean on = placed.course().equals(litCourse) && placed.piece().order() == litOrder;
        placed.piece().highlight(on);
    }

    public void tick(Minecraft client) {
        if (client.level != level) {
            rebuild(client.level);
            return;
        }
        if (level == null || ++tick % 20 != 0) return;

        for (Placed placed : pieces) {
            Piece piece = placed.piece();
            if (piece.missing() && level.hasChunkAt(BlockPos.containing(piece.anchor()))) {
                piece.remove();
                piece.spawn(level);
                applyHighlight(placed);
            }
        }
    }

    public void clear() {
        courses = List.of();
        rebuild(null);
    }

    private void rebuild(ClientLevel level) {
        pieces.forEach(placed -> placed.piece().remove());
        pieces.clear();
        this.level = level;
        if (level == null) return;

        String dimension = level.dimension().identifier().toString();
        for (CourseInfo course : courses) {
            if (course.dimension().equals(dimension)) add(course);
        }
        for (Placed placed : pieces) {
            placed.piece().spawn(level);
            applyHighlight(placed);
        }
    }

    private void add(CourseInfo course) {
        String id = course.id();
        pieces.add(new Placed(id, new StartZone(course)));
        for (int i = 0; i < course.checkpoints().size(); i++) {
            pieces.add(new Placed(id, new CheckpointMark(course.checkpoints().get(i), i + 1)));
        }
        if (course.finish() != null) {
            int order = course.checkpoints().size() + 1;
            pieces.add(new Placed(id, new FinishLine(course.finish(), course.finishYaw(), order)));
        }
        if (course.board() != null) pieces.add(new Placed(id, new BoardPiece(course)));
    }
}
