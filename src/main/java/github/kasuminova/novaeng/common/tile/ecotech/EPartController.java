package github.kasuminova.novaeng.common.tile.ecotech;

import github.kasuminova.mmce.common.world.MMWorldEventListener;
import github.kasuminova.mmce.common.world.MachineComponentManager;
import github.kasuminova.novaeng.NovaEngineeringCore;
import github.kasuminova.novaeng.common.block.prop.FacingProp;
import github.kasuminova.novaeng.common.tile.TileCustomController;
import github.kasuminova.novaeng.common.util.EPartMap;
import hellfirepvp.modularmachinery.ModularMachinery;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

public abstract class EPartController<P extends EPart<?>> extends TileCustomController {

    protected final EPartMap<P> parts = new EPartMap<>();
    protected boolean assembled = false;

    @Override
    public void doControllerTick() {
        if (!this.doStructureCheck() || !this.isStructureFormed()) {
            disassemble();
            return;
        }
        assemble();

//        final long tickStart = System.nanoTime();
        if (onSyncTick()) {
            this.tickExecutor = ModularMachinery.EXECUTE_MANAGER.addTask(this::onAsyncTick, timeRecorder.usedTimeAvg());
        }
//        timeRecorder.incrementUsedTime((int) TimeUnit.MICROSECONDS.convert(System.nanoTime() - tickStart, TimeUnit.NANOSECONDS));
    }

    protected abstract boolean onSyncTick();

    protected void onAsyncTick() {
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
            if (!(te instanceof AbstractEPart<?>)) {
                return;
            }
            P part = (P) te;
            part.setController(this);
            parts.addPart(part);
            onAddPart(part);
        });
    }

    protected abstract void onAddPart(P part);

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

    protected void assemble() {
        if (assembled) {
            return;
        }
        assembled = true;
        parts.assemble(this);
    }

    protected void disassemble() {
        if (!assembled) {
            return;
        }
        assembled = false;
        parts.disassemble();
    }

    protected void clearParts() {
        parts.clear();
    }

    public EPartMap<P> getParts() {
        return parts;
    }

    public boolean isAssembled() {
        return assembled;
    }

    @Override
    protected void checkRotation() {
        if (controllerRotation != null) {
            return;
        }
        IBlockState state = getWorld().getBlockState(getPos());
        if (getControllerBlock().isInstance(state.getBlock())) {
            controllerRotation = state.getValue(FacingProp.HORIZONTALS);
        } else {
            NovaEngineeringCore.log.warn("Invalid EPartController block at " + getPos() + " !");
            controllerRotation = EnumFacing.NORTH;
        }
    }

    protected abstract Class<? extends Block> getControllerBlock();

    @Override
    public void validate() {
        tileEntityInvalid = false;
        loaded = true;
    }

    @Override
    public void invalidate() {
        tileEntityInvalid = true;
        loaded = false;
        foundComponents.forEach((te, component) -> MachineComponentManager.INSTANCE.removeOwner(te, this));
        disassemble();
    }

    @Override
    public void onLoad() {
        loaded = true;
    }

    @Override
    public void onChunkUnload() {
        super.onChunkUnload();
        disassemble();
    }

    @Override
    public boolean isWorking() {
        return assembled;
    }

}
