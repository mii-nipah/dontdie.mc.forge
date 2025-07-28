package nipah.dontdie.compats;

import ichttt.mods.firstaid.api.damagesystem.AbstractPlayerDamageModel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.capabilities.Capability;
import nipah.dontdie.Compat;

import ichttt.mods.firstaid.api.CapabilityExtendedHealthSystem;
import nipah.dontdie.MUtils;

import java.util.List;
import java.util.Random;

public final class FirstAid {
    public static final boolean PRESENT =
            Compat.FIRST_AID;

    private static final Capability<AbstractPlayerDamageModel> CAP =
            CapabilityExtendedHealthSystem.INSTANCE;

    private FirstAid() {}

    public static boolean hurtSafePart(Player player, float amount, Random rand)
    {
        if (!PRESENT) {
            return false;
        }

        LazyOptional<AbstractPlayerDamageModel> opt = player.getCapability(CAP);
        return opt.map(model -> {
            var parts = List.of(
                model.BODY,
                model.HEAD,
                model.LEFT_ARM,
                model.RIGHT_ARM,
                model.LEFT_LEG,
                model.LEFT_FOOT,
                model.RIGHT_LEG,
                model.RIGHT_FOOT
            );
            var part = MUtils.randomItem(rand, parts);
            if(part == null) {
                return false;
            }
            if(part.canCauseDeath && part.currentHealth - amount <= 0) {
                return false;
            }
            part.damage(amount, player, true);
            model.scheduleResync();
            return true;
        })
        .orElse(false);
    }
}
