package github.kasuminova.novaeng.common.hypernet.computer.module;

import crafttweaker.annotations.ZenRegister;
import github.kasuminova.novaeng.common.hypernet.computer.module.base.ServerModuleBase;
import github.kasuminova.novaeng.common.hypernet.calculation.CalculateType;
import github.kasuminova.novaeng.common.hypernet.calculation.CalculateTypes;
import github.kasuminova.novaeng.common.hypernet.computer.ModularServer;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenRegister
@ZenClass("novaeng.hypernet.server.module.ModuleGPU")
public class ModuleGPU extends ModuleCalculateCard {

    public ModuleGPU(final ModularServer server, final ServerModuleBase<?> moduleBase, final double baseGeneration, final double energyConsumeRatio, final int hardwareBandwidth) {
        super(server, moduleBase, baseGeneration, energyConsumeRatio, hardwareBandwidth);
    }

    @ZenMethod
    public static ModuleGPU cast(ServerModule module) {
        return module instanceof ModuleGPU ? (ModuleGPU) module : null;
    }

    @Override
    public double getCalculateTypeEfficiency(final CalculateType type) {
        if (type == CalculateTypes.LOGIC) {
            return 5;
        }
        if (type == CalculateTypes.INTRICATE) {
            return 0.2;
        }
        if (type == CalculateTypes.NEURON) {
            return 0.1;
        }
        if (type == CalculateTypes.QBIT) {
            return 0.005;
        }
        return 0;
    }

}
