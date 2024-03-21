package github.kasuminova.novaeng.common.registry;

import com.google.common.base.Preconditions;
import github.kasuminova.novaeng.common.machine.MachineSpecial;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Map;

public class RegistryMachineSpecial {

    private static final Map<ResourceLocation, MachineSpecial> MACHINE_SPECIAL_REGISTRY = new Object2ObjectOpenHashMap<>();

    public static void registrySpecialMachine(final MachineSpecial machineSpecial) {
        Preconditions.checkNotNull(machineSpecial);
        MACHINE_SPECIAL_REGISTRY.put(machineSpecial.getRegistryName(), machineSpecial);
    }

    @Nullable
    public static MachineSpecial getSpecialMachine(final ResourceLocation registryName) {
        return MACHINE_SPECIAL_REGISTRY.get(registryName);
    }

    public static Map<ResourceLocation, MachineSpecial> getSpecialMachineRegistry() {
        return Collections.unmodifiableMap(MACHINE_SPECIAL_REGISTRY);
    }

}
