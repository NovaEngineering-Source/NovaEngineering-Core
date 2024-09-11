package github.kasuminova.novaeng.common.hypernet.calculation;

import github.kasuminova.novaeng.common.crafttweaker.util.NovaEngUtils;
import github.kasuminova.novaeng.common.hypernet.computer.exception.ModularServerException;
import net.minecraft.util.text.TextFormatting;

public interface Calculable {

    double calculate(CalculateRequest request) throws ModularServerException;

    double getCalculateTypeEfficiency(CalculateType type);

    default double applyEfficiency(double value, CalculateType type) {
        return value * getCalculateTypeEfficiency(type);
    }

    static String formatEfficiency(double value) {
        String formattedValue = NovaEngUtils.formatDouble(value * 100D, 3);

        if (value >= 5.0D) {
            return TextFormatting.DARK_PURPLE + formattedValue + TextFormatting.WHITE;
        } else if (value > 1.0D) {
            return TextFormatting.BLUE + formattedValue + TextFormatting.WHITE;
        } else if (value == 1.0D) {
            return TextFormatting.GREEN + formattedValue + TextFormatting.WHITE;
        } else if (value >= 0.5D) {
            return TextFormatting.YELLOW + formattedValue + TextFormatting.WHITE;
        } else {
            return TextFormatting.RED + formattedValue + TextFormatting.WHITE;
        }
    }
}