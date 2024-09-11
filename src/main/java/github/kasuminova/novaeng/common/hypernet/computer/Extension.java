package github.kasuminova.novaeng.common.hypernet.computer;

import github.kasuminova.novaeng.common.hypernet.calculation.CalculateRequest;

public interface Extension {

    void onCalculate(CalculateRequest request);

}
