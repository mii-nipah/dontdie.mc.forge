package nipah.dontdie.fabric;

import net.fabricmc.api.ModInitializer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffectInstance;

public final class DontdieFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        new FabricEvents().register(id -> {
            var effect = BuiltInRegistries.MOB_EFFECT.get(id);
            return effect == null ? null : ticks -> new MobEffectInstance(effect, ticks);
        }, ToastPacket::send);
    }
}
