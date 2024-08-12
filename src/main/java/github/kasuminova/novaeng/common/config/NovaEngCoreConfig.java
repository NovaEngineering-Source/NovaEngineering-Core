package github.kasuminova.novaeng.common.config;

import com.cleanroommc.configanytime.ConfigAnytime;
import github.kasuminova.novaeng.NovaEngineeringCore;
import github.kasuminova.stellarcore.common.config.StellarCoreConfig;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = NovaEngineeringCore.MOD_ID)
@Config(modid = NovaEngineeringCore.MOD_ID, name = NovaEngineeringCore.MOD_ID)
public class NovaEngCoreConfig {

    @Config.Name("Client")
    public static final Client CLIENT = new Client();

    public static class Client {

        @Config.RequiresMcRestart
        @Config.Name("EnableNovaEngTitle")
        public boolean enableNovaEngTitle = true;

    }

    /*
        必须在最后加载。
    */
    static {
        ConfigAnytime.register(StellarCoreConfig.class);
    }

    @SubscribeEvent
    public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.getModID().equals(NovaEngineeringCore.MOD_ID)) {
            ConfigManager.sync(NovaEngineeringCore.MOD_ID, Config.Type.INSTANCE);
        }
    }

}
