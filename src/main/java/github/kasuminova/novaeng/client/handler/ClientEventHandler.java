package github.kasuminova.novaeng.client.handler;

import github.kasuminova.novaeng.NovaEngineeringCore;
import github.kasuminova.novaeng.client.util.TitleUtils;
import github.kasuminova.novaeng.common.config.NovaEngCoreConfig;
import github.kasuminova.novaeng.common.profiler.CPacketProfiler;
import github.kasuminova.novaeng.common.profiler.TEUpdatePacketProfiler;
import github.kasuminova.novaeng.mixin.minecraft.AccessorParticleManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("MethodMayBeStatic")
public class ClientEventHandler {
    public static final ClientEventHandler INSTANCE = new ClientEventHandler();

    public static int debugPacketProfilerMessageLimit   = 5;
    public static int debugTEPacketProfilerMessageLimit = 5;

    private long clientTick = 0;

    private final List<String> debugMessageCache          = new ArrayList<>();
    private       boolean      debugMessageUpdateRequired = true;

    private ClientEventHandler() {
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.START) {
            return;
        }
        clientTick++;

        if (clientTick % 5 == 0) {
            if (NovaEngCoreConfig.CLIENT.enableNovaEngTitle) {
                TitleUtils.checkTitleState();
            }
            debugMessageUpdateRequired = true;
        }

        if (clientTick % 20 == 0) {
            checkParticleEffects();
        }
    }

    private static void checkParticleEffects() {
        final ParticleManager effectRenderer = Minecraft.getMinecraft().effectRenderer;
        if (effectRenderer == null) {
            NovaEngineeringCore.log.warn("Particle effect renderer is null.");
            return;
        }

        final AccessorParticleManager accessor = (AccessorParticleManager) effectRenderer;
        long totalParticles = getTotalParticles(accessor);

        if (totalParticles > 50000) {
            effectRenderer.clearEffects(Minecraft.getMinecraft().world);
            NovaEngineeringCore.log.warn(
                    "Particle effect renderer has been cleared due to too many particles (Current: {}, Limit: {}).",
                    totalParticles, 50000
            );
        }
    }

    private static long getTotalParticles(final AccessorParticleManager accessor) {
        return Arrays.stream(accessor.getFxLayers())
                     .flatMapToLong(layers -> Arrays.stream(layers).mapToLong(ArrayDeque::size))
                     .sum();
    }

    @SubscribeEvent
    public void onDebugText(RenderGameOverlayEvent.Text event) {
        if (!Minecraft.getMinecraft().gameSettings.showDebugInfo) {
            return;
        }

        if (debugMessageUpdateRequired) {
            final ParticleManager effectRenderer = Minecraft.getMinecraft().effectRenderer;
            if (effectRenderer == null) {
                NovaEngineeringCore.log.warn("Particle effect renderer is null.");
            }

            debugMessageUpdateRequired = false;
            debugMessageCache.clear();
            debugMessageCache.add("");
            debugMessageCache.add(TextFormatting.BLUE + "[NovaEngineering-Core] Ver: " + NovaEngineeringCore.VERSION);

            if (effectRenderer != null) {
                debugMessageCache.add(TextFormatting.GREEN + "Particles: " + TextFormatting.DARK_GREEN + getTotalParticles((AccessorParticleManager) effectRenderer));
            }

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
