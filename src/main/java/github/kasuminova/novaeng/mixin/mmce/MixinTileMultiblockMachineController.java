package github.kasuminova.novaeng.mixin.mmce;

import github.kasuminova.novaeng.common.hypernet.NetNodeCache;
import github.kasuminova.novaeng.common.registry.RegistryHyperNet;
import hellfirepvp.modularmachinery.common.tiles.base.TileMultiblockMachineController;
import net.minecraft.tileentity.TileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TileMultiblockMachineController.class)
public class MixinTileMultiblockMachineController extends TileEntity {

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

    @Unique
    @Override
    public void onChunkUnload() {
        super.onChunkUnload();
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