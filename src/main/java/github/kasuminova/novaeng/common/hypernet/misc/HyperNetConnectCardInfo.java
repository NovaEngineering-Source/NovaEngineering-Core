package github.kasuminova.novaeng.common.hypernet.misc;

import net.minecraft.util.math.BlockPos;

import java.util.UUID;

public class HyperNetConnectCardInfo {
    private final BlockPos pos;
    private final UUID networkOwner;

    public HyperNetConnectCardInfo(final BlockPos pos, final UUID networkOwner) {
        this.pos = pos;
        this.networkOwner = networkOwner;
    }

    public BlockPos getPos() {
        return pos;
    }

    public UUID getNetworkOwner() {
        return networkOwner;
    }
}
