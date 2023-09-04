package github.kasuminova.novaeng.common.hypernet.research;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import github.kasuminova.novaeng.common.registry.RegistryHyperNet;
import hellfirepvp.modularmachinery.common.util.ItemUtils;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.FMLCommonHandler;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenMethod;
import stanhebben.zenscript.annotations.ZenSetter;

import java.util.*;
import java.util.stream.Collectors;

@ZenRegister
@ZenClass("novaeng.hypernet.research.ResearchCognitionData")
public class ResearchCognitionData {
    private final String researchName;
    private final String translatedName;

    private final ItemStack previewStack;

    private final float techLevel;
    private final double requiredPoints;
    private final float minComputationPointPerTick;

    private boolean hideByDefault = false;

    private final boolean cycleResearch;
    private final int maxCycle;

    private final List<String> descriptions;
    private final List<String> unlockedDescriptions;
    private final List<ResearchCognitionData> dependencies;

    private final Object2IntOpenHashMap<ResearchCognitionData> cycleDependencies = new Object2IntOpenHashMap<>();

    public ResearchCognitionData(final String researchName,
                                 final String translatedName,
                                 final ItemStack previewStack,
                                 final float techLevel,
                                 final double requiredPoints,
                                 final float minComputationPointPerTick,
                                 final List<String> descriptions,
                                 final List<String> unlockedDescriptions,
                                 final List<ResearchCognitionData> dependencies)
    {
        this.researchName = researchName;
        this.previewStack = previewStack.getCount() != 1 ? ItemUtils.copyStackWithSize(previewStack, 1) : previewStack;
        this.techLevel = techLevel;
        this.requiredPoints = requiredPoints;
        this.minComputationPointPerTick = minComputationPointPerTick;
        this.dependencies = dependencies;
        this.cycleResearch = false;
        this.maxCycle = 0;
        this.translatedName = translatedName;

        if (FMLCommonHandler.instance().getSide().isClient()) {
            this.descriptions = descriptions;
            this.unlockedDescriptions = unlockedDescriptions;
        } else {
            this.descriptions = new ArrayList<>();
            this.unlockedDescriptions = new ArrayList<>();
        }
    }

    public ResearchCognitionData(final String researchName,
                                 final String translatedName,
                                 final ItemStack previewStack,
                                 final float techLevel,
                                 final double requiredPoints,
                                 final float minComputationPointPerTick,
                                 final List<String> descriptions,
                                 final List<String> unlockedDescriptions,
                                 final List<ResearchCognitionData> dependencies,
                                 final int maxCycle)
    {
        this.researchName = researchName;
        this.previewStack = previewStack.getCount() != 1 ? ItemUtils.copyStackWithSize(previewStack, 1) : previewStack;
        this.techLevel = techLevel;
        this.requiredPoints = requiredPoints;
        this.minComputationPointPerTick = minComputationPointPerTick;
        this.dependencies = dependencies;
        this.cycleResearch = true;
        this.maxCycle = maxCycle;

        if (FMLCommonHandler.instance().getSide().isClient()) {
            this.translatedName = translatedName;
            this.descriptions = descriptions;
            this.unlockedDescriptions = unlockedDescriptions;
        } else {
            this.translatedName = "";
            this.descriptions = new ArrayList<>();
            this.unlockedDescriptions = new ArrayList<>();
        }
    }

    @ZenMethod
    public static ResearchCognitionData create(final String researchName,
                                               final String translatedName,
                                               final IItemStack previewStackCT,
                                               final float techLevel,
                                               final double requiredPoints,
                                               final float minComputationPointPerTick,
                                               final String[] descriptions,
                                               final String[] unlockedDescriptions,
                                               final String[] dependenciesArr)
    {
        List<ResearchCognitionData> dependencies = Arrays.stream(dependenciesArr)
                .map(RegistryHyperNet::getResearchCognitionData)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return new ResearchCognitionData(
                researchName,
                translatedName,
                CraftTweakerMC.getItemStack(previewStackCT),
                techLevel,
                requiredPoints,
                minComputationPointPerTick,
                Arrays.asList(descriptions),
                Arrays.asList(unlockedDescriptions),
                dependencies);
    }

