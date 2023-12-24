package github.kasuminova.novaeng.common.hypernet.server;

import com.github.bsideup.jabel.Desugar;

@Desugar
public record CalculateReply(double generated) {
}