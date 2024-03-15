package github.kasuminova.novaeng.common.adapter.botania;

import com.meteor.extrabotany.api.ExtraBotanyAPI;
import crafttweaker.util.IEventHandler;
import github.kasuminova.mmce.common.event.Phase;
import github.kasuminova.mmce.common.event.recipe.FactoryRecipeFinishEvent;
import github.kasuminova.mmce.common.event.recipe.FactoryRecipeTickEvent;
import github.kasuminova.mmce.common.event.recipe.RecipeCheckEvent;
import github.kasuminova.mmce.common.event.recipe.RecipeEvent;
import github.kasuminova.novaeng.common.machine.IllumPool;
import hellfirepvp.modularmachinery.common.crafting.MachineRecipe;
import hellfirepvp.modularmachinery.common.crafting.adapter.RecipeAdapter;
import hellfirepvp.modularmachinery.common.crafting.helper.ComponentRequirement;
import hellfirepvp.modularmachinery.common.crafting.requirement.RequirementItem;
import hellfirepvp.modularmachinery.common.lib.RequirementTypesMM;
import hellfirepvp.modularmachinery.common.machine.IOType;
import hellfirepvp.modularmachinery.common.modifier.RecipeModifier;
import hellfirepvp.modularmachinery.common.tiles.base.TileMultiblockMachineController;
import hellfirepvp.modularmachinery.common.util.ItemUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.api.recipe.RecipeManaInfusion;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class AdapterBotaniaManaPool extends RecipeAdapter {

    public AdapterBotaniaManaPool() {
        super(new ResourceLocation("botania", "pool"));
    }

    @Nonnull
    @Override
    public Collection<MachineRecipe> createRecipesFor(final ResourceLocation owningMachineName,
                                                      final List<RecipeModifier> modifiers,
                                                      final List<ComponentRequirement<?, ?>> additionalRequirements,
                                                      final Map<Class<?>, List<IEventHandler<RecipeEvent>>> eventHandlers,
                                                      final List<String> recipeTooltips) {
        if (!owningMachineName.equals(IllumPool.REGISTRY_NAME)) {
            throw new IllegalArgumentException("AdapterBotaniaManaPool only can be used with IllumPool.");
        }

        List<MachineRecipe> recipes = new ArrayList<>();
        for (final RecipeManaInfusion infusionRecipe : BotaniaAPI.manaInfusionRecipes) {
            MachineRecipe recipe = createRecipeShell(
                    new ResourceLocation("botania",  "mana_infusion_" + incId),
                    owningMachineName,
                    20, 0, false);

            // Item Input
            int inAmount = Math.round(RecipeModifier.applyModifiers(modifiers, RequirementTypesMM.REQUIREMENT_ITEM, IOType.INPUT, 1, false));
            if (inAmount >= 1) {
                Object input = infusionRecipe.getInput();
                if (input instanceof String oreDict) {
                    recipe.addRequirement(new RequirementItem(IOType.INPUT, oreDict, inAmount));
                } else {
                    recipe.addRequirement(new RequirementItem(IOType.INPUT, ItemUtils.copyStackWithSize((ItemStack) input, inAmount)));
                }
            }

            // Item Output
            ItemStack output = infusionRecipe.getOutput();
            int outAmount = Math.round(RecipeModifier.applyModifiers(modifiers, RequirementTypesMM.REQUIREMENT_ITEM, IOType.OUTPUT, output.getCount(), false));
            if (outAmount >= 1) {
                recipe.addRequirement(new RequirementItem(IOType.OUTPUT, ItemUtils.copyStackWithSize(output, outAmount)));
            }

            // Mana
            int manaToConsume = infusionRecipe.getManaToConsume();
            recipe.addRecipeEventHandler(RecipeCheckEvent.class, (IEventHandler<RecipeCheckEvent>) event -> {
                if (event.phase != Phase.START) return;
                IllumPool.onRecipeCheck(event, manaToConsume);
            });
            recipe.addRecipeEventHandler(FactoryRecipeFinishEvent.class, IllumPool::onRecipeFinished);
            recipe.addRecipeEventHandler(FactoryRecipeTickEvent.class, (IEventHandler<FactoryRecipeTickEvent>) event -> {
                if (event.phase != Phase.START) return;
                IllumPool.onRecipeTick(event, manaToConsume);
            });
            recipe.addTooltip("魔力消耗：§b" + manaToConsume);

            // Catalyst
            IBlockState catalyst = infusionRecipe.getCatalyst();
            if (catalyst == null) {
                addDefaultCatalystHandler(recipe);
                recipe.addTooltip("辉光魔力池模式：§a普通");
            } else if (catalyst.equals(RecipeManaInfusion.conjurationState)) {
                recipe.addRecipeEventHandler(RecipeCheckEvent.class, (IEventHandler<RecipeCheckEvent>) event -> {
                    if (event.phase != Phase.START) return;
                    TileMultiblockMachineController controller = event.getController();
                    if (!controller.hasModifierReplacement(IllumPool.CONJURATION_CATALYST)) {
                        event.setFailed("辉光魔力池模式不匹配或缺少物品输入！");
                    }
                });
                recipe.addTooltip("辉光魔力池模式：§d炼造");
            } else if (catalyst.equals(RecipeManaInfusion.alchemyState)) {
                recipe.addRecipeEventHandler(RecipeCheckEvent.class, (IEventHandler<RecipeCheckEvent>) event -> {
                    if (event.phase != Phase.START) return;
                    TileMultiblockMachineController controller = event.getController();
                    if (!controller.hasModifierReplacement(IllumPool.ALCHEMY_CATALYST)) {
                        event.setFailed("辉光魔力池模式不匹配或缺少物品输入！");
                    }
                });
                recipe.addTooltip("辉光魔力池模式：§e炼金");
            } else if (catalyst.equals(ExtraBotanyAPI.dimensionState)) {
                recipe.addRecipeEventHandler(RecipeCheckEvent.class, (IEventHandler<RecipeCheckEvent>) event -> {
                    if (event.phase != Phase.START) return;
                    TileMultiblockMachineController controller = event.getController();
                    if (!controller.hasModifierReplacement(IllumPool.DIMENSION_CATALYST)) {
                        event.setFailed("辉光魔力池模式不匹配或缺少物品输入！");
                    }
                });
                recipe.addTooltip("辉光魔力池模式：§5次元");
            } else {
                addDefaultCatalystHandler(recipe);
            }
            recipe.addTooltip("消耗 §a1§f 点§e辉光魔力§f可替代 §a25%§f 的§b魔力消耗§f。");

            recipes.add(recipe);
            incId++;
        }

        return recipes;
    }

    protected static void addDefaultCatalystHandler(final MachineRecipe recipe) {
        recipe.addRecipeEventHandler(RecipeCheckEvent.class, (IEventHandler<RecipeCheckEvent>) event -> {
            if (event.phase != Phase.START) return;
            TileMultiblockMachineController controller = event.getController();
            boolean hasCatalyst = false;
            if (controller.hasModifierReplacement(IllumPool.ALCHEMY_CATALYST)) {
                hasCatalyst = true;
            } else if (controller.hasModifierReplacement(IllumPool.CONJURATION_CATALYST)) {
                hasCatalyst = true;
            } else if (controller.hasModifierReplacement(IllumPool.DIMENSION_CATALYST)) {
                hasCatalyst = true;
            }
            if (hasCatalyst) {
                event.setFailed("辉光魔力池模式不匹配或缺少物品输入！");
                return;
            }
            if (!controller.hasModifierReplacement(IllumPool.NORMAL_CATALYST)) {
                event.setFailed("辉光魔力池模式不匹配或缺少物品输入！");
            }
        });
    }

}
