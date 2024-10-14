package github.kasuminova.novaeng.common.machine;

import crafttweaker.util.IEventHandler;
import github.kasuminova.mmce.common.event.Phase;
import github.kasuminova.mmce.common.event.recipe.RecipeEvent;
import github.kasuminova.mmce.common.event.recipe.RecipeTickEvent;
import github.kasuminova.mmce.common.itemtype.ChancedIngredientStack;
import github.kasuminova.novaeng.common.crafttweaker.expansion.RecipePrimerHyperNet;
import github.kasuminova.novaeng.common.crafttweaker.hypernet.HyperNetHelper;
import github.kasuminova.novaeng.common.tile.machine.GeocentricDrillController;
import hellfirepvp.modularmachinery.ModularMachinery;
import hellfirepvp.modularmachinery.common.crafting.MachineRecipe;
import hellfirepvp.modularmachinery.common.crafting.helper.ComponentRequirement;
import hellfirepvp.modularmachinery.common.crafting.requirement.RequirementEnergy;
import hellfirepvp.modularmachinery.common.crafting.requirement.RequirementIngredientArray;
import hellfirepvp.modularmachinery.common.crafting.requirement.RequirementItem;
import hellfirepvp.modularmachinery.common.integration.crafttweaker.RecipeBuilder;
import hellfirepvp.modularmachinery.common.integration.crafttweaker.RecipePrimer;
import hellfirepvp.modularmachinery.common.lib.RequirementTypesMM;
import hellfirepvp.modularmachinery.common.machine.DynamicMachine;
import hellfirepvp.modularmachinery.common.machine.IOType;
import hellfirepvp.modularmachinery.common.modifier.RecipeModifier;
import hellfirepvp.modularmachinery.common.util.ItemUtils;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.OreDictionary;

import java.util.*;

public class GeocentricDrill implements MachineSpecial {

    public static final GeocentricDrill GEOCENTRIC_DRILL = new GeocentricDrill();
    public static final ResourceLocation REGISTRY_NAME = new ResourceLocation(ModularMachinery.MODID, "earth_drill");
    public static final ResourceLocation RECIPE_REGISTRY_NAME = new ResourceLocation(ModularMachinery.MODID, "earth_drill_working");

    public static final int ENERGY_PER_TICK = 12_000_000;
    public static final int ORE_COUNT = 6;

    public static final int ACCELERATE_MULTIPLIER = 15;

    public static final int MIN_DEPTH = 1000;
    public static final int MAX_DEPTH = 20000;

    public static final int PARALLELISM_PER_DEPTH = 25;
    public static final int COMPUTATION_POINT_PER_PARALLELISM = 4;

    public static final int MAX_PARALLELISM = MAX_DEPTH / PARALLELISM_PER_DEPTH;

    private final Map<String, ItemStack> rawOres = new Object2ObjectLinkedOpenHashMap<>();

    private static void onRecipePreTick(RecipeTickEvent event) {
        if (event.phase == Phase.START) {
            if (!(event.getController() instanceof GeocentricDrillController geoDrill)) {
                return;
            }
            if (geoDrill.diveOrAscend()) {
                if (!geoDrill.hasModifier("diveOrAscend")) {
                    geoDrill.addModifier("diveOrAscend",
                            new RecipeModifier(RequirementTypesMM.REQUIREMENT_ENERGY, IOType.INPUT,
                                    2F, RecipeModifier.OPERATION_MULTIPLY,
                                    false
                            )
                    );
                }
                geoDrill.markNoUpdateSync();
            } else if (geoDrill.hasModifier("diveOrAscend")) {
                geoDrill.removeModifier("diveOrAscend");
                geoDrill.markNoUpdateSync();
            }
            return;
        }

        if (event.phase == Phase.END) {
            if (!(event.getController() instanceof GeocentricDrillController geoDrill)) {
                return;
            }
            if (geoDrill.getDepth() < MIN_DEPTH) {
                event.preventProgressing("craftcheck.failure.geocentric_drill_depth");
                geoDrill.markNoUpdateSync();
            }
        }
    }

    @Override
    public void preInit(final DynamicMachine machine) {
        HyperNetHelper.proxyMachineForHyperNet(machine.getRegistryName());
        machine.setInternalParallelism(MAX_PARALLELISM);

        Map<String, ItemStack> rawOres = new Object2ObjectLinkedOpenHashMap<>();
        Arrays.stream(OreDictionary.getOreNames())
                .filter(oreName -> oreName.startsWith("rawOre"))
                .forEach(oreName -> {
                    NonNullList<ItemStack> ores = OreDictionary.getOres(oreName);
                    if (!ores.isEmpty()) {
                        ItemStack stack = ores.get(0).copy();
                        stack.setCount(ORE_COUNT);
                        rawOres.put(oreName, stack);
                    }
                });

        addRFToolsDimShard(rawOres);
        addEnvironmentTechOres(rawOres);
        addGeocentricQuartzCrystalOre(rawOres);

        this.rawOres.clear();
        this.rawOres.putAll(rawOres);

        RecipePrimer primer = RecipeBuilder.newBuilder(RECIPE_REGISTRY_NAME.getPath(), REGISTRY_NAME.getPath(), 20);
        primer.addEnergyPerTickInput(ENERGY_PER_TICK);

        float chance = 1F / rawOres.size();
        List<ComponentRequirement<?, ?>> components = primer.getComponents();
        for (final ItemStack ore : rawOres.values()) {
            RequirementItem output = new RequirementItem(IOType.OUTPUT, ore.copy());
            output.setChance(chance);
            components.add(output);
        }

        RecipePrimerHyperNet.requireComputationPoint(primer, COMPUTATION_POINT_PER_PARALLELISM);
        primer.build();
    }

