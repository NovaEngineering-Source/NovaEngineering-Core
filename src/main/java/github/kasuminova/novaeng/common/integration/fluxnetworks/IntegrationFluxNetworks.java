package github.kasuminova.novaeng.common.integration.fluxnetworks;

import github.kasuminova.novaeng.NovaEngineeringCore;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Optional;
import sonar.fluxnetworks.common.handler.TileEntityHandler;

public class IntegrationFluxNetworks {

    @Optional.Method(modid = "fluxnetworks")
    public static void preInit() {
        if (Loader.isModLoaded("mekanism")) {
            initMekanismIntegration();
        }
    }

    @Optional.Method(modid = "mekanism")
    public static void initMekanismIntegration() {
        //在列表头部插入适配器，保证不被其他类型覆盖结果。
        //Insert adapters in the head of the list to ensure that the results are not overwritten by other types.
        TileEntityHandler.tileEnergyHandlers.add(0, MekanismEnergyHandler.INSTANCE);
        NovaEngineeringCore.log.info("Mekanism <===> FluxNetworks is initialized!");
    }

}
