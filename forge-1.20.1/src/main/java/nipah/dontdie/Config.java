package nipah.dontdie;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.registries.ForgeRegistries;
import nipah.dontdie.DeathPenalty.Settings;
import java.util.List;

final class Config {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    private static final ForgeConfigSpec.IntValue MAX_HITS = BUILDER
            .comment("Maximum number of hits the player will take after respawning.")
            .defineInRange("maxHits", Settings.MAX_HITS, 0, Integer.MAX_VALUE);
    private static final ForgeConfigSpec.IntValue MAX_DAMAGE = BUILDER
            .comment("The maximum amount of damage the player will take on each hit.")
            .defineInRange("maxHitDamage", Settings.MAX_HIT_DAMAGE, 0, Integer.MAX_VALUE);
    private static final ForgeConfigSpec.IntValue MIN_EFFECTS = BUILDER
            .comment("Minimum number of effects to apply on death.")
            .defineInRange("minEffects", Settings.MIN_EFFECTS, 0, Integer.MAX_VALUE);
    private static final ForgeConfigSpec.IntValue MAX_EFFECTS = BUILDER
            .comment("Maximum number of effects to apply on death.")
            .defineInRange("maxEffects", Settings.MAX_EFFECTS, 0, Integer.MAX_VALUE);
    private static final ForgeConfigSpec.IntValue HUNGER_LOST_ON_DEATH = BUILDER
            .comment("Hunger lost on death.")
            .defineInRange("hungerLostOnDeath", Settings.HUNGER_LOST_ON_DEATH, 0, 20);
    private static final ForgeConfigSpec.ConfigValue<List<? extends String>> DEATH_EFFECTS = BUILDER
            .comment("Death effects in the format id|chance%|minimum-maximums.")
            .defineListAllowEmpty("items", Settings.ITEMS, EffectDefinition::isValid);

    static final ForgeConfigSpec SPEC = BUILDER.build();
    static volatile Settings settings;

    static void onLoad(ModConfigEvent event) {
        if (event.getConfig().getSpec() == SPEC) {
            var effects = EffectDefinition.resolve(DEATH_EFFECTS.get(), id -> {
                var effect = ForgeRegistries.MOB_EFFECTS.getValue(id);
                return effect == null ? null : ticks -> new MobEffectInstance(effect, ticks);
            });
            settings = new Settings(MAX_HITS.get(), MAX_DAMAGE.get(), MIN_EFFECTS.get(), MAX_EFFECTS.get(),
                    HUNGER_LOST_ON_DEATH.get(), effects);
        }
    }
}
