package github.kasuminova.novaeng.common.hypernet.proc.server.module.base;

import github.kasuminova.novaeng.common.hypernet.proc.server.Calculable;
import github.kasuminova.novaeng.common.hypernet.proc.server.module.ServerModule;

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
