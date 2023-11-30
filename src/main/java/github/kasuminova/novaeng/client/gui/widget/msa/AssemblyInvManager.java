package github.kasuminova.novaeng.client.gui.widget.msa;

import github.kasuminova.mmce.client.gui.widget.container.Column;
import github.kasuminova.novaeng.common.container.slot.AssemblySlotManager;

public class AssemblyInvManager extends Column {
    protected final AssemblySlotManager slotManager;
    protected AssemblyInv currentOpened = null;

    public AssemblyInvManager(final AssemblySlotManager slotManager) {
        this.slotManager = slotManager;
    }

    public void openInv(final AssemblyInv inv) {
        if (currentOpened != inv) {
            if (currentOpened != null) {
                currentOpened.closeInv();
            }
            inv.openInv();
            currentOpened = inv;
        }
    }

    public AssemblyInvManager addInv(final AssemblyInv inv) {
        addWidget(inv);
        return this;
    }

}