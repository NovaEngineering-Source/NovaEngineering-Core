package github.kasuminova.novaeng.common.registry;

import com.google.common.base.Preconditions;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import github.kasuminova.mmce.common.upgrade.UpgradeType;
import github.kasuminova.novaeng.common.hypernet.*;
import github.kasuminova.novaeng.common.hypernet.research.ResearchCognitionData;
import github.kasuminova.novaeng.common.hypernet.research.ResearchStation;
import github.kasuminova.novaeng.common.hypernet.research.ResearchStationType;
import github.kasuminova.novaeng.common.hypernet.upgrade.type.ProcessorModuleCPUType;
import github.kasuminova.novaeng.common.hypernet.upgrade.type.ProcessorModuleGPUType;
import github.kasuminova.novaeng.common.hypernet.upgrade.type.ProcessorModuleRAMType;
import hellfirepvp.modularmachinery.ModularMachinery;
import hellfirepvp.modularmachinery.common.machine.DynamicMachine;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.command.ICommandSender;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

@ZenRegister
@ZenClass("novaeng.hypernet.RegistryHyperNet")
public class RegistryHyperNet {
    private static Item hyperNetConnectCard = Items.AIR;

    private static final Set<ResourceLocation> SUPPORTED_MACHINERY = new ObjectOpenHashSet<>();
    private static final Set<ResourceLocation> COMPUTATION_CENTERS = new ObjectOpenHashSet<>();

    private static final Map<ResourceLocation, Class<? extends NetNode>> REGISTERED_NODE_TYPE = new Object2ObjectOpenHashMap<>();

    private static final Map<String, ComputationCenterType> COMPUTATION_CENTER_TYPE = new Object2ObjectOpenHashMap<>();
    private static final Map<String, DataProcessorType>     DATA_PROCESSOR_TYPE     = new Object2ObjectOpenHashMap<>();
    private static final Map<String, ResearchStationType>   RESEARCH_STATION_TYPE   = new Object2ObjectOpenHashMap<>();
    private static final Map<String, DatabaseType>          DATABASE_TYPE           = new Object2ObjectOpenHashMap<>();

    private static final Map<String, ResearchCognitionData> RESEARCH_COGNITION = new Object2ObjectLinkedOpenHashMap<>();

