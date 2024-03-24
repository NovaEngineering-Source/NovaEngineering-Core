package github.kasuminova.novaeng.common.tile.estorage;

import appeng.api.config.Actionable;

public class EStorageEnergyCell extends EStoragePart implements Comparable<EStorageEnergyCell> {

    protected double energyStored = 0D;
    protected double maxEnergyStore = 0D;

    public double injectPower(final double amt, final Actionable mode) {
        if (amt < 0.000001) {
            return 0;
        }
        if (energyStored >= maxEnergyStore) {
            return amt;
        }
        final double maxCanInsert = maxEnergyStore - energyStored;
        final double toInsert = Math.min(amt, maxCanInsert);
        if (mode == Actionable.MODULATE) {
            energyStored += toInsert;
        }
        return maxCanInsert - toInsert;
    }

    public double extractPower(final double amt, final Actionable mode) {
        if (energyStored <= 0) {
            return 0;
        }
        final double maxCanExtract = energyStored;
        final double toExtract = Math.min(amt, maxCanExtract);
        if (mode == Actionable.MODULATE) {
            energyStored -= toExtract;
        }
        return maxCanExtract - toExtract;
    }

    public double getEnergyStored() {
        return energyStored;
    }

    public double getMaxEnergyStore() {
        return maxEnergyStore;
    }

    @Override
    public int compareTo(final EStorageEnergyCell o) {
        return Double.compare(o.energyStored, energyStored);
    }
}
