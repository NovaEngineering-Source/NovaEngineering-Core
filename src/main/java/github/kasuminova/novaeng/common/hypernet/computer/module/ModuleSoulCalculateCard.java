package github.kasuminova.novaeng.common.hypernet.computer.module;

import crafttweaker.annotations.ZenRegister;
import github.kasuminova.novaeng.common.hypernet.computer.module.base.ServerModuleBase;
import github.kasuminova.novaeng.common.hypernet.calculation.CalculateType;
import github.kasuminova.novaeng.common.hypernet.calculation.CalculateTypes;
import github.kasuminova.novaeng.common.hypernet.computer.ModularServer;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenRegister
@ZenClass("novaeng.hypernet.server.module.ModuleSoulCalculateCard")
public class ModuleSoulCalculateCard extends ModuleCalculateCard {

    public ModuleSoulCalculateCard(final ModularServer server, final ServerModuleBase<?> moduleBase, final double baseGeneration, final double energyConsumeRatio, final int hardwareBandwidth) {
        super(server, moduleBase, baseGeneration, energyConsumeRatio, hardwareBandwidth);
    }

    @ZenMethod
    public static ModuleSoulCalculateCard cast(ServerModule module) {
        return module instanceof ModuleSoulCalculateCard ? (ModuleSoulCalculateCard) module : null;
    }

    @Override
    public double getCalculateTypeEfficiency(final CalculateType type) {
        if (type == CalculateTypes.NEURON) {
            return 1;
        }
        if (type == CalculateTypes.INTRICATE) {
            return 0.01;
        }
        if (type == CalculateTypes.LOGIC) {
            return 0.005;
        }
        if (type == CalculateTypes.QBIT) {
            return 0.0001;
        }

        return 0;
    }

}