package github.kasuminova.mmce.client.gui.widget.event;

import github.kasuminova.mmce.client.gui.widget.base.DynamicWidget;
import net.minecraft.client.gui.inventory.GuiContainer;

public abstract class WidgetEvent extends GuiEvent {
    protected final DynamicWidget sender;

    public WidgetEvent(final GuiContainer gui, final DynamicWidget sender) {
        super(gui);
        this.sender = sender;
    }

    public DynamicWidget getSender() {
        return sender;
    }
}