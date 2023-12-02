package github.kasuminova.novaeng.common.hypernet.proc.server;

import github.kasuminova.novaeng.common.hypernet.proc.CalculateRequest;
import github.kasuminova.novaeng.common.hypernet.proc.CalculateType;
import github.kasuminova.novaeng.common.hypernet.proc.server.exception.ModularServerException;

public interface Calculable {

    double calculate(CalculateRequest request) throws ModularServerException;

    double getCalculateTypeEfficiency(CalculateType type);

    default double applyEfficiency(double value, CalculateType type) {
        return value * getCalculateTypeEfficiency(type);
    }

}