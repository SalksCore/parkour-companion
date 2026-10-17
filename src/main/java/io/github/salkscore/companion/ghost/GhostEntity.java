package io.github.salkscore.companion.ghost;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.player.RemotePlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.PlayerSkin;

// invisible mais visible pour nous : le jeu le dessine en transparent
final class GhostEntity extends RemotePlayer {

    GhostEntity(ClientLevel level, GameProfile profile) {
        super(level, profile);
    }

    @Override
    public boolean isInvisible() {
        return true;
    }

    @Override
    public boolean isInvisibleTo(Player player) {
        return false;
    }

    @Override
    public PlayerSkin getSkin() {
        LocalPlayer player = Minecraft.getInstance().player;
        return player != null ? player.getSkin() : super.getSkin();
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    protected void pushEntities() {
    }
}
