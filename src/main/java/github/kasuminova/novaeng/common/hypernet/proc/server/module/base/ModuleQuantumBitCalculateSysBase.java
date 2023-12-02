package github.kasuminova.novaeng.common.hypernet.proc.server.module.base;

import github.kasuminova.novaeng.common.hypernet.proc.server.CalculateServer;
import github.kasuminova.novaeng.common.hypernet.proc.server.module.ModuleQuantumBitCalculateSys;
import net.minecraft.item.ItemStack;

public class ModuleQuantumBitCalculateSysBase extends ModuleCalculateCardBase<ModuleQuantumBitCalculateSys> {
    public ModuleQuantumBitCalculateSysBase(final String registryName) {
        super(registryName);
    }

    @Override
    public ModuleQuantumBitCalculateSys createInstance(final CalculateServer server, final ItemStack moduleStack) {
        return null;
    }
}
