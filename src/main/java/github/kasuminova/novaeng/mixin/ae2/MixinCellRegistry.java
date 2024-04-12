package github.kasuminova.novaeng.mixin.ae2;

import appeng.api.storage.ICellInventoryHandler;
import appeng.api.storage.ISaveProvider;
import appeng.api.storage.IStorageChannel;
import appeng.core.features.registries.cell.CellRegistry;
import github.kasuminova.novaeng.common.estorage.EStorageCellHandler;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CellRegistry.class)
@SuppressWarnings("MethodMayBeStatic")
public class MixinCellRegistry {

    @Inject(method = "getCellInventory", at = @At("HEAD"), cancellable = true, remap = false)
    private void injectEStorageGetCellInventory(final ItemStack stack,
                                                final ISaveProvider container,
                                                final IStorageChannel<?> channel,
                                                final CallbackInfoReturnable<ICellInventoryHandler<?>> cir)
    {
        if (stack.isEmpty()) {
            cir.setReturnValue(null);
            return;
        }
        EStorageCellHandler handler = EStorageCellHandler.getHandler(stack);
        if (handler != null) {
            cir.setReturnValue(handler.getCellInventory(stack, container, channel));
        }
    }

}
