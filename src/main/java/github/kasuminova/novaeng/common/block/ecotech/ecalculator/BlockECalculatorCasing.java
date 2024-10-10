package github.kasuminova.novaeng.common.block.ecotech.ecalculator;

import github.kasuminova.novaeng.NovaEngineeringCore;
import net.minecraft.util.ResourceLocation;

public class BlockECalculatorCasing extends BlockECalculator {

    public static final BlockECalculatorCasing INSTANCE = new BlockECalculatorCasing();

    protected BlockECalculatorCasing() {
        this.setDefaultState(this.blockState.getBaseState());
        this.setRegistryName(new ResourceLocation(NovaEngineeringCore.MOD_ID, "ecalculator_casing"));
        this.setTranslationKey(NovaEngineeringCore.MOD_ID + '.' + "ecalculator_casing");
    }

}
