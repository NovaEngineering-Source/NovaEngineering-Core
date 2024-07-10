package github.kasuminova.novaeng.common.hypernet.server.module;

import crafttweaker.annotations.ZenRegister;
import github.kasuminova.novaeng.common.hypernet.server.CalculateType;
import github.kasuminova.novaeng.common.hypernet.server.CalculateTypes;
import github.kasuminova.novaeng.common.hypernet.server.ModularServer;
import github.kasuminova.novaeng.common.hypernet.server.module.base.ServerModuleBase;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenRegister
@ZenClass("novaeng.hypernet.server.module.ModuleCPU")
public class ModuleCPU extends ModuleCalculable {

    public ModuleCPU(final ModularServer server, final ServerModuleBase<?> moduleBase, final double baseGeneration, final double energyConsumeRatio, final int hardwareBandwidth) {
        super(server, moduleBase, baseGeneration, energyConsumeRatio, hardwareBandwidth);
    }

    @ZenMethod
    public static ModuleCPU cast(ServerModule module) {
        return module instanceof ModuleCPU ? (ModuleCPU) module : null;
    }

    @Override
    public double getCalculateTypeEfficiency(final CalculateType type) {
        if (type == CalculateTypes.INTRICATE || type == CalculateTypes.LOGIC) {
            return 1;
        }
        if (type == CalculateTypes.NEURON) {
            return 0.025;
        }
        if (type == CalculateTypes.QBIT) {
            return 0.001;
        }

        return 0;
    }

}