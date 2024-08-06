package github.kasuminova.novaeng.common.tile.efabricator;

import hellfirepvp.modularmachinery.common.tiles.base.TileEntitySynchronized;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class EFabricatorPart extends TileEntitySynchronized {
    protected EFabricatorController fabricatorController = null;
    protected boolean loaded = false;

    public void setController(final EFabricatorController storageController) {
        this.fabricatorController = storageController;
    }

    @Nullable
    public EFabricatorController getController() {
        return fabricatorController;
    }

    public void onAssembled() {
    }

    public void onDisassembled() {
    }

    @Override
    public void onLoad() {
        super.onLoad();
        this.loaded = true;
    }

    @Override
    public void onChunkUnload() {
        loaded = false;
        super.onChunkUnload();
        if (fabricatorController != null) {
            fabricatorController.disassemble();
        }
    }

    @Override
    public void invalidate() {
        loaded = false;
        super.invalidate();
        if (fabricatorController != null) {
            fabricatorController.disassemble();
        }
    }

    @Override
    public boolean shouldRefresh(@Nonnull final World world, @Nonnull final BlockPos pos, final IBlockState oldState, final IBlockState newSate) {
        return oldState.getBlock() != newSate.getBlock();
    }

    @Override
    public void validate() {
        super.validate();
    }

}
