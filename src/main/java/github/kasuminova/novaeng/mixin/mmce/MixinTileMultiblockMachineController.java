package github.kasuminova.novaeng.mixin.mmce;

import github.kasuminova.novaeng.common.hypernet.old.NetNodeCache;
import github.kasuminova.novaeng.common.machine.MachineSpecial;
import github.kasuminova.novaeng.common.registry.RegistryHyperNet;
import github.kasuminova.novaeng.common.registry.RegistryMachineSpecial;
import hellfirepvp.modularmachinery.common.machine.DynamicMachine;
import hellfirepvp.modularmachinery.common.tiles.base.TileMultiblockMachineController;
import net.minecraft.tileentity.TileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(TileMultiblockMachineController.class)
public abstract class MixinTileMultiblockMachineController extends TileEntity {

    @Nullable
    @Shadow(remap = false)
    public abstract DynamicMachine getFoundMachine();

    @Shadow(remap = false)
    public abstract TileMultiblockMachineController getController();

    @Inject(method = "doRestrictedTick", at = @At("HEAD"), remap = false, cancellable = true)
    private void injectUpdate(final CallbackInfo ci) {
        DynamicMachine foundMachine = getFoundMachine();
        if (foundMachine == null) {
            return;
        }
        MachineSpecial specialMachine = RegistryMachineSpecial.getSpecialMachine(foundMachine.getRegistryName());

        boolean client = getWorld().isRemote;
        if (client) {
            ci.cancel();
        }

        if (specialMachine != null) {
            if (client) {
                specialMachine.onClientTick(getController());
            } else {
                specialMachine.onSyncTick(getController());
            }
        }
    }

    @Inject(method = "resetMachine", at = @At("HEAD"), remap = false)
    private void onResetMachine(final boolean clearData, final CallbackInfo ci) {
        if (clearData) {
            novaeng_hypernet$removeCache();
        }
    }

    @Inject(method = "invalidate", at = @At("HEAD"))
    private void onInvalidate(final CallbackInfo ci) {
        novaeng_hypernet$removeCache();
    }

    @Inject(method = "onChunkUnload", at = @At("RETURN"), remap = false)
    public void injectOnChunkUnload(final CallbackInfo ci) {
        novaeng_hypernet$removeCache();
    }

    @Unique
    private void novaeng_hypernet$removeCache() {
        TileMultiblockMachineController controller = (TileMultiblockMachineController) (Object) this;
        if (RegistryHyperNet.isHyperNetSupported(controller.getFoundMachine())) {
            NetNodeCache.removeCache(controller);
        }
    }
}