    private static final Map<UpgradeType, ProcessorModuleCPUType> DATA_PROCESSOR_MODULE_CPU_TYPE = new Object2ObjectOpenHashMap<>();
    private static final Map<UpgradeType, ProcessorModuleRAMType> DATA_PROCESSOR_MODULE_RAM_TYPE = new Object2ObjectOpenHashMap<>();

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
    public static void addHyperNetSupportedMachinery(@Nonnull final String name) {
        ResourceLocation registryName = new ResourceLocation(ModularMachinery.MODID, name);
        SUPPORTED_MACHINERY.add(registryName);
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
    public static void addComputationCenterType(@Nonnull final ComputationCenterType type) {
        COMPUTATION_CENTER_TYPE.put(type.getTypeName(), type);
        addHyperNetSupportedMachinery(new ResourceLocation(ModularMachinery.MODID, type.getTypeName()));
    }

    @ZenMethod
    public static void addDataProcessorType(@Nonnull final DataProcessorType type) {
        DATA_PROCESSOR_TYPE.put(type.getTypeName(), type);
        registerHyperNetNode(new ResourceLocation(ModularMachinery.MODID, type.getTypeName()), DataProcessor.class);
    }

    @ZenMethod
    public static void addResearchStationType(@Nonnull final ResearchStationType type) {
        RESEARCH_STATION_TYPE.put(type.getTypeName(), type);
        registerHyperNetNode(new ResourceLocation(ModularMachinery.MODID, type.getTypeName()), ResearchStation.class);
    }

    @ZenMethod
    public static void addDatabaseType(@Nonnull final DatabaseType type) {
        DATABASE_TYPE.put(type.getTypeName(), type);
        registerHyperNetNode(new ResourceLocation(ModularMachinery.MODID, type.getTypeName()), Database.class);
    }

    @ZenMethod
    public static ComputationCenterType getComputationCenterType(@Nonnull final String typeName) {
        return COMPUTATION_CENTER_TYPE.get(typeName);
    }

    public static Collection<ComputationCenterType> getAllComputationsCenterTypes() {
        return Collections.unmodifiableCollection(COMPUTATION_CENTER_TYPE.values());
    }

    @ZenMethod
    public static DataProcessorType getDataProcessorType(@Nonnull final String typeName) {
        return DATA_PROCESSOR_TYPE.get(typeName);
    }

    public static Collection<DataProcessorType> getAllDataProcessorTypes() {
        return Collections.unmodifiableCollection(DATA_PROCESSOR_TYPE.values());
    }

    @ZenMethod
    public static ResearchStationType getResearchStationType(@Nonnull final String typeName) {
        return RESEARCH_STATION_TYPE.get(typeName);
    }

    public static Collection<ResearchStationType> getAllResearchStationTypes() {
        return Collections.unmodifiableCollection(RESEARCH_STATION_TYPE.values());
    }

    @ZenMethod
    public static DatabaseType getDatabaseType(@Nonnull final String typeName) {
        return DATABASE_TYPE.get(typeName);
    }

    public static Collection<DatabaseType> getAllDatabaseTypes() {
        return Collections.unmodifiableCollection(DATABASE_TYPE.values());
    }
    
    public static <T extends NetNode> void registerHyperNetNode(@Nonnull final ResourceLocation registryName,
                                                                @Nonnull final Class<T> nodeClass)
    {
        Preconditions.checkNotNull(registryName);
        Preconditions.checkNotNull(nodeClass);

        REGISTERED_NODE_TYPE.put(registryName, nodeClass);
        addHyperNetSupportedMachinery(registryName);
    }

    public static Class<? extends NetNode> getNodeType(@Nonnull final DynamicMachine machine) {
        return getNodeType(machine.getRegistryName());
    }

    public static Class<? extends NetNode> getNodeType(@Nonnull final ResourceLocation registryName) {
        return REGISTERED_NODE_TYPE.get(registryName);
    }

    @ZenMethod
    public static void addResearchCognitionData(ResearchCognitionData data) {
        RESEARCH_COGNITION.put(data.getResearchName(), data);
    }

    @ZenMethod
    public static ResearchCognitionData getResearchCognitionData(@Nonnull final String researchName) {
        return RESEARCH_COGNITION.get(researchName);
    }

    public static Collection<ResearchCognitionData> getAllResearchCognitionData() {
        return Collections.unmodifiableCollection(RESEARCH_COGNITION.values());
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

        REGISTERED_NODE_TYPE.clear();
        
        DATA_PROCESSOR_TYPE.clear();
        DATABASE_TYPE.clear();
        
        RESEARCH_STATION_TYPE.clear();
        RESEARCH_COGNITION.clear();

        DATA_PROCESSOR_MODULE_CPU_TYPE.clear();
        DATA_PROCESSOR_MODULE_RAM_TYPE.clear();
    }

    public static void clearRegistry(ICommandSender sender) {
        sender.sendMessage(new TextComponentString(String.format(
                "[NovaEngineering-Core] Cleared %s supported machinery registry.", SUPPORTED_MACHINERY.size()))
        );
        sender.sendMessage(new TextComponentString(String.format(
                "[NovaEngineering-Core] Cleared %s computation center type registry.", COMPUTATION_CENTER_TYPE.size()))
        );
        sender.sendMessage(new TextComponentString(String.format(
                "[NovaEngineering-Core] Cleared %s data processor type registry.", DATA_PROCESSOR_TYPE.size()))
        );
        sender.sendMessage(new TextComponentString(String.format(
                "[NovaEngineering-Core] Cleared %s research station type registry.", RESEARCH_STATION_TYPE.size()))
        );
        sender.sendMessage(new TextComponentString(String.format(
                "[NovaEngineering-Core] Cleared %s database type registry.", DATABASE_TYPE.size()))
        );
        sender.sendMessage(new TextComponentString(String.format(
                "[NovaEngineering-Core] Cleared %s registered node type.", REGISTERED_NODE_TYPE.size()))
        );
        sender.sendMessage(new TextComponentString(String.format(
                "[NovaEngineering-Core] Cleared %s research cognition registry.", RESEARCH_COGNITION.size()))
        );
        sender.sendMessage(new TextComponentString(String.format(
                "[NovaEngineering-Core] Cleared %s data processor cpu type registry.", DATA_PROCESSOR_MODULE_CPU_TYPE.size()))
        );
        sender.sendMessage(new TextComponentString(String.format(
                "[NovaEngineering-Core] Cleared %s data processor ram type registry.", DATA_PROCESSOR_MODULE_RAM_TYPE.size()))
        );

        clearRegistry();
    }
}
