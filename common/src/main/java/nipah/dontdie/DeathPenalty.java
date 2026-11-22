package nipah.dontdie;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.food.FoodData;

import java.util.List;
import java.util.Random;

public final class DeathPenalty {
    private final Random random = new Random();
    private final TickScheduler scheduler = new TickScheduler();
    private final Hit hit;

    public DeathPenalty(Hit hit) {
        this.hit = hit;
    }

    public record Settings(int maxHits, int maxHitDamage, int minEffects, int maxEffects,
                           int hungerLostOnDeath, List<EffectDefinition.Resolved> effects) {
        public static final int MAX_HITS = 13;
        public static final int MAX_HIT_DAMAGE = 3;
        public static final int MIN_EFFECTS = 1;
        public static final int MAX_EFFECTS = 3;
        public static final int HUNGER_LOST_ON_DEATH = 0;
        public static final List<String> ITEMS = List.of(
                "minecraft:slowness|30%|60-180s",
                "minecraft:blindness|10%|30-60s",
                "minecraft:poison|3%|5-15s");
    }

    @FunctionalInterface
    public interface Hit {
        void apply(ServerPlayer player, int damage, Random random);
    }

    public static void vanillaHit(ServerPlayer player, int damage, Random random) {
        if (player.getHealth() > damage) {
            player.hurt(player.damageSources().fellOutOfWorld(), damage);
        }
    }

    public static void restoreHunger(FoodData oldFood, FoodData food, Settings settings) {
        int level = Math.max(0, Math.min(20, oldFood.getFoodLevel()) - settings.hungerLostOnDeath());
        food.setFoodLevel(level);
        food.setSaturation(Math.max(0, Math.min(oldFood.getSaturationLevel(), level)));
    }

    public void apply(ServerPlayer player, Settings settings) {
        var server = player.server;
        int hits = inclusive(0, settings.maxHits());
        for (int i = 0; i < hits; i++) {
            int damage = inclusive(0, settings.maxHitDamage());
            if (damage > 0) {
                scheduler.schedule(i * 11L, () -> {
                    // A later death, logout, or replacement player invalidates these hits.
                    if (server.getPlayerList().getPlayer(player.getUUID()) == player
                            && player.isAlive() && !player.isSpectator()) {
                        hit.apply(player, damage, random);
                    }
                });
            }
        }

        var effects = settings.effects();
        int count = inclusive(settings.minEffects(), Math.max(settings.minEffects(), settings.maxEffects()));
        for (int i = 0; i < count && !effects.isEmpty() && player.isAlive(); i++) {
            var effect = effects.get(random.nextInt(effects.size()));
            var definition = effect.definition();
            if (random.nextInt(100) < definition.chance()) {
                player.addEffect(effect.create().apply(inclusive(definition.minTicks(), definition.maxTicks())));
            }
        }
    }

    public void tick() {
        scheduler.tick();
    }

    public void clear() {
        scheduler.clear();
    }

    private int inclusive(int min, int max) {
        return (int) random.nextLong(min, (long) max + 1);
    }
}
