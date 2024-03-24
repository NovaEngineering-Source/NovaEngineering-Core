package github.kasuminova.novaeng.common.block.estorage;

import github.kasuminova.novaeng.NovaEngineeringCore;
import github.kasuminova.novaeng.common.block.estorage.prop.EnergyCellStatus;
import github.kasuminova.novaeng.common.block.estorage.prop.FacingProp;
import github.kasuminova.novaeng.common.core.CreativeTabNovaEng;
import github.kasuminova.novaeng.common.tile.estorage.EStorageEnergyCell;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@SuppressWarnings("deprecation")
public class BlockEStorageEnergyCell extends BlockContainer {
    public static final BlockEStorageEnergyCell L4 = new BlockEStorageEnergyCell("l4", 10_000_000D);
    public static final BlockEStorageEnergyCell L6 = new BlockEStorageEnergyCell("l6", 100_000_000D);
    public static final BlockEStorageEnergyCell L9 = new BlockEStorageEnergyCell("l9", 1_000_000_000D);

    protected final double maxEnergyStore;

    protected BlockEStorageEnergyCell(final String level, double maxEnergyStore) {
        super(Material.IRON);
        this.setHardness(20.0F);
        this.setResistance(2000.0F);
        this.setSoundType(SoundType.METAL);
        this.setHarvestLevel("pickaxe", 2);
        this.setCreativeTab(CreativeTabNovaEng.INSTANCE);
        this.setDefaultState(this.blockState.getBaseState()
                .withProperty(FacingProp.HORIZONTALS, EnumFacing.NORTH)
                .withProperty(EnergyCellStatus.STATUS, EnergyCellStatus.EMPTY)
        );
        this.setRegistryName(new ResourceLocation(NovaEngineeringCore.MOD_ID, "estorage_energy_cell_" + level));
        this.setTranslationKey(NovaEngineeringCore.MOD_ID + '.' + "estorage_energy_cell_" + level);
        this.maxEnergyStore = maxEnergyStore;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(@Nonnull final World world, @Nonnull final IBlockState state) {
        return new EStorageEnergyCell(maxEnergyStore);
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(@Nonnull final World world, final int meta) {
        return new EStorageEnergyCell(maxEnergyStore);
    }

    @Override
    public boolean hasTileEntity() {
        return true;
    }

    @Nonnull
    public IBlockState getStateForPlacement(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        return this.getDefaultState().withProperty(FacingProp.HORIZONTALS, placer.getHorizontalFacing().getOpposite());
    }

    public int getMetaFromState(IBlockState state) {
        return state.getValue(FacingProp.HORIZONTALS).getHorizontalIndex();
    }

    @Nonnull
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState()
                .withProperty(FacingProp.HORIZONTALS, EnumFacing.byHorizontalIndex(meta))
                .withProperty(EnergyCellStatus.STATUS, EnergyCellStatus.EMPTY);
    }

    @Nonnull
    @Override
    public IBlockState getActualState(@Nonnull final IBlockState state, @Nonnull final IBlockAccess worldIn, @Nonnull final BlockPos pos) {
        return super.getActualState(state, worldIn, pos);
    }

    @Nonnull
    @Override
    @SuppressWarnings("deprecation")
    public IBlockState withRotation(IBlockState state, Rotation rot) {
        return state.withProperty(FacingProp.HORIZONTALS, rot.rotate(state.getValue(FacingProp.HORIZONTALS)));
    }

    @Nonnull
    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FacingProp.HORIZONTALS, EnergyCellStatus.STATUS);
    }

    @Nonnull
    public EnumBlockRenderType getRenderType(@Nonnull IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

}
