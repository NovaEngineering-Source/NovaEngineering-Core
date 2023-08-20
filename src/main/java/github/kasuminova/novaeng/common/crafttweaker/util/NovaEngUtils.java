package github.kasuminova.novaeng.common.crafttweaker.util;

import crafttweaker.annotations.ZenRegister;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import java.text.NumberFormat;

@ZenRegister
@ZenClass("novaeng.NovaEngUtils")
public class NovaEngUtils {
    @ZenMethod
    public static String formatFloat(float value, int decimalFraction) {
        NumberFormat nf = NumberFormat.getNumberInstance();
        nf.setMaximumFractionDigits(decimalFraction);
        return nf.format(value);
    }

    @ZenMethod
    public static String formatDouble(double value, int decimalFraction) {
        NumberFormat nf = NumberFormat.getNumberInstance();
        nf.setMaximumFractionDigits(decimalFraction);
        return nf.format(value);
    }

    @ZenMethod
    public static String formatPercent(double num1, double num2) {
        return NovaEngUtils.formatDouble((num1 / num2) * 100D, 2) + "%";
    }

    @ZenMethod
    public static String formatFLOPS(float value) {
        if (value < 1000.0F) {
            return NovaEngUtils.formatFloat(value, 2) + " TFloPS";
        }
        return NovaEngUtils.formatFloat(value / 1000.0F, 2) + " PFloPS";
    }
}
