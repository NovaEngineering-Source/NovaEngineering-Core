package github.kasuminova.novaeng.common.hypernet.proc;

import github.kasuminova.novaeng.common.crafttweaker.util.NovaEngUtils;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class CalculateTypeIntricate extends CalculateType {

    CalculateTypeIntricate() {
        super("intricate");
    }

    @SideOnly(Side.CLIENT)
    @Override
    public String format(final double value) {
        return I18n.format("novaeng.hypernet.calculate.type." + typeName, formatLogic(value));
    }

    static String formatLogic(double value) {
        if (value < 1D) {
            return NovaEngUtils.formatDouble(value * 1_000D, 1) + "G";
        }
        if (value < 1_000D) {
            return NovaEngUtils.formatDouble(value, 1) + "T";
        }
        if (value < 1_000_000D) {
            return NovaEngUtils.formatDouble(value / 1_000D, 1) + "P";
        }

        return NovaEngUtils.formatDouble(value / 1_000_000D, 1) + "E";
    }

}