package github.kasuminova.novaeng.common.tile.ecotech.ecalculator;

import github.kasuminova.novaeng.common.tile.ecotech.EPartController;
import net.minecraft.block.Block;

public class ECalculatorController extends EPartController<ECalculatorPart> {

    @Override
    protected boolean onSyncTick() {
        return false;
    }

    @Override
    protected void onAddPart(final ECalculatorPart part) {

    }

    @Override
    protected Class<? extends Block> getControllerBlock() {
        return null;
    }

}
