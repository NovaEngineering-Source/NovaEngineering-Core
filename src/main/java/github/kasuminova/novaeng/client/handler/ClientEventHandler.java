package github.kasuminova.novaeng.client.handler;

import github.kasuminova.novaeng.NovaEngineeringCore;
import github.kasuminova.novaeng.client.util.TitleUtils;
import github.kasuminova.novaeng.common.profiler.PacketProfiler;
import github.kasuminova.novaeng.common.profiler.TEUpdatePacketProfiler;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("MethodMayBeStatic")
public class ClientEventHandler {
    public static final ClientEventHandler INSTANCE = new ClientEventHandler();

    public static int debugPacketProfilerMessageLimit = 5;
    public static int debugTEPacketProfilerMessageLimit = 5;

    private long clientTick = 0;

    private final List<String> debugMessageCache = new ArrayList<>();
    private boolean debugMessageUpdateRequired = true;

    private ClientEventHandler() {
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.START) {
            return;
        }
        clientTick++;

        if (clientTick % 5 == 0) {
            TitleUtils.checkTitleState();
            debugMessageUpdateRequired = true;
        }
    }

    @SubscribeEvent
    public void onDebugText(RenderGameOverlayEvent.Text event) {
        if (!Minecraft.getMinecraft().gameSettings.showDebugInfo) {
            return;
        }

        if (debugMessageUpdateRequired) {
            debugMessageUpdateRequired = false;
            debugMessageCache.clear();
            debugMessageCache.add("");
            debugMessageCache.add(TextFormatting.BLUE + "[NovaEngineering-Core] Ver: " + NovaEngineeringCore.VERSION);
            debugMessageCache.addAll(PacketProfiler.getProfilerMessages(debugPacketProfilerMessageLimit));
            debugMessageCache.addAll(TEUpdatePacketProfiler.getProfilerMessages(debugTEPacketProfilerMessageLimit));
        }

        List<String> right = event.getRight();
        right.addAll(debugMessageCache);
    }

    @SubscribeEvent
    public void onServerConnected(FMLNetworkEvent.ClientConnectedToServerEvent event) {
        PacketProfiler.enabled = true;
        PacketProfiler.PACKET_TOTAL_SIZE.clear();
        PacketProfiler.TOTAL_RECEIVED_DATA_SIZE.set(0);
        PacketProfiler.profilerStartTime = System.currentTimeMillis();
        TEUpdatePacketProfiler.TE_UPDATE_PACKET_TOTAL_SIZE.clear();

        TitleUtils.setRandomTitleSync(String.format("*%s*", event.getManager().getRemoteAddress()));
    }

    @SubscribeEvent
    public void onServerDisconnected(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        PacketProfiler.enabled = false;
        PacketProfiler.profilerStopTime = System.currentTimeMillis();

        TitleUtils.setRandomTitleSync();
    }
}
