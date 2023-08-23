package github.kasuminova.novaeng.common.integration;

import github.kasuminova.novaeng.NovaEngineeringCore;
import github.kasuminova.novaeng.common.hypernet.base.HyperNetRecipeManager;
import github.kasuminova.novaeng.common.registry.RegistryHyperNet;
import net.minecraft.command.ICommandSender;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import youyihj.zenutils.api.reload.ScriptReloadEvent;

@SuppressWarnings("MethodMayBeStatic")
public class IntegrationCRT {
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
    public void onScriptsReloaded(ScriptReloadEvent.Post event) {
        HyperNetRecipeManager.registerRecipes();
    }
}
