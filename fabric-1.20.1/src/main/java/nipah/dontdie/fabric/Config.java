package nipah.dontdie.fabric;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffectInstance;
import nipah.dontdie.DeathPenalty.Settings;
import nipah.dontdie.EffectDefinition;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

final class Config {
    private Config() {}

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    static Settings defaults() {
        return new Values().validate();
    }

    static Settings load(Path path) throws IOException {
        if (!Files.exists(path)) {
            Files.createDirectories(path.getParent());
            Files.writeString(path, GSON.toJson(new Values()) + System.lineSeparator(), StandardCharsets.UTF_8);
        }
        try (var reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            Values values = GSON.fromJson(reader, Values.class);
            if (values == null) {
                throw new IllegalArgumentException("Config must contain a JSON object");
            }
            return values.validate();
        }
    }

    private static class Values {
        int maxHits = Settings.MAX_HITS;
        int maxHitDamage = Settings.MAX_HIT_DAMAGE;
        int minEffects = Settings.MIN_EFFECTS;
        int maxEffects = Settings.MAX_EFFECTS;
        int hungerLostOnDeath = Settings.HUNGER_LOST_ON_DEATH;
        List<String> items = Settings.ITEMS;

        Settings validate() {
            // Bound per-respawn work even when a config contains an accidental huge number.
            range("maxHits", maxHits, 0, 1000);
            range("maxHitDamage", maxHitDamage, 0, Integer.MAX_VALUE);
            range("minEffects", minEffects, 0, 1000);
            range("maxEffects", maxEffects, minEffects, 1000);
            range("hungerLostOnDeath", hungerLostOnDeath, 0, 20);
            if (items == null) {
                throw new IllegalArgumentException("items must be a list");
            }
            var effects = EffectDefinition.resolve(items, id -> {
                var effect = BuiltInRegistries.MOB_EFFECT.get(id);
                return effect == null ? null : ticks -> new MobEffectInstance(effect, ticks);
            });
            return new Settings(maxHits, maxHitDamage, minEffects, maxEffects, hungerLostOnDeath, effects);
        }

        private static void range(String name, int value, int min, int max) {
            if (value < min || value > max) {
                throw new IllegalArgumentException(name + " must be between " + min + " and " + max);
            }
        }
    }
}
