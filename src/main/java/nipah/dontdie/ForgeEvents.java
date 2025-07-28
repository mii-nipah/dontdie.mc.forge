package nipah.dontdie;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import nipah.dontdie.compats.FirstAid;

import java.util.Random;

@Mod.EventBusSubscriber(modid = Dontdie.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class ForgeEvents {
    static final Random rand = new Random();

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent e) {
        if (!(e.getEntity() instanceof ServerPlayer player) || player.isSpectator()) {
            return;
        }
        var server = player.server;
        var hits = rand.nextInt(0, Config.maxHits + 1);
        var effectsCount = rand.nextInt(Config.minEffects, Config.maxEffects + 1);
        Dontdie.LOGGER.info("Player {} respawned with {} hits, and {} (possible) effects.",
                player.getName().getString(), hits, effectsCount);

        // Apply hits
        var damageSource = player.damageSources().fellOutOfWorld();
        for(int i = 0; i < hits; i++) {
            var damagePerHit = rand.nextInt(0, Config.maxDamage + 1);
            int finalI = i;
            TickScheduler.schedule(i * 11, ts -> {
                if (player.isAlive()) {
                    if(Compat.FIRST_AID) {
                        FirstAid.hurtSafePart(player, damagePerHit, rand);
                    }
                    else {
                        player.hurt(damageSource, damagePerHit);
                    }
                    Dontdie.LOGGER.info("Player {} took {} damage on hit #{}.", player.getName().getString(), damagePerHit, finalI + 1);
                }
            });
        }

        // Apply effects
        for (int i = 0; i < effectsCount; i++) {
            var effectInfo = MUtils.randomItem(rand, Config.deathEffects);
            if (effectInfo == null) {
                continue;
            }
            if(!MUtils.testChance(rand, effectInfo.chance())) {
                Dontdie.LOGGER.info("Skipping effect {} for player {} due to chance.", effectInfo.effect().getDisplayName().getString(), player.getName().getString());
                continue;
            }
            if (player.isAlive()) {
                var mobEffect = effectInfo.effect();
                var minDuration = effectInfo.ticksMinDuration();
                var maxDuration = effectInfo.ticksMaxDuration();
                var duration = rand.nextInt(minDuration, maxDuration + 1);
                var effect = new MobEffectInstance(mobEffect, duration);
                player.addEffect(effect);
                Dontdie.LOGGER.info("Player {} received effect {} for {} ticks.", player.getName().getString(), mobEffect.getDisplayName().getString(), duration);
            }
        }

        MUtils.notifyToast(
            player,
            Component.translatable("dontdie.toast.title")
                .withStyle(ChatFormatting.RED),
            Component.translatable("dontdie.toast.subtitle")
                .withStyle(ChatFormatting.GRAY)
        );
    }
}
