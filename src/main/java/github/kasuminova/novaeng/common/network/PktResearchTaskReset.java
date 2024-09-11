package github.kasuminova.novaeng.common.network;

import github.kasuminova.novaeng.common.container.ContainerHyperNetTerminal;
import github.kasuminova.novaeng.common.hypernet.old.ComputationCenter;
import github.kasuminova.novaeng.common.hypernet.old.HyperNetTerminal;
import github.kasuminova.novaeng.common.hypernet.old.research.ResearchStation;
import github.kasuminova.novaeng.common.tile.TileHyperNetTerminal;
import hellfirepvp.modularmachinery.ModularMachinery;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PktResearchTaskReset implements IMessage, IMessageHandler<PktResearchTaskReset, IMessage> {
    public PktResearchTaskReset() {

    }

    @Override
    public void fromBytes(final ByteBuf buf) {

    }

    @Override
    public void toBytes(final ByteBuf buf) {

    }

    @Override
    public IMessage onMessage(final PktResearchTaskReset message, final MessageContext ctx) {
        EntityPlayerMP player = ctx.getServerHandler().player;
        Container container = player.openContainer;
        if (!(container instanceof ContainerHyperNetTerminal)) {
            return null;
        }

        TileHyperNetTerminal terminal = ((ContainerHyperNetTerminal) container).getOwner();

        HyperNetTerminal nodeProxy = terminal.getNodeProxy();
        ComputationCenter center = nodeProxy.getCenter();
        if (center == null) {
            return null;
        }

        for (final ResearchStation station : center.getNode(ResearchStation.class)) {
            ModularMachinery.EXECUTE_MANAGER.addSyncTask(() -> station.provideTask(null, null));
        }
        return null;
    }
}