    private static void addRFToolsDimShard(final Map<String, ItemStack> rawOres) {
        List<ItemStack> genDimShards = OreDictionary.getOres("gemDimensionalShard", false);
        if (!genDimShards.isEmpty()) {
            rawOres.put("gemDimensionalShard", ItemUtils.copyStackWithSize(genDimShards.get(0), 4));
        }
    }

    private static void addGeocentricQuartzCrystalOre(final Map<String, ItemStack> rawOres) {
        Item geocentricCrystal = Item.REGISTRY.getObject(new ResourceLocation("contenttweaker", "geocentric_crystal"));
        if (geocentricCrystal != null) {
            rawOres.put("crystalGeocentric", new ItemStack(geocentricCrystal, 1));
        }
    }

    private static void addEnvironmentTechOres(final Map<String, ItemStack> rawOres) {
        List<ItemStack> crystalLitherite = OreDictionary.getOres("crystalLitherite", false);
        if (!crystalLitherite.isEmpty()) {
            rawOres.put("crystalLitherite", ItemUtils.copyStackWithSize(crystalLitherite.get(0), 4));
        }
        List<ItemStack> crystalErodium = OreDictionary.getOres("crystalErodium", false);
        if (!crystalErodium.isEmpty()) {
            rawOres.put("crystalErodium", ItemUtils.copyStackWithSize(crystalErodium.get(0), 3));
        }
        List<ItemStack> crystalLonsdaleite = OreDictionary.getOres("crystalLonsdaleite", false);
        if (!crystalLonsdaleite.isEmpty()) {
            rawOres.put("crystalLonsdaleite", ItemUtils.copyStackWithSize(crystalLonsdaleite.get(0), 2));
        }
        List<ItemStack> crystalKyronite = OreDictionary.getOres("crystalKyronite", false);
        if (!crystalKyronite.isEmpty()) {
            rawOres.put("crystalKyronite", ItemUtils.copyStackWithSize(crystalKyronite.get(0), 2));
        }
        List<ItemStack> crystalPladium = OreDictionary.getOres("crystalPladium", false);
        if (!crystalPladium.isEmpty()) {
            rawOres.put("crystalPladium", ItemUtils.copyStackWithSize(crystalPladium.get(0), 2));
        }
        List<ItemStack> crystalIonite = OreDictionary.getOres("crystalIonite", false);
        if (!crystalIonite.isEmpty()) {
            rawOres.put("crystalIonite", ItemUtils.copyStackWithSize(crystalIonite.get(0), 2));
        }
        List<ItemStack> crystalAethium = OreDictionary.getOres("crystalAethium", false);
        if (!crystalAethium.isEmpty()) {
            rawOres.put("crystalAethium", ItemUtils.copyStackWithSize(crystalAethium.get(0), 1));
        }
    }

    public Map<String, ItemStack> getRawOres() {
        return Collections.unmodifiableMap(rawOres);
    }

    public MachineRecipe rebuildRecipe(final MachineRecipe original, final Set<String> accelerateOres) {
        MachineRecipe recipe = new MachineRecipe("", RECIPE_REGISTRY_NAME, REGISTRY_NAME, 10, 0, false, true);
        recipe.addRequirement(new RequirementEnergy(IOType.INPUT, ENERGY_PER_TICK));

        float chance = 1F / (rawOres.size() + (accelerateOres.size() * (GeocentricDrill.ACCELERATE_MULTIPLIER - 1)));
        List<ChancedIngredientStack> output = new ArrayList<>();
        rawOres.forEach((oreName, ore) -> output.add(new ChancedIngredientStack(
                ore.copy(), accelerateOres.contains(oreName) ? chance * GeocentricDrill.ACCELERATE_MULTIPLIER : chance)
        ));
        recipe.addRequirement(new RequirementIngredientArray(output, IOType.OUTPUT));
        original.getRecipeEventHandlers().forEach((eventClass, handlerList) -> {
            for (final IEventHandler<RecipeEvent> handler : handlerList) {
                recipe.addRecipeEventHandler(eventClass, handler);
            }
        });
        recipe.addRecipeEventHandler(RecipeTickEvent.class, GeocentricDrill::onRecipePreTick);

        return recipe;
    }

    @Override
    public ResourceLocation getRegistryName() {
        return REGISTRY_NAME;
    }

}
