package nipah.dontdie.fabric;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public final class ToastPacket {
    public static final ResourceLocation ID = new ResourceLocation(DontdieFabric.MOD_ID, "toast");

    static void send(ServerPlayer player) {
        var title = Component.translatable("dontdie.toast.title").withStyle(ChatFormatting.RED);
        var subtitle = Component.translatable("dontdie.toast.subtitle").withStyle(ChatFormatting.GRAY);
        if (ServerPlayNetworking.canSend(player, ID)) {
            var buffer = PacketByteBufs.create();
            buffer.writeComponent(title);
            buffer.writeComponent(subtitle);
            ServerPlayNetworking.send(player, ID, buffer);
        } else {
            // The penalties also work on a dedicated server with unmodded clients.
            player.sendSystemMessage(Component.literal("Death Penalty! Thought it would be that easy?"));
        }
    }

    private ToastPacket() {}
}
