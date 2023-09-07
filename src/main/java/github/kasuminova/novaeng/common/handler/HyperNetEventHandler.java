package github.kasuminova.novaeng.common.handler;

import github.kasuminova.mmce.common.event.Phase;
import github.kasuminova.mmce.common.event.machine.MachineTickEvent;
import github.kasuminova.mmce.common.util.concurrent.Action;
import github.kasuminova.novaeng.NovaEngineeringCore;
import github.kasuminova.novaeng.common.crafttweaker.hypernet.HyperNetHelper;
import github.kasuminova.novaeng.common.hypernet.ComputationCenter;
import github.kasuminova.novaeng.common.hypernet.NetNode;
import github.kasuminova.novaeng.common.hypernet.NetNodeCache;
import github.kasuminova.novaeng.common.hypernet.misc.ConnectResult;
import github.kasuminova.novaeng.common.network.PktHyperNetStatus;
import github.kasuminova.novaeng.common.network.PktTerminalGuiData;
import github.kasuminova.novaeng.common.registry.RegistryHyperNet;
import github.kasuminova.novaeng.common.tile.TileHyperNetTerminal;
import hellfirepvp.modularmachinery.common.container.ContainerBase;
import hellfirepvp.modularmachinery.common.machine.DynamicMachine;
import hellfirepvp.modularmachinery.common.tiles.TileFactoryController;
import hellfirepvp.modularmachinery.common.tiles.base.TileMultiblockMachineController;
import io.netty.util.internal.shaded.org.jctools.queues.atomic.MpscLinkedAtomicQueue;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;

@SuppressWarnings("MethodMayBeStatic")
public class HyperNetEventHandler {
    public static final HyperNetEventHandler INSTANCE = new HyperNetEventHandler();

    private static final MpscLinkedAtomicQueue<Action> TICK_START_ACTIONS = new MpscLinkedAtomicQueue<>();

    public static void addTickStartAction(final Action action) {
        TICK_START_ACTIONS.offer(action);
    }

    private HyperNetEventHandler() {

    }

    private static ComputationCenter getCenterFromNode(final TileMultiblockMachineController ctrl) {
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

    private static void sendResultMessage(final ConnectResult result, final EntityPlayer player, final ComputationCenter center, final NetNode cached) {
        switch (result) {
            case SUCCESS:
                player.sendMessage(new TextComponentTranslation(
                        "novaeng.hypernet.connect.result.success",
                        center.getConnectedMachineryCount(), center.getType().getMaxConnections()
                ));
                break;
            case UNKNOWN_CENTER:
                player.sendMessage(new TextComponentTranslation(
                        "novaeng.hypernet.connect.result.unknown_center"));
                break;
            case CENTER_NOT_WORKING:
                player.sendMessage(new TextComponentTranslation(
                        "novaeng.hypernet.connect.result.center_not_working"));
                break;
            case UNSUPPORTED_NODE:
                player.sendMessage(new TextComponentTranslation(
                        "novaeng.hypernet.connect.result.unsupported_node"));
                break;
            case CENTER_REACHED_CONNECTION_LIMIT:
                player.sendMessage(new TextComponentTranslation(
                        "novaeng.hypernet.connect.result.center_reached_connection_limit",
                        center.getType().getMaxConnections()
                ));
                break;
            case NODE_TYPE_REACHED_MAX_PRESENCES:
                player.sendMessage(new TextComponentTranslation(
                        "novaeng.hypernet.connect.result.node_type_reached_max_presences",
                        cached.getNodeMaxPresences()
                ));
                break;
        }
    }

    @SubscribeEvent
    public void onMachineTick(final MachineTickEvent event) {
        if (event.phase != Phase.START) {
            return;
        }

        TileMultiblockMachineController ctrl = event.getController();
        DynamicMachine foundMachine = ctrl.getFoundMachine();
        if (!RegistryHyperNet.isHyperNetSupported(foundMachine)) {
            return;
        }

        NetNode cached = NetNodeCache.getCache(ctrl, RegistryHyperNet.getNodeType(foundMachine));
        if (cached != null) {
            cached.onMachineTick();
        }
    }

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

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        World world = event.getWorld();
        if (world.isRemote) {
            return;
        }

        EntityPlayer player = event.getEntityPlayer();

        if (player.isSneaking()) {
            return;
        }

        TileEntity te = world.getTileEntity(event.getPos());
        if (!(te instanceof TileMultiblockMachineController)) {
            return;
        }

        if (te instanceof TileHyperNetTerminal && player instanceof EntityPlayerMP) {
            TileHyperNetTerminal terminal = (TileHyperNetTerminal) te;
            NovaEngineeringCore.NET_CHANNEL.sendTo(new PktTerminalGuiData(terminal), (EntityPlayerMP) player);
            return;
        }

        ItemStack stack = event.getItemStack();
        if (stack.getItem() != RegistryHyperNet.getHyperNetConnectCard()) {
            return;
        }

        TileMultiblockMachineController ctrl = (TileMultiblockMachineController) te;
        DynamicMachine foundMachine = ctrl.getFoundMachine();
        if (!RegistryHyperNet.isHyperNetSupported(foundMachine)) {
            return;
        }

        event.setCanceled(true);

        if (RegistryHyperNet.isComputationCenter(foundMachine.getRegistryName())) {
            HyperNetHelper.writeConnectCardInfo(ComputationCenter.from(ctrl), stack);
            return;
        }

        tryConnectToCenter(ctrl, stack, world, player, foundMachine);
    }

