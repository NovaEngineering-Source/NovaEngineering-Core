package github.kasuminova.novaeng.mixin;

import net.minecraftforge.fml.common.Loader;
import zone.rong.mixinbooter.ILateMixinLoader;

import java.util.Collections;
import java.util.List;

public class NovaEngineeringCoreMixinLoader implements ILateMixinLoader {
    @Override
    public List<String> getMixinConfigs() {
        return Collections.singletonList("mixins.novaeng_core.json");
    }

    @Override
    public boolean shouldMixinConfigQueue(final String mixinConfig) {
        return Loader.isModLoaded("nuclearcraft") && Loader.isModLoaded("appliedenergistics2");
    }
}
