package github.kasuminova.novaeng.common.hypernet.research;

import crafttweaker.annotations.ZenRegister;
import github.kasuminova.novaeng.common.hypernet.NetNodeCache;
import github.kasuminova.novaeng.common.hypernet.base.NetNodeType;
import hellfirepvp.modularmachinery.common.integration.crafttweaker.MachineModifier;
import hellfirepvp.modularmachinery.common.integration.crafttweaker.RecipeBuilder;
import hellfirepvp.modularmachinery.common.machine.factory.FactoryRecipeThread;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenRegister
@ZenClass("novaeng.hypernet.ResearchStationType")
public class ResearchStationType extends NetNodeType {
    public static final String RESEARCH_STATION_WORKING_THREAD_NAME = "novaeng.hypernet.research_station.hypercol";

    private final float maxTechLevel;

    public ResearchStationType(final String typeName,
                               final long energyUsage,
                               final float maxTechLevel)
    {
        super(typeName, energyUsage);
        this.maxTechLevel = maxTechLevel;
    }

    @ZenMethod
    public static ResearchStationType create(final String typeName,
                                             final long energyUsage,
                                             final float maxTechLevel)
    {
        return new ResearchStationType(typeName, energyUsage, maxTechLevel);
    }

    public void registerRecipesAndThreads() {
        String name = typeName;
        MachineModifier.addCoreThread(name, FactoryRecipeThread.createCoreThread(RESEARCH_STATION_WORKING_THREAD_NAME));

        RecipeBuilder.newBuilder(name + "_working", name, 100, 100, false)
                .addEnergyPerTickInput(energyUsage)
                .addCheckHandler(event -> {
                    ResearchStation station = NetNodeCache.getCache(event.getController(), ResearchStation.class);
                    if (station != null) {
                        station.onRecipeCheck(event);
                    }
                })
                .addFactoryPreTickHandler(event -> {
                    ResearchStation station = NetNodeCache.getCache(event.getController(), ResearchStation.class);
                    if (station != null) {
                        station.onWorkingTick(event);
                    }
                })
                .addRecipeTooltip(
                        "novaeng.hypernet.research_station.working.tooltip.0",
                        "novaeng.hypernet.research_station.working.tooltip.1"
                )
                .setThreadName(RESEARCH_STATION_WORKING_THREAD_NAME)
                .build();
    }

    @ZenGetter("maxTechLevel")
    public float getMaxTechLevel() {
        return maxTechLevel;
    }
}
