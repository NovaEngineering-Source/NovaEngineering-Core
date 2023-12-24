package github.kasuminova.novaeng.client.gui.widget.msa.event;

import github.kasuminova.mmce.client.gui.widget.event.GuiEvent;
import github.kasuminova.novaeng.common.hypernet.server.ModularServer;
import net.minecraft.client.gui.inventory.GuiContainer;

public class ModularServerUpdateEvent extends GuiEvent {

    protected final ModularServer server;

    public ModularServerUpdateEvent(final GuiContainer gui, ModularServer server) {
        super(gui);
        this.server = server;
    }

    public ModularServer getServer() {
        return server;
    }
}
