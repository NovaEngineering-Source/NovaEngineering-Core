package github.kasuminova.novaeng.common.network;

import github.kasuminova.novaeng.common.container.ContainerEFabricatorController;
import github.kasuminova.novaeng.common.tile.efabricator.EFabricatorController;
import hellfirepvp.modularmachinery.ModularMachinery;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PktEFabricatorGUIAction implements IMessage, IMessageHandler<PktEFabricatorGUIAction, IMessage> {

    private Action action = null;

    public PktEFabricatorGUIAction() {
    }

    public PktEFabricatorGUIAction(final Action action) {
        this.action = action;
    }

    @Override
    public void fromBytes(final ByteBuf buf) {
        this.action = Action.values()[buf.readByte()];
    }

    @Override
    public void toBytes(final ByteBuf buf) {
        buf.writeByte(this.action.ordinal());
    }

    @Override
    public IMessage onMessage(final PktEFabricatorGUIAction message, final MessageContext ctx) {
        EntityPlayerMP player = ctx.getServerHandler().player;
        if (!(player.openContainer instanceof ContainerEFabricatorController efGUI)) {
            return null;
        }
        ModularMachinery.EXECUTE_MANAGER.addSyncTask(() -> {
            EFabricatorController owner = efGUI.getOwner();
            switch (message.action) {
                case ENABLE_OVERCLOCKING -> owner.setOverclocked(true);
                case DISABLE_OVERCLOCKING -> owner.setOverclocked(false);
                case ENABLE_ACTIVE_COOLANT -> owner.setActiveCooling(true);
                case DISABLE_ACTIVE_COOLANT -> owner.setActiveCooling(false);
            }
        });
        return null;
    }

    public enum Action {
        
        ENABLE_OVERCLOCKING(),
        DISABLE_OVERCLOCKING(),
        
        ENABLE_ACTIVE_COOLANT(),
        DISABLE_ACTIVE_COOLANT(),
        
    }

}
