package nipah.dontdie.fabric;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

record Config(int maxHits, int maxHitDamage, int minEffects, int maxEffects,
              int hungerLostOnDeath, List<EffectDefinition> deathEffects) {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    static Config defaults() {
        return new Values().validate();
    }

    static Config load(Path path) throws IOException {
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
        int maxHits = 13;
        int maxHitDamage = 3;
        int minEffects = 1;
        int maxEffects = 3;
        int hungerLostOnDeath = 0;
        List<String> items = List.of(
                "minecraft:slowness|30%|60-180s",
                "minecraft:blindness|10%|30-60s",
                "minecraft:poison|3%|5-15s");

        Config validate() {
            // Bound per-respawn work even when a config contains an accidental huge number.
            range("maxHits", maxHits, 0, 1000);
            range("maxHitDamage", maxHitDamage, 0, Integer.MAX_VALUE);
            range("minEffects", minEffects, 0, 1000);
            range("maxEffects", maxEffects, minEffects, 1000);
            range("hungerLostOnDeath", hungerLostOnDeath, 0, 20);
            if (items == null) {
                throw new IllegalArgumentException("items must be a list");
            }
            return new Config(maxHits, maxHitDamage, minEffects, maxEffects,
                    hungerLostOnDeath, items.stream().map(EffectDefinition::parse).toList());
        }

        private static void range(String name, int value, int min, int max) {
            if (value < min || value > max) {
                throw new IllegalArgumentException(name + " must be between " + min + " and " + max);
            }
        }
    }
}
