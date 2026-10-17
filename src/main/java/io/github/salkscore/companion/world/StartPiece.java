package io.github.salkscore.companion.world;

import com.mojang.math.Transformation;
import io.github.salkscore.companion.net.CourseInfo;
import io.github.salkscore.companion.util.TimeFormat;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Display;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;

// Socle lumineux qui tourne sur la plaque de départ, avec une étiquette au-dessus
final class StartPiece implements Piece {

    private static final float PAD = 1.8f;
    private static final float LABEL_W = 2.6f;
    private static final float LABEL_H = LABEL_W * 24 / 64;
    private static final double LABEL_Y = 2.35;
    private static final float TEXT_SCALE = 0.8f;

    private final CourseInfo course;
    private Display.ItemDisplay pad;
    private Display.ItemDisplay label;
    private Display.TextDisplay text;

    StartPiece(CourseInfo course) {
        this.course = course;
    }

    @Override
    public void spawn(ClientLevel level) {
        Vec3 start = course.start();
        pad = ClientEntities.item(level, start.add(0, 0.07, 0), "start_pad", padTransform(0f));

        Vec3 top = start.add(0, LABEL_Y, 0);
        label = ClientEntities.item(level, top, "label_panel",
                ClientEntities.transform(0, 0, 0, new Vector3f(LABEL_W, LABEL_H, 1f)));
        label.setBillboardConstraints(Display.BillboardConstraints.VERTICAL);

        Vector3f scale = new Vector3f(TEXT_SCALE);
        text = ClientEntities.text(level, top, content(), 120,
                ClientEntities.transform(0, -0.33f, 0.04f, scale));
        text.setBillboardConstraints(Display.BillboardConstraints.VERTICAL);
    }

    private Component content() {
        Component best = course.hasBest()
                ? Component.empty()
                        .append(Glyphs.of(Glyphs.TROPHY))
                        .append(" ")
                        .append(Component.translatable("decor.parkour-companion.best",
                                TimeFormat.format(course.best()), course.rank())
                                .withColor(Palette.GOLD))
                : Component.translatable("decor.parkour-companion.no-best")
                        .withColor(Palette.MUTED);

        return Component.empty()
                .append(course.name().copy().withStyle(style -> style.withBold(true)))
                .append("\n")
                .append(Glyphs.of(Glyphs.PLAY))
                .append(" ")
                .append(Component.translatable("decor.parkour-companion.start")
                        .withColor(Palette.TEAL))
                .append("\n")
                .append(best);
    }

    @Override
    public void tick(int tick) {
        if (tick % 10 != 0 || ClientEntities.gone(pad)) return;
        pad.setTransformationInterpolationDelay(0);
        pad.setTransformationInterpolationDuration(10);
        pad.setTransformation(padTransform((float) Math.toRadians(tick * 2.5)));
    }

    private static Transformation padTransform(float angle) {
        return new Transformation(new Vector3f(0, 0.5f, 0), new Quaternionf().rotateY(angle),
                new Vector3f(PAD, 1f, PAD), new Quaternionf());
    }

    @Override
    public void remove() {
        ClientEntities.remove(pad);
        ClientEntities.remove(label);
        ClientEntities.remove(text);
    }

    @Override
    public boolean missing() {
        return ClientEntities.gone(pad) || ClientEntities.gone(label) || ClientEntities.gone(text);
    }

    @Override
    public Vec3 anchor() {
        return course.start();
    }
}
