package nipah.dontdie;

import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.IConfigSpec;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.slf4j.Logger;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Forge's config APIs
@EventBusSubscriber(modid = Dontdie.MODID)
public class Config
{
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    private static final ModConfigSpec.IntValue MAX_HITS = BUILDER
            .comment("Maximum number of hits the player will take after respawning.")
            .defineInRange("maxHits", 13, 0, Integer.MAX_VALUE);
    private static final ModConfigSpec.IntValue MAX_DAMAGE = BUILDER
            .comment("The maximum amount of damage the player will take on each hit.")
            .defineInRange("maxHitDamage", 3, 0, Integer.MAX_VALUE);

    private static final ModConfigSpec.IntValue MIN_EFFECTS = BUILDER
            .comment("Minimum number of effects to apply on death.")
            .defineInRange("minEffects", 1, 0, Integer.MAX_VALUE);
    private static final ModConfigSpec.IntValue MAX_EFFECTS = BUILDER
            .comment("Maximum number of effects to apply on death.")
            .defineInRange("maxEffects", 3, 0, Integer.MAX_VALUE);

    // a list of strings that are treated as resource locations for items
    private static final ModConfigSpec.ConfigValue<List<? extends String>> DEATH_EFFECTS = BUILDER
            .comment("A list of items to log on common setup.")
            .defineListAllowEmpty("items",
                    List.of(
                            "minecraft:slowness|30%|60-180s",
                            "minecraft:blindness|10%|30-60s",
                            "minecraft:poison|3%|5-15s"
                    ),
                    Config::validateEffectName);

    static final IConfigSpec SPEC = BUILDER.build();

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
        return BuiltInRegistries.ITEM.containsKey(MUtils.ResLoc(itemName));
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

        var effKey = ResourceKey.create(Registries.MOB_EFFECT, MUtils.ResLoc(itemName));
        var effect = BuiltInRegistries.MOB_EFFECT.getHolderOrThrow(effKey);

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
