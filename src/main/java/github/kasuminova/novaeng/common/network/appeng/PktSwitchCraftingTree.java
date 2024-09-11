package github.kasuminova.novaeng.common.network.appeng;

import appeng.api.networking.crafting.ICraftingJob;
import appeng.api.networking.security.IActionHost;
import appeng.container.ContainerOpenContext;
import appeng.container.implementations.ContainerCraftConfirm;
import appeng.container.interfaces.IInventorySlotAware;
import appeng.core.sync.GuiBridge;
import appeng.crafting.CraftingJob;
import appeng.crafting.CraftingTreeNode;
import appeng.util.Platform;
import github.kasuminova.novaeng.NovaEngineeringCore;
import github.kasuminova.novaeng.common.CommonProxy;
import github.kasuminova.novaeng.common.container.appeng.ContainerCraftingTree;
import github.kasuminova.novaeng.mixin.ae2.AccessorContainerCraftConfirm;
import hellfirepvp.modularmachinery.ModularMachinery;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.concurrent.FutureTask;

public class PktSwitchCraftingTree implements IMessage, IMessageHandler<PktSwitchCraftingTree, IMessage> {

    public PktSwitchCraftingTree() {
    }

    @Override
    public void fromBytes(final ByteBuf buf) {
    }

    @Override
    public void toBytes(final ByteBuf buf) {
    }

    @Override
    public IMessage onMessage(final PktSwitchCraftingTree message, final MessageContext ctx) {
        EntityPlayerMP player = ctx.getServerHandler().player;
        ModularMachinery.EXECUTE_MANAGER.addSyncTask(() -> {
            if (player.openContainer instanceof ContainerCraftConfirm confirm) {
                processConfirmGUI(confirm, player);
            } else if (player.openContainer instanceof ContainerCraftingTree craftingTree) {
                processTreeGUI(craftingTree, player);
            }
        });
        return null;
    }

    private static void processTreeGUI(final ContainerCraftingTree craftingTree, final EntityPlayerMP player) {
        ContainerOpenContext context = craftingTree.getOpenContext();
        final TileEntity te = context.getTile();
        final Object target = craftingTree.getTarget();
        if (target instanceof IActionHost ah) {
            if (te != null) {
                Platform.openGUI(player, te, context.getSide(), GuiBridge.GUI_CRAFTING_CONFIRM);
            } else if (ah instanceof IInventorySlotAware slotAware) {
                Platform.openGUI(player, slotAware.getInventorySlot(), GuiBridge.GUI_CRAFTING_CONFIRM, slotAware.isBaubleSlot());
            }

            if (player.openContainer instanceof ContainerCraftConfirm confirm) {
                confirm.setJob(craftingTree.getJob());
                confirm.detectAndSendChanges();
            }
        }
    }

    private static void processConfirmGUI(final ContainerCraftConfirm confirm, final EntityPlayerMP player) {
        AccessorContainerCraftConfirm accessor = (AccessorContainerCraftConfirm) confirm;
        ICraftingJob result = accessor.getResult();
        if (result == null) {
            return;
        }

        CraftingTreeNode tree;
        if (!(result instanceof CraftingJob job)) {
            return;
        }
        tree = job.getTree();

        player.openGui(NovaEngineeringCore.MOD_ID, CommonProxy.GuiType.CRAFTING_TREE.ordinal(), player.world, (int) player.posX, (int) player.posY, (int) player.posZ);
        if (!(player.openContainer instanceof ContainerCraftingTree craftingTree)) {
            return;
        }

        craftingTree.setOpenContext(confirm.getOpenContext());
        FutureTask<ICraftingJob> fakeFuture = new FutureTask<>(() -> result);
        craftingTree.setJob(fakeFuture);
        fakeFuture.run();
        NovaEngineeringCore.NET_CHANNEL.sendTo(new PktCraftingTreeData(tree), player);
    }

}
