package github.kasuminova.novaeng.common.hypernet.proc;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class CalculateType {
    protected final String typeName;

    public CalculateType(String typeName) {
        this.typeName = typeName;
    }

    @SideOnly(Side.CLIENT)
    public abstract String format(double value);

    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof CalculateType type) {
            return typeName.equals(type.typeName);
        }
        return false;
    }

    public String getModifierKey() {
        return "calculate_type_" + typeName;
    }

    public String getTypeName() {
        return typeName;
    }

    @Override
    public int hashCode() {
        return typeName.hashCode();
    }

    @Override
    public String toString() {
        return typeName;
    }

}