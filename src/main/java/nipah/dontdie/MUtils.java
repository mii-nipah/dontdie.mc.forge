package nipah.dontdie;

import net.minecraft.ChatFormatting;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.List;
import java.util.Random;
import java.util.function.Predicate;

public class MUtils {
    public static ResourceLocation ResLoc(String locationName) {
        return ResourceLocation.parse(locationName);
    }
    public static <T extends Registry<T>> ResourceKey<T> ResKey(ResourceLocation location, ResourceKey<? extends T> registry) {
        return ResourceKey.create(registry, location);
    }
    public static int secondsToTicks(int seconds) {
        return seconds * 20;
    }
    public static <T> T randomItem(Random rand, List<T> items) {
        if (items.isEmpty()) return null;
        return items.get(rand.nextInt(items.size()));
    }
    public static <T> T randomItemMatching(Random rand, List<T> items, Predicate<T> predicate) {
        if (items.isEmpty()) return null;
        var clone = items.stream().filter(predicate).toList();
        return clone.get(rand.nextInt(clone.size()));
    }
    public static boolean testChance(Random rand, int chance) {
        if (chance < 0 || chance > 100) {
            throw new IllegalArgumentException("Chance must be between 0 and 100");
        }
        return rand.nextInt(100) < chance;
    }

    public static void notify(ServerPlayer player, String title, String message) {
        MutableComponent msg = Component.literal(title)
                .withStyle(ChatFormatting.RED)
                .append(Component.literal(message)
                        .withStyle(ChatFormatting.GRAY));

        player.sendSystemMessage(msg);
    }
    public static void notifyToast(ServerPlayer player, String title, String message) {
        notifyToast(
            player,
            Component.literal(title).withStyle(ChatFormatting.RED),
            Component.literal(message).withStyle(ChatFormatting.GRAY)
        );
    }
    public static void notifyToast(ServerPlayer player, Component title, Component message) {
        PacketDistributor.sendToPlayer(
            player,
            new NetworkHandler.ToastMsg(title, message)
        );
    }
}
