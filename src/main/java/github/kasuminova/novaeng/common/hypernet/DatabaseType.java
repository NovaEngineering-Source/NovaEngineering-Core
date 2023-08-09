package github.kasuminova.novaeng.common.hypernet;

import crafttweaker.annotations.ZenRegister;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenRegister
@ZenClass("novaeng.hypernet.DatabaseType")
public class DatabaseType {

    private final String typeName;
    private final int maxResearchCognitionStoreSize;

    public DatabaseType(final String typeName, final int maxResearchCognitionStoreSize) {
        this.typeName = typeName;
        this.maxResearchCognitionStoreSize = maxResearchCognitionStoreSize;
    }

    @ZenMethod
    public static DatabaseType create(final String typeName, final int maxResearchCognitionStoreSize) {
        return new DatabaseType(typeName, maxResearchCognitionStoreSize);
    }

    @ZenGetter("typeName")
    public String getTypeName() {
        return typeName;
    }

    @ZenGetter("maxResearchCognitionStoreSize")
    public int getMaxResearchCognitionStoreSize() {
        return maxResearchCognitionStoreSize;
    }
}
