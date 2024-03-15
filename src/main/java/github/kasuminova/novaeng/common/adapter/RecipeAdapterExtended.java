package github.kasuminova.novaeng.common.adapter;

import github.kasuminova.novaeng.common.adapter.astralsorcery.AdapterStarlightInfuser;
import github.kasuminova.novaeng.common.adapter.botania.AdapterBotaniaManaPool;
import github.kasuminova.novaeng.common.adapter.mc.AdapterMCFurnaceWithExp;
import github.kasuminova.novaeng.common.adapter.nco.AdapterNCOElectrolyzer;
import github.kasuminova.novaeng.common.adapter.nco.AdapterNCOPressurizer;
import hellfirepvp.modularmachinery.common.base.Mods;
import hellfirepvp.modularmachinery.common.lib.RegistriesMM;
import net.minecraftforge.fml.common.Loader;

public class RecipeAdapterExtended {

    public static void registerAdapter() {
        RegistriesMM.ADAPTER_REGISTRY.register(new AdapterMCFurnaceWithExp());
        if (Mods.NUCLEARCRAFT_OVERHAULED.isPresent()) {
            RegistriesMM.ADAPTER_REGISTRY.register(new AdapterNCOPressurizer());
            RegistriesMM.ADAPTER_REGISTRY.register(new AdapterNCOElectrolyzer());
        }
        if (Loader.isModLoaded("thermalexpansion") && Mods.MEKANISM.isPresent() && Mods.NUCLEARCRAFT_OVERHAULED.isPresent() && Mods.IC2.isPresent()) {
            RegistriesMM.ADAPTER_REGISTRY.register(new AdapterShredder());
        }
        if (Mods.BOTANIA.isPresent()) {
            RegistriesMM.ADAPTER_REGISTRY.register(new AdapterBotaniaManaPool());
        }
        if (Mods.ASTRAL_SORCERY.isPresent()) {
            RegistriesMM.ADAPTER_REGISTRY.register(new AdapterStarlightInfuser());
        }
    }

}
