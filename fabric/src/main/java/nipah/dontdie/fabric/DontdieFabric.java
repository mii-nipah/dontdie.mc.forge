package nipah.dontdie.fabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class DontdieFabric implements ModInitializer {
    public static final String MOD_ID = "dontdie";
    private static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    private final Random random = new Random();
    private final TickScheduler scheduler = new TickScheduler();
    private Config config = Config.defaults();
    private List<DeathEffect> effects = List.of();

    @Override
    public void onInitialize() {
        ServerLifecycleEvents.SERVER_STARTING.register(server -> {
            scheduler.clear();
            var path = FabricLoader.getInstance().getConfigDir().resolve("dontdie.json");
            try {
                config = Config.load(path);
            } catch (IOException | RuntimeException ex) {
                LOGGER.error("Could not load {}; using the default death penalties", path, ex);
                config = Config.defaults();
            }
            effects = resolveEffects(config);
        });
        ServerLifecycleEvents.SERVER_STOPPED.register(server -> scheduler.clear());
        ServerTickEvents.END_SERVER_TICK.register(server -> scheduler.tick());
        ServerPlayerEvents.COPY_FROM.register(this::copyHunger);
        ServerPlayerEvents.AFTER_RESPAWN.register(this::afterRespawn);
    }

    private void copyHunger(ServerPlayer oldPlayer, ServerPlayer player, boolean alive) {
        // alive is true when returning from the End, which is not a death.
        if (alive || player.isSpectator()) {
            return;
        }
        var oldFood = oldPlayer.getFoodData();
        var restored = HungerPenalty.restore(oldFood.getFoodLevel(), oldFood.getSaturationLevel(),
                config.hungerLostOnDeath());
        player.getFoodData().setFoodLevel(restored.foodLevel());
        player.getFoodData().setSaturation(restored.saturation());
    }

    private void afterRespawn(ServerPlayer oldPlayer, ServerPlayer player, boolean alive) {
        if (alive || player.isSpectator()) {
            return;
        }
        var server = player.server;
        int hits = inclusive(0, config.maxHits());
        for (int i = 0; i < hits; i++) {
            int damage = inclusive(0, config.maxHitDamage());
            scheduler.schedule(i * 11, () -> {
                // A later death, logout, or replacement player invalidates queued hits.
                if (server.getPlayerList().getPlayer(player.getUUID()) != player
                        || !player.isAlive() || player.isSpectator()) {
                    return;
                }
                if (damage > 0 && player.getHealth() > damage) {
                    player.hurt(player.damageSources().fellOutOfWorld(), damage);
                }
            });
        }

        int count = inclusive(config.minEffects(), config.maxEffects());
        for (int i = 0; i < count && !effects.isEmpty(); i++) {
            var effect = effects.get(random.nextInt(effects.size()));
            var definition = effect.definition();
            if (player.isAlive() && random.nextInt(100) < definition.chance()) {
                player.addEffect(new MobEffectInstance(effect.effect(),
                        inclusive(definition.minTicks(), definition.maxTicks())));
            }
        }
        ToastPacket.send(player);
    }

    private int inclusive(int min, int max) {
        return (int) random.nextLong(min, (long) max + 1);
    }

    private static List<DeathEffect> resolveEffects(Config config) {
        List<DeathEffect> effects = new ArrayList<>();
        for (var definition : config.deathEffects()) {
            var id = new ResourceLocation(definition.id());
            // Resolve the effect registry, not the item registry.
            var effect = BuiltInRegistries.MOB_EFFECT.getOptional(id);
            if (effect.isPresent()) {
                effects.add(new DeathEffect(definition, effect.get()));
            } else {
                LOGGER.warn("Ignoring unknown death effect {}", id);
            }
        }
        return List.copyOf(effects);
    }

    private record DeathEffect(EffectDefinition definition, MobEffect effect) {}
}
