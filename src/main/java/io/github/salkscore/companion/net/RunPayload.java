package io.github.salkscore.companion.net;

import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;

public record RunPayload(RunEvent event) implements CustomPacketPayload {

    public static final Type<RunPayload> TYPE =
            new Type<>(Identifier.fromNamespaceAndPath("parkour", "run"));

    public static final StreamCodec<RegistryFriendlyByteBuf, RunPayload> CODEC = StreamCodec.of(
            (buf, payload) -> {
                throw new UnsupportedOperationException("Paquet serveur uniquement");
            },
            RunPayload::read);

    private static RunPayload read(RegistryFriendlyByteBuf buf) {
        RunEvent event = switch (buf.readByte()) {
            case 0 -> readStart(buf);
            case 1 -> new RunEvent.Checkpoint(buf.readVarInt(), buf.readLong());
            case 2 -> new RunEvent.Finish(buf.readLong(), buf.readVarInt());
            default -> new RunEvent.Stop();
        };
        return new RunPayload(event);
    }

    private static RunEvent.Start readStart(RegistryFriendlyByteBuf buf) {
        String course = buf.readUtf();
        Component name = parseName(buf.readUtf(), course);
        String dimension = buf.readUtf();

        int count = buf.readVarInt();
        List<Vec3> points = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            points.add(new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble()));
        }
        return new RunEvent.Start(course, name, dimension, List.copyOf(points));
    }

    private static Component parseName(String json, String fallback) {
        try {
            return ComponentSerialization.CODEC
                    .parse(JsonOps.INSTANCE, JsonParser.parseString(json))
                    .result()
                    .orElse(Component.literal(fallback));
        } catch (RuntimeException e) {
            return Component.literal(fallback);
        }
    }

    @Override
    public Type<RunPayload> type() {
        return TYPE;
    }
}
