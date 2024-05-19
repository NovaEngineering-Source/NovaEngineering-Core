package github.kasuminova.novaeng.mixin.ic2;

import ic2.core.block.TileEntityInventory;
import ic2.core.block.invslot.InvSlot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(TileEntityInventory.class)
public interface AccessorTileEntityInventory {

    @Accessor(remap = false)
    List<InvSlot> getInvSlots();

}
