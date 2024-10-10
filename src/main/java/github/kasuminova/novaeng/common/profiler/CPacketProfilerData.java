package github.kasuminova.novaeng.common.profiler;

import io.netty.buffer.ByteBuf;

import javax.annotation.Nonnull;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class CPacketProfilerData implements Comparable<CPacketProfilerData> {

    private final Map<String, PacketData> packets = new HashMap<>();
    private final Map<String, PacketData> tileEntityPackets = new HashMap<>();

    private final float networkBandwidthPerSecond;

    public CPacketProfilerData(final float networkBandwidthPerSecond) {
        this.networkBandwidthPerSecond = networkBandwidthPerSecond;
    }

    public void addPacket(final String packetName, final int count, final long totalSize) {
        packets.putIfAbsent(packetName, new PacketData(count, totalSize));
    }

    public void addTileEntityPacket(final String packetName, final int count, final long totalSize) {
        tileEntityPackets.putIfAbsent(packetName, new PacketData(count, totalSize));
    }

    public Map<String, PacketData> getPackets() {
        return packets;
    }

    public Map<String, PacketData> getTileEntityPackets() {
        return tileEntityPackets;
    }

    public float getNetworkBandwidthPerSecond() {
        return networkBandwidthPerSecond;
    }

    public void writeToBuffer(final ByteBuf buf) {
        buf.writeFloat(networkBandwidthPerSecond);
        buf.writeInt(packets.size());
        for (Map.Entry<String, PacketData> entry : packets.entrySet()) {
            buf.writeShort(entry.getKey().length());
            buf.writeCharSequence(entry.getKey(), StandardCharsets.UTF_8);
            buf.writeInt(entry.getValue().count());
            buf.writeLong(entry.getValue().totalSize());
        }
        buf.writeInt(tileEntityPackets.size());
        for (Map.Entry<String, PacketData> entry : tileEntityPackets.entrySet()) {
            buf.writeShort(entry.getKey().length());
            buf.writeCharSequence(entry.getKey(), StandardCharsets.UTF_8);
            buf.writeInt(entry.getValue().count());
            buf.writeLong(entry.getValue().totalSize());
        }
    }

    public static CPacketProfilerData readFromBuffer(final ByteBuf buf) {
        CPacketProfilerData data = new CPacketProfilerData(buf.readFloat());
        int packetCount = buf.readInt();
        for (int i = 0; i < packetCount; i++) {
            String packetName = buf.readCharSequence(buf.readShort(), StandardCharsets.UTF_8).toString();
            int count = buf.readInt();
            long totalSize = buf.readLong();
            data.addPacket(packetName, count, totalSize);
        }
        int tileEntityPacketCount = buf.readInt();
        for (int i = 0; i < tileEntityPacketCount; i++) {
            String packetName = buf.readCharSequence(buf.readShort(), StandardCharsets.UTF_8).toString();
            int count = buf.readInt();
            long totalSize = buf.readLong();
            data.addTileEntityPacket(packetName, count, totalSize);
        }
        return data;
    }

    @Override
    public int compareTo(@Nonnull final CPacketProfilerData o) {
        return Float.compare(o.networkBandwidthPerSecond, networkBandwidthPerSecond);
    }

    public static final class PacketData implements Comparable<PacketData> {
        private int count;
        private long totalSize;

        public PacketData(int count, long totalSize) {
            this.count = count;
            this.totalSize = totalSize;
        }

        public void merge(PacketData data) {
            this.count += data.count;
            this.totalSize += data.totalSize;
        }

        public int count() {
            return count;
        }

        public long totalSize() {
            return totalSize;
        }

        @Override
        public String toString() {
            return "PacketData[" +
                   "count=" + count + ", " +
                   "totalSize=" + totalSize + ']';
        }

        @Override
        public int compareTo(@Nonnull final PacketData o) {
            return Long.compare(o.totalSize, totalSize);
        }

    }

}
