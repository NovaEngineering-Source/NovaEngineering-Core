package github.kasuminova.novaeng.common.tile.estorage;

import appeng.api.config.Actionable;
import github.kasuminova.mmce.common.util.concurrent.ActionExecutor;
import github.kasuminova.mmce.common.world.MMWorldEventListener;
import github.kasuminova.novaeng.NovaEngineeringCore;
import github.kasuminova.novaeng.common.block.estorage.BlockEStorageController;
import github.kasuminova.novaeng.common.block.estorage.prop.FacingProp;
import github.kasuminova.novaeng.common.tile.TileCustomController;
import github.kasuminova.novaeng.common.tile.estorage.bus.EStorageBus;
import hellfirepvp.modularmachinery.common.machine.MachineRegistry;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

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
        this.workMode = WorkMode.SYNC;
        this.parentMachine = MachineRegistry.getRegistry().getMachine(machineRegistryName);
    }

    public EStorageController() {
        this.workMode = WorkMode.SYNC;
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
        clearParts();
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
                energyCells.add(energyCell);
            }
            if (part instanceof EStorageCellDrive drive) {
                cellDrives.add(drive);
            }
            if (part instanceof EStorageMEChannel channel) {
                this.channel = channel;
            }
        });
    }

    protected boolean canCheckStructure() {
        if (lastStructureCheckTick == -1 || (isStructureFormed() && foundComponents.isEmpty())) {
            return true;
        }
        if (isStructureFormed()) {
            BlockPos pos = getPos();
            Vec3i min = foundPattern.getMin();
            Vec3i max = foundPattern.getMax();
            return MMWorldEventListener.INSTANCE.isAreaChanged(getWorld(), pos.add(min), pos.add(max));
        } else {
            return ticksExisted % Math.min(structureCheckDelay + this.structureCheckCounter * 5, maxStructureCheckDelay) == 0;
        }
    }

    protected void disassemble() {
        if (!assembled) {
            return;
        }
        assembled = false;
        this.storageParts.forEach(part -> {
            part.onDisassembled();
            part.setController(null);
        });
        clearParts();
    }

    protected void clearParts() {
        this.storageParts.forEach(part -> {
            part.setController(null);
        });
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
        this.storageParts.forEach(EStoragePart::onAssembled);
        this.energyCells.forEach(energyCell -> {
            this.energyStored += energyCell.getEnergyStored();
            this.maxEnergyStore += energyCell.getMaxEnergyStore();
        });
    }

    @Override
    public void invalidate() {
        super.invalidate();
        disassemble();
    }

    @Override
    public void onChunkUnload() {
        super.onChunkUnload();
        disassemble();
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

    @Override
    protected void checkRotation() {
        if (controllerRotation != null) {
            return;
        }
        IBlockState state = getWorld().getBlockState(getPos());
        if (state.getBlock() instanceof BlockEStorageController) {
            controllerRotation = state.getValue(FacingProp.HORIZONTALS);
        } else {
            NovaEngineeringCore.log.warn("Invalid estorage controller block at " + getPos() + " !");
            controllerRotation = EnumFacing.NORTH;
        }
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
