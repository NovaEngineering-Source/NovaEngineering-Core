package github.kasuminova.novaeng.common.hypernet;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IIngredient;
import github.kasuminova.novaeng.common.hypernet.base.NetNodeTypeRepairable;
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

import java.util.List;

@ZenRegister
@ZenClass("novaeng.hypernet.DataProcessorType")
public class DataProcessorType extends NetNodeTypeRepairable {
    public static final String PROCESSOR_WORKING_THREAD_NAME = "novaeng.hypernet.data_processor.compute_scheduler";

    private final int heatDistribution;
    private final int overheatThreshold;
    private final int circuitDurability;
    private final int minCircuitConsumeAmount;
    private final int maxCircuitConsumeAmount;
    private final float circuitConsumeChance;

    private String dynamicPatternName = "";

    public DataProcessorType(final String typeName,
                             final long energyUsage,
                             final int heatDistribution,
                             final int overheatThreshold,
                             final int circuitDurability,
                             final int minCircuitConsumeAmount,
                             final int maxCircuitConsumeAmount,
                             final float circuitConsumeChance)
    {
        super(typeName, energyUsage);
        this.heatDistribution = heatDistribution;
        this.overheatThreshold = overheatThreshold;
        this.circuitDurability = circuitDurability;
        this.minCircuitConsumeAmount = minCircuitConsumeAmount;
        this.maxCircuitConsumeAmount = maxCircuitConsumeAmount;
        this.circuitConsumeChance = circuitConsumeChance;
    }

    @ZenMethod
    public static DataProcessorType create(final String typeName,
                                           final long energyUsage,
                                           final int heatDistribution,
                                           final int overheatThreshold,
                                           final int circuitDurability,
                                           final int minCircuitConsumeAmount,
                                           final int maxCircuitConsumeAmount,
                                           final float circuitConsumeChance)
    {
        return new DataProcessorType(typeName,
                energyUsage,
                heatDistribution,
                overheatThreshold,
                circuitDurability,
                minCircuitConsumeAmount,
                maxCircuitConsumeAmount,
                circuitConsumeChance);
    }

    public void registerRecipesAndThreads() {
        String name = typeName;
        MachineModifier.addCoreThread(name, FactoryRecipeThread.createCoreThread(PROCESSOR_WORKING_THREAD_NAME));
        MachineModifier.addCoreThread(name, FactoryRecipeThread.createCoreThread(FIX_THREAD_NAME));

        MMEvents.onStructureUpdate(name, event -> {
            DataProcessor processor = NetNodeCache.getCache(event.getController(), DataProcessor.class);
            if (processor != null) {
                processor.onStructureUpdate();
            }
        });

        RecipeBuilder.newBuilder(name + "_working", name, 20, 100, false)
                .addEnergyPerTickInput(energyUsage)
                .addCheckHandler(event -> {
                    DataProcessor processor = NetNodeCache.getCache(event.getController(), DataProcessor.class);
                    if (processor != null) {
                        processor.onRecipeCheck(event);
                    }
                })
                .addFactoryPreTickHandler(event -> {
                    DataProcessor processor = NetNodeCache.getCache(event.getController(), DataProcessor.class);
                    if (processor != null) {
                        processor.onWorkingTick(event);
                    }
                })
                .addRecipeTooltip(
                        "novaeng.hypernet.data_processor.working.tooltip.0",
                        "novaeng.hypernet.data_processor.working.tooltip.1"
                )
                .setThreadName(PROCESSOR_WORKING_THREAD_NAME)
                .build();

        int counter = 0;
        for (final Tuple<List<IIngredient>, Integer> fixIngredient : fixIngredientList) {
            List<IIngredient> input = fixIngredient.getFirst();
            int durability = fixIngredient.getSecond();
            RecipeBuilder.newBuilder(name + "_fix_" + counter, name, 100, 101 + counter, false)
                    .addEnergyPerTickInput(energyUsage / 2)
                    .addInputs(input.toArray(new IIngredient[0]))
                    .addCheckHandler(event -> {
                        DataProcessor processor = NetNodeCache.getCache(event.getController(), DataProcessor.class);
                        if (processor != null) {
                            processor.onDurabilityFixRecipeCheck(event, durability);
                        }
                    })
                    .addFactoryFinishHandler(event -> {
                        DataProcessor processor = NetNodeCache.getCache(event.getController(), DataProcessor.class);
                        if (processor != null) {
                            processor.fixCircuit(durability);
                        }
                    })
                    .addRecipeTooltip(FMLCommonHandler.instance().getSide().isClient()
                            ? new String[]{I18n.format("novaeng.hypernet.repair.tooltip", durability)}
                            : new String[0])
                    .setThreadName(FIX_THREAD_NAME)
                    .build();

            counter++;
        }
    }

    @ZenMethod
    public DataProcessorType addFixIngredient( final int durability, final IIngredient... ingredients) {
        return (DataProcessorType) super.addFixIngredient(durability, ingredients);
    }

    @ZenGetter("heatDistribution")
    public int getHeatDistribution() {
        return heatDistribution;
    }

    @ZenGetter("overheatThreshold")
    public int getOverheatThreshold() {
        return overheatThreshold;
    }

    @ZenGetter("circuitDurability")
    public int getCircuitDurability() {
        return circuitDurability;
    }

    @ZenGetter("minCircuitConsumeAmount")
    public int getMinCircuitConsumeAmount() {
        return minCircuitConsumeAmount;
    }

    @ZenGetter("maxCircuitConsumeAmount")
    public int getMaxCircuitConsumeAmount() {
        return maxCircuitConsumeAmount;
    }

    @ZenGetter("circuitConsumeChance")
    public float getCircuitConsumeChance() {
        return circuitConsumeChance;
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
