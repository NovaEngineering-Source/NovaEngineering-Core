package github.kasuminova.novaeng.client.gui.widget.msa;

import github.kasuminova.mmce.client.gui.widget.container.Column;
import github.kasuminova.mmce.client.gui.widget.event.GuiEvent;
import github.kasuminova.novaeng.client.gui.widget.msa.event.AssemblerInvUpdateEvent;
import github.kasuminova.novaeng.common.container.slot.AssemblySlotManager;
import github.kasuminova.novaeng.common.hypernet.server.ModularServer;

public class AssemblyInvManager extends Column {
    protected AssemblySlotManager slotManager;
    protected AssemblyInv currentOpened = null;

    public AssemblyInvManager(final AssemblySlotManager slotManager) {
        this.slotManager = slotManager;
    }

    @Override
    public boolean onGuiEvent(final GuiEvent event) {
        if (event instanceof AssemblerInvUpdateEvent serverUpdateEvent) {
            ModularServer server = serverUpdateEvent.getServer();
            this.slotManager = server == null ? null : server.getSlotManager();
        }
        return super.onGuiEvent(event);
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