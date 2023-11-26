package github.kasuminova.novaeng.common.hypernet.proc.server;

import github.kasuminova.novaeng.common.hypernet.proc.CalculateReply;
import github.kasuminova.novaeng.common.hypernet.proc.CalculateRequest;

public abstract class CalculateServer {

    public abstract CalculateReply calculate(CalculateRequest request);

}