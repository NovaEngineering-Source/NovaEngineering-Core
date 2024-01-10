package github.kasuminova.novaeng.mixin.eio;

import crazypants.enderio.conduits.conduit.item.IItemConduit;
import crazypants.enderio.conduits.conduit.item.NetworkedInventory;
import github.kasuminova.novaeng.mixin.util.CachedItemConduit;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nonnull;

@Mixin(NetworkedInventory.class)
public abstract class MixinNetworkedInventory {
    @Nonnull
    @Shadow(remap = false) @Final private IItemConduit con;

    @SuppressWarnings({"StaticVariableMayNotBeInitialized", "NonConstantFieldWithUpperCaseName"})
    @Shadow(remap = false) @Final private static boolean EXECUTE;

    @Shadow(remap = false) protected abstract int insertIntoTargets(@Nonnull final ItemStack toInsert);

    @Shadow(remap = false) protected abstract void onItemExtracted(final int slot, final int numInserted);

    @Inject(method = "doTransfer", at = @At("HEAD"), cancellable = true, remap = false)
    public void doTransfer(final IItemHandler inventory,
                           final ItemStack extractedItem,
                           final int slot,
                           final CallbackInfoReturnable<Boolean> cir) {
        if (!(con instanceof CachedItemConduit cachedItemConduit)) {
            return;
        }
        if (!cachedItemConduit.getCachedStack().isEmpty()) {
            cir.setReturnValue(false);
            return;
        }

        ItemStack extracted = inventory.extractItem(slot, extractedItem.getCount(), EXECUTE);
        int inserted = insertIntoTargets(extracted.copy());
        int notInserted = extracted.getCount() - inserted;

        if (notInserted > 0) {
            ItemStack notInsertedStack = extracted.copy();
            notInsertedStack.setCount(notInserted);
            cachedItemConduit.setCachedStack(notInsertedStack);
        } else {
            cachedItemConduit.setCachedStack(ItemStack.EMPTY);
        }

        onItemExtracted(slot, inserted);

        cir.setReturnValue(inserted > 0);
    }

    @Inject(
            method = "transferItems",
            at = @At(
                    value = "INVOKE",
                    target = "Lcrazypants/enderio/conduits/conduit/item/IItemConduit;getMaximumExtracted(Lnet/minecraft/util/EnumFacing;)I",
                    remap = false
            ),
            cancellable = true,
            remap = false)
    public void onTransferItems(final CallbackInfoReturnable<Boolean> cir) {
        if (!(con instanceof CachedItemConduit cachedItemConduit)) {
            return;
        }

        ItemStack cachedStack = cachedItemConduit.getCachedStack();
        if (cachedStack.isEmpty()) {
            return;
        }

        int inserted = insertIntoTargets(cachedStack.copy());
        if (inserted == cachedStack.getCount()) {
            cachedItemConduit.setCachedStack(ItemStack.EMPTY);
            return;
        }
        if (inserted == 0) {
            cir.setReturnValue(false);
            return;
        }
        cachedStack.shrink(inserted);
    }

}
