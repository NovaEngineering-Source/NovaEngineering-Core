package github.kasuminova.novaeng.common.block;

import github.kasuminova.novaeng.NovaEngineeringCore;
import github.kasuminova.novaeng.common.CommonProxy;
import github.kasuminova.novaeng.common.tile.TileModularServerAssembler;
import hellfirepvp.modularmachinery.ModularMachinery;
import hellfirepvp.modularmachinery.common.block.BlockController;
import hellfirepvp.modularmachinery.common.machine.DynamicMachine;
import hellfirepvp.modularmachinery.common.machine.MachineRegistry;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BlockModularServerAssembler extends BlockController {
    public static final BlockModularServerAssembler INSTANCE = new BlockModularServerAssembler();

    private BlockModularServerAssembler() {
        setRegistryName(new ResourceLocation(NovaEngineeringCore.MOD_ID, "modular_server_assembler"));
        setTranslationKey(NovaEngineeringCore.MOD_ID + '.' + "modular_server_assembler");
    }

    @Override
    public AxisAlignedBB getBoundingBox(final IBlockState state, final IBlockAccess source, final BlockPos pos) {
        return super.getBoundingBox(state, source, pos);
    }

    public DynamicMachine getParentMachine() {
        return MachineRegistry.getRegistry().getMachine(
                new ResourceLocation(ModularMachinery.MODID, "modular_server_assembler")
        );
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileModularServerAssembler();
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileModularServerAssembler();
    }

    @Override
    public boolean onBlockActivated(World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull EntityPlayer playerIn, @Nonnull EnumHand hand, @Nonnull EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!worldIn.isRemote) {
            TileEntity te = worldIn.getTileEntity(pos);
            if (te instanceof TileModularServerAssembler) {
                playerIn.openGui(NovaEngineeringCore.MOD_ID, CommonProxy.GuiType.MODULAR_SERVER_ASSEMBLER.ordinal(), worldIn, pos.getX(), pos.getY(), pos.getZ());
            }
        }
        return true;
    }

    @Nonnull
    @Override
    @SideOnly(Side.CLIENT)
    public String getLocalizedName() {
        return I18n.format("tile.novaeng_core.modular_server_assembler.name");
    }

    public void breakBlock(World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
//        TileEntity te = worldIn.getTileEntity(pos);
//        if (te instanceof TileHyperNetTerminal terminal) {
//            IOInventory inv = terminal.getCardInventory();
//            for (int i = 0; i < inv.getSlots(); i++) {
//                ItemStack stack = inv.getStackInSlot(i);
//                if (!stack.isEmpty()) {
//                    spawnAsEntity(worldIn, pos, stack);
//                    inv.setStackInSlot(i, ItemStack.EMPTY);
//                }
//            }
//        }
        super.breakBlock(worldIn, pos, state);
    }

    @Override
    public boolean canConnectRedstone(@Nonnull IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nullable EnumFacing side) {
        return false;
    }

    @Override
    public boolean hasComparatorInputOverride(@Nonnull IBlockState state) {
        return false;
    }
}
