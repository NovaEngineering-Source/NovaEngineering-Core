package github.kasuminova.novaeng.common.network;

import github.kasuminova.novaeng.common.container.ContainerGeocentricDrill;
import github.kasuminova.novaeng.common.tile.machine.GeocentricDrillController;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.nio.charset.StandardCharsets;

public class PktGeocentricDrillControl implements IMessage, IMessageHandler<PktGeocentricDrillControl, IMessage> {

    private Type type;
    private int depth;
    private String accelerateOre;

    public PktGeocentricDrillControl() {
    }

    public PktGeocentricDrillControl(Type type, int depth) {
        this.type = type;
        this.depth = depth;
    }

    public PktGeocentricDrillControl(Type type, String accelerateOre) {
        this.type = type;
        this.accelerateOre = accelerateOre;
    }

    @Override
    public void fromBytes(final ByteBuf buf) {
        type = Type.values()[buf.readByte()];
        if (type == Type.SET_TARGET_DEPTH) {
            depth = buf.readShort();
        } else if (type == Type.ADD_ACCELERATE_ORE || type == Type.REMOVE_ACCELERATE_ORE) {
            byte len = buf.readByte();
            accelerateOre = buf.readCharSequence(len, StandardCharsets.UTF_8).toString();
        }
    }

    @Override
    public void toBytes(final ByteBuf buf) {
        buf.writeByte(type.ordinal());
        if (type == Type.SET_TARGET_DEPTH) {
            buf.writeShort(depth);
        } else if (type == Type.ADD_ACCELERATE_ORE || type == Type.REMOVE_ACCELERATE_ORE) {
            buf.writeByte(accelerateOre.length());
            buf.writeCharSequence(accelerateOre, StandardCharsets.UTF_8);
        }
    }

    @Override
    public IMessage onMessage(final PktGeocentricDrillControl message, final MessageContext ctx) {
        if (ctx.getServerHandler().player.openContainer instanceof ContainerGeocentricDrill containerGeocentricDrill) {
            GeocentricDrillController drill = containerGeocentricDrill.getOwner();
            switch (message.type) {
                case SET_TARGET_DEPTH -> drill.setTargetDepth(message.depth);
                case ADD_ACCELERATE_ORE -> drill.addAccelerateOre(message.accelerateOre);
                case REMOVE_ACCELERATE_ORE -> drill.removeAccelerateOre(message.accelerateOre);
            }
        }
        return null;
    }

    public enum Type {
        SET_TARGET_DEPTH,
        ADD_ACCELERATE_ORE,
        REMOVE_ACCELERATE_ORE,
    }

}
