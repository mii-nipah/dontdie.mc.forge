package nipah.dontdie.compats;

import ichttt.mods.firstaid.api.enums.EnumPlayerPart;
import ichttt.mods.firstaid.common.util.CommonUtils;
import net.minecraft.server.level.ServerPlayer;
import nipah.dontdie.DeathPenalty;

import java.util.Random;
import java.util.function.Predicate;

/** Built against both the original First Aid and First Aid New, which share these classes and the mod id. */
public final class FirstAid {
    private FirstAid() {}

    public static DeathPenalty.Hit hit(Predicate<String> isModLoaded) {
        return isModLoaded.test("firstaid") ? Installed::hurtSafePart : DeathPenalty::vanillaHit;
    }

    // First Aid is optional, so only this class may link against it.
    private static final class Installed {
        static void hurtSafePart(ServerPlayer player, int damage, Random random) {
            // The API has no loader-independent lookup; First Aid's own helper is the same in all of them.
            var model = CommonUtils.getDamageModel(player);
            if (model == null) {
                return;
            }
            var part = model.getFromEnum(EnumPlayerPart.VALUES[random.nextInt(EnumPlayerPart.VALUES.length)]);
            // Emptying a vital part kills or knocks out. Without vital parts, emptying all of them does.
            boolean vital = part.canCauseDeath || model.hasNoCritical();
            if (!vital || part.currentHealth > damage) {
                part.damage(damage, player, true);
                model.scheduleResync();
            }
        }
    }
}
