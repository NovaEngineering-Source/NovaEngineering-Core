package github.kasuminova.novaeng.common.hypernet.proc.server.module.base;

import github.kasuminova.novaeng.common.hypernet.proc.server.module.ModuleCalculateCard;

public abstract class ModuleCalculateCardBase<T extends ModuleCalculateCard> extends ServerModuleBase<T> {

    public ModuleCalculateCardBase(final String registryName) {
        super(registryName);
    }

}
