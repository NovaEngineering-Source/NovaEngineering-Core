package github.kasuminova.novaeng.mixin;

import github.kasuminova.novaeng.NovaEngineeringCore;
import net.minecraftforge.fml.common.Loader;
import zone.rong.mixinbooter.ILateMixinLoader;

import java.util.*;
import java.util.function.BooleanSupplier;

@SuppressWarnings({"unused", "SameParameterValue"})
public class NovaEngCoreLateMixinLoader implements ILateMixinLoader {

    private static final Map<String, BooleanSupplier> MIXIN_CONFIGS = new LinkedHashMap<>();

    static {
        addMixinCFG("mixins.novaeng_core.json");
        addModdedMixinCFG("mixins.novaeng_core_ae.json",                   "appliedenergistics2");
        addModdedMixinCFG("mixins.novaeng_core_armourers_workshop.json",   "armourers_workshop");
        addModdedMixinCFG("mixins.novaeng_core_astralsorcery.json",        "astralsorcery");
        addModdedMixinCFG("mixins.novaeng_core_athenaeum.json",            "athenaeum");
        addModdedMixinCFG("mixins.novaeng_core_avaritia.json",             "avaritia");
        addModdedMixinCFG("mixins.novaeng_core_betterchat.json",           "betterchat");
        addModdedMixinCFG("mixins.novaeng_core_biomesoplenty.json",        "biomesoplenty");
        addModdedMixinCFG("mixins.novaeng_core_bloodmagic.json",           "bloodmagic");
        addModdedMixinCFG("mixins.novaeng_core_botania.json",              "botania");
        addModdedMixinCFG("mixins.novaeng_core_cfm.json",                  "cfm");
        addModdedMixinCFG("mixins.novaeng_core_chisel.json",               "chisel");
        addModdedMixinCFG("mixins.novaeng_core_eio.json",                  "enderioconduits");
        addModdedMixinCFG("mixins.novaeng_core_extrabotany.json",          "extrabotany");
        addModdedMixinCFG("mixins.novaeng_core_fluxnetworks.json",         "fluxnetworks");
        addModdedMixinCFG("mixins.novaeng_core_ic2.json",                  "ic2");
        addModdedMixinCFG("mixins.novaeng_core_igi.json",                  "ingameinfoxml");
        addModdedMixinCFG("mixins.novaeng_core_immersiveengineering.json", "immersiveengineering");
        addModdedMixinCFG("mixins.novaeng_core_legendarytooltips.json",    "legendarytooltips");
        addModdedMixinCFG("mixins.novaeng_core_mek_top.json",              "mekanism", "theoneprobe");
        addModdedMixinCFG("mixins.novaeng_core_mekanism.json",             "mekanism");
        addModdedMixinCFG("mixins.novaeng_core_mets.json",                 "mets");
        addModdedMixinCFG("mixins.novaeng_core_nco.json",                  "nuclearcraft");
        addModdedMixinCFG("mixins.novaeng_core_oreexcavation.json",        "oreexcavation");
        addModdedMixinCFG("mixins.novaeng_core_rgb_chat.json",             "jianghun");
        addModdedMixinCFG("mixins.novaeng_core_scalingguis.json",          "scalingguis");
        addModdedMixinCFG("mixins.novaeng_core_techguns.json",             "techguns");
        addModdedMixinCFG("mixins.novaeng_core_theoneprobe.json",          "theoneprobe");
        addModdedMixinCFG("mixins.novaeng_core_thermaldynamics.json",      "thermaldynamics");
    }

    @Override
    public List<String> getMixinConfigs() {
        return new ArrayList<>(MIXIN_CONFIGS.keySet());
    }

    @Override
    public boolean shouldMixinConfigQueue(final String mixinConfig) {
        BooleanSupplier supplier = MIXIN_CONFIGS.get(mixinConfig);
        if (supplier == null) {
            NovaEngineeringCore.log.warn("Mixin config {} is not found in config map! It will never be loaded.", mixinConfig);
            return false;
        }
        return supplier.getAsBoolean();
    }

    private static boolean modLoaded(final String modID) {
        return Loader.isModLoaded(modID);
    }

    private static void addModdedMixinCFG(final String mixinConfig, final String modID) {
        MIXIN_CONFIGS.put(mixinConfig, () -> modLoaded(modID));
    }

    private static void addModdedMixinCFG(final String mixinConfig, final String modID, final String... modIDs) {
        MIXIN_CONFIGS.put(mixinConfig, () -> modLoaded(modID) && Arrays.stream(modIDs).allMatch(Loader::isModLoaded));
    }

    private static void addMixinCFG(final String mixinConfig) {
        MIXIN_CONFIGS.put(mixinConfig, () -> true);
    }

    private static void addMixinCFG(final String mixinConfig, final BooleanSupplier conditions) {
        MIXIN_CONFIGS.put(mixinConfig, conditions);
    }
}
