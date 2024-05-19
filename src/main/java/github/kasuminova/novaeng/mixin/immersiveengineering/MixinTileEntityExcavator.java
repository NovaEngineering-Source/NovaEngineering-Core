package github.kasuminova.novaeng.mixin.immersiveengineering;

import blusunrize.immersiveengineering.common.blocks.metal.TileEntityExcavator;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(TileEntityExcavator.class)
public class MixinTileEntityExcavator {
    
    @Redirect(
            method = "doProcessOutput",
            at = @At(
                    value = "INVOKE",
                    target = "Lblusunrize/immersiveengineering/common/util/Utils;dropStackAtPos(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/item/ItemStack;Lnet/minecraft/util/EnumFacing;)V", 
                    remap = false
            ),
            remap = false
    )
    private void redirectDoProcessOutput(final World ei, final BlockPos world, final ItemStack pos, final EnumFacing stack) {
        // 销毁物品！为什么不做掉落物清理！
    }
    
}
