package github.kasuminova.novaeng.common.hypernet.computer;

import github.kasuminova.novaeng.common.hypernet.calculation.CalculateReply;
import github.kasuminova.novaeng.common.hypernet.calculation.CalculateRequest;

public abstract class CalculateServer {

    public abstract CalculateReply calculate(CalculateRequest request);

}