package github.kasuminova.novaeng.common.block.estorage;

import appeng.tile.inventory.AppEngCellInventory;
import github.kasuminova.novaeng.NovaEngineeringCore;
import github.kasuminova.novaeng.common.block.estorage.prop.*;
import github.kasuminova.novaeng.common.core.CreativeTabNovaEng;
import github.kasuminova.novaeng.common.tile.estorage.EStorageCellDrive;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@SuppressWarnings("deprecation")
public class BlockEStorageCellDrive extends BlockEStoragePart {

    public static final BlockEStorageCellDrive INSTANCE = new BlockEStorageCellDrive();

    protected BlockEStorageCellDrive() {
        super(Material.IRON);
        this.setHardness(20.0F);
        this.setResistance(2000.0F);
        this.setSoundType(SoundType.METAL);
        this.setHarvestLevel("pickaxe", 2);
        this.setCreativeTab(CreativeTabNovaEng.INSTANCE);
        this.setDefaultState(this.blockState.getBaseState()
                .withProperty(FacingProp.HORIZONTALS, EnumFacing.NORTH)
                .withProperty(DriveStorageType.STORAGE_TYPE, DriveStorageType.EMPTY)
                .withProperty(DriveStorageLevel.STORAGE_LEVEL, DriveStorageLevel.EMPTY)
                .withProperty(DriveStorageCapacity.STORAGE_CAPACITY, DriveStorageCapacity.EMPTY)
                .withProperty(DriveStatus.STATUS, DriveStatus.IDLE)
        );
        this.setRegistryName(new ResourceLocation(NovaEngineeringCore.MOD_ID, "estorage_cell_drive"));
        this.setTranslationKey(NovaEngineeringCore.MOD_ID + '.' + "estorage_cell_drive");
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(@Nonnull final World world, @Nonnull final IBlockState state) {
        return new EStorageCellDrive();
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(@Nonnull final World world, final int meta) {
        return new EStorageCellDrive();
    }

    @Override
    public void breakBlock(World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
        TileEntity te = worldIn.getTileEntity(pos);
        if (te instanceof EStorageCellDrive drive) {
            AppEngCellInventory inv = drive.getDriveInv();
            for (int i = 0; i < inv.getSlots(); i++) {
                ItemStack stack = inv.getStackInSlot(i);
                if (!stack.isEmpty()) {
                    spawnAsEntity(worldIn, pos, stack);
                    inv.setStackInSlot(i, ItemStack.EMPTY);
                }
            }
        }

        super.breakBlock(worldIn, pos, state);
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
        return new BlockStateContainer(this,
                FacingProp.HORIZONTALS,
                DriveStorageType.STORAGE_TYPE,
                DriveStorageLevel.STORAGE_LEVEL,
                DriveStorageCapacity.STORAGE_CAPACITY,
                DriveStatus.STATUS
        );
    }

}
