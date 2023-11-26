package github.kasuminova.mmce.client.gui.widget.event;

import net.minecraft.client.gui.inventory.GuiContainer;

public abstract class GuiEvent {
    protected final GuiContainer gui;

    public GuiEvent(final GuiContainer gui) {
        this.gui = gui;
    }

    public GuiContainer getGui() {
        return gui;
    }
}
