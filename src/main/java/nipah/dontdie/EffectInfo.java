package nipah.dontdie;

import net.minecraft.world.effect.MobEffect;

public record EffectInfo(
        MobEffect effect,
        int chance,
        int ticksMinDuration,
        int ticksMaxDuration
) {}
