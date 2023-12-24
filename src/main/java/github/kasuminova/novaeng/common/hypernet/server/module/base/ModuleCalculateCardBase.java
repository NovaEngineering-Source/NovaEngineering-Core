package github.kasuminova.novaeng.common.hypernet.server.module.base;

import crafttweaker.annotations.ZenRegister;
import github.kasuminova.novaeng.common.hypernet.server.module.ModuleCalculateCard;
import stanhebben.zenscript.annotations.ZenClass;

@ZenRegister
@ZenClass("novaeng.hypernet.server.module.base.ModuleCalculateCardBase")
public abstract class ModuleCalculateCardBase<T extends ModuleCalculateCard> extends ModuleCalculableBase<T> {

    public ModuleCalculateCardBase(final String registryName, double baseGeneration, double energyConsumeRatio, int hardwareBandwidth) {
        super(registryName, baseGeneration, energyConsumeRatio, hardwareBandwidth);
    }

}
