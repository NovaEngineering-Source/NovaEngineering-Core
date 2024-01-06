package github.kasuminova.novaeng.mixin;

import net.minecraftforge.fml.common.Loader;
import zone.rong.mixinbooter.ILateMixinLoader;

import java.util.Arrays;
import java.util.List;

@SuppressWarnings("unused")
public class NovaEngCoreLateMixinLoader implements ILateMixinLoader {

    @Override
    public List<String> getMixinConfigs() {
        return Arrays.asList("mixins.novaeng_core.json","mixins.novaeng_core_ae.json","mixins.novaeng_core_igi.json", "mixins.novaeng_core_nco.json", "mixins.novaeng_core_rgb_chat.json");
    }

    @Override
    public boolean shouldMixinConfigQueue(final String mixinConfig) {
        return switch (mixinConfig) {
            case "mixins.novaeng_core_ae.json" -> Loader.isModLoaded("appliedenergistics2");
            case "mixins.novaeng_core_igi.json" -> Loader.isModLoaded("ingameinfoxml");
            case "mixins.novaeng_core_nco.json" -> Loader.isModLoaded("nuclearcraft");
            case "mixins.novaeng_core_rgb_chat.json" -> Loader.isModLoaded("jianghun");
            default -> true;
        };
    }

    public static boolean isCleanroomLoader() {
        try {
            Class.forName("com.cleanroommc.boot.Main");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
