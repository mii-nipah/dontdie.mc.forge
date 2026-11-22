package nipah.dontdie;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.fml.event.config.ModConfigEvent;
import nipah.dontdie.DeathPenalty.Settings;
import java.util.List;

final class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    private static final ModConfigSpec.IntValue MAX_HITS = BUILDER
            .comment("Maximum number of hits the player will take after respawning.")
            .defineInRange("maxHits", Settings.MAX_HITS, 0, Integer.MAX_VALUE);
    private static final ModConfigSpec.IntValue MAX_DAMAGE = BUILDER
            .comment("The maximum amount of damage the player will take on each hit.")
            .defineInRange("maxHitDamage", Settings.MAX_HIT_DAMAGE, 0, Integer.MAX_VALUE);
    private static final ModConfigSpec.IntValue MIN_EFFECTS = BUILDER
            .comment("Minimum number of effects to apply on death.")
            .defineInRange("minEffects", Settings.MIN_EFFECTS, 0, Integer.MAX_VALUE);
    private static final ModConfigSpec.IntValue MAX_EFFECTS = BUILDER
            .comment("Maximum number of effects to apply on death.")
            .defineInRange("maxEffects", Settings.MAX_EFFECTS, 0, Integer.MAX_VALUE);
    private static final ModConfigSpec.IntValue HUNGER_LOST_ON_DEATH = BUILDER
            .comment("Hunger lost on death.")
            .defineInRange("hungerLostOnDeath", Settings.HUNGER_LOST_ON_DEATH, 0, 20);
    private static final ModConfigSpec.ConfigValue<List<? extends String>> DEATH_EFFECTS = BUILDER
            .comment("Death effects in the format id|chance%|minimum-maximums.")
            .defineListAllowEmpty("items", () -> Settings.ITEMS, EffectDefinition::isValid);

    static final ModConfigSpec SPEC = BUILDER.build();
    static volatile Settings settings;

    static void onLoad(ModConfigEvent event) {
        if (event.getConfig().getSpec() == SPEC) {
            var effects = EffectDefinition.resolve(DEATH_EFFECTS.get(), id -> {
                var effect = BuiltInRegistries.MOB_EFFECT.getHolder(ResourceKey.create(Registries.MOB_EFFECT, id))
                        .orElse(null);
                return effect == null ? null : ticks -> new MobEffectInstance(effect, ticks);
            });
            settings = new Settings(MAX_HITS.get(), MAX_DAMAGE.get(), MIN_EFFECTS.get(), MAX_EFFECTS.get(),
                    HUNGER_LOST_ON_DEATH.get(), effects);
        }
    }
}
