package github.kasuminova.novaeng.common.crafttweaker.expansion;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.annotations.ZenRegister;
import github.kasuminova.novaeng.common.hypernet.old.NetNodeCache;
import github.kasuminova.novaeng.common.hypernet.old.machine.AssemblyLine;
import hellfirepvp.modularmachinery.ModularMachinery;
import hellfirepvp.modularmachinery.common.crafting.helper.ComponentRequirement;
import hellfirepvp.modularmachinery.common.integration.crafttweaker.RecipeBuilder;
import hellfirepvp.modularmachinery.common.integration.crafttweaker.RecipePrimer;
import net.minecraft.util.ResourceLocation;
import stanhebben.zenscript.annotations.ZenExpansion;
import stanhebben.zenscript.annotations.ZenMethod;

import java.util.List;

@ZenRegister
@ZenExpansion("mods.modularmachinery.RecipePrimer")
public class RecipePrimerAssemblyLine {
    public static final ResourceLocation ASSEMBLY_LINE = new ResourceLocation(ModularMachinery.MODID, "assembly_line");

    @ZenMethod
    public static RecipePrimer setAssemblyLineProxied(RecipePrimer primer) {
        ResourceLocation machineName = primer.getParentMachineName();
        if (!machineName.equals(ASSEMBLY_LINE)) {
            CraftTweakerAPI.logError(
                    "Cannot proxy AssemblyLine recipe for `" + primer.getRecipeRegistryName() +
                    "`, because parent machine is not `assembly_line`!");
            return primer;
        }

        return primer.addPostCheckHandler(event -> {
            if (AssemblyLine.isNotAssemblyLine(event.getController())) return;
            AssemblyLine assemblyLine = NetNodeCache.getCache(event.getController(), AssemblyLine.class);
            if (assemblyLine != null) assemblyLine.onAssemblyLineRecipeCheck(event);
        }).addFactoryPreTickHandler(event -> {
            if (AssemblyLine.isNotAssemblyLine(event.getController())) return;
            AssemblyLine assemblyLine = NetNodeCache.getCache(event.getController(), AssemblyLine.class);
            if (assemblyLine != null) assemblyLine.onAssemblyLineRecipePreTick(event);
        }).addFactoryPostTickHandler(event -> {
            if (AssemblyLine.isNotAssemblyLine(event.getController())) return;
            AssemblyLine assemblyLine = NetNodeCache.getCache(event.getController(), AssemblyLine.class);
            if (assemblyLine != null) assemblyLine.onAssemblyLineRecipePostTick(event);
        }).addFactoryFinishHandler(event -> {
            if (AssemblyLine.isNotAssemblyLine(event.getController())) return;
            AssemblyLine assemblyLine = NetNodeCache.getCache(event.getController(), AssemblyLine.class);
            if (assemblyLine != null) assemblyLine.onAssemblyLineRecipeFinished(event);
        }).addFactoryFailureHandler(event -> {
            if (AssemblyLine.isNotAssemblyLine(event.getController())) return;
            AssemblyLine assemblyLine = NetNodeCache.getCache(event.getController(), AssemblyLine.class);
            if (assemblyLine != null) assemblyLine.onAssemblyLineRecipeFailure(event);
        });
    }

    @ZenMethod
    public static RecipePrimer convertAssemblyLineRecipeToDefaultRecipeAndRegister(RecipePrimer primer, String machineName) {
        String recipeName = primer.getRecipeRegistryName().getPath() + ".copy." + machineName;
        RecipePrimer newPrimer = RecipeBuilder.newBuilder(
                recipeName, machineName, primer.getTotalProcessingTickTime(), primer.getPriority(), primer.voidPerTickFailure()
        );

        List<ComponentRequirement<?, ?>> components = newPrimer.getComponents();
        for (final ComponentRequirement<?, ?> component : primer.getComponents()) {
            ComponentRequirement<?, ?> copied = component.deepCopy().postDeepCopy(component);
            copied.setTag(null);
            components.add(copied);
        }

        newPrimer.getRecipeEventHandlers().putAll(primer.getRecipeEventHandlers());
        newPrimer.getTooltipList().addAll(primer.getTooltipList());

        newPrimer.build();

        return primer;
    }

}
