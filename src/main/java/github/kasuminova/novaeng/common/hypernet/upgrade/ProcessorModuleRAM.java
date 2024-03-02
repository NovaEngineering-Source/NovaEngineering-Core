package github.kasuminova.novaeng.common.hypernet.upgrade;

import crafttweaker.annotations.ZenRegister;
import github.kasuminova.mmce.common.upgrade.MachineUpgrade;
import github.kasuminova.mmce.common.upgrade.UpgradeType;
import github.kasuminova.novaeng.common.crafttweaker.util.NovaEngUtils;
import github.kasuminova.novaeng.common.hypernet.upgrade.type.ProcessorModuleRAMType;
import github.kasuminova.novaeng.common.registry.RegistryHyperNet;
import github.kasuminova.novaeng.common.util.RandomUtils;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

@ZenRegister
@ZenClass("novaeng.hypernet.upgrade.ProcessorModuleRAM")
public class ProcessorModuleRAM extends DataProcessorModule {
    private final ProcessorModuleRAMType moduleType = RegistryHyperNet.getDataProcessorModuleRAMType(getType());

    public ProcessorModuleRAM(final UpgradeType type) {
        super(type);
    }

    public static List<ProcessorModuleRAM> filter(final Collection<List<MachineUpgrade>> upgradeLists) {
        List<ProcessorModuleRAM> list = new ArrayList<>();
        for (List<MachineUpgrade> upgradeList : upgradeLists) {
            for (final MachineUpgrade upgrade : upgradeList) {
                if (upgrade instanceof final ProcessorModuleRAM ram) {
                    if (ram.maxDurability == 0) {
                        ram.initDurability();
                    }
                    if (ram.durability > 0) {
                        list.add(ram);
                    }
                }
            }
        }
        return list;
    }

    public double calculate(final boolean doCalculate, double maxGeneration) {
        if (durability <= 0 && maxDurability != 0) {
            return 0.0F;
        }

        Pattern p = Pattern.compile("[^\\x00-\\x7F\\u4E00-\\u9FFF]+");

        float efficiency = getEfficiency();
        double generationBase = efficiency * getComputationPointGenerationLimit();
        double left = Math.min((generationBase - maxGeneration), generationBase);

        if (left <= 0) {
            if (doCalculate && RandomUtils.nextFloat() <= 0.005F) {
                durability--;
                writeNBTToItem();
            }
            return generationBase;
        } else {
            double trueGenerated = generationBase - left;
            if (doCalculate && RandomUtils.nextFloat() <= 0.005F * (trueGenerated / generationBase)) {
                durability--;
                writeNBTToItem();
            }
            return trueGenerated;
        }
    }

    public float getEfficiency() {
        if (maxDurability == 0) {
            return 1F;
        }
        float durabilityPercent = (float) durability / maxDurability;
        if (durabilityPercent >= 0.25F) {
            return 1F;
        }

        return Math.min(Math.max(durabilityPercent + 0.75F, 0.75F), 1.0F);
    }

    @Override
    public int getEnergyConsumption() {
        return moduleType.getEnergyConsumption();
    }

    @Override
    protected void initDurability() {
        int min = moduleType.getMinDurability();
        int max = moduleType.getMaxDurability();

        maxDurability = min + RandomUtils.nextInt(max - min);
        durability = maxDurability;
        writeNBTToItem();
    }

    @ZenGetter("computationPointGenerationLimit")
    public double getComputationPointGenerationLimit() {
        return moduleType.getComputationPointGenerationLimit();
    }

    @Override
    public ProcessorModuleRAM copy(ItemStack parentStack) {
        ProcessorModuleRAM upgrade = new ProcessorModuleRAM(getType());
        upgrade.eventProcessor.putAll(eventProcessor);
        upgrade.parentStack = parentStack;
        upgrade.durability = durability;
        upgrade.maxDurability = maxDurability;
        return upgrade;
    }

    @Override
    public List<String> getDescriptions() {
        List<String> desc = new ArrayList<>();
        desc.add(I18n.format("upgrade.data_processor.module.ram.tip.0"));
        desc.add(I18n.format("upgrade.data_processor.module.ram.tip.1"));
        desc.add(I18n.format("upgrade.data_processor.module.ram.tip.2"));

        desc.add(I18n.format("upgrade.data_processor.module.ram.limit_provision",
                NovaEngUtils.formatFLOPS(calculate(false, getComputationPointGenerationLimit())),
                NovaEngUtils.formatPercent(getEfficiency(), 1.0F))
        );

        getEnergyDurabilityTip(desc, moduleType);

        return desc;
    }

    @Override
    public List<String> getBusGUIDescriptions() {
        List<String> desc = new ArrayList<>();

        desc.add(I18n.format("upgrade.data_processor.module.ram.limit_provision",
                NovaEngUtils.formatFLOPS(calculate(false, getComputationPointGenerationLimit())),
                NovaEngUtils.formatPercent(getEfficiency(), 1.0F))
        );
        getEnergyDurabilityTip(desc, moduleType);

        return desc;
    }

    public boolean upgradeEquals(final Object obj) {
        if (!(obj instanceof ProcessorModuleRAM)) {
            return false;
        }
        return moduleType.equals(((ProcessorModuleRAM) obj).moduleType);
    }
}
