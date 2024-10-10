package github.kasuminova.novaeng.common.block.ecotech.ecalculator;

import github.kasuminova.novaeng.NovaEngineeringCore;
import net.minecraft.util.ResourceLocation;

public class BlockECalculatorThreadCoreHyper extends BlockECalculatorThreadCore {

    public static final BlockECalculatorThreadCoreHyper L4 = new BlockECalculatorThreadCoreHyper("l4", 0, 4);
    public static final BlockECalculatorThreadCoreHyper L6 = new BlockECalculatorThreadCoreHyper("l6", 1, 7);
    public static final BlockECalculatorThreadCoreHyper L9 = new BlockECalculatorThreadCoreHyper("l9", 2, 14);

    protected BlockECalculatorThreadCoreHyper(final String level, final int threads, final int hyperThreads) {
        super(
                new ResourceLocation(NovaEngineeringCore.MOD_ID, "ecalculator_thread_core_hyper_" + level),
                NovaEngineeringCore.MOD_ID + '.' + "ecalculator_thread_core_hyper_" + level,
                threads, hyperThreads
        );
    }

}
