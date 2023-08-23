package github.kasuminova.novaeng.common.hypernet;

import crafttweaker.annotations.ZenRegister;
import github.kasuminova.novaeng.common.hypernet.base.NetNodeType;
import hellfirepvp.modularmachinery.common.integration.crafttweaker.MachineModifier;
import hellfirepvp.modularmachinery.common.integration.crafttweaker.RecipeBuilder;
import hellfirepvp.modularmachinery.common.machine.factory.FactoryRecipeThread;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenRegister
@ZenClass("novaeng.hypernet.DatabaseType")
public class DatabaseType extends NetNodeType {
    public static final String DATABASE_WORKING_THREAD_NAME = "novaeng.hypernet.data_forwarding_processor";

    private final int maxResearchDataStoreSize;

    public DatabaseType(final String typeName,
                        final long energyUsage,
                        final int maxResearchDataStoreSize)
    {
        super(typeName, energyUsage);
        this.maxResearchDataStoreSize = maxResearchDataStoreSize;
    }

    @ZenMethod
    public static DatabaseType create(final String typeName,
                                      final long energyUsage,
                                      final int maxResearchDataStoreSize)
    {
        return new DatabaseType(typeName, energyUsage, maxResearchDataStoreSize);
    }

    public void registerRecipesAndThreads() {
        String name = typeName;
        MachineModifier.addCoreThread(name, FactoryRecipeThread.createCoreThread(DATABASE_WORKING_THREAD_NAME));

        RecipeBuilder.newBuilder(name + "_working", name, 20, 100, false)
                .addEnergyPerTickInput(energyUsage)
                .addFactoryPreTickHandler(event -> {
                    Database database = NetNodeCache.getCache(event.getController(), Database.class);
                    if (database != null) {
                        database.onWorkingTick(event);
                    }
                })
                .addRecipeTooltip(
                        "novaeng.hypernet.database.working.tooltip.0",
                        "novaeng.hypernet.database.working.tooltip.1"
                )
                .setThreadName(DATABASE_WORKING_THREAD_NAME)
                .build();
    }

    @ZenGetter("maxResearchDataStoreSize")
    public int getMaxResearchDataStoreSize() {
        return maxResearchDataStoreSize;
    }
}
