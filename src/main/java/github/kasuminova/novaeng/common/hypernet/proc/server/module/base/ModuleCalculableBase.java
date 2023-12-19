package github.kasuminova.novaeng.common.hypernet.proc.server.module.base;

import crafttweaker.annotations.ZenRegister;
import github.kasuminova.novaeng.common.hypernet.proc.server.Calculable;
import github.kasuminova.novaeng.common.hypernet.proc.server.module.ServerModule;
import stanhebben.zenscript.annotations.ZenClass;

@ZenRegister
@ZenClass("novaeng.hypernet.server.module.base.ModuleCalculableBase")
public abstract class ModuleCalculableBase<T extends ServerModule & Calculable> extends ServerModuleBase<T> {

    protected final double baseGeneration;
    protected final double energyConsumeRatio;
    protected final int hardwareBandwidth;

    public ModuleCalculableBase(final String registryName, double baseGeneration, double energyConsumeRatio, int hardwareBandwidth) {
        super(registryName);
        this.baseGeneration = baseGeneration;
        this.energyConsumeRatio = energyConsumeRatio;
        this.hardwareBandwidth = hardwareBandwidth;
    }
}
