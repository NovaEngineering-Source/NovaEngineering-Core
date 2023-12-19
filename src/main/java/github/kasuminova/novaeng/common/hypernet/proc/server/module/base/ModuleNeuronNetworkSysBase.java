package github.kasuminova.novaeng.common.hypernet.proc.server.module.base;

import crafttweaker.annotations.ZenRegister;
import github.kasuminova.novaeng.common.hypernet.proc.server.ModularServer;
import github.kasuminova.novaeng.common.hypernet.proc.server.module.ModuleNeuronNetworkSys;
import net.minecraft.item.ItemStack;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenRegister
@ZenClass("novaeng.hypernet.server.module.base.ModuleNeuronNetworkSysBase")
public class ModuleNeuronNetworkSysBase extends ModuleCalculateCardBase<ModuleNeuronNetworkSys> {

    public ModuleNeuronNetworkSysBase(final String registryName, final double baseGeneration, final double energyConsumeRatio, final int hardwareBandwidth) {
        super(registryName, baseGeneration, energyConsumeRatio, hardwareBandwidth);
    }

    @ZenMethod
    public static ModuleNeuronNetworkSysBase create(final String registryName, final double baseGeneration, final double energyConsumeRatio, final int hardwareBandwidth) {
        return new ModuleNeuronNetworkSysBase(registryName, baseGeneration, energyConsumeRatio, hardwareBandwidth);
    }

    @Override
    public ModuleNeuronNetworkSys createInstance(final ModularServer server, final ItemStack moduleStack) {
        return new ModuleNeuronNetworkSys(server, this, baseGeneration, energyConsumeRatio, hardwareBandwidth);
    }
}
