package github.kasuminova.novaeng.common.handler;

import github.kasuminova.mmce.common.event.Phase;
import github.kasuminova.mmce.common.event.machine.MachineTickEvent;
import github.kasuminova.novaeng.common.hypernet.NetNode;
import github.kasuminova.novaeng.common.hypernet.NetNodeCache;
import github.kasuminova.novaeng.common.registry.RegistryHyperNet;
import hellfirepvp.modularmachinery.common.machine.DynamicMachine;
import hellfirepvp.modularmachinery.common.tiles.base.TileMultiblockMachineController;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@SuppressWarnings("MethodMayBeStatic")
public class HyperNetMachineEventHandler {
    public static final HyperNetMachineEventHandler INSTANCE = new HyperNetMachineEventHandler();

    private HyperNetMachineEventHandler() {

    }

    @SubscribeEvent
    public void onMachineTick(final MachineTickEvent event) {
        if (event.phase != Phase.START) {
            return;
        }

        TileMultiblockMachineController ctrl = event.getController();
        DynamicMachine foundMachine = ctrl.getFoundMachine();
        if (!RegistryHyperNet.isHyperNetSupported(foundMachine)) {
            return;
        }

        NetNode cached = NetNodeCache.getCache(ctrl, RegistryHyperNet.getNodeType(foundMachine));
        if (cached != null) {
            cached.onMachineTick();
        }
    }
}
