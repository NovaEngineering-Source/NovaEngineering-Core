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
    public static String formatNumber(long value) {
        if (value < 1_000L) {
            return String.valueOf(value);
        } else if (value < 1_000_000L) {
            return formatFloat((float) value / 1_000L, 2) + "K";
        } else if (value < 1_000_000_000L) {
            return formatDouble((double) value / 1_000_000L, 2) + "M";
        } else if (value < 1_000_000_000_000L) {
            return formatDouble((double) value / 1_000_000_000L, 2) + "G";
        } else if (value < 1_000_000_000_000_000L) {
            return formatDouble((double) value / 1_000_000_000_000L, 2) + "T";
        } else if (value < 1_000_000_000_000_000_000L) {
            return formatDouble((double) value / 1_000_000_000_000_000L, 2) + "P";
        } else {
            return formatDouble((double) value / 1_000_000_000_000_000_000L, 2) + "E";
        }
    }

    @ZenMethod
    public static String formatPercent(double num1, double num2) {
        return formatDouble((num1 / num2) * 100D, 2) + "%";
    }

    @ZenMethod
    public static String formatFLOPS(float value) {
        if (value < 1000.0F) {
            return formatFloat(value, 1) + "T FloPS";
        }
        return formatFloat(value / 1000.0F, 1) + "P FloPS";
    }
}
