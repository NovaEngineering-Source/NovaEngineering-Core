package github.kasuminova.novaeng.common.crafttweaker.util;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.world.IBlockPos;
import net.minecraft.util.math.BlockPos;
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
    public static String formatPercent(float num1, float num2) {
        return NovaEngUtils.formatFloat((num1 / num2) * 100F, 2) + "%";
    }

    @ZenMethod
    public static String formatFLOPS(float value) {
        if (value < 1000.0F) {
            return NovaEngUtils.formatFloat(value, 1) + " TFloPS";
        }
        return NovaEngUtils.formatFloat(value / 1000.0F, 2) + " PFloPS";
    }
}
