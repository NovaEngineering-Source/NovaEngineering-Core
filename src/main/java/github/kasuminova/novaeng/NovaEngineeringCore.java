package github.kasuminova.novaeng;

import github.kasuminova.novaeng.common.CommonProxy;
import github.kasuminova.novaeng.common.network.PktHyperNetStatus;
import github.kasuminova.novaeng.common.network.PktResearchTaskProvide;
import github.kasuminova.novaeng.common.network.PktResearchTaskReset;
import github.kasuminova.novaeng.common.network.PktTerminalGuiData;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.logging.log4j.Logger;

@Mod(modid = NovaEngineeringCore.MOD_ID, name = NovaEngineeringCore.MOD_NAME, version = NovaEngineeringCore.VERSION,
        dependencies = "required-after:forge@[14.23.5.2847,);" +
                "required-after:modularmachinery@[1.11.1,);" +
                "required-after:theoneprobe@[1.12-1.4.28,);",
        acceptedMinecraftVersions = "[1.12, 1.13)"
)
@SuppressWarnings("MethodMayBeStatic")
public class NovaEngineeringCore {
    public static final String MOD_ID = "novaeng_core";
    public static final String MOD_NAME = "Nova Engineering: Core";

    public static final String VERSION = "1.5.2";

    public static final String CLIENT_PROXY = "github.kasuminova.novaeng.client.ClientProxy";
    public static final String COMMON_PROXY = "github.kasuminova.novaeng.common.CommonProxy";

    public static final SimpleNetworkWrapper NET_CHANNEL = NetworkRegistry.INSTANCE.newSimpleChannel(MOD_ID);

    @Mod.Instance(MOD_ID)
    public static NovaEngineeringCore instance = null;
    @SidedProxy(clientSide = CLIENT_PROXY, serverSide = COMMON_PROXY)
    public static CommonProxy proxy = null;
    public static Logger log = null;

    @Mod.EventHandler
    public void construction(FMLConstructionEvent event) {
        proxy.construction();
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        event.getModMetadata().version = VERSION;
        log = event.getModLog();

        NET_CHANNEL.registerMessage(PktHyperNetStatus.class, PktHyperNetStatus.class, 0, Side.CLIENT);
        NET_CHANNEL.registerMessage(PktTerminalGuiData.class, PktTerminalGuiData.class, 1, Side.CLIENT);

        NET_CHANNEL.registerMessage(PktResearchTaskProvide.class, PktResearchTaskProvide.class, 100, Side.SERVER);
        NET_CHANNEL.registerMessage(PktResearchTaskReset.class, PktResearchTaskReset.class, 101, Side.SERVER);

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
}
