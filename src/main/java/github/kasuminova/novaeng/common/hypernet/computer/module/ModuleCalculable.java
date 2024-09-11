package github.kasuminova.novaeng.common.hypernet.computer.module;

import crafttweaker.annotations.ZenRegister;
import github.kasuminova.novaeng.common.hypernet.computer.module.base.ServerModuleBase;
import github.kasuminova.novaeng.common.hypernet.calculation.Calculable;
import github.kasuminova.novaeng.common.hypernet.calculation.CalculateRequest;
import github.kasuminova.novaeng.common.hypernet.computer.HardwareBandwidthConsumer;
import github.kasuminova.novaeng.common.hypernet.computer.ModularServer;
import github.kasuminova.novaeng.common.hypernet.computer.exception.ModularServerException;
import github.kasuminova.novaeng.common.hypernet.calculation.modifier.ModifierKeys;
import github.kasuminova.novaeng.common.hypernet.calculation.modifier.ModifierManager;
import stanhebben.zenscript.annotations.ZenClass;

@ZenRegister
@ZenClass("novaeng.hypernet.server.module.ModuleCalculable")
public abstract class ModuleCalculable extends ServerModule implements Calculable, HardwareBandwidthConsumer {
    protected double baseGeneration;
    protected double energyConsumeRatio;
    protected int hardwareBandwidth;

    public ModuleCalculable(final ModularServer server, final ServerModuleBase<?> moduleBase, final double baseGeneration, final double energyConsumeRatio, final int hardwareBandwidth) {
        super(server, moduleBase);
        this.baseGeneration = baseGeneration;
        this.energyConsumeRatio = energyConsumeRatio;
        this.hardwareBandwidth = hardwareBandwidth;
    }

    @Override
    public double calculate(final CalculateRequest request) throws ModularServerException {
        double efficiency = getCalculateTypeEfficiency(request.type());

        ModifierManager modifier = request.modifier();
        double maxCanGenerated = modifier.apply(ModifierKeys.GLOBAL_CALCULATE_EFFICIENCY, 
                modifier.apply(request.type().getModifierKey(), baseGeneration * efficiency)
        );

        double generated = Math.min(maxCanGenerated, request.maxRequired());
        if (!request.simulate()) {
            getServer().consumeEnergy(Math.round((generated / efficiency) * energyConsumeRatio));
        }

        return generated;
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
