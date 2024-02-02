package github.kasuminova.novaeng.mixin;

import net.minecraftforge.fml.common.Loader;
import zone.rong.mixinbooter.ILateMixinLoader;

import java.util.Arrays;
import java.util.List;

@SuppressWarnings("unused")
public class NovaEngCoreLateMixinLoader implements ILateMixinLoader {

    @Override
    public List<String> getMixinConfigs() {
        return Arrays.asList(
                "mixins.novaeng_core_ae.json",
                "mixins.novaeng_core_armourers_workshop.json",
                "mixins.novaeng_core_astralsorcery.json",
                "mixins.novaeng_core_avaritia.json",
                "mixins.novaeng_core_biomesoplenty.json",
                "mixins.novaeng_core_bloodmagic.json",
                "mixins.novaeng_core_eio.json",
                "mixins.novaeng_core_fluxnetworks.json",
                "mixins.novaeng_core_igi.json",
                "mixins.novaeng_core_legendarytooltips.json",
                "mixins.novaeng_core_mek_top.json",
                "mixins.novaeng_core_mekanism.json",
                "mixins.novaeng_core_nco.json",
                "mixins.novaeng_core_oreexcavation.json",
                "mixins.novaeng_core_rgb_chat.json",
                "mixins.novaeng_core_techguns.json",
                "mixins.novaeng_core_theoneprobe.json",
                "mixins.novaeng_core_thermaldynamics.json"
        );
    }

    @Override
    public boolean shouldMixinConfigQueue(final String mixinConfig) {
        return switch (mixinConfig) {
            case "mixins.novaeng_core_ae.json" -> Loader.isModLoaded("appliedenergistics2");
            case "mixins.novaeng_core_armourers_workshop.json" -> Loader.isModLoaded("armourers_workshop");
            case "mixins.novaeng_core_astralsorcery.json" -> Loader.isModLoaded("astralsorcery");
            case "mixins.novaeng_core_avaritia.json" -> Loader.isModLoaded("avaritia");
            case "mixins.novaeng_core_biomesoplenty.json" -> Loader.isModLoaded("biomesoplenty");
            case "mixins.novaeng_core_bloodmagic.json" -> Loader.isModLoaded("bloodmagic");
            case "mixins.novaeng_core_eio.json" -> Loader.isModLoaded("enderioconduits");
            case "mixins.novaeng_core_fluxnetworks.json" -> Loader.isModLoaded("fluxnetworks");
            case "mixins.novaeng_core_igi.json" -> Loader.isModLoaded("ingameinfoxml");
            case "mixins.novaeng_core_legendarytooltips.json" -> Loader.isModLoaded("legendarytooltips");
            case "mixins.novaeng_core_mek_top.json" -> Loader.isModLoaded("mekanism") && Loader.isModLoaded("theoneprobe");
            case "mixins.novaeng_core_mekanism.json" -> Loader.isModLoaded("mekanism");
            case "mixins.novaeng_core_nco.json" -> Loader.isModLoaded("nuclearcraft");
            case "mixins.novaeng_core_oreexcavation.json" -> Loader.isModLoaded("oreexcavation");
            case "mixins.novaeng_core_rgb_chat.json" -> Loader.isModLoaded("jianghun");
            case "mixins.novaeng_core_techguns.json" -> Loader.isModLoaded("techguns");
            case "mixins.novaeng_core_theoneprobe.json" -> Loader.isModLoaded("theoneprobe");
            case "mixins.novaeng_core_thermaldynamics.json" -> Loader.isModLoaded("thermaldynamics");
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
