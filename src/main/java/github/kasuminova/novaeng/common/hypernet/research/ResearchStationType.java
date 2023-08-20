package github.kasuminova.novaeng.common.hypernet.research;

import crafttweaker.annotations.ZenRegister;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenRegister
@ZenClass("novaeng.hypernet.ResearchStationType")
public class ResearchStationType {
    private final String typeName;
    private final float maxTechLevel;

    public ResearchStationType(final String typeName, final float maxTechLevel) {
        this.typeName = typeName;
        this.maxTechLevel = maxTechLevel;
    }

    @ZenMethod
    public static ResearchStationType create(final String typeName, final float maxTechLevel) {
        return new ResearchStationType(typeName, maxTechLevel);
    }

    @ZenGetter("typeName")
    public String getTypeName() {
        return typeName;
    }

    @ZenGetter("maxTechLevel")
    public float getMaxTechLevel() {
        return maxTechLevel;
    }
}
