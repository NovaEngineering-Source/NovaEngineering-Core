package github.kasuminova.novaeng.common.hypernet;

import github.kasuminova.novaeng.common.util.WorldPos;
import net.minecraft.tileentity.TileEntity;

public class NetProxy implements NetNode {

    private final TileEntity tile;

    public NetProxy(final TileEntity tile) {
        this.tile = tile;
    }

    @Override
    public WorldPos getPos() {
        return WorldPos.of(tile);
    }

}
