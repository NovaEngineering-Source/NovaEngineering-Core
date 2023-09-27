package github.kasuminova.novaeng.common.profiler;

import net.minecraft.network.Packet;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.Tuple;

import java.util.concurrent.ConcurrentHashMap;

public class PacketProfiler {
    public static final ConcurrentHashMap<Class<?>, Tuple<Long, Long>> PACKET_TOTAL_SIZE = new ConcurrentHashMap<>();

    public static boolean enabled = false;

    public static long profilerStartTime = 0;

    public static void onPacketReceived(Packet<?> packet, int packetSize) {
        if (!enabled) {
            return;
        }

        if (PACKET_TOTAL_SIZE.isEmpty()) {
            profilerStartTime = System.currentTimeMillis();
        }

        PACKET_TOTAL_SIZE.compute(packet.getClass(), (key, value) -> value == null
                ? new Tuple<>(1L, (long) packetSize)
                : new Tuple<>(value.getFirst() + 1, value.getSecond() + packetSize));

        if (packet instanceof SPacketUpdateTileEntity) {
            TEUpdatePacketProfiler.onPacketReceived((SPacketUpdateTileEntity) packet, packetSize);
        }
    }
}
