package nipah.dontdie.compats;

import ichttt.mods.firstaid.api.damagesystem.AbstractPlayerDamageModel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import nipah.dontdie.Compat;

import ichttt.mods.firstaid.api.CapabilityExtendedHealthSystem;
import nipah.dontdie.MUtils;

import java.util.List;
import java.util.Random;

public final class FirstAid {
    public static final boolean PRESENT =
            Compat.FIRST_AID;

    private FirstAid() {}

    public static boolean hurtSafePart(Player player, float amount, Random rand)
    {
        if (!PRESENT) {
            return false;
        }
        // TODO: if possible
        return true;
    }
}
