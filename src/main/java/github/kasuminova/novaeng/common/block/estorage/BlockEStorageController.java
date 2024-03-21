package github.kasuminova.novaeng.common.block.estorage;

import github.kasuminova.novaeng.NovaEngineeringCore;
import github.kasuminova.novaeng.common.tile.estorage.EStorageController;
import hellfirepvp.modularmachinery.ModularMachinery;
import hellfirepvp.modularmachinery.common.block.BlockController;
import hellfirepvp.modularmachinery.common.machine.DynamicMachine;
import hellfirepvp.modularmachinery.common.machine.MachineRegistry;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BlockEStorageController extends BlockController {

    protected final ResourceLocation registryName;
    protected final ResourceLocation machineRegistryName;

    public BlockEStorageController(final String level) {
        registryName = new ResourceLocation(NovaEngineeringCore.MOD_ID, "extendable_digital_storage_subsystem_" + level);
        machineRegistryName = new ResourceLocation(ModularMachinery.MODID, registryName.getPath());
        setRegistryName(registryName);
        setTranslationKey(NovaEngineeringCore.MOD_ID + '.' + registryName.getPath());
    }

    @Override
    public boolean onBlockActivated(final World worldIn, @Nonnull final BlockPos pos, @Nonnull final IBlockState state, @Nonnull final EntityPlayer playerIn, @Nonnull final EnumHand hand, @Nonnull final EnumFacing facing, final float hitX, final float hitY, final float hitZ) {

        return true;
    }

    public DynamicMachine getParentMachine() {
        return MachineRegistry.getRegistry().getMachine(machineRegistryName);
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(final World world, final IBlockState state) {
        return new EStorageController(machineRegistryName);
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(final World worldIn, final int meta) {
        return new EStorageController(machineRegistryName);
    }
}
