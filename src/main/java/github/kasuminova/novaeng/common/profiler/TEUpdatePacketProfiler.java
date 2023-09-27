package github.kasuminova.novaeng.common.profiler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;

import java.util.concurrent.ConcurrentHashMap;

public class TEUpdatePacketProfiler {
    public static final ConcurrentHashMap<Class<?>, Tuple<Long, Long>> TE_UPDATE_PACKET_TOTAL_SIZE = new ConcurrentHashMap<>();

    public static void onPacketReceived(SPacketUpdateTileEntity packet, int packetSize) {
        WorldClient world = Minecraft.getMinecraft().world;
        BlockPos pos = packet.getPos();
        if (world.isBlockLoaded(pos)) {
            TileEntity te = world.getTileEntity(pos);
            if (te == null) {
                return;
            }

            TE_UPDATE_PACKET_TOTAL_SIZE.compute(te.getClass(), (key, value) -> value == null
                    ? new Tuple<>(1L, (long) packetSize)
                    : new Tuple<>(value.getFirst() + 1, value.getSecond() + packetSize));
        }
    }

}
