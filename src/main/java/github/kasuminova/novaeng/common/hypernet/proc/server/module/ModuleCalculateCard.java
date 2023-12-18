package github.kasuminova.novaeng.common.hypernet.proc.server.module;

import github.kasuminova.novaeng.common.hypernet.proc.server.*;
import github.kasuminova.novaeng.common.hypernet.proc.server.module.base.ServerModuleBase;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nonnull;

public abstract class ModuleCalculateCard extends ModuleCalculable {

    public ModuleCalculateCard(final ModularServer server,final ServerModuleBase<?> moduleBase, final double baseGeneration, final double energyConsumeRatio, final int hardwareBandwidth) {
        super(server, moduleBase, baseGeneration, energyConsumeRatio, hardwareBandwidth);
    }

}
