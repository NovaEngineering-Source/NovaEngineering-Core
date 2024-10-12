package github.kasuminova.novaeng.common.block.ecotech.ecalculator;

import github.kasuminova.novaeng.NovaEngineeringCore;
import github.kasuminova.novaeng.common.block.ecotech.ecalculator.prop.Levels;
import github.kasuminova.novaeng.common.block.ecotech.ecalculator.prop.ParallelProcStatus;
import github.kasuminova.novaeng.common.block.prop.FacingProp;
import github.kasuminova.novaeng.common.tile.ecotech.ecalculator.ECalculatorParallelProc;
import github.kasuminova.novaeng.common.tile.ecotech.ecalculator.ECalculatorPart;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;

@SuppressWarnings("deprecation")
public class BlockECalculatorParallelProc extends BlockECalculatorPart {

    public static final BlockECalculatorParallelProc L4 = new BlockECalculatorParallelProc("l4", 64);
    public static final BlockECalculatorParallelProc L6 = new BlockECalculatorParallelProc("l6", 512);
    public static final BlockECalculatorParallelProc L9 = new BlockECalculatorParallelProc("l9", 4096);

    protected final int parallelism;

    protected BlockECalculatorParallelProc(final String level, final int parallelism) {
        super(Material.IRON);
        this.parallelism = parallelism;
        this.setRegistryName(new ResourceLocation(NovaEngineeringCore.MOD_ID, "ecalculator_parallel_proc_" + level));
        this.setTranslationKey(NovaEngineeringCore.MOD_ID + '.' + "ecalculator_parallel_proc_" + level);
        this.setDefaultState(this.blockState.getBaseState()
                .withProperty(FacingProp.HORIZONTALS, EnumFacing.NORTH)
                .withProperty(ParallelProcStatus.STATUS, ParallelProcStatus.OFF)
        );
    }

    public int getParallelism() {
        return parallelism;
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(@Nonnull final World worldIn, final int meta) {
        return new ECalculatorParallelProc(this.parallelism);
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(@Nonnull final World world, @Nonnull final IBlockState state) {
        return new ECalculatorParallelProc(this.parallelism);
    }

    @Nonnull
    @Override
    public IBlockState getActualState(@Nonnull final IBlockState state, @Nonnull final IBlockAccess world, @Nonnull final BlockPos pos) {
        TileEntity te = world.getTileEntity(pos);
        if (!(te instanceof ECalculatorPart part)) {
            return state;
        }

        Levels level = part.getControllerLevel();
        if (level == null) {
            return state;
        }

        return state.withProperty(ParallelProcStatus.STATUS, ParallelProcStatus.ON);
    }

    @Override
    public int getLightValue(@Nonnull final IBlockState state) {
        if (state.getValue(ParallelProcStatus.STATUS) == ParallelProcStatus.ON) {
            return 12;
        }
        return 6;
    }

    @Nonnull
    @Override
    public IBlockState getStateFromMeta(final int meta) {
        return getDefaultState().withProperty(FacingProp.HORIZONTALS, EnumFacing.byHorizontalIndex(meta));
    }

    @Override
    public int getMetaFromState(@Nonnull final IBlockState state) {
        return state.getValue(FacingProp.HORIZONTALS).getHorizontalIndex();
    }

    @Nonnull
    public IBlockState getStateForPlacement(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        return this.getDefaultState().withProperty(FacingProp.HORIZONTALS, placer.getHorizontalFacing().getOpposite());
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
        return new BlockStateContainer(this, FacingProp.HORIZONTALS, ParallelProcStatus.STATUS);
    }

}
