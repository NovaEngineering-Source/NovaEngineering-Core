package github.kasuminova.novaeng.common.block.estorage;

import github.kasuminova.novaeng.common.core.CreativeTabHyperNet;
import github.kasuminova.novaeng.common.tile.estorage.EStorageEnergyCell;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@SuppressWarnings("deprecation")
public class BlockEStorageEnergyCell extends BlockContainer {
    public static final BlockEStorageMEChannel L4 = new BlockEStorageMEChannel();
    public static final BlockEStorageMEChannel L6 = new BlockEStorageMEChannel();
    public static final BlockEStorageMEChannel L9 = new BlockEStorageMEChannel();

    public static final PropertyEnum<EnumFacing> FACING = PropertyEnum.create("facing", EnumFacing.class, EnumFacing.HORIZONTALS);

    protected BlockEStorageEnergyCell() {
        super(Material.IRON);
        this.setHardness(20.0F);
        this.setResistance(2000.0F);
        this.setSoundType(SoundType.METAL);
        this.setHarvestLevel("pickaxe", 2);
        this.setCreativeTab(CreativeTabHyperNet.INSTANCE);
        this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(@Nonnull final World world, @Nonnull final IBlockState state) {
        return new EStorageEnergyCell();
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(@Nonnull final World world, final int meta) {
        return new EStorageEnergyCell();
    }

    @Override
    public boolean hasTileEntity() {
        return true;
    }

    @Nonnull
    public IBlockState getStateForPlacement(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
    }

    public int getMetaFromState(IBlockState state) {
        return state.getValue(FACING).getHorizontalIndex();
    }

    @Nonnull
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(FACING, EnumFacing.byHorizontalIndex(meta));
    }

    @Nonnull
    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING);
    }

}
