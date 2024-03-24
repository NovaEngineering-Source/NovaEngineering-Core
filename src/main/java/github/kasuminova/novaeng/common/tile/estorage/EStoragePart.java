package github.kasuminova.novaeng.common.tile.estorage;

import hellfirepvp.modularmachinery.common.tiles.base.TileEntitySynchronized;

public abstract class EStoragePart extends TileEntitySynchronized {
    protected EStorageController storageController = null;

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

}
