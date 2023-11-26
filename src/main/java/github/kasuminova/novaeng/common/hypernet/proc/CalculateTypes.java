package github.kasuminova.novaeng.common.hypernet.proc;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class CalculateTypes {
    public static final CalculateTypeIntricate INTRICATE = new CalculateTypeIntricate();
    public static final CalculateTypeLogic LOGIC = new CalculateTypeLogic();
    public static final CalculateTypeNeuron NEURON = new CalculateTypeNeuron();
    public static final CalculateTypeQbit QBIT = new CalculateTypeQbit();

    private static final Map<String, CalculateType> AVAILABLE_TYPES = new HashMap<>();

    static {
        registerType(INTRICATE);
        registerType(LOGIC);
        registerType(NEURON);
        registerType(QBIT);
    }

    public static void registerType(CalculateType type) {
        AVAILABLE_TYPES.put(type.typeName, type);
    }

    public static CalculateType getTypeByName(String typeName) {
        return AVAILABLE_TYPES.get(typeName);
    }

    public static Map<String, CalculateType> getAvailableTypes() {
        return Collections.unmodifiableMap(AVAILABLE_TYPES);
    }

}