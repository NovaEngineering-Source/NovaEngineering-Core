package github.kasuminova.novaeng.common.hypernet.proc.server;

import github.kasuminova.novaeng.common.hypernet.proc.CalculateRequest;
import github.kasuminova.novaeng.common.hypernet.proc.CalculateType;

public interface Calculable {

    double calculate(CalculateRequest request);

    double getCalculateTypeEfficiency(CalculateType type);
}