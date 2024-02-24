package github.kasuminova.novaeng.common.item;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class ItemBlockAngel extends ItemBlock {

    public ItemBlockAngel(final Block block) {
        super(block);
    }

    @Override
    public void addInformation(@Nonnull final ItemStack stack,
                               @Nullable final World worldIn,
                               @Nonnull final List<String> tooltip,
                               @Nonnull final ITooltipFlag flagIn)
    {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        tooltip.add(I18n.format("tile.novaeng_core.angel_block.tip.0"));
        tooltip.add(I18n.format("tile.novaeng_core.angel_block.tip.1"));
        tooltip.add(I18n.format("tile.novaeng_core.angel_block.tip.2"));
    }

    @Nonnull
    @Override
    public ActionResult<ItemStack> onItemRightClick(@Nonnull final World world,
                                                    @Nonnull final EntityPlayer player,
                                                    @Nonnull final EnumHand hand)
    {
        EnumFacing playerFacing = player.getAdjustedHorizontalFacing();
        BlockPos pos = new BlockPos(player.posX, player.posY + player.eyeHeight, player.posZ);
        if (!player.isSneaking()) {
            pos = pos.offset(playerFacing, 2);
        } else {
            pos = pos.add(0, -player.height, 0);
        }

        EnumActionResult result = onItemUse(player, world, BlockPos.ORIGIN, hand, playerFacing, pos.getX(), pos.getY(), pos.getZ());
        return new ActionResult<>(result, player.getHeldItem(hand));
    }

    @Override
    public EnumActionResult onItemUse(@Nonnull final EntityPlayer player,
                                      @Nonnull final World worldIn,
                                      @Nonnull final BlockPos _0,
                                      @Nonnull final EnumHand hand,
                                      @Nonnull final EnumFacing facing,
                                      final float hitX,
                                      final float hitY,
                                      final float hitZ)
    {
        EnumFacing playerFacing = player.getAdjustedHorizontalFacing();
        BlockPos pos = new BlockPos(player.posX, player.posY + player.eyeHeight, player.posZ);
        if (!player.isSneaking()) {
            pos = pos.offset(playerFacing, 2);
        } else {
            pos = pos.add(0, -player.height, 0);
        }

        IBlockState blockState = worldIn.getBlockState(pos);
        Block block = blockState.getBlock();
        if (!block.isReplaceable(worldIn, pos)) {
            return EnumActionResult.FAIL;
        }

        ItemStack held = player.getHeldItem(hand);
        if (held.isEmpty() || !player.canPlayerEdit(pos, facing, held) || !worldIn.mayPlace(this.block, pos, false, facing, player)) {
            return EnumActionResult.FAIL;
        }

        int meta = this.getMetadata(held.getMetadata());
        IBlockState placeState = this.block.getStateForPlacement(worldIn, pos, facing, hitX, hitY, hitZ, meta, player, hand);
        if (placeBlockAt(held, player, worldIn, pos, facing, hitX, hitY, hitZ, placeState)) {
            placeState = worldIn.getBlockState(pos);
            SoundType soundtype = placeState.getBlock().getSoundType(placeState, worldIn, pos, player);
            worldIn.playSound(player, pos, soundtype.getPlaceSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
            held.shrink(1);
        }

        return EnumActionResult.SUCCESS;
    }
}
