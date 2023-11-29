package github.kasuminova.novaeng.common.container.slot;

import github.kasuminova.novaeng.common.tile.TileModularServerAssembler;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

public class AssemblySlotManager {
    private final TileModularServerAssembler tile;

    private final Map<String, Map<Integer, SlotCondition>> inventorySlots = new HashMap<>();

    public AssemblySlotManager(final TileModularServerAssembler tile) {
        this.tile = tile;
    }

    public SlotCondition addSlot(@Nonnull final SlotCondition slot, @Nonnull final String inventoryName, final int slotId) {
        inventorySlots.computeIfAbsent(inventoryName, v -> new HashMap<>()).put(slotId, slot);
        return slot;
    }

}
