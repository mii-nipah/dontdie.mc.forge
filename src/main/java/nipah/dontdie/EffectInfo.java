package nipah.dontdie;

import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;

public record EffectInfo(
        Holder<MobEffect> effect,
        int chance,
        int ticksMinDuration,
        int ticksMaxDuration
) {}
