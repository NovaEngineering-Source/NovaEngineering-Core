package github.kasuminova.novaeng.common.tile.estorage;

import appeng.api.config.Actionable;
import github.kasuminova.mmce.common.util.concurrent.ActionExecutor;
import github.kasuminova.novaeng.common.tile.TileCustomController;
import github.kasuminova.novaeng.common.tile.estorage.bus.EStorageBus;
import hellfirepvp.modularmachinery.common.machine.MachineRegistry;
import net.minecraft.util.ResourceLocation;

import java.util.*;

public class EStorageController extends TileCustomController {

    protected final List<EStoragePart> storageParts = new ArrayList<>();
    protected final List<EStorageBus> storageBuses = new ArrayList<>();
    protected final List<EStorageCellDrive> cellDrives = new ArrayList<>();
    protected final TreeSet<EStorageEnergyCell> energyCells = new TreeSet<>();

    protected EStorageMEChannel channel = null;

    protected boolean assembled = false;

    protected double energyStored = 0;
    protected double maxEnergyStore = 0;

    public EStorageController(final ResourceLocation machineRegistryName) {
        this.parentMachine = MachineRegistry.getRegistry().getMachine(machineRegistryName);
    }

    @Override
    public void doControllerTick() {
        this.tickExecutor = new ActionExecutor(() -> {
            if (!this.doStructureCheck() || !this.isStructureFormed()) {
                disassemble();
                return;
            }
            assemble();
        });
        this.tickExecutor.run();
    }

    protected void disassemble() {
        if (!assembled) {
            return;
        }
        assembled = false;
        this.storageBuses.clear();
        this.cellDrives.clear();
        this.energyCells.clear();
        this.channel = null;
    }

    protected void assemble() {
        if (assembled) {
            return;
        }
        assembled = true;
    }

    public double injectPower(final double amt, final Actionable mode) {
        double toInject = amt;

        List<EStorageEnergyCell> toReInsert = new LinkedList<>();
        Iterator<EStorageEnergyCell> it = energyCells.descendingIterator();
        while (it.hasNext()) {
            EStorageEnergyCell cell = it.next();
            double prev = toInject;
            toInject -= toInject - cell.injectPower(toInject, mode);
            if (prev != toInject && mode == Actionable.MODULATE) {
                it.remove();
                toReInsert.add(cell);
            }
            if (toInject <= 0) {
                break;
            }
        }

        if (!toReInsert.isEmpty()) {
            energyCells.addAll(toReInsert);
        }

        return toInject;
    }

    public double extractPower(final double amt, final Actionable mode) {
        double extracted = 0;

        List<EStorageEnergyCell> toReInsert = new LinkedList<>();
        Iterator<EStorageEnergyCell> it = energyCells.descendingIterator();
        while (it.hasNext()) {
            EStorageEnergyCell cell = it.next();
            double prev = extracted;
            extracted += cell.extractPower(amt - extracted, mode);
            if (prev != extracted && mode == Actionable.MODULATE) {
                it.remove();
                toReInsert.add(cell);
            }
            if (extracted >= amt) {
                break;
            }
        }

        if (!toReInsert.isEmpty()) {
            energyCells.addAll(toReInsert);
        }

        return extracted;
    }

    public double getEnergyStored() {
        return energyStored;
    }

    public double getMaxEnergyStore() {
        return maxEnergyStore;
    }

    public List<EStorageBus> getStorageBuses() {
        return storageBuses;
    }

    public List<EStorageCellDrive> getCellDrives() {
        return cellDrives;
    }

    public Set<EStorageEnergyCell> getEnergyCells() {
        return energyCells;
    }

    public EStorageMEChannel getChannel() {
        return channel;
    }

    public boolean isAssembled() {
        return assembled;
    }

    @Override
    public boolean isWorking() {
        return assembled;
    }

}
