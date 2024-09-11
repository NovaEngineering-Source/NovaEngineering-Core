package github.kasuminova.novaeng.common.hypernet.computer.module.base;

import com.github.bsideup.jabel.Desugar;
import crafttweaker.annotations.ZenRegister;
import github.kasuminova.novaeng.common.crafttweaker.util.NovaEngUtils;
import github.kasuminova.novaeng.common.hypernet.computer.module.ServerModule;
import github.kasuminova.novaeng.common.hypernet.calculation.Calculable;
import github.kasuminova.novaeng.common.hypernet.calculation.CalculateType;
import github.kasuminova.novaeng.common.hypernet.calculation.CalculateTypes;
import net.minecraft.client.resources.I18n;
import stanhebben.zenscript.annotations.ZenClass;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

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

    @Override
    public List<String> getTooltip(final T moduleInstance) {
        List<String> tooltip = new ArrayList<>(super.getTooltip(moduleInstance));

        tooltip.add(I18n.format("novaeng.hypernet.hardware_bandwidth.consume", this.hardwareBandwidth));
        tooltip.add(I18n.format("novaeng.hypernet.calculable.tip.base_ratio", this.baseGeneration));
        tooltip.add(I18n.format("novaeng.hypernet.calculable.tip.energy_consume_ratio", NovaEngUtils.formatNumber(Math.round(this.energyConsumeRatio))));
        tooltip.add(I18n.format("novaeng.hypernet.calculable.supported"));

        PriorityQueue<EfficiencyTip> efficiencyTips = new PriorityQueue<>();

        for (final CalculateType type : CalculateTypes.getAvailableTypes().values()) {
            double typeEfficiency = moduleInstance.getCalculateTypeEfficiency(type);
            efficiencyTips.add(new EfficiencyTip(typeEfficiency, I18n.format("novaeng.hypernet.calculate.tip.efficiency",
                    type.getFormattedTypeName(),
                    type.format(baseGeneration * typeEfficiency),
                    Calculable.formatEfficiency(typeEfficiency)
            )));
        }

        for (final EfficiencyTip efficiencyTip : efficiencyTips) {
            tooltip.add("  " + efficiencyTip.tip());
        }
        return tooltip;
    }

    @Desugar
    record EfficiencyTip(double efficiency, String tip) implements Comparable<EfficiencyTip> {
        @Override
        public int compareTo(final EfficiencyTip efficiencyTip) {
            return Double.compare(efficiencyTip.efficiency, efficiency);
        }
    }
}
