package github.kasuminova.novaeng.common.block;

import github.kasuminova.novaeng.NovaEngineeringCore;
import github.kasuminova.novaeng.common.CommonProxy;
import github.kasuminova.novaeng.common.tile.TileHyperNetTerminal;
import hellfirepvp.modularmachinery.ModularMachinery;
import hellfirepvp.modularmachinery.common.block.BlockController;
import hellfirepvp.modularmachinery.common.machine.DynamicMachine;
import hellfirepvp.modularmachinery.common.machine.MachineRegistry;
import hellfirepvp.modularmachinery.common.util.IOInventory;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Random;
import java.util.UUID;

public class BlockHyperNetTerminal extends BlockController {
    public static final BlockHyperNetTerminal INSTANCE = new BlockHyperNetTerminal();

    private BlockHyperNetTerminal() {
        setRegistryName(new ResourceLocation(NovaEngineeringCore.MOD_ID, "hypernet_terminal_controller"));
        setTranslationKey(NovaEngineeringCore.MOD_ID + '.' + "hypernet_terminal_controller");
    }

    public DynamicMachine getParentMachine() {
        return MachineRegistry.getRegistry().getMachine(
                new ResourceLocation(ModularMachinery.MODID, "hypernet_terminal")
        );
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileHyperNetTerminal();
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileHyperNetTerminal();
    }

    @Override
    public boolean onBlockActivated(World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull EntityPlayer playerIn, @Nonnull EnumHand hand, @Nonnull EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!worldIn.isRemote) {
            TileEntity te = worldIn.getTileEntity(pos);
            if (te instanceof TileHyperNetTerminal) {
                playerIn.openGui(NovaEngineeringCore.MOD_ID, CommonProxy.GuiType.HYPERNET_TERMINAL.ordinal(), worldIn, pos.getX(), pos.getY(), pos.getZ());
            }
        }
        return true;
    }

    @Nonnull
    @Override
    @SideOnly(Side.CLIENT)
    public String getLocalizedName() {
        return I18n.format("tile.novaeng_core.hypernet_terminal_controller.name");
    }

    @Override
    public void getDrops(@Nonnull final NonNullList<ItemStack> drops, @Nonnull final IBlockAccess world, @Nonnull final BlockPos pos, @Nonnull final IBlockState state, final int fortune) {
//        Random rand = world instanceof World ? ((World) world).rand : RANDOM;
//
//        TileEntity te = world.getTileEntity(pos);
//        if (te instanceof TileHyperNetTerminal ctrl && ctrl.getOwner() != null) {
//            UUID ownerUUID = ctrl.getOwner();
//            Item dropped = getItemDropped(state, rand, fortune);
//            ItemStack stackCtrl = new ItemStack(dropped, 1);
//            if (ownerUUID != null) {
//                NBTTagCompound tag = new NBTTagCompound();
//                tag.setString("owner", ownerUUID.toString());
//                stackCtrl.setTagCompound(tag);
//            }
//            drops.add(stackCtrl);
//        } else {
//            super.getDrops(drops, world, pos, state, fortune);
//        }
    }

    @Override
    public void breakBlock(World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
        Random rand = worldIn.rand;
        TileEntity te = worldIn.getTileEntity(pos);
        if (te instanceof TileHyperNetTerminal ctrl) {
            IOInventory inv = ctrl.getCardInventory();
            for (int i = 0; i < inv.getSlots(); i++) {
                ItemStack stack = inv.getStackInSlot(i);
                if (!stack.isEmpty()) {
                    spawnAsEntity(worldIn, pos, stack);
                    inv.setStackInSlot(i, ItemStack.EMPTY);
                }
            }

            UUID ownerUUID = ctrl.getOwner();
            Item dropped = getItemDropped(state, rand, damageDropped(state));
            ItemStack stackCtrl = new ItemStack(dropped, 1);
            if (ownerUUID != null) {
                NBTTagCompound tag = new NBTTagCompound();
                tag.setString("owner", ownerUUID.toString());
                stackCtrl.setTagCompound(tag);
            }
            spawnAsEntity(worldIn, pos, stackCtrl);
        }

        // TODO MM warn.
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
