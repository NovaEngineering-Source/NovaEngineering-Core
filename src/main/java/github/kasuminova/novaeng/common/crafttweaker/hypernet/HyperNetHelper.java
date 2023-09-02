package github.kasuminova.novaeng.common.crafttweaker.hypernet;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.world.IBlockPos;
import github.kasuminova.mmce.common.event.client.ControllerGUIRenderEvent;
import github.kasuminova.mmce.common.helper.IMachineController;
import github.kasuminova.novaeng.common.crafttweaker.util.NovaEngUtils;
import github.kasuminova.novaeng.common.hypernet.ComputationCenter;
import github.kasuminova.novaeng.common.hypernet.ComputationCenterCache;
import github.kasuminova.novaeng.common.hypernet.NetNodeCache;
import github.kasuminova.novaeng.common.hypernet.NetNodeImpl;
import github.kasuminova.novaeng.common.registry.RegistryHyperNet;
import hellfirepvp.modularmachinery.ModularMachinery;
import hellfirepvp.modularmachinery.common.integration.crafttweaker.event.MMEvents;
import hellfirepvp.modularmachinery.common.machine.DynamicMachine;
import hellfirepvp.modularmachinery.common.machine.MachineRegistry;
import hellfirepvp.modularmachinery.common.tiles.base.TileMultiblockMachineController;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

@ZenRegister
@ZenClass("novaeng.hypernet.HyperNetHelper")
public class HyperNetHelper {

    /**
     * 自动代理一个模块化机械，使其能够接入 HyperNet 计算网络，并在控制器添加网络相关信息。
     * 默认使用 {@link NetNodeImpl} 实现。
     *
     * @param machineName 机械名称
     */
    @ZenMethod
    public static void proxyMachineForHyperNet(String machineName) {
        MMEvents.WAIT_FOR_REGISTER_LIST.add(() -> {
            DynamicMachine machine = MachineRegistry.getRegistry().getMachine(new ResourceLocation(ModularMachinery.MODID, machineName));
            if (machine != null) {
                proxyMachineForHyperNet(machine);
            }
        });
    }

    public static void proxyMachineForHyperNet(DynamicMachine machine) {
        ResourceLocation registryName = machine.getRegistryName();
        RegistryHyperNet.registerHyperNetNode(registryName, NetNodeImpl.class);

        if (!FMLCommonHandler.instance().getSide().isClient()) {
            return;
        }

        machine.addMachineEventHandler(ControllerGUIRenderEvent.class, event -> {
            TileMultiblockMachineController ctrl = event.getController();
            NetNodeImpl node = NetNodeCache.getCache(ctrl, NetNodeImpl.class);
            if (node == null) {
                return;
            }

            if (ctrl.getTicksExisted() % 20 == 0) {
                node.readNBT();
            }

            List<String> tips = new ArrayList<>();
            tips.add(I18n.format("gui.hypernet.controller.title"));

            if (node.isConnected()) {
                tips.add(I18n.format("gui.hypernet.controller.connected"));
                tips.add(I18n.format(
                        "gui.hypernet.controller.computation_point_consumption.total",
                        NovaEngUtils.formatFLOPS(ComputationCenterCache.getComputationPointConsumption()),
                        NovaEngUtils.formatFLOPS(ComputationCenterCache.getComputationPointGeneration()))
                );
                if (node.isWorking()) {
                    tips.add(I18n.format("gui.hypernet.controller.computation_point_consumption") +
                            NovaEngUtils.formatFLOPS(node.getComputationPointConsumption())
                    );
                }
            } else {
                tips.add(I18n.format("gui.hypernet.controller.disconnected"));
            }

//            tips.add(I18n.format("gui.hypernet.controller.version"));
//            tips.add(I18n.format("gui.hypernet.controller.footer"));

            event.setExtraInfo(tips.toArray(new String[0]));
        });
    }

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
        BlockPos blockPos = readConnectCardInfo(ctrl, CraftTweakerMC.getItemStack(stackCT));
        return blockPos == null ? null : CraftTweakerMC.getIBlockPos(blockPos);
    }

    public static BlockPos readConnectCardInfo(IMachineController ctrl, ItemStack stack) {
        if (stack.isEmpty()) {
            return null;
        }

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

        return pos;
    }

}
