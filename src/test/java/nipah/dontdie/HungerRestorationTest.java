package nipah.dontdie;

import net.minecraft.world.food.FoodData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HungerRestorationTest {
    @ParameterizedTest
    @CsvSource({
            "20, 0, 20", "12, 0, 12", "0, 0, 0",
            "20, 4, 16", "12, 4, 8", "4, 4, 0",
            "3, 4, 0", "0, 4, 0", "20, 20, 0"
    })
    void restoresPreDeathHungerMinusPenalty(int before, int penalty, int expected) {
        FoodData original = food(before, 0.0F);
        FoodData respawned = new FoodData();

        HungerRestoration.restore(original, respawned, penalty);

        assertEquals(expected, respawned.getFoodLevel());
        assertEquals(before, original.getFoodLevel());
    }

    @Test
    void doesNotGrantFreshRespawnSaturation() {
        FoodData respawned = new FoodData();

        HungerRestoration.restore(food(12, 0.0F), respawned, 0);

        assertEquals(0.0F, respawned.getSaturationLevel());
    }

    @Test
    void preservesSaturationBelowRemainingHunger() {
        FoodData original = food(12, 3.5F);
        FoodData respawned = new FoodData();

        HungerRestoration.restore(original, respawned, 4);

        assertEquals(3.5F, respawned.getSaturationLevel());
        assertEquals(3.5F, original.getSaturationLevel());
    }

    @Test
    void capsSaturationAtRemainingHunger() {
        FoodData respawned = new FoodData();

        HungerRestoration.restore(food(12, 10.0F), respawned, 4);

        assertEquals(8.0F, respawned.getSaturationLevel());
    }

    @Test
    void clearsSaturationWhenPenaltyEmptiesHunger() {
        FoodData respawned = new FoodData();

        HungerRestoration.restore(food(3, 3.0F), respawned, 4);

        assertEquals(0.0F, respawned.getSaturationLevel());
    }

    @Test
    void repeatedDeathsUseTheLatestHungerLevel() {
        FoodData firstRespawn = new FoodData();
        FoodData secondRespawn = new FoodData();

        HungerRestoration.restore(food(12, 0.0F), firstRespawn, 4);
        HungerRestoration.restore(firstRespawn, secondRespawn, 4);

        assertEquals(8, firstRespawn.getFoodLevel());
        assertEquals(4, secondRespawn.getFoodLevel());
    }

    private static FoodData food(int level, float saturation) {
        FoodData food = new FoodData();
        food.setFoodLevel(level);
        food.setSaturation(saturation);
        return food;
    }
}
