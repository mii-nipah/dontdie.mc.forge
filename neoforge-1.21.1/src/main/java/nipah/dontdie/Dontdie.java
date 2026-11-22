package nipah.dontdie;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;

@Mod(Dontdie.MODID)
public final class Dontdie {
    public static final String MODID = "dontdie";

    public Dontdie(IEventBus modBus, ModContainer container) {
        container.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
        modBus.addListener(Config::onLoad);
        modBus.addListener(NetworkHandler::register);
    }
}
