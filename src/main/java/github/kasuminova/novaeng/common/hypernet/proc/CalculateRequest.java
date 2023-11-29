package github.kasuminova.novaeng.common.hypernet.proc;

import com.github.bsideup.jabel.Desugar;
import github.kasuminova.novaeng.common.hypernet.proc.server.modifier.CalculateModifier;
import hellfirepvp.modularmachinery.common.tiles.base.TileMultiblockMachineController;

import java.util.Map;

@Desugar
public record CalculateRequest(
        double maxRequired,
        boolean simulate,
        CalculateType type,
        CalculateStage stage,
        TileMultiblockMachineController requester,
        Map<String, CalculateModifier> modifiers,
        Map<String, Object> extraParameters) {

    public CalculateRequest subtractMaxRequired(double maxRequired) {
        return new CalculateRequest(this.maxRequired - maxRequired, simulate, type, stage, requester, modifiers, extraParameters);
    }

}