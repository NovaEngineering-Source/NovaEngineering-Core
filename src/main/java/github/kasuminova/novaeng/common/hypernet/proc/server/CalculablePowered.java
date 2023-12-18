package github.kasuminova.novaeng.common.hypernet.proc.server;

import github.kasuminova.novaeng.common.hypernet.proc.CalculateRequest;
import github.kasuminova.novaeng.common.hypernet.proc.server.exception.ModularServerException;

public interface CalculablePowered extends Calculable {

    default double calculate(final CalculateRequest request) throws ModularServerException {
        double maxCanGenerated = request.modifiers().get(request.type().getModifierKey()).apply(applyEfficiency(getBaseGeneration(), request.type()));
        double maxGeneration = Math.min(maxCanGenerated, request.maxRequired());

        if (!request.simulate()) {
            getServer().consumeEnergy(Math.round(maxGeneration * getEnergyConsumeRatio()));
        }

        return maxGeneration;
    }

    ModularServer getServer();

    double getBaseGeneration();

    double getEnergyConsumeRatio();

}
