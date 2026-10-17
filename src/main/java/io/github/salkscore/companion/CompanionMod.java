package io.github.salkscore.companion;

import com.mojang.blaze3d.platform.InputConstants;
import io.github.salkscore.companion.hud.RunHud;
import io.github.salkscore.companion.net.HelloPayload;
import io.github.salkscore.companion.net.RunPayload;
import io.github.salkscore.companion.run.RunTracker;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.C2SPlayChannelEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;

public final class CompanionMod implements ClientModInitializer {

    public static final String ID = "parkour-companion";

    private final Config config = Config.load();
    private final RunTracker tracker = new RunTracker(config);
    private KeyMapping ghostKey;
    private KeyMapping hudKey;

    @Override
    public void onInitializeClient() {
        PayloadTypeRegistry.playS2C().register(RunPayload.TYPE, RunPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(HelloPayload.TYPE, HelloPayload.CODEC);
        ClientPlayNetworking.registerGlobalReceiver(RunPayload.TYPE,
                (payload, context) -> tracker.handle(payload.event()));

        C2SPlayChannelEvents.REGISTER.register((handler, sender, client, channels) -> {
            if (channels.contains(HelloPayload.TYPE.id())) sender.sendPacket(HelloPayload.INSTANCE);
        });
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> tracker.stop());

        KeyMapping.Category category = KeyMapping.Category.register(id("main"));
        ghostKey = key("ghost", GLFW.GLFW_KEY_G, category);
        hudKey = key("hud", GLFW.GLFW_KEY_H, category);
        ClientTickEvents.END_CLIENT_TICK.register(this::tick);

        HudElementRegistry.addLast(id("hud"), new RunHud(config, tracker));
    }

    private void tick(Minecraft client) {
        while (ghostKey.consumeClick()) {
            tracker.setGhost(!config.ghost);
            config.save();
            notify(client, "ghost", config.ghost);
        }
        while (hudKey.consumeClick()) {
            config.hud = !config.hud;
            config.save();
            notify(client, "hud", config.hud);
        }
        tracker.tick(client);
    }

    private static void notify(Minecraft client, String what, boolean on) {
        if (client.player == null) return;
        String state = on ? "on" : "off";
        client.player.displayClientMessage(
                Component.translatable("msg." + ID + "." + what + "." + state), true);
    }

    private static KeyMapping key(String name, int code, KeyMapping.Category category) {
        KeyMapping mapping = new KeyMapping("key." + ID + "." + name,
                InputConstants.Type.KEYSYM, code, category);
        return KeyBindingHelper.registerKeyBinding(mapping);
    }

    private static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(ID, path);
    }
}
