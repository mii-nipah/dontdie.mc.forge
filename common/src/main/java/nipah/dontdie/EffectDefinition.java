package nipah.dontdie;

import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;

import java.util.List;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public record EffectDefinition(ResourceLocation id, int chance, int minTicks, int maxTicks) {
    private static final Pattern FORMAT = Pattern.compile("([^|]+)\\|([0-9]{1,3})%\\|([0-9]+)-([0-9]+)s");

    public static EffectDefinition parse(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Death effects cannot be null");
        }
        var match = FORMAT.matcher(value);
        if (match.matches()) {
            var id = ResourceLocation.tryParse(match.group(1));
            int chance = Integer.parseInt(match.group(2));
            try {
                int min = Math.multiplyExact(Integer.parseInt(match.group(3)), 20);
                int max = Math.multiplyExact(Integer.parseInt(match.group(4)), 20);
                if (id != null && !id.getPath().isEmpty() && chance <= 100 && min <= max) {
                    return new EffectDefinition(id, chance, min, max);
                }
            } catch (ArithmeticException ex) {
                throw new IllegalArgumentException("Death effect duration is too large: " + value, ex);
            }
        }
        throw new IllegalArgumentException("Invalid death effect: " + value);
    }

    public static boolean isValid(Object value) {
        if (!(value instanceof String text)) {
            return false;
        }
        try {
            parse(text);
            return true;
        } catch (IllegalArgumentException ex) {
            return false;
        }
    }

    public static List<Resolved> resolve(List<? extends String> items,
                                         Function<ResourceLocation, IntFunction<MobEffectInstance>> findEffect) {
        return items.stream().map(EffectDefinition::parse).flatMap(definition -> {
            var create = findEffect.apply(definition.id());
            if (create == null) {
                LogUtils.getLogger().warn("Ignoring unknown death effect {}", definition.id());
                return Stream.empty();
            }
            return Stream.of(new Resolved(definition, create));
        }).toList();
    }

    public record Resolved(EffectDefinition definition, IntFunction<MobEffectInstance> create) {}
}
