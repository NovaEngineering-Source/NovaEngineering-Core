package github.kasuminova.novaeng.common.hypernet.computer.module.base;

import crafttweaker.annotations.ZenRegister;
import github.kasuminova.novaeng.common.hypernet.computer.module.ModuleExtensionCard;
import stanhebben.zenscript.annotations.ZenClass;

@ZenRegister
@ZenClass("novaeng.hypernet.server.module.base.ModuleExtensionCardBase")
public abstract class ModuleExtensionCardBase extends ServerModuleBase<ModuleExtensionCard> {

    public ModuleExtensionCardBase(final String registryName) {
        super(registryName);
    }

}
