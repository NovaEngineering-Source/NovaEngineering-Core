package github.kasuminova.novaeng.common.hypernet.proc;

import github.kasuminova.novaeng.common.crafttweaker.util.NovaEngUtils;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class CalculateTypeQbit extends CalculateType {
    CalculateTypeQbit() {
        super("qbit");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public String format(final double value) {
        return I18n.format("novaeng.hypernet.calculate.type." + typeName, NovaEngUtils.formatDouble(value, 1));
    }
}
