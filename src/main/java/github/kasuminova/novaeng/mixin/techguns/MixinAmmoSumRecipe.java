package github.kasuminova.novaeng.mixin.techguns;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import techguns.recipes.AmmoSumRecipeFactory;

@Mixin(AmmoSumRecipeFactory.AmmoSumRecipe.class)
public class MixinAmmoSumRecipe {

    @SuppressWarnings("MethodMayBeStatic")
    @Redirect(method = "getCraftingResult",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/item/ItemStack;getTagCompound()Lnet/minecraft/nbt/NBTTagCompound;",
                    remap = true),
            remap = true
    )
    private NBTTagCompound onGetCraftingResult(final ItemStack instance) {
        NBTTagCompound tag = instance.getTagCompound();
        if (tag == null) {
            tag = new NBTTagCompound();
            instance.setTagCompound(tag);
        }
        return tag;
    }

}
