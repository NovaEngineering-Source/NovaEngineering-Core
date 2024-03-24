package github.kasuminova.novaeng.common.block.estorage;

import appeng.api.AEApi;
import appeng.api.storage.ICellInventoryHandler;
import appeng.api.storage.IStorageChannel;
import appeng.api.storage.data.IAEStack;
import appeng.tile.inventory.AppEngCellInventory;
import github.kasuminova.novaeng.common.core.CreativeTabHyperNet;
import github.kasuminova.novaeng.common.estorage.EStorageCellHandler;
import github.kasuminova.novaeng.common.item.estorage.EStorageCell;
import github.kasuminova.novaeng.common.tile.estorage.EStorageCellDrive;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;

@SuppressWarnings("deprecation")
public class BlockEStorageCellDrive extends BlockContainer {

    public static final BlockEStorageCellDrive INSTANCE = new BlockEStorageCellDrive();

    public static final PropertyEnum<EnumFacing> FACING = PropertyEnum.create("facing", EnumFacing.class, EnumFacing.HORIZONTALS);

    public static final PropertyEnum<StorageType> STORAGE_TYPE = PropertyEnum.create("storage_type", StorageType.class);
    public static final PropertyEnum<StorageLevel> STORAGE_LEVEL = PropertyEnum.create("storage_level", StorageLevel.class);
    public static final PropertyEnum<StorageCapacity> STORAGE_CAPACITY = PropertyEnum.create("storage_capacity", StorageCapacity.class);

    public static final PropertyEnum<Status> STATUS = PropertyEnum.create("status", Status.class);

    protected BlockEStorageCellDrive() {
        super(Material.IRON);
        this.setHardness(20.0F);
        this.setResistance(2000.0F);
        this.setSoundType(SoundType.METAL);
        this.setHarvestLevel("pickaxe", 2);
        this.setCreativeTab(CreativeTabHyperNet.INSTANCE);
        this.setDefaultState(this.blockState.getBaseState()
                .withProperty(FACING, EnumFacing.NORTH)
                .withProperty(STORAGE_TYPE, StorageType.EMPTY)
                .withProperty(STORAGE_LEVEL, StorageLevel.EMPTY)
                .withProperty(STORAGE_CAPACITY, StorageCapacity.EMPTY)
                .withProperty(STATUS, Status.IDLE)
        );
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
    public boolean hasTileEntity() {
        return true;
    }

    @Nonnull
    @Override
    @SuppressWarnings("rawtypes")
    public IBlockState getActualState(@Nonnull final IBlockState state, @Nonnull final IBlockAccess world, @Nonnull final BlockPos pos) {
        TileEntity te = world.getTileEntity(pos);
        if (!(te instanceof EStorageCellDrive drive)) {
            return state;
        }
        AppEngCellInventory driveInv = drive.getDriveInv();
        ItemStack stack = driveInv.getStackInSlot(0);
        if (stack.isEmpty()) {
            return state;
        }

        EStorageCellHandler handler = EStorageCellHandler.getHandler(stack);
        if (handler == null) {
            return state;
        }

        EStorageCell<?> cell = (EStorageCell<?>) stack.getItem();
        BlockEStorageCellDrive.StorageLevel level = cell.getLevel();
        BlockEStorageCellDrive.StorageType type = EStorageCellDrive.getCellType(cell);
        if (type == null) {
            return state;
        }

        final Collection<IStorageChannel<? extends IAEStack<?>>> storageChannels = AEApi.instance().storage().storageChannels();
        ICellInventoryHandler cellInventory = null;
        for (final IStorageChannel<? extends IAEStack<?>> channel : storageChannels) {
            cellInventory = handler.getCellInventory(stack, drive, channel);
            if (cellInventory != null) {
                break;
            }
        }

        if (cellInventory == null) {
            return state;
        }

        return state.withProperty(BlockEStorageCellDrive.STORAGE_LEVEL, level)
                .withProperty(BlockEStorageCellDrive.STORAGE_TYPE, type)
                .withProperty(BlockEStorageCellDrive.STATUS, drive.isWriting() ? Status.RUN : Status.IDLE)
                .withProperty(BlockEStorageCellDrive.STORAGE_CAPACITY, EStorageCellDrive.getCapacity(cellInventory));
    }

    @Nonnull
    @Override
    public IBlockState getStateFromMeta(final int meta) {
        return getDefaultState().withProperty(FACING, EnumFacing.byHorizontalIndex(meta));
    }

    @Nonnull
    public IBlockState getStateForPlacement(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
    }

    @Nonnull
    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING, STORAGE_TYPE, STORAGE_LEVEL, STORAGE_CAPACITY, STATUS);
    }

    @Override
    public int getMetaFromState(@Nonnull final IBlockState state) {
        return state.getValue(FACING).ordinal();
    }

    public enum StorageType implements IStringSerializable {
        EMPTY("empty"),
        ITEM("item"),
        FLUID("fluid");

        private final String name;

        StorageType(String name) {
            this.name = name;
        }

        @Nonnull
        @Override
        public String getName() {
            return name;
        }
    }

    public enum StorageLevel implements IStringSerializable {
        EMPTY("empty"),
        A("A"),
        B("B"),
        C("C");

        private final String name;

        StorageLevel(String name) {
            this.name = name;
        }

        @Nonnull
        @Override
        public String getName() {
            return name;
        }
    }

    public enum Status implements IStringSerializable {
        IDLE("idle"),
        RUN("run");

        private final String name;

        Status(String name) {
            this.name = name;
        }

        @Nonnull
        @Override
        public String getName() {
            return name;
        }
    }

    public enum StorageCapacity implements IStringSerializable {
        EMPTY("empty"),
        TYPE_MAX("type_max"),
        FULL("full");

        private final String name;

        StorageCapacity(String name) {
            this.name = name;
        }

        @Nonnull
        @Override
        public String getName() {
            return name;
        }
    }


}
