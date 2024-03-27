package github.kasuminova.novaeng.common.block.estorage;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;

import javax.annotation.Nonnull;

@SuppressWarnings("deprecation")
public abstract class BlockEStoragePart extends BlockContainer {

    protected BlockEStoragePart(final Material materialIn) {
        super(materialIn);
    }

    @Override
    public boolean hasTileEntity() {
        return true;
    }

    @Override
    public boolean isOpaqueCube(@Nonnull final IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullBlock(@Nonnull final IBlockState state) {
        return false;
    }

    @Nonnull
    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Nonnull
    public EnumBlockRenderType getRenderType(@Nonnull IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }
}
