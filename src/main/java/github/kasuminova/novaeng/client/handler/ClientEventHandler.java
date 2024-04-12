package github.kasuminova.novaeng.client.handler;

import com.llamalad7.betterchat.gui.GuiBetterChat;
import github.kasuminova.novaeng.NovaEngineeringCore;
import github.kasuminova.novaeng.client.gui.hudcaching.HUDCaching;
import github.kasuminova.novaeng.client.util.TitleUtils;
import github.kasuminova.novaeng.common.profiler.CPacketProfiler;
import github.kasuminova.novaeng.common.profiler.TEUpdatePacketProfiler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Optional;
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
    public void onClientRenderTick(TickEvent.RenderTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        
        if (Loader.isModLoaded("betterchat")) {
            handleBetterChatAnim();
        }
    }

    @Optional.Method(modid = "betterchat")
    protected static void handleBetterChatAnim() {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.gameSettings.hideGUI) {
            return;
        }
        GuiNewChat gui = mc.ingameGUI.getChatGUI();
        if (gui instanceof GuiBetterChat && GuiBetterChat.percentComplete < 1F && HUDCaching.enable) {
            HUDCaching.dirty = true;
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
            debugMessageCache.addAll(CPacketProfiler.getProfilerMessages(debugPacketProfilerMessageLimit));
            debugMessageCache.addAll(TEUpdatePacketProfiler.getProfilerMessages(debugTEPacketProfilerMessageLimit));
        }

        List<String> right = event.getRight();
        right.addAll(debugMessageCache);
    }

    @SubscribeEvent
    public void onServerConnected(FMLNetworkEvent.ClientConnectedToServerEvent event) {
        CPacketProfiler.enabled = true;
        CPacketProfiler.PACKET_TOTAL_SIZE.clear();
        CPacketProfiler.TOTAL_RECEIVED_DATA_SIZE.set(0);
        CPacketProfiler.profilerStartTime = System.currentTimeMillis();
        TEUpdatePacketProfiler.TE_UPDATE_PACKET_TOTAL_SIZE.clear();

        TitleUtils.setRandomTitleSync(String.format("*%s*", event.getManager().getRemoteAddress()));
    }

    @SubscribeEvent
    public void onServerDisconnected(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        CPacketProfiler.enabled = false;
        CPacketProfiler.profilerStopTime = System.currentTimeMillis();

        TitleUtils.setRandomTitleSync();
    }
}
