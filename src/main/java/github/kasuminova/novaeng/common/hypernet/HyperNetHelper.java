package github.kasuminova.novaeng.common.hypernet;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.world.IBlockPos;
import github.kasuminova.mmce.common.helper.IMachineController;
import github.kasuminova.novaeng.common.registry.RegistryHyperNet;
import hellfirepvp.modularmachinery.common.machine.DynamicMachine;
import hellfirepvp.modularmachinery.common.tiles.base.TileMultiblockMachineController;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import javax.annotation.Nullable;

@ZenRegister
@ZenClass("novaeng.hypernet.HyperNetHelper")
public class HyperNetHelper {

    public static boolean supportsHyperNet(final TileMultiblockMachineController ctrl) {
        return RegistryHyperNet.isHyperNetSupported(ctrl.getFoundMachine());
    }

    public static boolean isComputationCenter(final TileMultiblockMachineController ctrl) {
        DynamicMachine foundMachine = ctrl.getFoundMachine();
        if (foundMachine == null) {
            return false;
        }

        return RegistryHyperNet.isComputationCenter(foundMachine.getRegistryName());
    }

    @ZenMethod
    public static IItemStack writeConnectCardInfo(ComputationCenter center, IItemStack stackCT) {
        return CraftTweakerMC.getIItemStack(writeConnectCardInfo(center, CraftTweakerMC.getItemStack(stackCT)));
    }

    public static ItemStack writeConnectCardInfo(ComputationCenter center, ItemStack stack) {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setLong("pos", center.getOwner().getPos().toLong());
        stack.setTagCompound(tag);

        return stack;
    }

    @Nullable
    @ZenMethod
    public static IBlockPos readConnectCardInfo(IMachineController ctrl, IItemStack stackCT) {
        ItemStack stack = CraftTweakerMC.getItemStack(stackCT);

        NBTTagCompound tag = stack.getTagCompound();
        if (tag == null || !tag.hasKey("pos")) {
            return null;
        }

        BlockPos pos = BlockPos.fromLong(tag.getLong("pos"));
        World world = ctrl.getController().getWorld();
        if (!world.isBlockLoaded(pos)) {
            return null;
        }

        TileEntity te = world.getTileEntity(pos);
        if (!(te instanceof TileMultiblockMachineController)) {
            return null;
        }

        TileMultiblockMachineController center = (TileMultiblockMachineController) te;
        if (!HyperNetHelper.isComputationCenter(center)) {
            return null;
        }

        return CraftTweakerMC.getIBlockPos(pos);
    }

}
