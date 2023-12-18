package github.kasuminova.novaeng.common.hypernet.proc.server.module.base;

import github.kasuminova.novaeng.common.hypernet.proc.server.ModularServer;
import github.kasuminova.novaeng.common.hypernet.proc.server.module.ModuleRAM;
import net.minecraft.item.ItemStack;

public class ModuleRAMBase extends ServerModuleBase<ModuleRAM> {
    protected final int hardwareBandwidthProvision;

    public ModuleRAMBase(final String registryName, int hardwareBandwidthProvision) {
        super(registryName);
        this.hardwareBandwidthProvision = hardwareBandwidthProvision;
    }

    @Override
    public ModuleRAM createInstance(final ModularServer server, final ItemStack moduleStack) {
        return new ModuleRAM(server, this, hardwareBandwidthProvision);
    }

}
