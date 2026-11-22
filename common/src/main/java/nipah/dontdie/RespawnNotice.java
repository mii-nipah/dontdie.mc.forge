package nipah.dontdie;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public final class RespawnNotice {
    private RespawnNotice() {}

    public static Component title() {
        return Component.translatable("dontdie.toast.title").withStyle(ChatFormatting.RED);
    }

    public static Component subtitle() {
        return Component.translatable("dontdie.toast.subtitle").withStyle(ChatFormatting.GRAY);
    }

    public static void sendFallback(ServerPlayer player) {
        player.sendSystemMessage(Component.literal("Death Penalty! Thought it would be that easy?"));
    }
}
