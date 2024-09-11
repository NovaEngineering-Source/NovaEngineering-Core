package github.kasuminova.novaeng.common.hypernet.old.recipe;

import github.kasuminova.novaeng.common.hypernet.old.ComputationCenterType;
import github.kasuminova.novaeng.common.hypernet.old.DataProcessorType;
import github.kasuminova.novaeng.common.hypernet.old.DatabaseType;
import github.kasuminova.novaeng.common.hypernet.old.research.ResearchStationType;
import github.kasuminova.novaeng.common.registry.RegistryHyperNet;

public class HyperNetRecipeManager {

    public static void registerRecipes() {
        registerHyperNetRecipes();
    }

    private static void registerHyperNetRecipes() {
        RegistryHyperNet.getAllComputationsCenterTypes().forEach(ComputationCenterType::registerRecipesAndThreads);
        RegistryHyperNet.getAllDataProcessorTypes().forEach(DataProcessorType::registerRecipesAndThreads);
        RegistryHyperNet.getAllDatabaseTypes().forEach(DatabaseType::registerRecipesAndThreads);
        RegistryHyperNet.getAllResearchStationTypes().forEach(ResearchStationType::registerRecipesAndThreads);
    }
}
