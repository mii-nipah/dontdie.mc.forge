package nipah.dontdie;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

@EventBusSubscriber(modid = Dontdie.MODID)
public final class NeoForgeEvents {
    private static final DeathPenalty PENALTY = new DeathPenalty(DeathPenalty::vanillaHit);

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        if (event.isWasDeath() && event.getEntity() instanceof ServerPlayer player && !player.isSpectator()) {
            DeathPenalty.restoreHunger(event.getOriginal().getFoodData(), player.getFoodData(), Config.settings);
        }
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (!event.isEndConquered() && event.getEntity() instanceof ServerPlayer player && !player.isSpectator()) {
            PENALTY.apply(player, Config.settings);
            NetworkHandler.sendToast(player);
        }
    }

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        PENALTY.tick();
    }

    @SubscribeEvent
    public static void onServerStopped(ServerStoppedEvent event) {
        PENALTY.clear();
    }
}
