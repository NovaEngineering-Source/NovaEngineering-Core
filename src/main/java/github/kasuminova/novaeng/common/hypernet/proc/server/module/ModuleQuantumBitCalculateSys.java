package github.kasuminova.novaeng.common.hypernet.proc.server.module;

import crafttweaker.annotations.ZenRegister;
import github.kasuminova.novaeng.common.hypernet.proc.CalculateType;
import github.kasuminova.novaeng.common.hypernet.proc.CalculateTypes;
import github.kasuminova.novaeng.common.hypernet.proc.server.ModularServer;
import github.kasuminova.novaeng.common.hypernet.proc.server.module.base.ServerModuleBase;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenRegister
@ZenClass("novaeng.hypernet.module.ModuleQuantumBitCalculateSys")
public class ModuleQuantumBitCalculateSys extends ModuleCalculateCard {

    public ModuleQuantumBitCalculateSys(final ModularServer server,final ServerModuleBase<?> moduleBase, final double baseGeneration, final double energyConsumeRatio, final int hardwareBandwidth) {
        super(server, moduleBase, baseGeneration, energyConsumeRatio, hardwareBandwidth);
    }

    @ZenMethod
    public static ModuleQuantumBitCalculateSys cast(ServerModule module) {
        return module instanceof ModuleQuantumBitCalculateSys ? (ModuleQuantumBitCalculateSys) module : null;
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