    @ZenMethod
    public static ResearchCognitionData createCycle(final String researchName,
                                                    final String translatedName,
                                                    final IItemStack previewStackCT,
                                                    final float techLevel,
                                                    final double requiredPoints,
                                                    final float minComputationPointPerTick,
                                                    final String[] descriptions,
                                                    final String[] unlockedDescriptions,
                                                    final String[] dependenciesArr,
                                                    final int maxCycle)
    {
        List<ResearchCognitionData> dependencies = Arrays.stream(dependenciesArr)
                .map(RegistryHyperNet::getResearchCognitionData)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return new ResearchCognitionData(
                researchName,
                translatedName,
                CraftTweakerMC.getItemStack(previewStackCT),
                techLevel,
                requiredPoints,
                minComputationPointPerTick,
                Arrays.asList(descriptions),
                Arrays.asList(unlockedDescriptions),
                dependencies,
                maxCycle);
    }

    @ZenMethod
    public ResearchCognitionData addCycleDependence(String researchName, int minCycle) {
        ResearchCognitionData data = RegistryHyperNet.getResearchCognitionData(researchName);
        if (data != null) {
            cycleDependencies.addTo(data, minCycle);
        }
        return this;
    }

    @ZenMethod
    public ResearchCognitionData addCycleDependence(ResearchCognitionData data, int minCycle) {
        cycleDependencies.addTo(data, minCycle);
        return this;
    }

    @ZenGetter("researchName")
    public String getResearchName() {
        return researchName;
    }

    @ZenGetter("translatedName")
    public String getTranslatedName() {
        return translatedName;
    }

    public ItemStack getPreviewStack() {
        return previewStack;
    }

    @ZenGetter("previewStack")
    public IItemStack getPreviewStackCT() {
        return CraftTweakerMC.getIItemStack(previewStack);
    }

    @ZenGetter("techLevel")
    public float getTechLevel() {
        return techLevel;
    }

    @ZenGetter("requiredPoints")
    public double getRequiredPoints() {
        return requiredPoints;
    }

    @ZenGetter("minComputationPointPerTick")
    public float getMinComputationPointPerTick() {
        return minComputationPointPerTick;
    }

    @ZenGetter("hideByDefault")
    public boolean isHideByDefault() {
        return hideByDefault;
    }

    @ZenMethod
    @ZenSetter("hideByDefault")
    public void setHideByDefault(final boolean hideByDefault) {
        this.hideByDefault = hideByDefault;
    }

    public List<String> getDescriptions() {
        return Collections.unmodifiableList(descriptions);
    }

    @ZenGetter("cycleResearch")
    public boolean isCycleResearch() {
        return cycleResearch;
    }

    @ZenGetter("maxCycle")
    public int getMaxCycle() {
        return maxCycle;
    }

    @ZenGetter("descriptions")
    public String[] getDescriptionsArray() {
        return descriptions.toArray(new String[0]);
    }

    public List<String> getUnlockedDescriptions() {
        return Collections.unmodifiableList(unlockedDescriptions);
    }

    @ZenGetter("unlockedDescriptions")
    public String[] getUnlockedDescriptionsArray() {
        return unlockedDescriptions.toArray(new String[0]);
    }

    public List<ResearchCognitionData> getDependencies() {
        return Collections.unmodifiableList(dependencies);
    }

    public Object2IntOpenHashMap<ResearchCognitionData> getCycleDependencies() {
        return cycleDependencies;
    }

    @Override
    public int hashCode() {
        return researchName.hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ResearchCognitionData)) {
            return false;
        }
        final ResearchCognitionData another = (ResearchCognitionData) obj;
        return researchName.equals(another.researchName);
    }
}
