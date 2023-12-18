package github.kasuminova.novaeng.common.hypernet.proc.server.module;

import github.kasuminova.novaeng.common.hypernet.proc.CalculateRequest;
import github.kasuminova.novaeng.common.hypernet.proc.CalculateType;
import github.kasuminova.novaeng.common.hypernet.proc.CalculateTypes;
import github.kasuminova.novaeng.common.hypernet.proc.server.CalculateServer;
import github.kasuminova.novaeng.common.hypernet.proc.server.ModularServer;
import github.kasuminova.novaeng.common.hypernet.proc.server.exception.ModularServerException;

public class ModuleNeuronNetworkSys extends ModuleCalculateCard {

    public ModuleNeuronNetworkSys(final ModularServer parent) {
        super(parent);
    }

    @Override
    public double getCalculateTypeEfficiency(final CalculateType type) {
        if (type == CalculateTypes.NEURON) {
            return 1;
        }
        if (type == CalculateTypes.INTRICATE) {
            return 0.01;
        }
        if (type == CalculateTypes.LOGIC) {
            return 0.005;
        }
        if (type == CalculateTypes.QBIT) {
            return 0.0001;
        }

        return 0;
    }

    @Override
    public int getHardwareBandwidth() {
        return 0;
    }
}