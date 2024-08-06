package github.kasuminova.novaeng.common.network;

import appeng.api.networking.IGridNode;
import appeng.api.networking.IMachineSet;
import appeng.container.implementations.ContainerPatternEncoder;
import appeng.container.slot.SlotRestrictedInput;
import appeng.me.GridAccessException;
import github.kasuminova.novaeng.common.tile.efabricator.EFabricatorMEChannel;
import github.kasuminova.novaeng.mixin.ae2.AccessorContainerPatternEncoder;
import hellfirepvp.modularmachinery.ModularMachinery;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PktPatternTermUploadPattern implements IMessage, IMessageHandler<PktPatternTermUploadPattern, IMessage> {

    @Override
    public void fromBytes(final ByteBuf buf) {
    }

    @Override
    public void toBytes(final ByteBuf buf) {
    }

    @Override
    public IMessage onMessage(final PktPatternTermUploadPattern message, final MessageContext ctx) {
        EntityPlayerMP player = ctx.getServerHandler().player;
        ModularMachinery.EXECUTE_MANAGER.addSyncTask(() -> {
            Container container = player.openContainer;
            if (!(container instanceof ContainerPatternEncoder encoder)) {
                return;
            }

            SlotRestrictedInput patternSlotOUT = ((AccessorContainerPatternEncoder) encoder).getPatternSlotOUT();
            ItemStack patternStack = patternSlotOUT.getStack();
            if (patternStack.isEmpty()) {
                return;
            }

            try {
                IMachineSet channelNodes = encoder.getPart().getProxy().getGrid().getMachines(EFabricatorMEChannel.class);
                for (final IGridNode channelNode : channelNodes) {
                    EFabricatorMEChannel channel = (EFabricatorMEChannel) channelNode.getMachine();
                    if (channel.insertPattern(patternStack)) {
                        patternSlotOUT.putStack(ItemStack.EMPTY);
                        break;
                    }
                }
            } catch (GridAccessException ignored) {
            }
        });
        return null;
    }

}
