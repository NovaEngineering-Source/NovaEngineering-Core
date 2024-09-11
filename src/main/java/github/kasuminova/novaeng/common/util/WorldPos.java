package github.kasuminova.novaeng.common.util;

import com.github.bsideup.jabel.Desugar;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Objects;

@Desugar
public record WorldPos(World world, BlockPos pos, int hash) {

    public static WorldPos of(final World world, final BlockPos pos) {
        return new WorldPos(world, pos, Objects.hash(world, pos));
    }

    public static WorldPos of(final TileEntity te) {
        return of(te.getWorld(), te.getPos());
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof final WorldPos other) {
            return world == other.world && pos.equals(other.pos);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return hash;
    }

}
