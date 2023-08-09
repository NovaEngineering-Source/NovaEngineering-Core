package github.kasuminova.novaeng.client;


import github.kasuminova.novaeng.common.CommonProxy;
import github.kasuminova.novaeng.client.handler.HyperNetClientEventHandler;
import net.minecraftforge.common.MinecraftForge;

public class ClientProxy extends CommonProxy {
    @Override
    public void preInit() {
        super.preInit();
        MinecraftForge.EVENT_BUS.register(new HyperNetClientEventHandler());
    }

    @Override
    public void init() {
        super.init();
    }

    @Override
    public void postInit() {
        super.postInit();
    }
}
