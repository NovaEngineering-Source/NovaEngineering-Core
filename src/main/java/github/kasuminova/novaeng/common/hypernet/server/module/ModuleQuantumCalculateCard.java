package github.kasuminova.novaeng.common.hypernet.server.module;

import crafttweaker.annotations.ZenRegister;
import github.kasuminova.novaeng.common.hypernet.server.CalculateType;
import github.kasuminova.novaeng.common.hypernet.server.CalculateTypes;
import github.kasuminova.novaeng.common.hypernet.server.ModularServer;
import github.kasuminova.novaeng.common.hypernet.server.module.base.ServerModuleBase;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenRegister
@ZenClass("novaeng.hypernet.server.module.ModuleQuantumCalculateCard")
public class ModuleQuantumCalculateCard extends ModuleCalculateCard {

    public ModuleQuantumCalculateCard(final ModularServer server, final ServerModuleBase<?> moduleBase, final double baseGeneration, final double energyConsumeRatio, final int hardwareBandwidth) {
        super(server, moduleBase, baseGeneration, energyConsumeRatio, hardwareBandwidth);
    }

    @ZenMethod
    public static ModuleQuantumCalculateCard cast(ServerModule module) {
        return module instanceof ModuleQuantumCalculateCard ? (ModuleQuantumCalculateCard) module : null;
    }

    @Override
    public double getCalculateTypeEfficiency(final CalculateType type) {
        if (type == CalculateTypes.QBIT) {
            return 1;
        }
        if (type == CalculateTypes.INTRICATE) {
            return 0.5;
        }
        if (type == CalculateTypes.NEURON) {
            return 0.3;
        }
        if (type == CalculateTypes.LOGIC) {
            return 0.25;
        }

        return 0;
    }

}