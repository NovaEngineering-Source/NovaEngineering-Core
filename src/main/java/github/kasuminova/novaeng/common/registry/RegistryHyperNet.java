package github.kasuminova.novaeng.common.registry;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import github.kasuminova.mmce.common.upgrade.UpgradeType;
import github.kasuminova.novaeng.common.hypernet.*;
import github.kasuminova.novaeng.common.hypernet.research.ResearchCognitionData;
import github.kasuminova.novaeng.common.hypernet.research.ResearchStationType;
import github.kasuminova.novaeng.common.hypernet.upgrade.type.ProcessorModuleCPUType;
import github.kasuminova.novaeng.common.hypernet.upgrade.type.ProcessorModuleGPUType;
import github.kasuminova.novaeng.common.hypernet.upgrade.type.ProcessorModuleRAMType;
import hellfirepvp.modularmachinery.ModularMachinery;
import hellfirepvp.modularmachinery.common.machine.DynamicMachine;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import stanhebben.zenscript.annotations.NotNull;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@ZenRegister
@ZenClass("novaeng.hypernet.RegistryHyperNet")
public class RegistryHyperNet {
    private static Item hyperNetConnectCard = null;

    private static final Set<ResourceLocation> SUPPORTED_MACHINERY = new HashSet<>();
    private static final Set<ResourceLocation> COMPUTATION_CENTERS = new HashSet<>();

    private static final Map<String, ComputationCenterType> COMPUTATION_CENTER_TYPE = new HashMap<>();
    private static final Map<String, DataProcessorType>     DATA_PROCESSOR_TYPE     = new HashMap<>();
    private static final Map<String, ResearchStationType>   RESEARCH_STATION_TYPE   = new HashMap<>();
    private static final Map<String, DatabaseType>          DATABASE_TYPE           = new HashMap<>();
    private static final Map<String, ResearchCognitionData> RESEARCH_COGNITION      = new HashMap<>();

    private static final Map<UpgradeType, ProcessorModuleCPUType> DATA_PROCESSOR_MODULE_CPU_TYPE = new HashMap<>();
    private static final Map<UpgradeType, ProcessorModuleRAMType> DATA_PROCESSOR_MODULE_RAM_TYPE = new HashMap<>();

    public static boolean isHyperNetSupported(@Nullable final DynamicMachine machine) {
        if (machine == null) {
            return false;
        }
        return isHyperNetSupported(machine.getRegistryName());
    }

    public static boolean isHyperNetSupported(@Nonnull final ResourceLocation registryName) {
        return SUPPORTED_MACHINERY.contains(registryName);
    }

    public static void addHyperNetSupportedMachinery(@Nonnull final ResourceLocation registryName) {
        SUPPORTED_MACHINERY.add(registryName);
    }

    @ZenMethod
    public static void addHyperNetSupportedMachinery(@Nonnull final String registryName) {
        SUPPORTED_MACHINERY.add(new ResourceLocation(ModularMachinery.MODID, registryName));
    }

    @ZenMethod
    public static void addComputationCenter(@Nonnull String registryName) {
        addComputationCenter(new ResourceLocation(ModularMachinery.MODID, registryName));
    }

    public static void addComputationCenter(@Nonnull ResourceLocation registryName) {
        COMPUTATION_CENTERS.add(registryName);
    }

    @ZenMethod
    public static boolean isComputationCenter(@Nonnull String registryName) {
        return isComputationCenter(new ResourceLocation(ModularMachinery.MODID, registryName));
    }

    public static boolean isComputationCenter(@Nonnull ResourceLocation registryName) {
        return COMPUTATION_CENTERS.contains(registryName);
    }

    @ZenMethod
    public static void addComputationCenterType(@NotNull final ComputationCenterType type) {
        COMPUTATION_CENTER_TYPE.put(type.getTypeName(), type);
    }

    @ZenMethod
    public static ComputationCenterType getComputationCenterType(@NotNull final String typeName) {
        return COMPUTATION_CENTER_TYPE.get(typeName);
    }

    @ZenMethod
    public static void addDataProcessorType(@NotNull final DataProcessorType type) {
        DATA_PROCESSOR_TYPE.put(type.getTypeName(), type);
    }

    @ZenMethod
    public static DataProcessorType getDataProcessorType(@NotNull final String typeName) {
        return DATA_PROCESSOR_TYPE.get(typeName);
    }

    @ZenMethod
    public static void addResearchStationType(@NotNull final ResearchStationType type) {
        RESEARCH_STATION_TYPE.put(type.getTypeName(), type);
    }

    @ZenMethod
    public static ResearchStationType getResearchStationType(@NotNull final String typeName) {
        return RESEARCH_STATION_TYPE.get(typeName);
    }

    @ZenMethod
    public static void addDatabaseType(@NotNull final DatabaseType type) {
        DATABASE_TYPE.put(type.getTypeName(), type);
    }

    @ZenMethod
    public static DatabaseType getDatabaseType(@NotNull final String typeName) {
        return DATABASE_TYPE.get(typeName);
    }

    @ZenMethod
    public static void addResearchCognitionData(ResearchCognitionData data) {
        RESEARCH_COGNITION.put(data.getResearchName(), data);
    }

    @ZenMethod
    public static ResearchCognitionData getResearchCognitionData(@NotNull final String researchName) {
        return RESEARCH_COGNITION.get(researchName);
    }

    @ZenMethod
    public static ProcessorModuleCPUType getDataProcessorModuleCPUType(final UpgradeType upgradeType) {
        return DATA_PROCESSOR_MODULE_CPU_TYPE.get(upgradeType);
    }

    @ZenMethod
    public static ProcessorModuleGPUType getDataProcessorModuleGPUType(final UpgradeType upgradeType) {
        ProcessorModuleCPUType gpuType = DATA_PROCESSOR_MODULE_CPU_TYPE.get(upgradeType);
        return !(gpuType instanceof ProcessorModuleGPUType) ? null : (ProcessorModuleGPUType) gpuType;
    }

    @ZenMethod
    public static ProcessorModuleRAMType getDataProcessorModuleRAMType(final UpgradeType upgradeType) {
        return DATA_PROCESSOR_MODULE_RAM_TYPE.get(upgradeType);
    }

    public static void addDataProcessorModuleCPUType(final UpgradeType upgradeType, final ProcessorModuleCPUType type) {
        DATA_PROCESSOR_MODULE_CPU_TYPE.put(upgradeType, type);
    }

    public static void addDataProcessorModuleRAMType(final UpgradeType upgradeType, final ProcessorModuleRAMType type) {
        DATA_PROCESSOR_MODULE_RAM_TYPE.put(upgradeType, type);
    }

    @ZenMethod
    public static void setHyperNetConnectCard(IItemStack stackCT) {
        hyperNetConnectCard = CraftTweakerMC.getItemStack(stackCT).getItem();
    }

    public static void setHyperNetConnectCard(final Item hyperNetConnectCard) {
        RegistryHyperNet.hyperNetConnectCard = hyperNetConnectCard;
    }

    public static Item getHyperNetConnectCard() {
        return hyperNetConnectCard;
    }

    @ZenMethod
    public static void clearRegistry() {
        SUPPORTED_MACHINERY.clear();
        COMPUTATION_CENTER_TYPE.clear();

        DATA_PROCESSOR_TYPE.clear();
        RESEARCH_STATION_TYPE.clear();
        DATABASE_TYPE.clear();
        RESEARCH_COGNITION.clear();

        DATA_PROCESSOR_MODULE_CPU_TYPE.clear();
        DATA_PROCESSOR_MODULE_RAM_TYPE.clear();
    }
}
