package nipah.dontdie.fabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffectInstance;
import nipah.dontdie.compats.FirstAid;

public final class DontdieFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        PayloadTypeRegistry.playS2C().register(ToastPacket.TYPE, ToastPacket.CODEC);
        new FabricEvents(FirstAid.hit(FabricLoader.getInstance()::isModLoaded)).register(id -> {
            var effect = BuiltInRegistries.MOB_EFFECT.getHolder(id).orElse(null);
            return effect == null ? null : ticks -> new MobEffectInstance(effect, ticks);
        }, ToastPacket::send);
    }
}
