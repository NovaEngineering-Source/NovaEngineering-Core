package github.kasuminova.novaeng.common.hypernet.proc.server.module.base;

import github.kasuminova.novaeng.common.hypernet.proc.server.ModularServer;
import github.kasuminova.novaeng.common.hypernet.proc.server.module.ModuleCPU;
import github.kasuminova.novaeng.common.hypernet.proc.server.module.ModuleCalculateCard;
import net.minecraft.item.ItemStack;

public abstract class ModuleCalculateCardBase<T extends ModuleCalculateCard> extends ModuleCalculableBase<T> {

    public ModuleCalculateCardBase(final String registryName, double baseGeneration, double energyConsumeRatio, int hardwareBandwidth) {
        super(registryName, baseGeneration, energyConsumeRatio, hardwareBandwidth);
    }

}
