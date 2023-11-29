package github.kasuminova.novaeng.common.hypernet.proc.server;

import github.kasuminova.novaeng.common.hypernet.proc.CalculateRequest;

public interface Extension {

    void onCalculate(CalculateRequest request);

}
