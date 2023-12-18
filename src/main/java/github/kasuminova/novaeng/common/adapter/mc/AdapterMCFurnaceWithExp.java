package github.kasuminova.novaeng.common.adapter.mc;

import crafttweaker.util.IEventHandler;
import github.kasuminova.mmce.common.event.recipe.RecipeCheckEvent;
import github.kasuminova.mmce.common.event.recipe.RecipeEvent;
import hellfirepvp.modularmachinery.common.crafting.ActiveMachineRecipe;
import hellfirepvp.modularmachinery.common.crafting.MachineRecipe;
import hellfirepvp.modularmachinery.common.crafting.adapter.RecipeAdapter;
import hellfirepvp.modularmachinery.common.crafting.helper.ComponentRequirement;
import hellfirepvp.modularmachinery.common.crafting.requirement.RequirementEnergy;
import hellfirepvp.modularmachinery.common.crafting.requirement.RequirementItem;
import hellfirepvp.modularmachinery.common.lib.RequirementTypesMM;
import hellfirepvp.modularmachinery.common.machine.IOType;
import hellfirepvp.modularmachinery.common.modifier.RecipeModifier;
import hellfirepvp.modularmachinery.common.util.ItemUtils;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.FMLCommonHandler;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class AdapterMCFurnaceWithExp extends RecipeAdapter {
    public AdapterMCFurnaceWithExp() {
        super(new ResourceLocation("minecraft", "furnace_with_exp"));
    }

    @Nonnull
    @Override
    public Collection<MachineRecipe> createRecipesFor(ResourceLocation owningMachineName,
                                                      List<RecipeModifier> modifiers,
                                                      List<ComponentRequirement<?, ?>> additionalRequirements,
                                                      Map<Class<?>, List<IEventHandler<RecipeEvent>>> eventHandlers,
                                                      List<String> recipeTooltips) {
        FurnaceRecipes furnaceRecipes = FurnaceRecipes.instance();
        Map<ItemStack, ItemStack> inputOutputMap = furnaceRecipes.getSmeltingList();

        List<MachineRecipe> smeltingRecipes = new ArrayList<>(inputOutputMap.size());
        for (Map.Entry<ItemStack, ItemStack> smelting : inputOutputMap.entrySet()) {
            ItemStack input = smelting.getKey();
            ItemStack output = smelting.getValue();
            int tickTime = Math.round(Math.max(1, RecipeModifier.applyModifiers(modifiers, RequirementTypesMM.REQUIREMENT_DURATION, null, 120, false)));
            float experience = furnaceRecipes.getSmeltingExperience(output);

            MachineRecipe recipe = createRecipeShell(
                    new ResourceLocation("minecraft",  "smelting_with_exp_" + incId + "_" + input + "_" + output),
                    owningMachineName,
                    tickTime, 0, false);

            recipe.addRecipeEventHandler(RecipeCheckEvent.class, (IEventHandler<RecipeCheckEvent>) event -> {
                ActiveMachineRecipe machineRecipe = event.getActiveRecipe();
                machineRecipe.getDataCompound().setFloat("experience", experience);
            });

            if (FMLCommonHandler.instance().getSide().isClient() && experience > 0F) {
                recipe.addTooltip(I18n.format("novaeng.adapter.mc.furnace.exp", experience));
            }

            int inAmount = Math.round(RecipeModifier.applyModifiers(modifiers, RequirementTypesMM.REQUIREMENT_ITEM, IOType.INPUT, input.getCount(), false));
            if (inAmount > 0) {
                recipe.addRequirement(new RequirementItem(IOType.INPUT, ItemUtils.copyStackWithSize(input, inAmount)));
            }

            int outAmount = Math.round(RecipeModifier.applyModifiers(modifiers, RequirementTypesMM.REQUIREMENT_ITEM, IOType.OUTPUT, output.getCount(), false));
            if (outAmount > 0) {
                recipe.addRequirement(new RequirementItem(IOType.OUTPUT, ItemUtils.copyStackWithSize(output, outAmount)));
            }

            int inEnergy = Math.round(RecipeModifier.applyModifiers(modifiers, RequirementTypesMM.REQUIREMENT_ENERGY, IOType.INPUT, 20, false));
            if (inEnergy > 0) {
                recipe.addRequirement(new RequirementEnergy(IOType.INPUT, inEnergy));
            }

            RecipeAdapter.addAdditionalRequirements(recipe, additionalRequirements, eventHandlers, recipeTooltips);

            smeltingRecipes.add(recipe);
        }
        incId++;
        return smeltingRecipes;
    }
}
