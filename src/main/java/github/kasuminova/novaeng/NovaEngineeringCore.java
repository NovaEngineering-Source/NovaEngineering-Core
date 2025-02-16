package github.kasuminova.novaeng;

import github.kasuminova.novaeng.client.hitokoto.HitokotoAPI;
import github.kasuminova.novaeng.common.CommonProxy;
import github.kasuminova.novaeng.common.command.CommandSPacketProfiler;
import github.kasuminova.novaeng.common.config.NovaEngCoreConfig;
import github.kasuminova.novaeng.common.network.*;
import github.kasuminova.novaeng.common.network.packetprofiler.PktCProfilerReply;
import github.kasuminova.novaeng.common.network.packetprofiler.PktCProfilerRequest;
import github.kasuminova.novaeng.common.profiler.SPacketProfiler;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static github.kasuminova.novaeng.mixin.NovaEngCoreEarlyMixinLoader.LOG;
import static github.kasuminova.novaeng.mixin.NovaEngCoreEarlyMixinLoader.LOG_PREFIX;

@Mod(modid = NovaEngineeringCore.MOD_ID, name = NovaEngineeringCore.MOD_NAME, version = NovaEngineeringCore.VERSION,
        dependencies = "required-after:forge@[14.23.5.2847,);" + 
                       "required-after:modularmachinery@[2.1.0,);" + 
                       "required:theoneprobe@[1.12-1.4.28,);" + 
                       "required:appliedenergistics2@[v0.56.4,);" +
                       "required:ae2fc@[2.6.3-r,);" +
                       "required:configanytime@[2.0,);" + 
                       "required:mixinbooter@[8.0,);" +
                       "required:lumenized@[1.0.2,);",
        acceptedMinecraftVersions = "[1.12, 1.13)",
        acceptableRemoteVersions = "[1.21.0, 1.22.0)"
)
@SuppressWarnings("MethodMayBeStatic")
public class NovaEngineeringCore {
    public static final String MOD_ID = "novaeng_core";
    public static final String MOD_NAME = "Nova Engineering: Core";

    public static final String VERSION = Tags.VERSION;

    public static final String CLIENT_PROXY = "github.kasuminova.novaeng.client.ClientProxy";
    public static final String COMMON_PROXY = "github.kasuminova.novaeng.common.CommonProxy";

    public static final SimpleNetworkWrapper NET_CHANNEL = NetworkRegistry.INSTANCE.newSimpleChannel(MOD_ID);

    public static final ParallelNetworkManager PARALLEL_NETWORK_MANAGER = new ParallelNetworkManager();

    @Mod.Instance(MOD_ID)
    public static NovaEngineeringCore instance = null;
    @SidedProxy(clientSide = CLIENT_PROXY, serverSide = COMMON_PROXY)
    public static CommonProxy proxy = null;
    public static Logger log = LogManager.getLogger(MOD_ID);

    static {
        if (NovaEngCoreConfig.CLIENT.enableNovaEngTitle) {
            Thread thread = new Thread(() -> {
                String hitokoto = HitokotoAPI.getRandomHitokoto();
                if (hitokoto == null || hitokoto.isEmpty()) {
                    return;
                }
                LOG.info(LOG_PREFIX + hitokoto);
            });
            thread.setName("NovaEng Core Hitokoto Initializer");
            thread.start();
        }
    }

    @Mod.EventHandler
    public void construction(FMLConstructionEvent event) {
        proxy.construction();
    }

    @SuppressWarnings("ValueOfIncrementOrDecrementUsed")
    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        event.getModMetadata().version = VERSION;

        byte start = 0;

        NET_CHANNEL.registerMessage(PktHyperNetStatus.class, PktHyperNetStatus.class, start++, Side.CLIENT);
        NET_CHANNEL.registerMessage(PktTerminalGuiData.class, PktTerminalGuiData.class, start++, Side.CLIENT);
        NET_CHANNEL.registerMessage(PktResearchTaskComplete.class, PktResearchTaskComplete.class, start++, Side.CLIENT);
        NET_CHANNEL.registerMessage(PktCellDriveStatusUpdate.class, PktCellDriveStatusUpdate.class, start++, Side.CLIENT);
        NET_CHANNEL.registerMessage(PktEStorageGUIData.class, PktEStorageGUIData.class, start++, Side.CLIENT);
        NET_CHANNEL.registerMessage(PktEFabricatorWorkerStatusUpdate.class, PktEFabricatorWorkerStatusUpdate.class, start++, Side.CLIENT);
        NET_CHANNEL.registerMessage(PktEFabricatorGUIData.class, PktEFabricatorGUIData.class, start++, Side.CLIENT);
        NET_CHANNEL.registerMessage(PktEFabricatorPatternSearchGUIUpdate.class, PktEFabricatorPatternSearchGUIUpdate.class, start++, Side.CLIENT);
        NET_CHANNEL.registerMessage(PktCProfilerRequest.class, PktCProfilerRequest.class, start++, Side.CLIENT);
        NET_CHANNEL.registerMessage(PktECalculatorGUIData.class, PktECalculatorGUIData.class, start++, Side.CLIENT);
        NET_CHANNEL.registerMessage(PktMouseItemUpdate.class, PktMouseItemUpdate.class, start++, Side.CLIENT);

        start = 64;

        NET_CHANNEL.registerMessage(PktResearchTaskProvide.class, PktResearchTaskProvide.class, start++, Side.SERVER);
        NET_CHANNEL.registerMessage(PktResearchTaskReset.class, PktResearchTaskReset.class, start++, Side.SERVER);
        NET_CHANNEL.registerMessage(PktResearchTaskProvideCreative.class, PktResearchTaskProvideCreative.class, start++, Side.SERVER);
        NET_CHANNEL.registerMessage(PktPatternTermUploadPattern.class, PktPatternTermUploadPattern.class, start++, Side.SERVER);
        NET_CHANNEL.registerMessage(PktEFabricatorGUIAction.class, PktEFabricatorGUIAction.class, start++, Side.SERVER);
        NET_CHANNEL.registerMessage(PktEFabricatorPatternSearchGUIAction.class, PktEFabricatorPatternSearchGUIAction.class, start++, Side.SERVER);
        NET_CHANNEL.registerMessage(PktCProfilerReply.class, PktCProfilerReply.class, start++, Side.SERVER);
        NET_CHANNEL.registerMessage(PktGeocentricDrillControl.class, PktGeocentricDrillControl.class, start++, Side.SERVER);

        proxy.preInit();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init();
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
//        PARALLEL_NETWORK_MANAGER.init();
        proxy.postInit();
    }

    @Mod.EventHandler
    public void loadComplete(FMLLoadCompleteEvent event) {
        proxy.loadComplete();
    }

    @Mod.EventHandler
    public void onServerStart(FMLServerStartingEvent event) {
        event.registerServerCommand(CommandSPacketProfiler.INSTANCE);
    }

    @Mod.EventHandler
    public void onServerStopping(FMLServerStoppingEvent event) {
        log.info(TextFormatting.BLUE + "服务器正在关闭，正在生成网络包报告。");
        for (final String message : SPacketProfiler.getProfilerMessages()) {
            log.info(message);
        }
        log.info(TextFormatting.BLUE + "所有玩家的完整网络包报告：");
        for (final String message : SPacketProfiler.getFullProfilerMessages()) {
            log.info(message);
        }
    }

}
