package nipah.dontdie.fabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffectInstance;

public final class DontdieFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        PayloadTypeRegistry.playS2C().register(ToastPacket.TYPE, ToastPacket.CODEC);
        new FabricEvents().register(id -> {
            var effect = BuiltInRegistries.MOB_EFFECT.getHolder(id).orElse(null);
            return effect == null ? null : ticks -> new MobEffectInstance(effect, ticks);
        }, ToastPacket::send);
    }
}
