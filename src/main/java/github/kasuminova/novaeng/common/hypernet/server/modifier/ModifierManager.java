package github.kasuminova.novaeng.common.hypernet.server.modifier;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import java.util.Map;

public class ModifierManager {

    private final Map<String, CalculateModifier> modifiers = new Object2ObjectOpenHashMap<>();

    public double apply(final String key, final double value) {
        return modifiers.getOrDefault(key, CalculateModifier.DEFAULT_MODIFIER).apply(value);
    }

    public void add(final String key, double value) {
        modifiers.computeIfAbsent(key, v -> new CalculateModifier()).add(value);
    }

    public void multiply(final String key, double value) {
        modifiers.computeIfAbsent(key, v -> new CalculateModifier()).multiply(value);
    }

}
