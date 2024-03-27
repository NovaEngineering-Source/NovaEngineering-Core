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
    protected final Queue<EStorageEnergyCell> energyCellsMin = new PriorityQueue<>(Comparator.reverseOrder());
    protected final Queue<EStorageEnergyCell> energyCellsMax = new PriorityQueue<>();

    protected EStorageMEChannel channel = null;

    protected boolean assembled = false;

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
                energyCellsMax.add(energyCell);
                energyCellsMin.add(energyCell);
            }
            if (part instanceof EStorageCellDrive drive) {
                cellDrives.add(drive);
            }
            if (part instanceof EStorageMEChannel channel) {
                this.channel = channel;
            }
        });
    }

    @Override
    protected boolean canCheckStructure() {
        if (lastStructureCheckTick == -1 || (isStructureFormed() && !assembled)) {
            return true;
        }
        if (ticksExisted % 40 == 0) {
            return true;
        } else {
            if (isStructureFormed()) {
                BlockPos pos = getPos();
                Vec3i min = foundPattern.getMin();
                Vec3i max = foundPattern.getMax();
                return MMWorldEventListener.INSTANCE.isAreaChanged(getWorld(), pos.add(min), pos.add(max));
            }
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
        this.storageParts.forEach(part -> part.setController(null));
        this.storageParts.clear();
        this.storageBuses.clear();
        this.cellDrives.clear();
        this.energyCellsMax.clear();
        this.energyCellsMin.clear();
        this.channel = null;
    }

    protected void assemble() {
        if (assembled) {
            return;
        }
        assembled = true;
        this.storageParts.forEach(EStoragePart::onAssembled);
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
        EStorageEnergyCell cell;

        if (mode == Actionable.SIMULATE) {
            while ((cell = energyCellsMin.peek()) != null) {
                double prev = toInject;
                toInject -= (toInject - cell.injectPower(toInject, mode));
                if (toInject <= 0 || prev == toInject) {
                    break;
                }
            }
            return toInject;
        }

        List<EStorageEnergyCell> toReInsert = new LinkedList<>();
        while ((cell = energyCellsMin.poll()) != null) {
            double prev = toInject;
            toInject -= (toInject - cell.injectPower(toInject, mode));
            toReInsert.add(cell);
            if (toInject <= 0 || prev < toInject) {
                break;
            }
        }

        if (!toReInsert.isEmpty()) {
            energyCellsMin.addAll(toReInsert);
        }

        return toInject;
    }

    public double extractPower(final double amt, final Actionable mode) {
        double extracted = 0;
        EStorageEnergyCell cell;

        if (mode == Actionable.SIMULATE) {
            while ((cell = energyCellsMax.peek()) != null) {
                double prev = extracted;
                extracted += cell.extractPower(amt - extracted, mode);
                if (extracted >= amt || prev >= extracted) {
                    break;
                }
            }
            return extracted;
        }

        List<EStorageEnergyCell> toReInsert = new LinkedList<>();
        while ((cell = energyCellsMax.poll()) != null) {
            double prev = extracted;
            extracted += cell.extractPower(amt - extracted, mode);
            toReInsert.add(cell);
            if (extracted >= amt || prev == extracted) {
                break;
            }
        }

        if (!toReInsert.isEmpty()) {
            energyCellsMax.addAll(toReInsert);
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
        double energyStored = 0;
        for (final EStorageEnergyCell cell : energyCellsMax) {
            double stored = cell.getEnergyStored();
            if (stored <= 0.000001) {
                break;
            }
            energyStored += stored;
        }
        return energyStored;
    }

    public double getMaxEnergyStore() {
        double maxEnergyStore = 0;
        for (final EStorageEnergyCell energyCell : energyCellsMax) {
            maxEnergyStore += energyCell.getMaxEnergyStore();
        }
        return maxEnergyStore;
    }

    public List<EStorageBus> getStorageBuses() {
        return storageBuses;
    }

    public List<EStorageCellDrive> getCellDrives() {
        return cellDrives;
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
