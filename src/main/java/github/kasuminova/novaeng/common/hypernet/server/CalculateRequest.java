package github.kasuminova.novaeng.common.hypernet.server;

import com.github.bsideup.jabel.Desugar;
import github.kasuminova.novaeng.common.hypernet.server.modifier.CalculateModifier;
import net.minecraft.tileentity.TileEntity;

import java.util.Map;

@Desugar
public record CalculateRequest(
        double maxRequired,
        boolean simulate,
        CalculateType type,
        CalculateStage stage,
        TileEntity requester,
        Map<String, CalculateModifier> modifiers,
        Map<String, Object> extraParameters) {

    public CalculateRequest subtractMaxRequired(double maxRequired) {
        return new CalculateRequest(this.maxRequired - maxRequired, simulate, type, stage, requester, modifiers, extraParameters);
    }

}