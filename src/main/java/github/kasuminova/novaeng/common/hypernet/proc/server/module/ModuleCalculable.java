package github.kasuminova.novaeng.common.hypernet.proc.server.module;

import github.kasuminova.novaeng.common.hypernet.proc.CalculateRequest;
import github.kasuminova.novaeng.common.hypernet.proc.server.Calculable;
import github.kasuminova.novaeng.common.hypernet.proc.server.HardwareBandwidthConsumer;
import github.kasuminova.novaeng.common.hypernet.proc.server.ModularServer;
import github.kasuminova.novaeng.common.hypernet.proc.server.exception.ModularServerException;
import github.kasuminova.novaeng.common.hypernet.proc.server.module.base.ServerModuleBase;

public abstract class ModuleCalculable extends ServerModule implements Calculable, HardwareBandwidthConsumer {
    protected double baseGeneration;
    protected double energyConsumeRatio;
    protected int hardwareBandwidth;

    public ModuleCalculable(final ModularServer server,final ServerModuleBase<?> moduleBase, final double baseGeneration, final double energyConsumeRatio, final int hardwareBandwidth) {
        super(server, moduleBase);
        this.baseGeneration = baseGeneration;
        this.energyConsumeRatio = energyConsumeRatio;
        this.hardwareBandwidth = hardwareBandwidth;
    }

    @Override
    public double calculate(final CalculateRequest request) throws ModularServerException {
        double maxCanGenerated = request.modifiers().get(request.type().getModifierKey())
                .apply(applyEfficiency(baseGeneration, request.type()));
        double maxGeneration = Math.min(maxCanGenerated, request.maxRequired());

        if (!request.simulate()) {
            getServer().consumeEnergy(Math.round(maxGeneration * energyConsumeRatio));
        }

        return maxGeneration;
    }

    @Override
    public int getHardwareBandwidth() {
        return hardwareBandwidth;
    }

    public void setHardwareBandwidth(final int hardwareBandwidth) {
        this.hardwareBandwidth = hardwareBandwidth;
    }

    public double getBaseGeneration() {
        return baseGeneration;
    }

    public void setBaseGeneration(final double baseGeneration) {
        this.baseGeneration = baseGeneration;
    }

    public double getEnergyConsumeRatio() {
        return energyConsumeRatio;
    }

    public void setEnergyConsumeRatio(final double energyConsumeRatio) {
        this.energyConsumeRatio = energyConsumeRatio;
    }

}
