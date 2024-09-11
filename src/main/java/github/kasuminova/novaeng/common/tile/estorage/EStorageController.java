package github.kasuminova.novaeng.common.tile.estorage;

import appeng.api.config.Actionable;
import appeng.api.storage.ICellInventory;
import appeng.api.storage.ICellInventoryHandler;
import appeng.api.storage.data.IAEItemStack;
import github.kasuminova.mmce.common.util.concurrent.ActionExecutor;
import github.kasuminova.mmce.common.world.MMWorldEventListener;
import github.kasuminova.mmce.common.world.MachineComponentManager;
import github.kasuminova.novaeng.NovaEngineeringCore;
import github.kasuminova.novaeng.client.util.BlockModelHider;
import github.kasuminova.novaeng.common.block.estorage.BlockEStorageController;
import github.kasuminova.novaeng.common.block.prop.FacingProp;
import github.kasuminova.novaeng.common.estorage.ECellDriveWatcher;
import github.kasuminova.novaeng.common.tile.TileCustomController;
import github.kasuminova.novaeng.common.tile.estorage.bus.EStorageBus;
import hellfirepvp.modularmachinery.ModularMachinery;
import hellfirepvp.modularmachinery.client.ClientProxy;
import hellfirepvp.modularmachinery.common.machine.MachineRegistry;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.fml.common.FMLCommonHandler;

import javax.annotation.Nullable;
import java.util.*;

public class EStorageController extends TileCustomController {

    public static final List<BlockPos> HIDE_POS_LIST = Arrays.asList(
            new BlockPos(0, 1,  0),
            new BlockPos(0, -1, 0),

            new BlockPos(1, 1,  0),
            new BlockPos(1, 0,  0),
            new BlockPos(1, -1, 0),

            new BlockPos(0, 1,  1),
            new BlockPos(0, 0,  1),
            new BlockPos(0, -1, 1),

            new BlockPos(1, 1,  1),
            new BlockPos(1, 0,  1),
            new BlockPos(1, -1, 1)
    );

    protected final List<EStoragePart> storageParts = new ArrayList<>();
    protected final List<EStorageBus> storageBuses = new ArrayList<>();
    protected final List<EStorageCellDrive> cellDrives = new ArrayList<>();
    protected final Queue<EStorageEnergyCell> energyCellsMin = new PriorityQueue<>(Comparator.reverseOrder());
    protected final Queue<EStorageEnergyCell> energyCellsMax = new PriorityQueue<>();

    protected BlockEStorageController parentController = null;
    protected double idleDrain = 64;

    protected EStorageMEChannel channel = null;

    protected boolean assembled = false;

    public EStorageController(final ResourceLocation machineRegistryName) {
        this.workMode = WorkMode.SYNC;
        this.parentMachine = MachineRegistry.getRegistry().getMachine(machineRegistryName);
        this.parentController = BlockEStorageController.REGISTRY.get(new ResourceLocation(NovaEngineeringCore.MOD_ID, machineRegistryName.getPath()));
    }

    public EStorageController() {
        this.workMode = WorkMode.SYNC;
    }

    @Override
    public void doControllerTick() {
        onSyncTick();
    }

    protected void onSyncTick() {
        if (!this.doStructureCheck() || !this.isStructureFormed()) {
            disassemble();
            return;
        }
        assemble();

        if (world.getTotalWorldTime() % 5 == 0) {
            this.cellDrives.forEach(EStorageCellDrive::updateWriteState);
            this.energyCellsMax.forEach(cell -> {
                if (cell.shouldRecalculateCap()) {
                    cell.recalculateCapacity();
                }
            });
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
        }
        if (isStructureFormed()) {
            BlockPos pos = getPos();
            Vec3i min = foundPattern.getMin();
            Vec3i max = foundPattern.getMax();
            return MMWorldEventListener.INSTANCE.isAreaChanged(getWorld(), pos.add(min), pos.add(max));
        }
        return ticksExisted % Math.min(structureCheckDelay + this.structureCheckCounter * 5, maxStructureCheckDelay) == 0;
    }

