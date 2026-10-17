package io.github.salkscore.companion.world;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.phys.Vec3;

interface Piece {

    void spawn(ClientLevel level);

    void remove();

    boolean missing();

    Vec3 anchor();
}
