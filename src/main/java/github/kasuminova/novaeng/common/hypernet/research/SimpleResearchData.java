package github.kasuminova.novaeng.common.hypernet.research;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings({"FeatureEnvy", "unused"})
public class SimpleResearchData implements Serializable {
    private final String researchName;

    private final float techLevel;
    private final double requiredPoints;
    private final float minComputationPointPerTick;

    private final List<String> descriptions;
    private final List<String> unlockedDescriptions;
    private final List<String> dependencies;

    public SimpleResearchData(final String researchName,
                              final float techLevel,
                              final double requiredPoints,
                              final float minComputationPointPerTick,
                              final List<String> descriptions,
                              final List<String> unlockedDescriptions,
                              final List<String> dependencies)
    {
        this.researchName = researchName;
        this.techLevel = techLevel;
        this.requiredPoints = requiredPoints;
        this.minComputationPointPerTick = minComputationPointPerTick;
        this.descriptions = descriptions;
        this.unlockedDescriptions = unlockedDescriptions;
        this.dependencies = dependencies;
    }

    public static SimpleResearchData of(ResearchCognitionData data) {
        return new SimpleResearchData(
                data.getTranslatedName().replaceAll("§.", ""),
                data.getTechLevel(),
                data.getRequiredPoints(),
                data.getMinComputationPointPerTick(),
                data.getDescriptions().stream()
                        .map(desc -> desc.replaceAll("§.", ""))
                        .collect(Collectors.toList()),
                data.getUnlockedDescriptions().stream()
                        .map(desc -> desc.replaceAll("§.", ""))
                        .collect(Collectors.toList()),
                data.getDependencies().stream()
                        .map(dep -> dep.getTranslatedName().replaceAll("§.", ""))
                        .collect(Collectors.toList())
        );
    }

    public String getResearchName() {
        return researchName;
    }

    public float getTechLevel() {
        return techLevel;
    }

    public double getRequiredPoints() {
        return requiredPoints;
    }

    public float getMinComputationPointPerTick() {
        return minComputationPointPerTick;
    }

    public List<String> getDescriptions() {
        return descriptions;
    }

    public List<String> getUnlockedDescriptions() {
        return unlockedDescriptions;
    }

    public List<String> getDependencies() {
        return dependencies;
    }
}
