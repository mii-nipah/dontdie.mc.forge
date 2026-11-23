package nipah.dontdie.fabric;

import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import nipah.dontdie.DeathPenalty;
import nipah.dontdie.DeathPenalty.Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;

final class FabricEvents {
    static final String MOD_ID = "dontdie";
    private static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    private final DeathPenalty penalty;
    private Settings config;

    FabricEvents(DeathPenalty.Hit hit) {
        penalty = new DeathPenalty(hit);
    }

    void register(Function<ResourceLocation, IntFunction<MobEffectInstance>> findEffect, Consumer<ServerPlayer> sendToast) {
        ServerLifecycleEvents.SERVER_STARTING.register(server -> {
            penalty.clear();
            var path = FabricLoader.getInstance().getConfigDir().resolve("dontdie.json");
            try {
                config = Config.load(path).validate(findEffect);
            } catch (IOException | RuntimeException ex) {
                LOGGER.error("Could not load {}; using the default death penalties", path, ex);
                config = new Config().validate(findEffect);
            }
        });
        ServerLifecycleEvents.SERVER_STOPPED.register(server -> penalty.clear());
        ServerTickEvents.END_SERVER_TICK.register(server -> penalty.tick());
        ServerPlayerEvents.COPY_FROM.register((oldPlayer, player, alive) -> {
            if (!alive && !player.isSpectator()) {
                DeathPenalty.restoreHunger(oldPlayer.getFoodData(), player.getFoodData(), config);
            }
        });
        ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, player, alive) -> {
            if (!alive && !player.isSpectator()) {
                penalty.apply(player, config);
                sendToast.accept(player);
            }
        });
    }
}
