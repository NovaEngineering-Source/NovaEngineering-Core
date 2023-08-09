package github.kasuminova.novaeng.common.hypernet.research;

import crafttweaker.annotations.ZenRegister;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenMethod;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@ZenRegister
@ZenClass("novaeng.hypernet.ResearchCognitionData")
public class ResearchCognitionData {
    private final String researchName;
    private final String translatedName;
    private final float techLevel;
    private final List<String> descriptions;

    public ResearchCognitionData(final String researchName,
                                 final String translatedName,
                                 float techLevel,
                                 final List<String> descriptions) {
        this.researchName = researchName;
        this.translatedName = translatedName;
        this.techLevel = techLevel;
        this.descriptions = descriptions;
    }

    @ZenMethod
    public static ResearchCognitionData create(final String researchName,
                                               final String translatedName,
                                               float techLevel,
                                               final String[] descriptions) {
        return new ResearchCognitionData(researchName, translatedName, techLevel, Arrays.asList(descriptions));
    }

    @ZenGetter("researchName")
    public String getResearchName() {
        return researchName;
    }

    @ZenGetter("translatedName")
    public String getTranslatedName() {
        return translatedName;
    }

    @ZenGetter("techLevel")
    public float getTechLevel() {
        return techLevel;
    }

    @ZenGetter("descriptions")
    public String[] getDescriptionsArray() {
        return descriptions.toArray(new String[0]);
    }

    public List<String> getDescriptions() {
        return Collections.unmodifiableList(descriptions);
    }
}
