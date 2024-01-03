package github.kasuminova.novaeng.common.adapter.util;

import github.kasuminova.mmce.common.itemtype.ChancedIngredientStack;
import hellfirepvp.modularmachinery.common.crafting.MachineRecipe;
import hellfirepvp.modularmachinery.common.crafting.requirement.RequirementEnergy;
import hellfirepvp.modularmachinery.common.crafting.requirement.RequirementFluid;
import hellfirepvp.modularmachinery.common.crafting.requirement.RequirementIngredientArray;
import hellfirepvp.modularmachinery.common.crafting.requirement.RequirementItem;
import hellfirepvp.modularmachinery.common.lib.RequirementTypesMM;
import hellfirepvp.modularmachinery.common.machine.IOType;
import hellfirepvp.modularmachinery.common.modifier.RecipeModifier;
import hellfirepvp.modularmachinery.common.util.ItemUtils;
import nc.recipe.BasicRecipe;
import nc.recipe.NCRecipes;
import nc.recipe.ingredient.*;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class NCOBasicRecipeConverter {

    protected static void addItemIngredient(final List<RecipeModifier> modifiers,
                                            final IItemIngredient ingredient,
                                            final MachineRecipe recipe,
                                            final ItemStack stack,
                                            final IOType ioType) {
        int inAmount = Math.round(RecipeModifier.applyModifiers(modifiers, RequirementTypesMM.REQUIREMENT_ITEM, IOType.INPUT, stack.getCount(), false));

        if (ingredient instanceof final OreIngredient oreIngredient) {
            recipe.addRequirement(new RequirementItem(ioType, oreIngredient.oreName, inAmount));
            return;
        }

        if (ingredient instanceof final ItemArrayIngredient arrayIngredient) {
            List<IItemIngredient> ingredientList = arrayIngredient.ingredientList;
            List<ChancedIngredientStack> ingredientStackList = new ArrayList<>(ingredientList.size());
            for (IItemIngredient itemIngredient : ingredientList) {
                if (itemIngredient instanceof final OreIngredient oreIngredient) {
                    int subInAmount = Math.round(RecipeModifier.applyModifiers(modifiers, RequirementTypesMM.REQUIREMENT_ITEM, ioType, oreIngredient.stackSize, false));

                    ingredientStackList.add(new ChancedIngredientStack(oreIngredient.oreName, subInAmount));
                } else {
                    ItemStack ingredientStack = itemIngredient.getStack();
                    int subInAmount = Math.round(RecipeModifier.applyModifiers(modifiers, RequirementTypesMM.REQUIREMENT_ITEM, ioType, ingredientStack.getCount(), false));

                    ingredientStackList.add(new ChancedIngredientStack(ItemUtils.copyStackWithSize(ingredientStack, subInAmount)));
                }
            }

            recipe.addRequirement(new RequirementIngredientArray(ingredientStackList));
            return;
        }

        RequirementItem req = new RequirementItem(ioType, ItemUtils.copyStackWithSize(stack, inAmount));

        if (ingredient instanceof ChanceItemIngredient chanceItemIngredient) {
            float chance = RecipeModifier.applyModifiers(modifiers, RequirementTypesMM.REQUIREMENT_ITEM, IOType.OUTPUT, ((float) chanceItemIngredient.getChancePercent()) / 100F, true);
            req.setChance(chance);
        }

        recipe.addRequirement(req);
    }

    protected static void addFluidIngredient(final List<RecipeModifier> modifiers, final IFluidIngredient fluidIngredient, final MachineRecipe recipe, final IOType ioType) {
        FluidStack fluidStack = fluidIngredient.getStack();
        if (fluidStack == null) {
            return;
        }
        FluidStack copied = fluidStack.copy();
        int outputAmount = Math.round(RecipeModifier.applyModifiers(modifiers, RequirementTypesMM.REQUIREMENT_FLUID, ioType, copied.amount, false));
        if (outputAmount > 0) {
            copied.amount = outputAmount;
            recipe.addRequirement(new RequirementFluid(ioType, copied));
        }
    }

    public static List<MachineRecipe> convert(final Function<ItemStack, MachineRecipe> recipeSupplier, final Predicate<List<ItemStack>> inputFilter, final List<RecipeModifier> modifiers, final int baseEnergyUsage) {
        List<BasicRecipe> recipeList = NCRecipes.manufactory.getRecipeList();
        List<MachineRecipe> machineRecipes = new LinkedList<>();

        for (BasicRecipe basicRecipe : recipeList) {
            LinkedList<ItemStack> inputList = basicRecipe.getItemIngredients().stream()
                    .flatMap(ingredient -> ingredient.getInputStackHashingList().stream())
                    .collect(Collectors.toCollection(LinkedList::new));
            if (inputList.isEmpty() || !inputFilter.test(inputList)) {
                continue;
            }

            MachineRecipe recipe = recipeSupplier.apply(inputList.get(0));

            // Item Input
            basicRecipe.getItemIngredients().stream()
                    .filter(ingredient -> !(ingredient instanceof EmptyItemIngredient))
                    .forEach(ingredient -> addItemIngredient(modifiers, ingredient, recipe, ingredient.getStack(), IOType.INPUT));
            // Item Output
            basicRecipe.getItemProducts().stream()
                    .filter(itemProduct -> !(itemProduct instanceof EmptyItemIngredient))
                    .forEach(itemProduct -> addItemIngredient(modifiers, itemProduct, recipe, itemProduct.getStack(), IOType.OUTPUT));
            // Fluid Input
            basicRecipe.getFluidIngredients()
                    .forEach(fluidIngredient -> addFluidIngredient(modifiers, fluidIngredient, recipe, IOType.INPUT));
            // Fluid Output
            basicRecipe.getFluidProducts()
                    .forEach(fluidProduct -> addFluidIngredient(modifiers, fluidProduct, recipe, IOType.OUTPUT));

            recipe.addRequirement(new RequirementEnergy(IOType.INPUT, Math.round(RecipeModifier.applyModifiers(
                    modifiers, RequirementTypesMM.REQUIREMENT_ENERGY, IOType.INPUT, baseEnergyUsage, false)))
            );

            machineRecipes.add(recipe);
        }

        return machineRecipes;
    }



}
