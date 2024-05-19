package github.kasuminova.novaeng.mixin.ic2;

import ic2.core.IC2;
import ic2.core.block.invslot.InvSlot;
import ic2.core.block.machine.tileentity.TileEntityReplicator;
import net.minecraft.tileentity.TileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(TileEntityReplicator.class)
public abstract class MixinTileEntityReplicator extends TileEntity {

    @Shadow(remap = false)
    public abstract void setOverclockRates();

    /**
     * @author Kasumi_Nova
     * @reason No redstone calculate
     */
    @Overwrite
    public void markDirty() {
        if (this.world != null) {
            this.world.markChunkDirty(this.pos, this);
        }
        List<InvSlot> slots = ((AccessorTileEntityInventory) this).getInvSlots();
        for (final InvSlot invSlot : slots) {
            invSlot.onChanged();
        }
        if (IC2.platform.isSimulating()) {
            this.setOverclockRates();
        }
    }

}
