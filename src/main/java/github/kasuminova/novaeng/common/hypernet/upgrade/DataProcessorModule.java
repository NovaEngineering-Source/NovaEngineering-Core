package github.kasuminova.novaeng.common.hypernet.upgrade;

import crafttweaker.annotations.ZenRegister;
import github.kasuminova.mmce.common.upgrade.DynamicMachineUpgrade;
import github.kasuminova.mmce.common.upgrade.UpgradeType;
import github.kasuminova.novaeng.common.crafttweaker.util.NovaEngUtils;
import github.kasuminova.novaeng.common.hypernet.upgrade.type.ProcessorModuleType;
import hellfirepvp.modularmachinery.common.util.MiscUtils;
import net.minecraft.client.resources.I18n;
import net.minecraft.nbt.NBTTagCompound;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenSetter;

import java.util.List;

@ZenRegister
@ZenClass("novaeng.hypernet.upgrade.ProcessorModuleType")
public abstract class DataProcessorModule extends DynamicMachineUpgrade {
    protected int durability = 0;
    protected int maxDurability = 0;

    public DataProcessorModule(final UpgradeType type) {
        super(type);
    }

    @ZenGetter("energyConsumption")
    public abstract int getEnergyConsumption();

    protected void getEnergyDurabilityTip(final List<String> desc, ProcessorModuleType moduleType) {
        desc.add(I18n.format("upgrade.data_processor.module.energy.tip",
                MiscUtils.formatNumber(getEnergyConsumption()) + " RF"));

        if (maxDurability == 0) {
            desc.add(I18n.format("upgrade.data_processor.module.durability.unknown.tip",
                    moduleType.getMinDurability(), moduleType.getMaxDurability()));
        } else {
            desc.add(I18n.format("upgrade.data_processor.module.durability.tip",
                    durability, maxDurability, NovaEngUtils.formatPercent(durability, maxDurability)));
        }
    }

    @Override
    public void readItemNBT(final NBTTagCompound tag) {
        if (tag.hasKey("maxDurability")) {
            if (tag.hasKey("durability")) {
                durability = tag.getInteger("durability");
            }
            maxDurability = tag.getInteger("maxDurability");
        }
    }

    protected abstract void initDurability();

    @Override
    public NBTTagCompound writeItemNBT() {
        NBTTagCompound tag = new NBTTagCompound();
        if (maxDurability != 0) {
            tag.setInteger("durability", durability);
            tag.setInteger("maxDurability", maxDurability);
        }
        return tag;
    }

    public void writeNBTToItem() {
        if (parentBus != null && isValid()) {
            parentBus.markNoUpdateSync();
        }
//        if (parentStack == null) {
//            return;
//        }
//
//        CapabilityUpgrade capability = parentStack.getCapability(CapabilityUpgrade.MACHINE_UPGRADE_CAPABILITY, null);
//        if (capability == null) {
//            return;
//        }
//
//        List<MachineUpgrade> upgrades = capability.getUpgrades();
//
//        for (final MachineUpgrade upgrade : upgrades) {
//            if (!upgradeEquals(upgrade)) {
//                continue;
//            }
//
//            DataProcessorModule processorModule = (DataProcessorModule) upgrade;
//            processorModule.readItemNBT(writeItemNBT());
//            if (parentBus != null) {
//                parentBus.markNoUpdateSync();
//            }
//            return;
//        }
    }

    @ZenGetter("durability")
    public int getDurability() {
        return durability;
    }

    @ZenSetter("durability")
    public void setDurability(final int durability) {
        this.durability = durability;
    }

    @ZenGetter("maxDurability")
    public int getMaxDurability() {
        return maxDurability;
    }

    @ZenSetter("maxDurability")
    public void setMaxDurability(final int maxDurability) {
        this.maxDurability = maxDurability;
    }

    public boolean upgradeEquals(final Object obj) {
        if (!(obj instanceof DataProcessorModule)) {
            return false;
        }
        return type.equals(((DataProcessorModule) obj).type);
    }

    @Override
    public int hashCode() {
        return System.identityHashCode(this);
    }

    @Override
    public boolean equals(final Object obj) {
        return this == obj;
    }
}
