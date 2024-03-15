package github.kasuminova.novaeng.mixin.immersiveengineering;

import blusunrize.immersiveengineering.common.blocks.BlockIEMultiblock;
import blusunrize.immersiveengineering.common.blocks.TileEntityMultiblockPart;
import blusunrize.immersiveengineering.common.util.inventory.IIEInventory;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Collections;
import java.util.Iterator;

@Mixin(BlockIEMultiblock.class)
@SuppressWarnings("MethodMayBeStatic")
public class MixinBlockIEMultiblock {

    @Redirect(
            method = "breakBlock",
            at = @At(value = "INVOKE", target = "Ljava/util/Iterator;hasNext()Z", remap = false)
    )
    @SuppressWarnings("rawtypes")
    private boolean onBreakBlock(final Iterator instance, @Local(name = "master") TileEntityMultiblockPart master) {
        if (instance.hasNext()) {
            return true;
        }
        if (master instanceof IIEInventory ieInv) {
            NonNullList<ItemStack> inventory = ieInv.getInventory();
            Collections.fill(inventory, ItemStack.EMPTY);
        }
        return false;
    }

}
