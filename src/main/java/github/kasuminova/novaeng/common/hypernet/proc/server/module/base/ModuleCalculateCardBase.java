package github.kasuminova.novaeng.common.hypernet.proc.server.module.base;

import github.kasuminova.novaeng.common.hypernet.proc.server.module.ModuleCalculateCard;

public abstract class ModuleCalculateCardBase<T extends ModuleCalculateCard> extends ModuleCalculableBase<T> {

    public ModuleCalculateCardBase(final String registryName, double baseGeneration, double energyConsumeRatio, int hardwareBandwidth) {
        super(registryName, baseGeneration, energyConsumeRatio, hardwareBandwidth);
    }

}
