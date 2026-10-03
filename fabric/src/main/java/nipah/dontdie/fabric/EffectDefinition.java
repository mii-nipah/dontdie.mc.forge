package nipah.dontdie.fabric;

import java.util.regex.Pattern;

/** The same id|chance%|minimum-maximums notation as the Forge config. */
record EffectDefinition(String id, int chance, int minTicks, int maxTicks) {
    private static final Pattern FORMAT = Pattern.compile(
            "([a-z0-9_.-]+:[a-z0-9/._-]+)\\|([0-9]{1,3})%\\|([0-9]+)-([0-9]+)s");

    static EffectDefinition parse(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Death effects cannot be null");
        }
        var match = FORMAT.matcher(value);
        if (!match.matches()) {
            throw new IllegalArgumentException("Invalid death effect: " + value);
        }
        try {
            int chance = Integer.parseInt(match.group(2));
            int min = Math.multiplyExact(Integer.parseInt(match.group(3)), 20);
            int max = Math.multiplyExact(Integer.parseInt(match.group(4)), 20);
            if (chance > 100 || min > max) {
                throw new IllegalArgumentException("Invalid chance or duration range: " + value);
            }
            return new EffectDefinition(match.group(1), chance, min, max);
        } catch (ArithmeticException ex) {
            throw new IllegalArgumentException("Death effect duration is too large: " + value, ex);
        }
    }
}
