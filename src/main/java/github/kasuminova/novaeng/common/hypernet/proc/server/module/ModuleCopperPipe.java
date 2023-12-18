package github.kasuminova.novaeng.common.hypernet.proc.server.module;

import crafttweaker.annotations.ZenRegister;
import github.kasuminova.novaeng.common.hypernet.proc.server.CalculateServer;
import github.kasuminova.novaeng.common.hypernet.proc.server.ModularServer;
import github.kasuminova.novaeng.common.hypernet.proc.server.module.base.ServerModuleBase;
import net.minecraft.nbt.NBTTagCompound;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import javax.annotation.Nonnull;

@ZenRegister
@ZenClass("novaeng.hypernet.module.ModuleCopperPipe")
public class ModuleCopperPipe extends ModuleHeatRadiator {

    public ModuleCopperPipe(final ModularServer server,final ServerModuleBase<?> moduleBase, final int moduleAmount) {
        super(server, moduleBase, moduleAmount);
    }

    @ZenMethod
    public static ModuleCopperPipe cast(ServerModule module) {
        return module instanceof ModuleCopperPipe ? (ModuleCopperPipe) module : null;
    }

}
