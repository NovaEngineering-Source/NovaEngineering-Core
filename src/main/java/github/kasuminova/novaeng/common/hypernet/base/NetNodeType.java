package github.kasuminova.novaeng.common.hypernet.base;

import crafttweaker.annotations.ZenRegister;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;

@ZenRegister
@ZenClass("novaeng.hypernet.NetNodeType")
public abstract class NetNodeType {
    protected final String typeName;
    protected final long energyUsage;

    public NetNodeType(final String typeName, final long energyUsage) {
        this.typeName = typeName;
        this.energyUsage = energyUsage;
    }

    @ZenGetter("typeName")
    public String getTypeName() {
        return typeName;
    }

    @ZenGetter("energyUsage")
    public long getEnergyUsage() {
        return energyUsage;
    }
}
