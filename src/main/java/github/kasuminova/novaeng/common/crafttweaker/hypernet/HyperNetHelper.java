package github.kasuminova.novaeng.common.crafttweaker.hypernet;

import crafttweaker.annotations.ZenRegister;
import github.kasuminova.mmce.common.event.client.ControllerGUIRenderEvent;
import github.kasuminova.mmce.common.helper.IMachineController;
import github.kasuminova.novaeng.common.crafttweaker.util.NovaEngUtils;
import github.kasuminova.novaeng.common.hypernet.old.ComputationCenter;
import github.kasuminova.novaeng.common.hypernet.old.ComputationCenterCache;
import github.kasuminova.novaeng.common.hypernet.old.NetNodeCache;
import github.kasuminova.novaeng.common.hypernet.old.NetNodeImpl;
import github.kasuminova.novaeng.common.hypernet.old.misc.HyperNetConnectCardInfo;
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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
        proxyMachineForHyperNet(new ResourceLocation(ModularMachinery.MODID, machineName));
    }

    public static void proxyMachineForHyperNet(ResourceLocation registryName) {
        RegistryHyperNet.registerHyperNetNode(registryName, NetNodeImpl.class);
        MMEvents.WAIT_FOR_MODIFY.add(() -> {
            DynamicMachine machine = MachineRegistry.getRegistry().getMachine(registryName);
            if (machine != null) {
                addControllerGUIHyperNetInfo(machine, NetNodeImpl.class);
            }
        });
    }

    public static <T extends NetNodeImpl> void addControllerGUIHyperNetInfo(final DynamicMachine machine, Class<T> netNodeType) {
        if (!FMLCommonHandler.instance().getSide().isClient()) {
            return;
        }

        machine.addMachineEventHandler(ControllerGUIRenderEvent.class, event -> {
            TileMultiblockMachineController ctrl = event.getController();
            T node = NetNodeCache.getCache(ctrl, netNodeType);
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

    public static void writeConnectCardInfo(ComputationCenter center, UUID networkOwner, ItemStack stack) {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setLong("pos", center.getOwner().getPos().toLong());
        tag.setString("owner", networkOwner.toString());
        stack.setTagCompound(tag);
    }

    public static boolean isValidConnectCard(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }

        NBTTagCompound tag = stack.getTagCompound();
        return tag != null && tag.hasKey("pos") && tag.hasKey("owner");
    }

    public static HyperNetConnectCardInfo readConnectCardInfo(IMachineController ctrl, ItemStack stack) {
        if (stack.isEmpty()) {
            return null;
        }

        NBTTagCompound tag = stack.getTagCompound();
        if (tag == null || !tag.hasKey("pos") || !tag.hasKey("owner")) {
            return null;
        }

        BlockPos pos = BlockPos.fromLong(tag.getLong("pos"));
        World world = ctrl.getController().getWorld();
        if (!world.isBlockLoaded(pos)) {
            return null;
        }

        TileEntity te = world.getTileEntity(pos);
        if (!(te instanceof final TileMultiblockMachineController center)) {
            return null;
        }

        if (!HyperNetHelper.isComputationCenter(center)) {
            return null;
        }
        UUID networkOwner = UUID.fromString(tag.getString("owner"));

        return new HyperNetConnectCardInfo(pos, networkOwner);
    }

}
