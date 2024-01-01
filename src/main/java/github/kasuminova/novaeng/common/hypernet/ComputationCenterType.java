package github.kasuminova.novaeng.common.hypernet;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IIngredient;
import github.kasuminova.novaeng.common.hypernet.base.NetNodeTypeRepairable;
import hellfirepvp.modularmachinery.common.integration.crafttweaker.MachineModifier;
import hellfirepvp.modularmachinery.common.integration.crafttweaker.RecipeBuilder;
import hellfirepvp.modularmachinery.common.machine.factory.FactoryRecipeThread;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.Tuple;
import net.minecraftforge.fml.common.FMLCommonHandler;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenMethod;

import java.util.List;

@ZenRegister
@ZenClass("novaeng.hypernet.ComputationCenterType")
public class ComputationCenterType extends NetNodeTypeRepairable {
    public static final String CENTER_WORKING_THREAD_NAME = "novaeng.hypernet.data_forwarding_processor";

    private final int maxConnections;
    private final double maxComputationPointCarrying;
    private final int circuitDurability;
    private final int minCircuitConsumeAmount;
    private final int maxCircuitConsumeAmount;
    private final float circuitConsumeChance;

    public ComputationCenterType(final String typeName,
                                 final long energyUsage,
                                 final int maxConnections,
                                 final float maxComputationPointCarrying,
                                 final int circuitDurability,
                                 final int minCircuitConsumeAmount,
                                 final int maxCircuitConsumeAmount,
                                 final float circuitConsumeChance) {
        super(typeName, energyUsage);
        this.maxConnections = maxConnections;
        this.maxComputationPointCarrying = maxComputationPointCarrying;
        this.circuitDurability = circuitDurability;
        this.minCircuitConsumeAmount = minCircuitConsumeAmount;
        this.maxCircuitConsumeAmount = maxCircuitConsumeAmount;
        this.circuitConsumeChance = circuitConsumeChance;
    }

    @ZenMethod
    public static ComputationCenterType create(final String typeName,
                                               final long energyUsage,
                                               final int maxConnections,
                                               final float maxComputationPointCarrying,
                                               final int circuitDurability,
                                               final int minCircuitConsumeAmount,
                                               final int maxCircuitConsumeAmount,
                                               final float circuitConsumeChance) {
        return new ComputationCenterType(typeName,
                energyUsage,
                maxConnections,
                maxComputationPointCarrying,
                circuitDurability,
                minCircuitConsumeAmount,
                maxCircuitConsumeAmount,
                circuitConsumeChance);
    }

    public void registerRecipesAndThreads() {
        String name = typeName;
        MachineModifier.addCoreThread(name, FactoryRecipeThread.createCoreThread(CENTER_WORKING_THREAD_NAME));
        MachineModifier.addCoreThread(name, FactoryRecipeThread.createCoreThread(FIX_THREAD_NAME));

        RecipeBuilder.newBuilder(name + "_working", name, 20, 100, false)
                .addEnergyPerTickInput(energyUsage)
                .addCheckHandler(event ->
                        ComputationCenter.from(event.getController()).onRecipeCheck(event))
                .addFactoryPreTickHandler(event ->
                        ComputationCenter.from(event.getController()).onWorkingTick())
                .addRecipeTooltip("novaeng.hypernet.computation_center.working.tooltip.0")
                .addRecipeTooltip("novaeng.hypernet.computation_center.working.tooltip.1")
                .setThreadName(CENTER_WORKING_THREAD_NAME)
                .build();

        int counter = 0;
        for (final Tuple<List<IIngredient>, Integer> fixIngredient : fixIngredientList) {
            List<IIngredient> input = fixIngredient.getFirst();
            int durability = fixIngredient.getSecond();
            RecipeBuilder.newBuilder(name + "_fix_" + counter, name, 100, 101 + counter, false)
                    .addEnergyPerTickInput(energyUsage / 2)
                    .addInputs(input.toArray(new IIngredient[0]))
                    .addCheckHandler(event ->
                            ComputationCenter.from(event.getController()).onDurabilityFixRecipeCheck(event, durability))
                    .addFactoryFinishHandler(event ->
                            ComputationCenter.from(event.getController()).fixCircuit(durability))
                    .addRecipeTooltip(FMLCommonHandler.instance().getSide().isClient()
                            ? new String[]{I18n.format("novaeng.hypernet.repair.tooltip", durability)}
                            : new String[0])
                    .setThreadName(FIX_THREAD_NAME)
                    .build();

            counter++;
        }
    }

    @ZenMethod
    public ComputationCenterType addFixIngredient( final int durability, final IIngredient... ingredients) {
        return (ComputationCenterType) super.addFixIngredient(durability, ingredients);
    }

    @ZenGetter("maxConnections")
    public int getMaxConnections() {
        return maxConnections;
    }

    @ZenGetter("maxComputationPointCarrying")
    public double getMaxComputationPointCarrying() {
        return maxComputationPointCarrying;
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
}
