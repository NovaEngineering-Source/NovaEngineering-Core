package github.kasuminova.novaeng.common.handler;

import github.kasuminova.mmce.common.concurrent.Action;
import github.kasuminova.novaeng.NovaEngineeringCore;
import github.kasuminova.novaeng.common.hypernet.ComputationCenter;
import github.kasuminova.novaeng.common.hypernet.HyperNetHelper;
import github.kasuminova.novaeng.common.network.PktHyperNetStatus;
import github.kasuminova.novaeng.common.registry.RegistryHyperNet;
import hellfirepvp.modularmachinery.ModularMachinery;
import hellfirepvp.modularmachinery.common.block.BlockController;
import hellfirepvp.modularmachinery.common.block.BlockFactoryController;
import hellfirepvp.modularmachinery.common.container.ContainerBase;
import hellfirepvp.modularmachinery.common.lib.ItemsMM;
import hellfirepvp.modularmachinery.common.machine.DynamicMachine;
import hellfirepvp.modularmachinery.common.tiles.base.TileMultiblockMachineController;
import ink.ikx.mmce.core.AssemblyConfig;
import io.netty.util.internal.shaded.org.jctools.queues.atomic.MpscLinkedAtomicQueue;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;

@SuppressWarnings("MethodMayBeStatic")
public class HyperNetEventHandler {
    private static final MpscLinkedAtomicQueue<Action> TICK_START_ACTIONS = new MpscLinkedAtomicQueue<>();

    @SubscribeEvent
    public void onServerTickStart(final TickEvent.ServerTickEvent event) {
        if (event.side.isClient() || event.phase != TickEvent.Phase.START) {
            return;
        }

        Action action;
        while ((action = TICK_START_ACTIONS.poll()) != null) {
            try {
                action.doAction();
            } catch (Exception e) {
                NovaEngineeringCore.log.warn(e);
            }
        }
    }

    @SubscribeEvent
    public void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        World world = event.getWorld();
        if (world.isRemote) {
            return;
        }

        if (event.getEntityPlayer().isSneaking()) {
            return;
        }

        ItemStack stack = event.getItemStack();
        if (stack.getItem() != RegistryHyperNet.getHyperNetConnectCard()) {
            return;
        }

        TileEntity te = world.getTileEntity(event.getPos());
        if (!(te instanceof TileMultiblockMachineController)) {
            return;
        }

        TileMultiblockMachineController ctrl = (TileMultiblockMachineController) te;
        DynamicMachine foundMachine = ctrl.getFoundMachine();
        if (!RegistryHyperNet.isHyperNetSupported(foundMachine)) {
            return;
        }

        if (RegistryHyperNet.isComputationCenter(foundMachine.getRegistryName())) {
            HyperNetHelper.writeConnectCardInfo(ComputationCenter.from(ctrl), stack);
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.START || event.side == Side.CLIENT) {
            return;
        }

        EntityPlayer player = event.player;
        if (!(player.openContainer instanceof ContainerBase)) {
            return;
        }

        TileEntity te = ((ContainerBase<?>) player.openContainer).getOwner();

        if (!(te instanceof TileMultiblockMachineController)) {
            return;
        }

        TileMultiblockMachineController ctrl = (TileMultiblockMachineController) te;
        if (!HyperNetHelper.supportsHyperNet(ctrl)) {
            return;
        }

        ModularMachinery.EXECUTE_MANAGER.addSyncTask(() -> {
            if (!(event.player instanceof EntityPlayerMP)) {
                return;
            }

            EntityPlayerMP playerMP = (EntityPlayerMP) player;
            World world = playerMP.getEntityWorld();

            if (world.getWorldTime() % 15 != 0) {
                return;
            }

            ComputationCenter center = HyperNetHelper.isComputationCenter(ctrl)
                    ? ComputationCenter.from(ctrl)
                    : getCenter(ctrl);

            if (center != null) {
                NovaEngineeringCore.NET_CHANNEL.sendTo(new PktHyperNetStatus(center), playerMP);
            }
        });
    }

    public static void addTickStartAction(final Action action) {
        TICK_START_ACTIONS.offer(action);
    }

    private static ComputationCenter getCenter(final TileMultiblockMachineController ctrl) {
        NBTTagCompound customData = ctrl.getCustomDataTag();
        if (!customData.hasKey("centerPos")) {
            return null;
        }

        BlockPos centerPos = BlockPos.fromLong(customData.getLong("centerPos"));
        if (!ctrl.getWorld().isBlockLoaded(centerPos)) {
            return null;
        }

        TileEntity te = ctrl.getWorld().getTileEntity(centerPos);
        if (!(te instanceof TileMultiblockMachineController)) {
            return null;
        }

        TileMultiblockMachineController center = (TileMultiblockMachineController) te;
        if (!HyperNetHelper.supportsHyperNet(center) || !HyperNetHelper.isComputationCenter(center)) {
            return null;
        }

        return ComputationCenter.from(center);
    }

}
