package github.kasuminova.novaeng.common.handler;

import github.kasuminova.mmce.common.event.Phase;
import github.kasuminova.mmce.common.event.machine.MachineTickEvent;
import github.kasuminova.novaeng.NovaEngineeringCore;
import github.kasuminova.novaeng.common.hypernet.NetNode;
import github.kasuminova.novaeng.common.hypernet.NetNodeCache;
import github.kasuminova.novaeng.common.registry.RegistryHyperNet;
import hellfirepvp.modularmachinery.common.machine.DynamicMachine;
import hellfirepvp.modularmachinery.common.machine.MachineRegistry;
import hellfirepvp.modularmachinery.common.tiles.base.TileMultiblockMachineController;
import net.minecraft.util.ResourceLocation;

public class HyperNetMachineEventHandler {

    public static void onMachineTick(final MachineTickEvent event) {
        if (event.phase != Phase.START) {
            return;
        }

        TileMultiblockMachineController ctrl = event.getController();
        DynamicMachine foundMachine = ctrl.getFoundMachine();

        NetNode cached = NetNodeCache.getCache(ctrl, RegistryHyperNet.getNodeType(foundMachine));
        if (cached != null) {
            cached.onMachineTick();
        }
    }

    public static void registerHandler() {
        for (final ResourceLocation machineName : RegistryHyperNet.getAllHyperNetSupportedMachinery()) {
            DynamicMachine machine = MachineRegistry.getRegistry().getMachine(machineName);
            if (machine == null) {
                NovaEngineeringCore.log.warn("Cloud not find hypernet machine " + machineName + "!");
                continue;
            }
            machine.addMachineEventHandler(MachineTickEvent.class, HyperNetMachineEventHandler::onMachineTick);
        }
    }

}
