package nipah.dontdie.fabric;

record HungerPenalty(int foodLevel, float saturation) {
    static HungerPenalty restore(int oldFoodLevel, float oldSaturation, int hungerLostOnDeath) {
        int food = Math.max(0, Math.min(20, oldFoodLevel) - hungerLostOnDeath);
        return new HungerPenalty(food, Math.max(0, Math.min(oldSaturation, food)));
    }
}
