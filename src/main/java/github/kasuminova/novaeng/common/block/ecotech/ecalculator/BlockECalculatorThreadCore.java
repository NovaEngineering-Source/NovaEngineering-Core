package github.kasuminova.novaeng.common.block.ecotech.ecalculator;

import github.kasuminova.novaeng.NovaEngineeringCore;
import github.kasuminova.novaeng.common.block.ecotech.ecalculator.prop.Levels;
import github.kasuminova.novaeng.common.block.ecotech.ecalculator.prop.ThreadCoreStatus;
import github.kasuminova.novaeng.common.block.prop.FacingProp;
import github.kasuminova.novaeng.common.tile.ecotech.ecalculator.ECalculatorPart;
import github.kasuminova.novaeng.common.tile.ecotech.ecalculator.ECalculatorThreadCore;
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
public class BlockECalculatorThreadCore extends BlockECalculatorPart {

    public static final BlockECalculatorThreadCore L4 = new BlockECalculatorThreadCore("l4", 2, 0);
    public static final BlockECalculatorThreadCore L6 = new BlockECalculatorThreadCore("l6", 4, 0);
    public static final BlockECalculatorThreadCore L9 = new BlockECalculatorThreadCore("l9", 8, 0);

    protected final int threads;
    protected final int hyperThreads;

    protected BlockECalculatorThreadCore(final ResourceLocation registryName, final String translationKey, final int threads, final int hyperThreads) {
        super(Material.IRON);
        this.threads = threads;
        this.hyperThreads = hyperThreads;
        this.setRegistryName(registryName);
        this.setTranslationKey(translationKey);
        this.setDefaultState(this.blockState.getBaseState()
                .withProperty(FacingProp.HORIZONTALS, EnumFacing.NORTH)
                .withProperty(ThreadCoreStatus.STATUS, ThreadCoreStatus.OFF)
        );
    }

    protected BlockECalculatorThreadCore(final String level, final int threads, final int hyperThreads) {
        this(
                new ResourceLocation(NovaEngineeringCore.MOD_ID, "ecalculator_thread_core_" + level),
                NovaEngineeringCore.MOD_ID + '.' + "ecalculator_thread_core_" + level,
                threads, hyperThreads
        );
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(@Nonnull final World worldIn, final int meta) {
        return new ECalculatorThreadCore(this.threads, this.hyperThreads);
    }

    @Override
    public void breakBlock(@Nonnull final World worldIn, @Nonnull final BlockPos pos, @Nonnull final IBlockState state) {
        TileEntity te = worldIn.getTileEntity(pos);
        if (te instanceof ECalculatorThreadCore threadCore) {
            threadCore.onBlockDestroyed();
        }
        super.breakBlock(worldIn, pos, state);
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

        return state.withProperty(ThreadCoreStatus.STATUS, ThreadCoreStatus.ON);
    }

    @Override
    public int getLightValue(@Nonnull final IBlockState state) {
        if (state.getValue(ThreadCoreStatus.STATUS) == ThreadCoreStatus.RUN) {
            return 15;
        }
        if (state.getValue(ThreadCoreStatus.STATUS) == ThreadCoreStatus.ON) {
            return 10;
        }
        return 5;
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
        return new BlockStateContainer(this, FacingProp.HORIZONTALS, ThreadCoreStatus.STATUS);
    }

}
