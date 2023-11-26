package github.kasuminova.novaeng.common.hypernet.proc;

import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class CalculateTypeLogic extends CalculateType {
    CalculateTypeLogic() {
        super("logic");
    }

    @SideOnly(Side.CLIENT)
    @Override
    public String format(final double value) {
        return I18n.format("novaeng.hypernet.calculate.type." + typeName, CalculateTypeIntricate.formatLogic(value));
    }

}
