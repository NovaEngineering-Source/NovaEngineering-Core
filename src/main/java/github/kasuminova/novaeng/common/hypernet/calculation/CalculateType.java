package github.kasuminova.novaeng.common.hypernet.calculation;

import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class CalculateType {
    protected final String typeName;

    public CalculateType(String typeName) {
        this.typeName = typeName;
    }

    @SideOnly(Side.CLIENT)
    public abstract String format(double value);

    @SideOnly(Side.CLIENT)
    public String getFormattedTypeName() {
        return I18n.format("novaeng.hypernet.calculate.type_name." + typeName);
    }

    public String getModifierKey() {
        return "calculate_type_" + typeName;
    }

    public String getTypeName() {
        return typeName;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof CalculateType type) {
            return typeName.equals(type.typeName);
        }
        return false;
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