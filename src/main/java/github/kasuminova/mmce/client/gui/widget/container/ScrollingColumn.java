package github.kasuminova.mmce.client.gui.widget.container;

import github.kasuminova.mmce.client.gui.util.RenderOffset;
import github.kasuminova.mmce.client.gui.util.RenderSize;
import github.kasuminova.mmce.client.gui.widget.base.DynamicWidget;
import net.minecraft.client.gui.inventory.GuiContainer;

import java.util.ArrayList;
import java.util.List;

public class ScrollingColumn extends WidgetContainer {

    protected final List<DynamicWidget> widgets = new ArrayList<>();

    @Override
    public void preRenderInternal(final GuiContainer gui, final RenderSize renderSize, final RenderOffset renderOffset, final int mouseX, final int mouseY) {

    }

    @Override
    public void postRenderInternal(final GuiContainer gui, final RenderSize renderSize, final RenderOffset renderOffset, final int mouseX, final int mouseY) {

    }

    @Override
    public boolean onMouseClicked(final int absMouseX, final int absMouseY, final int mouseX, final int mouseY, final RenderOffset renderOffset, final int mouseButton) {
        return false;
    }

    @Override
    public boolean onMouseReleased(final int absMouseX, final int absMouseY, final int mouseX, final int mouseY, final RenderOffset renderOffset) {
        return false;
    }

    @Override
    public boolean onMouseDWheel(final int absMouseX, final int absMouseY, final int mouseX, final int mouseY, final RenderOffset renderOffset, final int wheel) {
        return false;
    }

    @Override
    public boolean onKeyTyped(final char typedChar, final int keyCode) {
        return false;
    }

    @Override
    public List<DynamicWidget> getWidgets() {
        return widgets;
    }

    @Override
    public ScrollingColumn addWidget(final DynamicWidget widget) {
        widgets.add(widget);
        return this;
    }

    @Override
    public void update(final GuiContainer gui) {

    }

}
