package nipah.dontdie;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import nipah.dontdie.client.ToastHelper;

import java.util.function.Supplier;

public final class NetworkHandler {
    private static final String PROTOCOL = "1";
    private static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(Dontdie.MODID, "main"),
            () -> PROTOCOL, PROTOCOL::equals, PROTOCOL::equals);

    static void init() {
        CHANNEL.messageBuilder(ToastMsg.class, 0, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(ToastMsg::encode)
                .decoder(ToastMsg::decode)
                .consumerMainThread(ToastMsg::handle)
                .add();
    }

    static void sendToast(ServerPlayer player) {
        CHANNEL.send(PacketDistributor.PLAYER.with(() -> player),
                new ToastMsg(RespawnNotice.title(), RespawnNotice.subtitle()));
    }

    public record ToastMsg(Component title, Component subtitle) {
        static void encode(ToastMsg m, FriendlyByteBuf buf) {
            buf.writeComponent(m.title);
            buf.writeComponent(m.subtitle);
        }
        static ToastMsg decode(FriendlyByteBuf buf) {
            return new ToastMsg(buf.readComponent(), buf.readComponent());
        }
        static void handle(ToastMsg m, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() ->
                    ToastHelper.show(m.title, m.subtitle));
            ctx.get().setPacketHandled(true);
        }
    }
}
