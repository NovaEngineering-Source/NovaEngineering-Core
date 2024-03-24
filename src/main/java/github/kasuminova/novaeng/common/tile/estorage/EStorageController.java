package github.kasuminova.novaeng.common.tile.estorage;

import appeng.api.config.Actionable;
import github.kasuminova.mmce.common.util.concurrent.ActionExecutor;
import github.kasuminova.novaeng.common.tile.TileCustomController;
import github.kasuminova.novaeng.common.tile.estorage.bus.EStorageBus;
import hellfirepvp.modularmachinery.common.machine.MachineRegistry;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

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

    public EStorageController() {
    }

    @Override
    public void doControllerTick() {
        this.tickExecutor = new ActionExecutor(this::onSyncTick);
        this.tickExecutor.run();
    }

    protected void onSyncTick() {
        if (!this.doStructureCheck() || !this.isStructureFormed()) {
            disassemble();
            return;
        }
        assemble();
        if (world.getTotalWorldTime() % 5 == 0) {
            this.cellDrives.forEach(EStorageCellDrive::updateWriteState);
        }
    }

    @Override
    protected void updateComponents() {
        super.updateComponents();
        this.foundPattern.getTileBlocksArray().forEach((pos, info) -> {
            BlockPos realPos = getPos().add(pos);
            if (!this.getWorld().isBlockLoaded(realPos)) {
                return;
            }
            TileEntity te = this.getWorld().getTileEntity(realPos);
            if (!(te instanceof EStoragePart part)) {
                return;
            }
            part.setController(this);
            storageParts.add(part);
            if (part instanceof EStorageBus bus) {
                storageBuses.add(bus);
            }
            if (part instanceof EStorageEnergyCell energyCell) {
                this.energyStored = energyCell.getEnergyStored();
                this.maxEnergyStore = energyCell.getMaxEnergyStore();
                energyCells.add(energyCell);
            }
            if (part instanceof EStorageCellDrive drive) {
                cellDrives.add(drive);
            }
        });
    }

    protected void disassemble() {
        if (!assembled) {
            return;
        }
        assembled = false;
        this.storageParts.forEach(part -> part.setController(null));
        this.storageParts.clear();
        this.storageBuses.clear();
        this.cellDrives.clear();
        this.energyCells.clear();
        this.energyStored = 0;
        this.maxEnergyStore = 0;
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
        if (mode == Actionable.MODULATE) {
            energyStored += amt - toInject;
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
        if (mode == Actionable.MODULATE) {
            energyStored += extracted;
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
