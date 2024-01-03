package github.kasuminova.novaeng.common.adapter.util;

import github.kasuminova.mmce.common.itemtype.ChancedIngredientStack;
import hellfirepvp.modularmachinery.common.crafting.MachineRecipe;
import hellfirepvp.modularmachinery.common.crafting.requirement.RequirementEnergy;
import hellfirepvp.modularmachinery.common.crafting.requirement.RequirementIngredientArray;
import hellfirepvp.modularmachinery.common.crafting.requirement.RequirementItem;
import hellfirepvp.modularmachinery.common.lib.RequirementTypesMM;
import hellfirepvp.modularmachinery.common.machine.IOType;
import hellfirepvp.modularmachinery.common.modifier.RecipeModifier;
import hellfirepvp.modularmachinery.common.util.ItemUtils;
import ic2.api.recipe.IRecipeInput;
import ic2.api.recipe.Recipes;
import net.minecraft.item.ItemStack;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public class IC2MachineRecipeConverter {

    public static List<MachineRecipe> convertMaceratorRecipes(final Function<ItemStack, MachineRecipe> recipeSupplier, final Predicate<List<ItemStack>> inputFilter, final List<RecipeModifier> modifiers, final int baseEnergyUsage) {
        return convert(recipeSupplier, inputFilter, modifiers, baseEnergyUsage, Recipes.macerator.getRecipes());
    }

    public static List<MachineRecipe> convert(final Function<ItemStack, MachineRecipe> recipeSupplier,
                                              final Predicate<List<ItemStack>> inputFilter,
                                              final List<RecipeModifier> modifiers,
                                              final int baseEnergyUsage,
                                              Iterable<? extends ic2.api.recipe.MachineRecipe<IRecipeInput, Collection<ItemStack>>> icRecipes) {
        List<MachineRecipe> machineRecipes = new LinkedList<>();

        for (final ic2.api.recipe.MachineRecipe<IRecipeInput, Collection<ItemStack>> icRecipe : icRecipes) {
            List<ItemStack> inputs = icRecipe.getInput().getInputs();
            if (inputs.isEmpty() || !inputFilter.test(inputs)) {
                continue;
            }
            MachineRecipe recipe = recipeSupplier.apply(inputs.get(0));

            if (inputs.size() == 1) {
                int inAmount = Math.round(RecipeModifier.applyModifiers(modifiers, RequirementTypesMM.REQUIREMENT_ITEM, IOType.INPUT, icRecipe.getInput().getAmount(), false));
                if (inAmount > 0) {
                    recipe.addRequirement(new RequirementItem(IOType.INPUT, ItemUtils.copyStackWithSize(inputs.get(0), inAmount)));
                }
            } else {
                List<ChancedIngredientStack> ingredientStackList = new LinkedList<>();
                for (ItemStack input : inputs) {
                    int inAmount = Math.round(RecipeModifier.applyModifiers(modifiers, RequirementTypesMM.REQUIREMENT_ITEM, IOType.INPUT, icRecipe.getInput().getAmount(), false));
                    if (inAmount > 0) {
                        ingredientStackList.add(new ChancedIngredientStack(input, inAmount));
                    }
                }
                recipe.addRequirement(new RequirementIngredientArray(ingredientStackList));
            }

            for (final ItemStack output : icRecipe.getOutput()) {
                int outAmount = Math.round(RecipeModifier.applyModifiers(modifiers, RequirementTypesMM.REQUIREMENT_ITEM, IOType.OUTPUT, output.getCount(), false));
                if (outAmount > 0) {
                    recipe.addRequirement(new RequirementItem(IOType.OUTPUT, ItemUtils.copyStackWithSize(output, outAmount)));
                }
            }

            int inEnergy = Math.round(RecipeModifier.applyModifiers(modifiers, RequirementTypesMM.REQUIREMENT_ENERGY, IOType.INPUT, baseEnergyUsage, false));
            if (inEnergy > 0) {
                recipe.addRequirement(new RequirementEnergy(IOType.INPUT, inEnergy));
            }

            machineRecipes.add(recipe);
        }

        return machineRecipes;
    }

}
