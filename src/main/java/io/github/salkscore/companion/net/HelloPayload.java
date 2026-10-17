package io.github.salkscore.companion.net;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

// Prévient le plugin que le mod est là, il coupe alors son action bar et sa bossbar
public record HelloPayload() implements CustomPacketPayload {

    public static final HelloPayload INSTANCE = new HelloPayload();
    public static final Type<HelloPayload> TYPE =
            new Type<>(Identifier.fromNamespaceAndPath("parkour", "hello"));
    public static final StreamCodec<RegistryFriendlyByteBuf, HelloPayload> CODEC =
            StreamCodec.unit(INSTANCE);

    @Override
    public Type<HelloPayload> type() {
        return TYPE;
    }
}
