package github.kasuminova.novaeng.common;

import github.kasuminova.novaeng.common.handler.HyperNetEventHandler;
import github.kasuminova.novaeng.common.integration.IntegrationCRT;
import net.minecraftforge.common.MinecraftForge;

@SuppressWarnings("MethodMayBeStatic")
public class CommonProxy {

    public void preInit() {
        MinecraftForge.EVENT_BUS.register(new IntegrationCRT());
        MinecraftForge.EVENT_BUS.register(new HyperNetEventHandler());
    }

    public void init() {

    }

    public void postInit() {

    }

}
