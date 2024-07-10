package github.kasuminova.novaeng.common.hypernet.server.module.base;

import crafttweaker.annotations.ZenRegister;
import github.kasuminova.novaeng.common.hypernet.server.ModularServer;
import github.kasuminova.novaeng.common.hypernet.server.module.ModuleRAM;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import java.util.ArrayList;
import java.util.List;

@ZenRegister
@ZenClass("novaeng.hypernet.server.module.base.ModuleRAMBase")
public class ModuleRAMBase extends ServerModuleBase<ModuleRAM> {
    protected final int hardwareBandwidthProvision;

    public ModuleRAMBase(final String registryName, final int hardwareBandwidthProvision) {
        super(registryName);
        this.hardwareBandwidthProvision = hardwareBandwidthProvision;
    }

    @Override
    public List<String> getTooltip(final ModuleRAM moduleInstance) {
        List<String> tooltip = new ArrayList<>(super.getTooltip(moduleInstance));
        tooltip.add(I18n.format("novaeng.hypernet.hardware_bandwidth.provide", this.hardwareBandwidthProvision));
        return tooltip;
    }

    @ZenMethod
    public static ModuleRAMBase create(final String registryName, final int hardwareBandwidthProvision) {
        return new ModuleRAMBase(registryName, hardwareBandwidthProvision);
    }

    @Override
    public ModuleRAM createInstance(final ModularServer server, final ItemStack moduleStack) {
        return new ModuleRAM(server, this, hardwareBandwidthProvision);
    }

}
