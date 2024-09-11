package github.kasuminova.novaeng.common.hypernet.computer.module.base;

import crafttweaker.annotations.ZenRegister;
import github.kasuminova.novaeng.common.hypernet.computer.ModularServer;
import github.kasuminova.novaeng.common.hypernet.computer.module.ModuleSoulCalculateCard;
import net.minecraft.item.ItemStack;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenRegister
@ZenClass("novaeng.hypernet.server.module.base.ModuleSoulCalculateCardBase")
public class ModuleSoulCalculateCardBase extends ModuleCalculateCardBase<ModuleSoulCalculateCard> {

    public ModuleSoulCalculateCardBase(final String registryName, final double baseGeneration, final double energyConsumeRatio, final int hardwareBandwidth) {
        super(registryName, baseGeneration, energyConsumeRatio, hardwareBandwidth);
    }

    @ZenMethod
    public static ModuleSoulCalculateCardBase create(final String registryName, final double baseGeneration, final double energyConsumeRatio, final int hardwareBandwidth) {
        return new ModuleSoulCalculateCardBase(registryName, baseGeneration, energyConsumeRatio, hardwareBandwidth);
    }

    @Override
    public ModuleSoulCalculateCard createInstance(final ModularServer server, final ItemStack moduleStack) {
        return new ModuleSoulCalculateCard(server, this, baseGeneration, energyConsumeRatio, hardwareBandwidth);
    }
}
