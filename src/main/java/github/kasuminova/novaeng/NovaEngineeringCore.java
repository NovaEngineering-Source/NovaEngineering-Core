package github.kasuminova.novaeng;

import github.kasuminova.novaeng.common.CommonProxy;
import github.kasuminova.novaeng.common.command.CommandSPacketProfiler;
import github.kasuminova.novaeng.common.network.*;
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

@Mod(modid = NovaEngineeringCore.MOD_ID, name = NovaEngineeringCore.MOD_NAME, version = NovaEngineeringCore.VERSION,
        dependencies = "required-after:forge@[14.23.5.2847,);" +
                "required-after:modularmachinery@[1.11.1,);" +
                "required-after:theoneprobe@[1.12-1.4.28,);" +
                "required-after:appliedenergistics2@[v0.56.3,);",
        acceptedMinecraftVersions = "[1.12, 1.13)"
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

    @Mod.EventHandler
    public void construction(FMLConstructionEvent event) {
        proxy.construction();
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        event.getModMetadata().version = VERSION;

        NET_CHANNEL.registerMessage(PktHyperNetStatus.class, PktHyperNetStatus.class, 0, Side.CLIENT);
        NET_CHANNEL.registerMessage(PktTerminalGuiData.class, PktTerminalGuiData.class, 1, Side.CLIENT);
        NET_CHANNEL.registerMessage(PktResearchTaskComplete.class, PktResearchTaskComplete.class, 2, Side.CLIENT);
        NET_CHANNEL.registerMessage(PktCellDriveStatusUpdate.class, PktCellDriveStatusUpdate.class, 3, Side.CLIENT);
        NET_CHANNEL.registerMessage(PktEStorageControllerGUIData.class, PktEStorageControllerGUIData.class, 4, Side.CLIENT);

        NET_CHANNEL.registerMessage(PktResearchTaskProvide.class, PktResearchTaskProvide.class, 100, Side.SERVER);
        NET_CHANNEL.registerMessage(PktResearchTaskReset.class, PktResearchTaskReset.class, 101, Side.SERVER);
        NET_CHANNEL.registerMessage(PktResearchTaskProvideCreative.class, PktResearchTaskProvideCreative.class, 102, Side.SERVER);

        proxy.preInit();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init();
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
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
