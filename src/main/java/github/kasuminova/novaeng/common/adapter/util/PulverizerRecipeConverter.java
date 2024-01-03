package github.kasuminova.novaeng.common.adapter.util;

import cofh.thermalexpansion.util.managers.machine.PulverizerManager;
import hellfirepvp.modularmachinery.common.crafting.MachineRecipe;
import hellfirepvp.modularmachinery.common.crafting.requirement.RequirementEnergy;
import hellfirepvp.modularmachinery.common.crafting.requirement.RequirementItem;
import hellfirepvp.modularmachinery.common.lib.RequirementTypesMM;
import hellfirepvp.modularmachinery.common.machine.IOType;
import hellfirepvp.modularmachinery.common.modifier.RecipeModifier;
import hellfirepvp.modularmachinery.common.util.ItemUtils;
import net.minecraft.item.ItemStack;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public class PulverizerRecipeConverter {

    public static List<MachineRecipe> convert(final Function<ItemStack, MachineRecipe> recipeSupplier, final Predicate<List<ItemStack>> inputFilter, final List<RecipeModifier> modifiers) {
        PulverizerManager.PulverizerRecipe[] recipeList = PulverizerManager.getRecipeList();

        List<MachineRecipe> machineRecipes = new LinkedList<>();

        for (final PulverizerManager.PulverizerRecipe recipe : recipeList) {
            int energy = recipe.getEnergy();
            ItemStack input = recipe.getInput();
            ItemStack primaryOutput = recipe.getPrimaryOutput();
            ItemStack secondaryOutput = recipe.getSecondaryOutput();
            int secondaryOutputChance = recipe.getSecondaryOutputChance();

            if (!inputFilter.test(Collections.singletonList(input))) {
                continue;
            }

            MachineRecipe machineRecipe = recipeSupplier.apply(input);

            // Energy
            machineRecipe.addRequirement(new RequirementEnergy(IOType.INPUT, Math.round(RecipeModifier.applyModifiers(
                    modifiers, RequirementTypesMM.REQUIREMENT_ENERGY, IOType.INPUT, (float) energy / 200, false)))
            );
            // Item Input
            int inAmount = Math.round(RecipeModifier.applyModifiers(modifiers, RequirementTypesMM.REQUIREMENT_ITEM, IOType.INPUT, input.getCount(), false));
            if (inAmount >= 1) {
                machineRecipe.addRequirement(new RequirementItem(IOType.INPUT, ItemUtils.copyStackWithSize(input, inAmount)));
            }
            // Item Output
            int outAmount = Math.round(RecipeModifier.applyModifiers(modifiers, RequirementTypesMM.REQUIREMENT_ITEM, IOType.OUTPUT, primaryOutput.getCount(), false));
            if (outAmount >= 1) {
                machineRecipe.addRequirement(new RequirementItem(IOType.OUTPUT, ItemUtils.copyStackWithSize(primaryOutput, outAmount)));
            }
            // Secondary Item Output
            int secondOutAmount = Math.round(RecipeModifier.applyModifiers(modifiers, RequirementTypesMM.REQUIREMENT_ITEM, IOType.OUTPUT, secondaryOutput.getCount(), false));
            if (secondOutAmount >= 1) {
                float chance = RecipeModifier.applyModifiers(modifiers, RequirementTypesMM.REQUIREMENT_ITEM, IOType.OUTPUT, (float) secondaryOutputChance / 100, true);
                RequirementItem req = new RequirementItem(IOType.OUTPUT, ItemUtils.copyStackWithSize(secondaryOutput, secondOutAmount));
                req.setChance(chance);
                machineRecipe.addRequirement(req);
            }

            machineRecipes.add(machineRecipe);
        }

        return machineRecipes;
    }

}
