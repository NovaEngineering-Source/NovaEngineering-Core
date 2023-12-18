package github.kasuminova.novaeng.mixin;

import net.minecraftforge.fml.common.Loader;
import zone.rong.mixinbooter.ILateMixinLoader;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class NovaEngineeringCoreMixinLoader implements ILateMixinLoader {
    @Override
    public List<String> getMixinConfigs() {
        return Arrays.asList("mixins.novaeng_core.json", "mixins.novaeng_cleanroom_compatibility.json");
    }

    @Override
    public boolean shouldMixinConfigQueue(final String mixinConfig) {
        switch (mixinConfig) {
            case "mixins.novaeng_core.json" -> {
                return Loader.isModLoaded("nuclearcraft") && Loader.isModLoaded("appliedenergistics2");
            }
            case "mixins.novaeng_cleanroom_compatibility.json" -> {
                return Loader.isModLoaded("nuclearcraft") && Loader.isModLoaded("touhoulittlemaid");
            }
        }
        return false;
    }
}
