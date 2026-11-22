package nipah.dontdie.fabric;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import nipah.dontdie.RespawnNotice;

record ToastPacket(Component title, Component subtitle) implements CustomPacketPayload {
    static final Type<ToastPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(FabricEvents.MOD_ID, "toast"));
    static final StreamCodec<RegistryFriendlyByteBuf, ToastPacket> CODEC = StreamCodec.composite(
            ComponentSerialization.STREAM_CODEC, ToastPacket::title,
            ComponentSerialization.STREAM_CODEC, ToastPacket::subtitle,
            ToastPacket::new);

    @Override
    public Type<ToastPacket> type() {
        return TYPE;
    }

    static void send(ServerPlayer player) {
        if (ServerPlayNetworking.canSend(player, TYPE)) {
            ServerPlayNetworking.send(player, new ToastPacket(RespawnNotice.title(), RespawnNotice.subtitle()));
        } else {
            RespawnNotice.sendFallback(player);
        }
    }
}