    protected void disassemble() {
        if (!assembled) {
            return;
        }
        assembled = false;
        this.storageParts.forEach(EStoragePart::onDisassembled);
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

    public double injectPower(final double amt, final Actionable mode) {
        double toInject = amt;

        if (mode == Actionable.SIMULATE) {
            for (final EStorageEnergyCell cell : energyCellsMin) {
                double prev = toInject;
                toInject -= (toInject - cell.injectPower(toInject, mode));
                if (toInject <= 0 || prev == toInject) {
                    break;
                }
            }
            return toInject;
        }

        List<EStorageEnergyCell> toReInsert = new ArrayList<>();
        EStorageEnergyCell cell;
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

        if (mode == Actionable.SIMULATE) {
            for (final EStorageEnergyCell cell : energyCellsMax) {
                double prev = extracted;
                extracted += cell.extractPower(amt - extracted, mode);
                if (extracted >= amt || prev >= extracted) {
                    break;
                }
            }
            return extracted;
        }

        EStorageEnergyCell cell;
        List<EStorageEnergyCell> toReInsert = new ArrayList<>();
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
    
    public void recalculateEnergyUsage() {
        double newIdleDrain = 64;
        for (final EStorageCellDrive drive : cellDrives) {
            ECellDriveWatcher<IAEItemStack> watcher = drive.getWatcher();
            if (watcher == null) {
                continue;
            }
            ICellInventoryHandler<?> cellInventory = (ICellInventoryHandler<?>) watcher.getInternal();
            if (cellInventory == null) {
                continue;
            }
            ICellInventory<?> cellInv = cellInventory.getCellInv();
            if (cellInv == null) {
                continue;
            }
            newIdleDrain += cellInv.getIdleDrain();
        }
        this.idleDrain = newIdleDrain;
        if (this.channel != null) {
            this.channel.getProxy().setIdlePowerUsage(idleDrain);
        }
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

    @Override
    public void validate() {
        tileEntityInvalid = false;
        loaded = true;

        if (!FMLCommonHandler.instance().getEffectiveSide().isClient()) {
            return;
        }

        ClientProxy.clientScheduler.addRunnable(() -> {
            BlockModelHider.hideOrShowBlocks(HIDE_POS_LIST, this);
            notifyStructureFormedState(isStructureFormed());
        }, 0);
    }

    @Override
    public void invalidate() {
        tileEntityInvalid = true;
        loaded = false;
        foundComponents.forEach((te, component) -> MachineComponentManager.INSTANCE.removeOwner(te, this));
        disassemble();
        if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {
            BlockModelHider.hideOrShowBlocks(HIDE_POS_LIST, this);
        }
    }

    @Override
    public void onLoad() {
        loaded = true;
        if (!FMLCommonHandler.instance().getEffectiveSide().isClient()) {
            return;
        }
        ClientProxy.clientScheduler.addRunnable(() -> {
            BlockModelHider.hideOrShowBlocks(HIDE_POS_LIST, this);
            notifyStructureFormedState(isStructureFormed());
        }, 0);
    }

    @Override
    public void onChunkUnload() {
        super.onChunkUnload();
        disassemble();
    }

    @Override
    public void readCustomNBT(final NBTTagCompound compound) {
        boolean prevLoaded = loaded;
        loaded = false;

        super.readCustomNBT(compound);

        loaded = prevLoaded;

        if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {
            ClientProxy.clientScheduler.addRunnable(() -> {
                BlockModelHider.hideOrShowBlocks(HIDE_POS_LIST, this);
                notifyStructureFormedState(isStructureFormed());
            }, 0);
        }
    }

    @Override
    protected void readMachineNBT(final NBTTagCompound compound) {
        super.readMachineNBT(compound);
        if (compound.hasKey("parentMachine")) {
            ResourceLocation rl = new ResourceLocation(compound.getString("parentMachine"));
            parentMachine = MachineRegistry.getRegistry().getMachine(rl);
            if (parentMachine != null) {
                this.parentController = BlockEStorageController.REGISTRY.get(new ResourceLocation(NovaEngineeringCore.MOD_ID, parentMachine.getRegistryName().getPath()));
            } else {
                ModularMachinery.log.info("Couldn't find machine named " + rl + " for controller at " + getPos());
            }
        }
    }

    public double getEnergyConsumePerTick() {
        return idleDrain;
    }

    public List<EStorageBus> getStorageBuses() {
        return storageBuses;
    }

    public List<EStorageCellDrive> getCellDrives() {
        return cellDrives;
    }

    @Nullable
    public EStorageMEChannel getChannel() {
        return channel;
    }

    public BlockEStorageController getParentController() {
        return parentController;
    }

    public boolean isAssembled() {
        return assembled;
    }

    @Override
    public boolean isWorking() {
        return assembled;
    }

}
