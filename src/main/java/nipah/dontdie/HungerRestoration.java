package nipah.dontdie;

import net.minecraft.world.food.FoodData;

final class HungerRestoration {
    private HungerRestoration() {}

    static void restore(FoodData original, FoodData respawned, int hungerLostOnDeath) {
        int foodLevel = Math.max(0, original.getFoodLevel() - hungerLostOnDeath);
        float saturation = Math.min(original.getSaturationLevel(), foodLevel);
        respawned.setFoodLevel(foodLevel);
        // A fresh player starts with saturation, which must not refill on death either.
        respawned.setSaturation(saturation);
    }
}
