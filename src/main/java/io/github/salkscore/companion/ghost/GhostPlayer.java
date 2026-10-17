package io.github.salkscore.companion.ghost;

import com.mojang.authlib.GameProfile;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Pose;

public final class GhostPlayer {

    // l'interpolation vanilla a 3 ticks de retard, on prend donc la frame un peu en avance
    private static final int LEAD = 3;
    private static final int ENTITY_ID = -0x5A1C0;

    private GhostRecording recording;
    private GhostEntity entity;

    public void start(GhostRecording recording, int tick) {
        stop();
        ClientLevel level = Minecraft.getInstance().level;
        if (recording == null || recording.frames().isEmpty() || level == null) return;

        this.recording = recording;
        entity = new GhostEntity(level, new GameProfile(UUID.randomUUID(), "Record"));
        entity.setId(ENTITY_ID);
        GhostFrame frame = recording.frame(tick);
        entity.snapTo(frame.x(), frame.y(), frame.z(), frame.yaw(), frame.pitch());
        entity.setYHeadRot(frame.head());
        level.addEntity(entity);
    }

    public void tick(int tick) {
        if (entity == null) return;
        if (entity.isRemoved()) {
            entity = null;
            return;
        }

        GhostFrame frame = recording.frame(tick + LEAD);
        entity.moveOrInterpolateTo(frame.position(), frame.yaw(), frame.pitch());
        entity.lerpHeadTo(frame.head(), LEAD);
        entity.setShiftKeyDown(frame.crouching());
        entity.setPose(frame.crouching() ? Pose.CROUCHING : Pose.STANDING);
    }

    public void stop() {
        ClientLevel level = Minecraft.getInstance().level;
        if (entity != null && level != null) {
            level.removeEntity(entity.getId(), Entity.RemovalReason.DISCARDED);
        }
        entity = null;
        recording = null;
    }

    public boolean active() {
        return entity != null;
    }
}
