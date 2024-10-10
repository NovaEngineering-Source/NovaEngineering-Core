package github.kasuminova.novaeng.common.network.packetprofiler;

import github.kasuminova.novaeng.common.profiler.CPacketProfiler;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.UUID;

public class PktCProfilerRequest implements IMessage, IMessageHandler<PktCProfilerRequest, PktCProfilerReply> {

    private UUID eventId = null;
    private int limit = 0;

    public PktCProfilerRequest() {
    }

    public PktCProfilerRequest(final UUID eventId, final int limit) {
        this.eventId = eventId;
        this.limit = limit;
    }

    @Override
    public void fromBytes(final ByteBuf buf) {
        limit = buf.readInt();
        eventId = new UUID(buf.readLong(), buf.readLong());
    }

    @Override
    public void toBytes(final ByteBuf buf) {
        buf.writeInt(limit);
        buf.writeLong(eventId.getMostSignificantBits());
        buf.writeLong(eventId.getLeastSignificantBits());
    }

    @Override
    public PktCProfilerReply onMessage(final PktCProfilerRequest message, final MessageContext ctx) {
        UUID eventId = message.eventId;
        int limit = message.limit;
        return new PktCProfilerReply(eventId, CPacketProfiler.getProfilerData(limit));
    }

}
