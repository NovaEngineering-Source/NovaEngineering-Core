package github.kasuminova.novaeng.common.adapter.util;

import hellfirepvp.modularmachinery.common.crafting.MachineRecipe;
import hellfirepvp.modularmachinery.common.crafting.requirement.RequirementEnergy;
import hellfirepvp.modularmachinery.common.crafting.requirement.RequirementItem;
import hellfirepvp.modularmachinery.common.lib.RequirementTypesMM;
import hellfirepvp.modularmachinery.common.machine.IOType;
import hellfirepvp.modularmachinery.common.modifier.RecipeModifier;
import hellfirepvp.modularmachinery.common.util.ItemUtils;
import mekanism.common.recipe.RecipeHandler;
import mekanism.common.recipe.inputs.ItemStackInput;
import mekanism.common.recipe.machines.CrusherRecipe;
import net.minecraft.item.ItemStack;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public class MekCrusherRecipeConverter {
    public static List<MachineRecipe> convert(final Function<ItemStack, MachineRecipe> recipeSupplier, final Predicate<List<ItemStack>> inputFilter, final List<RecipeModifier> modifiers, final int baseEnergyUsage) {
        HashMap<ItemStackInput, CrusherRecipe> crusherRecipes = RecipeHandler.Recipe.CRUSHER.get();
        List<MachineRecipe> machineRecipes = new LinkedList<>();

        crusherRecipes.forEach((input, mekRecipe) -> {
            ItemStack inputStack = input.ingredient;
            ItemStack outputStack = mekRecipe.getOutput().output;
            if (!inputFilter.test(Collections.singletonList(inputStack))) {
                return;
            }

            MachineRecipe recipe = recipeSupplier.apply(inputStack);

            int inAmount = Math.round(RecipeModifier.applyModifiers(modifiers, RequirementTypesMM.REQUIREMENT_ITEM, IOType.INPUT, inputStack.getCount(), false));
            if (inAmount > 0) {
                recipe.addRequirement(new RequirementItem(IOType.INPUT, ItemUtils.copyStackWithSize(inputStack, inAmount)));
            }
            int outAmount = Math.round(RecipeModifier.applyModifiers(modifiers, RequirementTypesMM.REQUIREMENT_ITEM, IOType.OUTPUT, outputStack.getCount(), false));
            if (outAmount > 0) {
                recipe.addRequirement(new RequirementItem(IOType.OUTPUT, ItemUtils.copyStackWithSize(outputStack, outAmount)));
            }
            int inEnergy = Math.round(RecipeModifier.applyModifiers(modifiers, RequirementTypesMM.REQUIREMENT_ENERGY, IOType.INPUT, baseEnergyUsage, false));
            if (inEnergy > 0) {
                recipe.addRequirement(new RequirementEnergy(IOType.INPUT, inEnergy));
            }

            machineRecipes.add(recipe);
        });

        return machineRecipes;
    }
}
