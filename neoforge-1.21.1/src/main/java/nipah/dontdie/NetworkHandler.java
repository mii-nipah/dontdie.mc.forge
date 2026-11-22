package nipah.dontdie;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.NetworkRegistry;
import nipah.dontdie.client.ToastHelper;

final class NetworkHandler {
    static void register(RegisterPayloadHandlersEvent event) {
        event.registrar("0.0.1").optional().playToClient(ToastMsg.TYPE, ToastMsg.CODEC,
                (message, context) -> ToastHelper.show(message.title(), message.subtitle()));
    }

    static void sendToast(ServerPlayer player) {
        if (NetworkRegistry.hasChannel(player.connection, ToastMsg.TYPE.id())) {
            PacketDistributor.sendToPlayer(player, new ToastMsg(RespawnNotice.title(), RespawnNotice.subtitle()));
        } else {
            RespawnNotice.sendFallback(player);
        }
    }

    record ToastMsg(Component title, Component subtitle) implements CustomPacketPayload {
        static final Type<ToastMsg> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Dontdie.MODID, "main"));
        static final StreamCodec<RegistryFriendlyByteBuf, ToastMsg> CODEC = StreamCodec.composite(
                ComponentSerialization.STREAM_CODEC, ToastMsg::title,
                ComponentSerialization.STREAM_CODEC, ToastMsg::subtitle,
                ToastMsg::new);

        @Override
        public Type<ToastMsg> type() {
            return TYPE;
        }
    }
}
