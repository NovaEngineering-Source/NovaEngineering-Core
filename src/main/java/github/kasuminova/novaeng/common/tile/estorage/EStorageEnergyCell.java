package github.kasuminova.novaeng.common.tile.estorage;

import appeng.api.config.Actionable;
import net.minecraft.nbt.NBTTagCompound;

public class EStorageEnergyCell extends EStoragePart implements Comparable<EStorageEnergyCell> {

    protected double energyStored = 0D;
    protected double maxEnergyStore = 0D;

    public EStorageEnergyCell() {
    }

    public EStorageEnergyCell(final double maxEnergyStore) {
        this.maxEnergyStore = maxEnergyStore;
    }

    public double injectPower(final double amt, final Actionable mode) {
        if (mode == Actionable.SIMULATE) {
            final double fakeBattery = energyStored + amt;
            if (fakeBattery > maxEnergyStore) {
                return fakeBattery - maxEnergyStore;
            }
            return 0;
        }

        if (amt < 0.000001) {
            return 0;
        }
        if (energyStored >= maxEnergyStore) {
            return amt;
        }
        final double maxCanInsert = maxEnergyStore - energyStored;
        final double toInsert = Math.min(amt, maxCanInsert);
        energyStored += toInsert;
        return maxCanInsert - toInsert;
    }

    public double extractPower(final double amt, final Actionable mode) {
        if (mode == Actionable.SIMULATE) {
            return Math.min(this.energyStored, amt);
        }

        if (energyStored <= 0) {
            return 0;
        }
        final double maxCanExtract = energyStored;
        final double toExtract = Math.min(amt, maxCanExtract);
        energyStored -= toExtract;
        return toExtract;
    }

    public double getEnergyStored() {
        return energyStored;
    }

    public double getMaxEnergyStore() {
        return maxEnergyStore;
    }

    @Override
    public void readCustomNBT(final NBTTagCompound tag) {
        super.readCustomNBT(tag);
        energyStored = tag.getDouble("energyStored");
        maxEnergyStore = tag.getDouble("maxEnergyStore");
    }

    @Override
    public void writeCustomNBT(final NBTTagCompound tag) {
        super.writeCustomNBT(tag);
        tag.setDouble("energyStored", energyStored);
        tag.setDouble("maxEnergyStore", maxEnergyStore);
    }

    @Override
    public int compareTo(final EStorageEnergyCell o) {
        return Double.compare(o.energyStored, energyStored);
    }
}
