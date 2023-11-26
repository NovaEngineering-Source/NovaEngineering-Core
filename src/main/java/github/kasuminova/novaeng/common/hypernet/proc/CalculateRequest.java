package github.kasuminova.novaeng.common.hypernet.proc;

import com.github.bsideup.jabel.Desugar;
import hellfirepvp.modularmachinery.common.tiles.base.TileMultiblockMachineController;

import java.util.Map;

@Desugar
public record CalculateRequest(double maxRequired, boolean simulate, CalculateType type, CalculateStage stage, TileMultiblockMachineController requester, Map<String, Object> extraParameters) {
}