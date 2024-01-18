package github.kasuminova.novaeng.mixin.eio;

import crazypants.enderio.base.capability.ItemTools;
import crazypants.enderio.base.filter.item.IItemFilter;
import crazypants.enderio.conduits.conduit.item.IItemConduit;
import crazypants.enderio.conduits.conduit.item.NetworkedInventory;
import crazypants.enderio.util.Prep;
import github.kasuminova.novaeng.mixin.util.CachedItemConduit;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Field;

@Mixin(NetworkedInventory.class)
public abstract class MixinNetworkedInventory {
    @Unique
    private static Field target$stickyInput = null;
    @Unique
    private static Field target$inv = null;

    static {
        Class<?> target = null;
        for (final Class<?> declaredClass : NetworkedInventory.class.getDeclaredClasses()) {
            if (!declaredClass.getSimpleName().equals("Target")) {
                continue;
            }
            target = declaredClass;
            break;
        }

        if (target != null) {
            try {
                target$stickyInput = target.getDeclaredField("stickyInput");
                target$stickyInput.setAccessible(true);
                target$inv = target.getDeclaredField("inv");
                target$inv.setAccessible(true);
            } catch (NoSuchFieldException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Nonnull
    @Shadow(remap = false) @Final private IItemConduit con;

    @SuppressWarnings({"StaticVariableMayNotBeInitialized", "NonConstantFieldWithUpperCaseName"})
    @Shadow(remap = false) @Final private static boolean EXECUTE;

    @Shadow(remap = false) protected abstract int insertIntoTargets(@Nonnull final ItemStack toInsert);

    @Shadow(remap = false) protected abstract void onItemExtracted(final int slot, final int numInserted);

    @Shadow(remap = false) protected abstract Iterable<Object> getTargetIterator();

    @Shadow(remap = false)
    private static IItemFilter valid(final IItemFilter filter) {
        return null;
    }

    @Shadow(remap = false)
    private static int positive(final int x) {
        return 0;
    }

    @Unique
    private static int novaeng$insertItemSimulate(NetworkedInventory targetInv, @Nonnull ItemStack item, IItemFilter filter) {
        InvokerNetworkedInventory inv = (InvokerNetworkedInventory) targetInv;
        if (!inv.callCanInsert() || Prep.isInvalid(item)) {
            return 0;
        }
        final IItemHandler inventory = inv.callGetInventory();
        if (inventory == null) {
            return 0;
        }
        if (filter == null) {
            return novaeng$simulateInsertItem(inventory, item);
        }
        if (filter.isLimited()) {
            final int count = filter.getMaxCountThatPassesFilter(inventory, item);
            if (count <= 0) {
                return 0;
            }
            final int maxInsert = ItemTools.getInsertLimit(inventory, item, count);
            if (maxInsert <= 0) {
                return 0;
            }
            if (maxInsert < item.getCount()) {
                item = item.copy();
                item.setCount(maxInsert);
            }
        } else if (!filter.doesItemPassFilter(inventory, item)) {
            return 0;
        }
        return novaeng$simulateInsertItem(inventory, item);
    }

    @Unique
    private static int novaeng$simulateInsertItem(@Nullable IItemHandler inventory, @Nonnull ItemStack item) {
        if (inventory == null || Prep.isInvalid(item)) {
            return 0;
        }
        int startSize = item.getCount();
        ItemStack res = ItemTools.insertItemStacked(inventory, item.copy(), true);
        return startSize - res.getCount();
    }

    @Unique
    private static boolean novaeng$getTargetStickyInput(Object obj) {
        try {
            return target$stickyInput.getBoolean(obj);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Unique
    private static NetworkedInventory novaeng$getTargetInv(Object obj) {
        try {
            return (NetworkedInventory) target$inv.get(obj);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

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
        int simulateInserted = novaeng$insertIntoTargetsSimulate(extractedItem.copy());
        if (simulateInserted <= 0) {
            cir.setReturnValue(false);
            return;
        }

        ItemStack extracted = inventory.extractItem(slot, simulateInserted, EXECUTE);
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

    @Unique
    private int novaeng$insertIntoTargetsSimulate(@Nonnull ItemStack toInsert) {
        if (Prep.isInvalid(toInsert)) {
            return 0;
        }

        final int totalToInsert = toInsert.getCount();
        // when true, a sticky filter has claimed this item and so only sticky outputs are allowed to handle it. sticky outputs are first in the target
        // list, so all sticky outputs are queried before any non-sticky one.
        boolean matchedStickyOutput = false;

        for (Object target : getTargetIterator()) {
            final IItemFilter filter = valid(novaeng$getTargetInv(target).getCon().getOutputFilter(novaeng$getTargetInv(target).getConDir()));
            if (novaeng$getTargetStickyInput(target) && !matchedStickyOutput && filter != null) {
                matchedStickyOutput = filter.doesItemPassFilter(novaeng$getTargetInv(target).getInventory(), toInsert);
            }
            if (novaeng$getTargetStickyInput(target) || !matchedStickyOutput) {
                toInsert.shrink(positive(novaeng$insertItemSimulate(novaeng$getTargetInv(target), toInsert, filter)));
                if (Prep.isInvalid(toInsert)) {
                    // everything has been inserted. we're done.
                    break;
                }
            } else if (!novaeng$getTargetStickyInput(target)) {
                // item has been claimed by a sticky output but there are no sticky outputs left in targets, so we can stop checking
                break;
            }
        }

        return totalToInsert - toInsert.getCount();
    }
}
