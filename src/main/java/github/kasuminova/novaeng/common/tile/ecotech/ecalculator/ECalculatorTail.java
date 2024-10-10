package github.kasuminova.novaeng.common.tile.ecotech.ecalculator;

public class ECalculatorTail extends ECalculatorPart {

    @Override
    public void onDisassembled() {
        super.onDisassembled();
        markForUpdateSync();
    }

    @Override
    public void onAssembled() {
        super.onAssembled();
        markForUpdateSync();
    }

}
