package io.github.salkscore.companion.world;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.phys.Vec3;

interface Piece {

    void spawn(ClientLevel level);

    void remove();

    // vrai quand le chunk a été déchargé et qu'il faut tout recréer
    boolean missing();

    Vec3 anchor();

    // 0 pour le départ, 1..n pour les checkpoints, n + 1 pour l'arrivée, -1 sinon
    default int order() {
        return -1;
    }

    default void highlight(boolean on) {
    }
}
