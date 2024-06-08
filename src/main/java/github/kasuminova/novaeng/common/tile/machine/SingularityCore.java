package github.kasuminova.novaeng.common.tile.machine;

import github.kasuminova.mmce.client.model.DynamicMachineModelRegistry;
import github.kasuminova.mmce.client.model.MachineControllerModel;
import github.kasuminova.novaeng.NovaEngineeringCore;
import github.kasuminova.novaeng.common.block.BlockSingularityCoreController;
import github.kasuminova.novaeng.common.tile.TileCustomController;
import hellfirepvp.modularmachinery.ModularMachinery;
import hellfirepvp.modularmachinery.common.block.BlockController;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;

public class SingularityCore extends TileCustomController {

    public SingularityCore() {
    }

    public SingularityCore(IBlockState state) {
        if (state.getBlock() instanceof BlockSingularityCoreController) {
            controllerRotation = state.getValue(BlockController.FACING);
        } else {
            ModularMachinery.log.warn("Invalid controller block at " + getPos() + " !");
            controllerRotation = EnumFacing.NORTH;
        }
    }

    @Override
    public void doControllerTick() {
        tickExecutor = ModularMachinery.EXECUTE_MANAGER.addTask(() -> {
            if (!doStructureCheck() || !isStructureFormed()) {
            }
        }, usedTimeAvg());
    }

    @Override
    protected void checkRotation() {
        IBlockState state = getWorld().getBlockState(getPos());
        if (state.getBlock() instanceof BlockSingularityCoreController) {
            controllerRotation = state.getValue(BlockController.FACING);
        } else {
            // wtf, where is the controller?
            NovaEngineeringCore.log.warn("Invalid controller block at {} !", getPos());
            controllerRotation = EnumFacing.NORTH;
        }
    }

    @Override
    public MachineControllerModel getCurrentModel() {
        if (!isStructureFormed()) {
            return null;
        }
        return DynamicMachineModelRegistry.INSTANCE.getMachineModel("singularity_core");
    }

    @Override
    public boolean isWorking() {
        return true;
    }

    @Override
    public double getMaxRenderDistanceSquared() {
        return 32768D;
    }

}
