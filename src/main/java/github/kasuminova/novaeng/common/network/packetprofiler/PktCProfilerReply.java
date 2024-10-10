package github.kasuminova.novaeng.common.network.packetprofiler;

import github.kasuminova.novaeng.common.profiler.CPacketProfilerData;
import github.kasuminova.novaeng.common.profiler.CPacketProfilerDataProcessor;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.UUID;

public class PktCProfilerReply implements IMessage, IMessageHandler<PktCProfilerReply, IMessage> {

    private UUID eventId = null;
    private CPacketProfilerData reply = null;

    public PktCProfilerReply() {
    }

    public PktCProfilerReply(final UUID eventId, final CPacketProfilerData reply) {
        this.eventId = eventId;
        this.reply = reply;
    }

    @Override
    public void fromBytes(final ByteBuf buf) {
        eventId = new UUID(buf.readLong(), buf.readLong());
        reply = CPacketProfilerData.readFromBuffer(buf);
    }

    @Override
    public void toBytes(final ByteBuf buf) {
        buf.writeLong(eventId.getMostSignificantBits());
        buf.writeLong(eventId.getLeastSignificantBits());
        reply.writeToBuffer(buf);
    }

    @Override
    public IMessage onMessage(final PktCProfilerReply message, final MessageContext ctx) {
        UUID eventId = message.eventId;
        CPacketProfilerData reply = message.reply;
        CPacketProfilerDataProcessor.INSTANCE.receive(eventId, ctx.getServerHandler().player.getGameProfile(), reply);
        return null;
    }

}
