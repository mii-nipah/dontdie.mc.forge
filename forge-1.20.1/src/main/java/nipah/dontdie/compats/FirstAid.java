package nipah.dontdie.compats;

import ichttt.mods.firstaid.api.damagesystem.AbstractPlayerDamageModel;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;

import ichttt.mods.firstaid.api.CapabilityExtendedHealthSystem;

import java.util.List;
import java.util.Random;

public final class FirstAid {
    private static final Capability<AbstractPlayerDamageModel> CAP =
            CapabilityExtendedHealthSystem.INSTANCE;

    private FirstAid() {}

    public static void hurtSafePart(Player player, float amount, Random rand) {
        player.getCapability(CAP).ifPresent(model -> {
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
            var part = parts.get(rand.nextInt(parts.size()));
            if (!part.canCauseDeath || part.currentHealth > amount) {
                part.damage(amount, player, true);
                model.scheduleResync();
            }
        });
    }
}
