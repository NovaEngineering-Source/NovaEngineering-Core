package github.kasuminova.novaeng.common.hypernet;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IIngredient;
import github.kasuminova.mmce.common.helper.IDynamicPatternInfo;
import github.kasuminova.novaeng.common.hypernet.base.NetNodeType;
import hellfirepvp.modularmachinery.common.crafting.ActiveMachineRecipe;
import hellfirepvp.modularmachinery.common.integration.crafttweaker.MachineModifier;
import hellfirepvp.modularmachinery.common.integration.crafttweaker.RecipeBuilder;
import hellfirepvp.modularmachinery.common.integration.crafttweaker.event.MMEvents;
import hellfirepvp.modularmachinery.common.machine.factory.FactoryRecipeThread;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.Tuple;
import net.minecraftforge.fml.common.FMLCommonHandler;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenMethod;

import java.util.ArrayList;
import java.util.List;

@ZenRegister
@ZenClass("novaeng.hypernet.DataProcessorType")
public class DataProcessorType extends NetNodeType {
    public static final String PROCESSOR_WORKING_THREAD_NAME = "novaeng.hypernet.data_processor.compute_scheduler";
    public static final String PROCESSOR_RADIATOR_THREAD_NAME = "novaeng.hypernet.data_processor.radiator";

    private final int heatDistribution;
    private final int overheatThreshold;

    private final List<Tuple<Tuple<IIngredient[], IIngredient[]>, Integer>> radiatorIngredientList = new ArrayList<>();

    private String dynamicPatternName = "";

    public DataProcessorType(final String typeName,
                             final long energyUsage,
                             final int heatDistribution,
                             final int overheatThreshold)
    {
        super(typeName, energyUsage);
        this.heatDistribution = heatDistribution;
        this.overheatThreshold = overheatThreshold;
    }

    @ZenMethod
    public static DataProcessorType create(final String typeName,
                                           final long energyUsage,
                                           final int heatDistribution,
                                           final int overheatThreshold)
    {
        return new DataProcessorType(typeName,
                energyUsage,
                heatDistribution,
                overheatThreshold);
    }

    public void registerRecipesAndThreads() {
        String name = typeName;
        MachineModifier.addCoreThread(name, FactoryRecipeThread.createCoreThread(PROCESSOR_WORKING_THREAD_NAME));
        if (!radiatorIngredientList.isEmpty()) {
            MachineModifier.addCoreThread(name, FactoryRecipeThread.createCoreThread(PROCESSOR_RADIATOR_THREAD_NAME));
        }

        MMEvents.onStructureUpdate(name, event -> {
            DataProcessor processor = NetNodeCache.getCache(event.getController(), DataProcessor.class);
            if (processor != null) processor.onStructureUpdate();
        });

        RecipeBuilder.newBuilder(name + "_working", name, 20, 100, false)
                .addEnergyPerTickInput(energyUsage)
                .addPostCheckHandler(event -> {
                    DataProcessor processor = NetNodeCache.getCache(event.getController(), DataProcessor.class);
                    if (processor != null) processor.onRecipeCheck(event);
                })
                .addFactoryPreTickHandler(event -> {
                    DataProcessor processor = NetNodeCache.getCache(event.getController(), DataProcessor.class);
                    if (processor != null) processor.onWorkingTick(event);
                })
                .addRecipeTooltip(
                        "novaeng.hypernet.data_processor.working.tooltip.0",
                        "novaeng.hypernet.data_processor.working.tooltip.1"
                )
                .setThreadName(PROCESSOR_WORKING_THREAD_NAME)
                .setParallelized(false)
                .build();

        int counter = 0;
        for (final Tuple<Tuple<IIngredient[], IIngredient[]>, Integer> radiatorIngredient : radiatorIngredientList) {
            Tuple<IIngredient[], IIngredient[]> io = radiatorIngredient.getFirst();
            IIngredient[] input = io.getFirst();
            IIngredient[] output = io.getSecond();
            int heatDistribution = radiatorIngredient.getSecond();
            RecipeBuilder.newBuilder(name + "_heat_dist_" + counter, name, 1, 101 + counter, false)
                    .addEnergyPerTickInput(energyUsage / 2)
                    .addInputs(input)
                    .addOutputs(output)
                    .addPostCheckHandler(event -> {
                        DataProcessor processor = NetNodeCache.getCache(event.getController(), DataProcessor.class);
                        if (processor == null) {
                            event.setFailed("?");
                            return;
                        }
                        processor.heatDistributionRecipeCheck(event, heatDistribution);
                        if (!event.isFailure() && !dynamicPatternName.isEmpty()) {
                            ActiveMachineRecipe activeRecipe = event.getActiveRecipe();
                            int parallelism = activeRecipe.getParallelism();
                            IDynamicPatternInfo dynamicPattern = event.getController().getDynamicPattern(dynamicPatternName);
                            if (dynamicPattern != null) {
                                int storedHU = processor.getStoredHU();
                                int maxParallelism = Math.min(storedHU / heatDistribution, activeRecipe.getMaxParallelism());
                                if (parallelism > dynamicPattern.getSize() || parallelism > maxParallelism) {
                                    event.setParallelism(Math.min(dynamicPattern.getSize(), maxParallelism));
                                }
                            } else {
                                if (parallelism > 1) {
                                    event.setParallelism(1);
                                }
                            }
                        }
                    })
                    .addFactoryFinishHandler(event -> {
                        DataProcessor processor = NetNodeCache.getCache(event.getController(), DataProcessor.class);
                        if (processor != null) processor.setStoredHU(processor.getStoredHU() - (heatDistribution * event.getActiveRecipe().getParallelism()));
                    })
                    .addRecipeTooltip(FMLCommonHandler.instance().getSide().isClient()
                            ? new String[]{I18n.format("novaeng.hypernet.radiator.tooltip", heatDistribution * 20)}
                            : new String[0])
                    .setThreadName(PROCESSOR_RADIATOR_THREAD_NAME)
                    .build();
            counter++;
        }
    }

    @ZenMethod
    public DataProcessorType addRadiatorIngredient(final int heatDistribution, final IIngredient[] ingredients, final IIngredient[] outputs) {
        radiatorIngredientList.add(new Tuple<>(new Tuple<>(ingredients, outputs), heatDistribution));
        return this;
    }

    @ZenGetter("heatDistribution")
    public int getHeatDistribution() {
        return heatDistribution;
    }

    @ZenGetter("overheatThreshold")
    public int getOverheatThreshold() {
        return overheatThreshold;
    }

    @ZenGetter("dynamicPatternName")
    public String getDynamicPatternName() {
        return dynamicPatternName;
    }

    @ZenMethod
    public DataProcessorType setDynamicPatternName(final String dynamicPatternName) {
        this.dynamicPatternName = dynamicPatternName;
        return this;
    }
}
