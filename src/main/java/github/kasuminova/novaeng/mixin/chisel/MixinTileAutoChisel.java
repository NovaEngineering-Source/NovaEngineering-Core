package github.kasuminova.novaeng.mixin.chisel;

import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import team.chisel.common.block.TileAutoChisel;

@Mixin(TileAutoChisel.class)
public class MixinTileAutoChisel {

    @Unique
    private long novaeng_core$interval = 20;

    @ModifyConstant(method = "update", constant = @Constant(longValue = 20L))
    private long modifyUpdateInterval(long __) {
        return novaeng_core$interval;
    }

    @Inject(method = "mergeOutput", at = @At("HEAD"), remap = false)
    private void injectMergeOutput(final ItemStack stack, final CallbackInfo ci) {
        novaeng_core$interval = Math.max(novaeng_core$interval - 5, 20);
    }

    @Inject(method = "setSourceSlot", at = @At("HEAD"), remap = false)
    private void injectSetSourceSlot(final int slot, final CallbackInfo ci) {
        if (slot == -1) {
            novaeng_core$interval = Math.min(novaeng_core$interval + 5, 100);
        }
    }

}
