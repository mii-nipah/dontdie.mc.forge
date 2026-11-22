package nipah.dontdie;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import nipah.dontdie.compats.FirstAid;

@Mod.EventBusSubscriber(modid = Dontdie.MODID)
public final class ForgeEvents {
    private static final DeathPenalty PENALTY = new DeathPenalty((player, damage, random) -> {
        if (ModList.get().isLoaded("firstaid")) {
            FirstAid.hurtSafePart(player, damage, random);
        } else {
            DeathPenalty.vanillaHit(player, damage, random);
        }
    });

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
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            PENALTY.tick();
        }
    }

    @SubscribeEvent
    public static void onServerStopped(ServerStoppedEvent event) {
        PENALTY.clear();
    }
}
