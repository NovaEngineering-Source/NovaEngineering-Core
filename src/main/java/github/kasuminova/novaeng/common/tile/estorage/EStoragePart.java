package github.kasuminova.novaeng.common.tile.estorage;

import hellfirepvp.modularmachinery.common.tiles.base.TileEntitySynchronized;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public abstract class EStoragePart extends TileEntitySynchronized {
    protected EStorageController storageController = null;
    protected boolean loaded = false;

    public void setController(final EStorageController storageController) {
        this.storageController = storageController;
    }

    public EStorageController getController() {
        return storageController;
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
        if (storageController != null) {
            storageController.disassemble();
        }
    }

    @Override
    public void invalidate() {
        loaded = false;
        super.invalidate();
        if (storageController != null) {
            storageController.disassemble();
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
