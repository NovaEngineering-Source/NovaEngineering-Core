package github.kasuminova.novaeng.common.container.slot;

import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class SlotConditionItemHandler extends SlotItemHandler {

    protected boolean enabled = true;

    public SlotConditionItemHandler(final IItemHandler inventoryIn, final int index, final int xPosition, final int yPosition) {
        super(inventoryIn, index, xPosition, yPosition);
    }

    public SlotConditionItemHandler setEnabled(final boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
