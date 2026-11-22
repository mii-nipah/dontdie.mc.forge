package nipah.dontdie.fabric;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import nipah.dontdie.RespawnNotice;

public final class ToastPacket {
    public static final ResourceLocation ID = new ResourceLocation(DontdieFabric.MOD_ID, "toast");

    static void send(ServerPlayer player) {
        if (ServerPlayNetworking.canSend(player, ID)) {
            var buffer = PacketByteBufs.create();
            buffer.writeComponent(RespawnNotice.title());
            buffer.writeComponent(RespawnNotice.subtitle());
            ServerPlayNetworking.send(player, ID, buffer);
        } else {
            // The penalties also work on a dedicated server with unmodded clients.
            RespawnNotice.sendFallback(player);
        }
    }

    private ToastPacket() {}
}