    private static void tryConnectToCenter(final TileMultiblockMachineController ctrl, final ItemStack stack, final World world, final EntityPlayer player, final DynamicMachine foundMachine) {
        BlockPos centerPos = HyperNetHelper.readConnectCardInfo(ctrl, stack);
        if (centerPos == null || !world.isBlockLoaded(centerPos)) {
            player.sendMessage(new TextComponentTranslation(
                    "novaeng.hypernet.connect.result.unknown_center"));
            return;
        }

        TileEntity centerTE = world.getTileEntity(centerPos);
        TileFactoryController centerCtrl = centerTE instanceof TileFactoryController
                ? (TileFactoryController) centerTE
                : null;
        if (centerCtrl == null || !HyperNetHelper.isComputationCenter(centerCtrl)) {
            player.sendMessage(new TextComponentTranslation(
                    "novaeng.hypernet.connect.result.unknown_center"));
            return;
        }

        NetNode cached = NetNodeCache.getCache(ctrl, RegistryHyperNet.getNodeType(foundMachine));
        ComputationCenter center = ComputationCenter.from(centerCtrl);
        if (cached == null || center == null) {
            player.sendMessage(new TextComponentTranslation(
                    "novaeng.hypernet.connect.result.unknown_center"));
            return;
        }

        ConnectResult result = cached.connectTo(centerPos);
        sendResultMessage(result, player, center, cached);
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.START || event.side == Side.CLIENT) {
            return;
        }
        if (!(event.player instanceof EntityPlayerMP)) {
            return;
        }

        EntityPlayerMP player = (EntityPlayerMP) event.player;
        World world = player.getEntityWorld();
        if (world.getWorldTime() % 15 != 0) {
            return;
        }

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

        if (ctrl instanceof TileHyperNetTerminal) {
            TileHyperNetTerminal terminal = (TileHyperNetTerminal) ctrl;
            NovaEngineeringCore.NET_CHANNEL.sendTo(new PktTerminalGuiData(terminal), player);
        }

        ComputationCenter center = HyperNetHelper.isComputationCenter(ctrl)
                ? ComputationCenter.from(ctrl)
                : getCenterFromNode(ctrl);

        if (center != null) {
            NovaEngineeringCore.NET_CHANNEL.sendTo(new PktHyperNetStatus(center), player);
        }
    }

}
