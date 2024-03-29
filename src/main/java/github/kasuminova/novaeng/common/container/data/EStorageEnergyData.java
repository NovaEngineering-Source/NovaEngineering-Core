package github.kasuminova.novaeng.common.container.data;

import com.github.bsideup.jabel.Desugar;

@Desugar
public record EStorageEnergyData(double energyStored, double maxEnergyStore, double energyConsumePerTick) {
}
