package github.kasuminova.novaeng.common.integration;

import github.kasuminova.novaeng.NovaEngineeringCore;
import github.kasuminova.novaeng.common.handler.HyperNetMachineEventHandler;
import github.kasuminova.novaeng.common.hypernet.old.HyperNetTerminal;
import github.kasuminova.novaeng.common.hypernet.old.machine.AssemblyLine;
import github.kasuminova.novaeng.common.hypernet.old.recipe.HyperNetRecipeManager;
import github.kasuminova.novaeng.common.registry.RegistryHyperNet;
import github.kasuminova.novaeng.common.registry.RegistryMachineSpecial;
import hellfirepvp.modularmachinery.ModularMachinery;
import hellfirepvp.modularmachinery.common.machine.DynamicMachine;
import hellfirepvp.modularmachinery.common.machine.MachineRegistry;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import youyihj.zenutils.api.reload.ScriptReloadEvent;

@SuppressWarnings("MethodMayBeStatic")
public class IntegrationCRT {
    public static final IntegrationCRT INSTANCE = new IntegrationCRT();

    private IntegrationCRT() {

    }

    @SubscribeEvent
    @Optional.Method(modid = "zenutils")
    public void onScriptsReloading(ScriptReloadEvent.Pre event) {
        ICommandSender requester = event.getRequester();
        if (requester != null) {
            RegistryHyperNet.clearRegistry(requester);
        } else {
            RegistryHyperNet.clearRegistry();
        }

        NovaEngineeringCore.log.info("[NovaEng-Core] Cleared HyperNet registry.");
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    @Optional.Method(modid = "zenutils")
    public void onScriptsReloadedPre(ScriptReloadEvent.Post event) {
        RegistryHyperNet.registerHyperNetNode(
                new ResourceLocation(ModularMachinery.MODID, "hypernet_terminal"),
                HyperNetTerminal.class
        );

        AssemblyLine.registerNetNode();

        // pre
        HyperNetRecipeManager.registerRecipes();
        RegistryMachineSpecial.getSpecialMachineRegistry().forEach((registryName, machineSpecial) -> {
            DynamicMachine machine = MachineRegistry.getRegistry().getMachine(registryName);
            if (machine != null) {
                machineSpecial.preInit(machine);
            }
        });
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    @Optional.Method(modid = "zenutils")
    public void onScriptsReloadedPost(ScriptReloadEvent.Post event) {
        // post
        HyperNetMachineEventHandler.registerHandler();
        RegistryMachineSpecial.getSpecialMachineRegistry().forEach((registryName, machineSpecial) -> {
            DynamicMachine machine = MachineRegistry.getRegistry().getMachine(registryName);
            if (machine != null) {
                machineSpecial.init(machine);
            }
        });
    }
}
