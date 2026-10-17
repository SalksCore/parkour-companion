package io.github.salkscore.companion.world;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.phys.Vec3;

interface Piece {

    void spawn(ClientLevel level);

    void remove();

    // vrai quand le chunk a été déchargé et qu'il faut tout recréer
    boolean missing();

    Vec3 anchor();

    default void tick(int tick) {
    }
}
