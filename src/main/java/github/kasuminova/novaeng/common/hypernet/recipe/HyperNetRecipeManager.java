package github.kasuminova.novaeng.common.hypernet.recipe;

import github.kasuminova.novaeng.common.hypernet.ComputationCenterType;
import github.kasuminova.novaeng.common.hypernet.DataProcessorType;
import github.kasuminova.novaeng.common.hypernet.DatabaseType;
import github.kasuminova.novaeng.common.hypernet.research.ResearchStationType;
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
