package nipah.dontdie;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Forge's config APIs
@Mod.EventBusSubscriber(modid = Dontdie.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config
{
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    private static final ForgeConfigSpec.IntValue MAX_HITS = BUILDER
            .comment("Maximum number of hits the player will take after respawning.")
            .defineInRange("maxHits", 13, 0, Integer.MAX_VALUE);
    private static final ForgeConfigSpec.IntValue MAX_DAMAGE = BUILDER
            .comment("The maximum amount of damage the player will take on each hit.")
            .defineInRange("maxHitDamage", 3, 0, Integer.MAX_VALUE);

    private static final ForgeConfigSpec.IntValue MIN_EFFECTS = BUILDER
            .comment("Minimum number of effects to apply on death.")
            .defineInRange("minEffects", 1, 0, Integer.MAX_VALUE);
    private static final ForgeConfigSpec.IntValue MAX_EFFECTS = BUILDER
            .comment("Maximum number of effects to apply on death.")
            .defineInRange("maxEffects", 3, 0, Integer.MAX_VALUE);

    // a list of strings that are treated as resource locations for items
    private static final ForgeConfigSpec.ConfigValue<List<? extends String>> DEATH_EFFECTS = BUILDER
            .comment("A list of items to log on common setup.")
            .defineListAllowEmpty("items",
                    List.of(
                            "minecraft:slowness|30%|60-180s",
                            "minecraft:blindness|10%|30-60s",
                            "minecraft:poison|3%|5-15s"
                    ),
                    Config::validateEffectName);

    static final ForgeConfigSpec SPEC = BUILDER.build();

    public static int maxHits;
    public static int maxDamage;

    public static int minEffects;
    public static int maxEffects;
    public static List<EffectInfo> deathEffects;

    private static boolean validateEffectName(final Object obj)
    {
        if(!(obj instanceof String effectComposite))
            return false;
        // split the string into parts, expecting a format like "minecraft:slowness|60-180s"
        String[] parts = effectComposite.split("\\|");
        if(parts.length != 3)
            return false;
        // the first part is the item name, the second part is the duration
        String itemName = parts[0];
        String chance = parts[1];
        String duration = parts[2];
        // check if the chance is in the format "30%" or "10%"
        if(!chance.matches("^(\\d{1,2}|100)%"))
            return false;
        // check if the duration is in the format "60-180s" or "30-60s"
        if(!duration.matches("^\\d+-\\d+s"))
            return false;
        // check if the item exists in the registry
        return ForgeRegistries.ITEMS.containsKey(MUtils.ResLoc(itemName));
    }
    static EffectInfo parseEffect(String effectComposite) {
        var parts = effectComposite.split("\\|");
        var itemName = parts[0];
        var chanceStr = parts[1];
        var durationStr = parts[2];

        var chanceNum = Integer.parseInt(chanceStr.replace("%", ""));

        var durationParts = durationStr.split("-");
        var minDuration = Integer.parseInt(durationParts[0].replace("s", ""));
        var maxDuration = Integer.parseInt(durationParts[1].replace("s", ""));

        var effect = ForgeRegistries.MOB_EFFECTS.getValue(MUtils.ResLoc(itemName));

        return new EffectInfo(
            effect,
            chanceNum,
            MUtils.secondsToTicks(minDuration),
            MUtils.secondsToTicks(maxDuration)
        );
    }

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event)
    {
        maxHits = MAX_HITS.get();
        maxDamage = MAX_DAMAGE.get();

        minEffects = MIN_EFFECTS.get();
        maxEffects = MAX_EFFECTS.get();
        // convert the list of strings into a list of effect infos
        deathEffects = DEATH_EFFECTS.get().stream()
            .map(Config::parseEffect)
            .collect(Collectors.toList());
    }
}
