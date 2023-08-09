package github.kasuminova.novaeng.common.hypernet.upgrade;

import crafttweaker.annotations.ZenRegister;
import github.kasuminova.mmce.common.upgrade.UpgradeType;
import github.kasuminova.novaeng.common.crafttweaker.util.NovaEngUtils;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import stanhebben.zenscript.annotations.ZenClass;

import java.util.ArrayList;
import java.util.List;

@ZenRegister
@ZenClass("novaeng.hypernet.upgrade.ProcessorModuleGPU")
public class ProcessorModuleGPU extends ProcessorModuleCPU {
    public ProcessorModuleGPU(final UpgradeType type) {
        super(type);
    }

    @Override
    public ProcessorModuleGPU copy(ItemStack parentStack) {
        ProcessorModuleGPU upgrade = new ProcessorModuleGPU(getType());
        upgrade.eventProcessor.putAll(eventProcessor);
        upgrade.parentStack = parentStack;
        upgrade.durability = durability;
        upgrade.maxDurability = maxDurability;
        return upgrade;
    }

    public float getEfficiency() {
        if (maxDurability == 0) {
            return 1F;
        }
        float durabilityPercent = (float) durability / maxDurability;
        if (durabilityPercent >= 0.5F) {
            return 1F;
        }

        return Math.min(Math.max(durabilityPercent + 0.5F, 0.75F), 1.0F);
    }

    @Override
    public List<String> getDescriptions() {
        List<String> desc = new ArrayList<>();
        desc.add(I18n.format("upgrade.data_processor.module.gpu.tip.0"));
        desc.add(I18n.format("upgrade.data_processor.module.gpu.tip.1"));
        desc.add(I18n.format("upgrade.data_processor.module.gpu.tip.2"));

        desc.add(I18n.format("upgrade.data_processor.module.cpu.generate",
                NovaEngUtils.formatFLOPS(calculate(false, getComputationPointGeneration())),
                NovaEngUtils.formatPercent(getEfficiency(), 1.0F))
        );

        getEnergyDurabilityTip(desc, moduleType);

        return desc;
    }
}
