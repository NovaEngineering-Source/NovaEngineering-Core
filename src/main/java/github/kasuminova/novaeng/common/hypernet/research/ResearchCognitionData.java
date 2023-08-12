package github.kasuminova.novaeng.common.hypernet.research;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import github.kasuminova.novaeng.common.registry.RegistryHyperNet;
import hellfirepvp.modularmachinery.common.util.ItemUtils;
import net.minecraft.item.ItemStack;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenMethod;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
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

    private final List<String> descriptions;
    private final List<String> unlockedDescriptions;
    private final List<ResearchCognitionData> dependencies;

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
        this.translatedName = translatedName;
        this.previewStack = previewStack.getCount() != 1 ? ItemUtils.copyStackWithSize(previewStack, 1) : previewStack;
        this.techLevel = techLevel;
        this.requiredPoints = requiredPoints;
        this.minComputationPointPerTick = minComputationPointPerTick;
        this.descriptions = descriptions;
        this.unlockedDescriptions = unlockedDescriptions;
        this.dependencies = dependencies;
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

    public List<String> getDescriptions() {
        return Collections.unmodifiableList(descriptions);
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
