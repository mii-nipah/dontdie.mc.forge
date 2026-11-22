package nipah.dontdie;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@Mod(Dontdie.MODID)
public final class Dontdie {
    public static final String MODID = "dontdie";

    public Dontdie() {
        var modBus = FMLJavaModLoadingContext.get().getModEventBus();
        var items = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
        var tabs = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

        // Keep the existing registry entries for worlds that already contain the item.
        var exampleItem = items.register("example_item", () -> new Item(new Item.Properties().food(
                new FoodProperties.Builder().alwaysEat().nutrition(1).saturationMod(2f).build())));
        tabs.register("dontdie", () -> CreativeModeTab.builder()
                .withTabsBefore(CreativeModeTabs.COMBAT)
                .icon(() -> exampleItem.get().getDefaultInstance())
                .displayItems((parameters, output) -> output.accept(exampleItem.get()))
                .build());
        items.register(modBus);
        tabs.register(modBus);

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
        modBus.addListener(Config::onLoad);
        NetworkHandler.init();
    }
}
