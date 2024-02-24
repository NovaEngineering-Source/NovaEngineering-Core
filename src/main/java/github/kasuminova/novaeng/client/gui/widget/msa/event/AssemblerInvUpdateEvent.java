package github.kasuminova.novaeng.client.gui.widget.msa.event;

import github.kasuminova.mmce.client.gui.widget.base.WidgetGui;
import github.kasuminova.mmce.client.gui.widget.event.GuiEvent;
import github.kasuminova.novaeng.common.hypernet.server.ModularServer;

public class AssemblerInvUpdateEvent extends GuiEvent {

    protected final ModularServer server;

    public AssemblerInvUpdateEvent(final WidgetGui gui, ModularServer server) {
        super(gui);
        this.server = server;
    }

    public ModularServer getServer() {
        return server;
    }
}
