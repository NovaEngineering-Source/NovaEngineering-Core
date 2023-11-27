package github.kasuminova.mmce.client.gui.widget.container;

import github.kasuminova.mmce.client.gui.util.RenderOffset;
import github.kasuminova.mmce.client.gui.util.RenderSize;
import github.kasuminova.mmce.client.gui.widget.base.DynamicWidget;
import github.kasuminova.mmce.client.gui.widget.event.GuiEvent;
import net.minecraft.client.gui.inventory.GuiContainer;

import java.util.ArrayList;
import java.util.List;

public class Row extends WidgetContainer {
    protected final List<DynamicWidget> widgets = new ArrayList<>();

    protected boolean upAligned = true;
    protected boolean downAligned = false;

    @Override
    protected void preRenderInternal(final GuiContainer gui, final RenderSize renderSize, final RenderOffset renderOffset, final int mouseX, final int mouseY) {
        int x = 0;

        int ySize = getYSize();

        for (final DynamicWidget widget : widgets) {
            if (widget.isDisabled()) {
                continue;
            }
            RenderOffset widgetRenderOffset = getWidgetRenderOffset(widget, ySize, x);
            if (widgetRenderOffset == null) {
                continue;
            }

            widget.preRender(gui, new RenderSize(widget.getXSize(), widget.getYSize()), widgetRenderOffset.add(renderOffset), mouseX, mouseY);
            x += widget.getMarginLeft() + widget.getXSize() + widget.getMarginRight();
        }
    }

    @Override
    protected void postRenderInternal(final GuiContainer gui, final RenderSize renderSize, final RenderOffset renderOffset, final int mouseX, final int mouseY) {
        int x = 0;

        int ySize = getYSize();

        for (final DynamicWidget widget : widgets) {
            if (widget.isDisabled()) {
                continue;
            }
            RenderOffset widgetRenderOffset = getWidgetRenderOffset(widget, ySize, x);
            if (widgetRenderOffset == null) {
                continue;
            }

            widget.postRender(gui, new RenderSize(widget.getXSize(), widget.getYSize()), widgetRenderOffset.add(renderOffset), mouseX, mouseY);
            x += widget.getMarginLeft() + widget.getXSize() + widget.getMarginRight();
        }
    }

    @Override
    public List<DynamicWidget> getWidgets() {
        return widgets;
    }

    @Override
    public Row addWidget(final DynamicWidget widget) {
        widgets.add(widget);
        return this;
    }

    // GUI EventHandlers

    @Override
    public void update(final GuiContainer gui) {
        widgets.forEach(widget -> widget.update(gui));
    }

    @Override
    public boolean onMouseClicked(final int absMouseX, final int absMouseY, final int mouseX, final int mouseY, final RenderOffset renderOffset, final int mouseButton) {
        int x = 0;

        int ySize = getYSize();

        for (final DynamicWidget widget : widgets) {
            if (widget.isDisabled()) {
                continue;
            }
            RenderOffset widgetRenderOffset = getWidgetRenderOffset(widget, ySize, x);
            if (widgetRenderOffset == null) {
                continue;
            }
            int offsetX = widgetRenderOffset.getOffsetX();
            int offsetY = widgetRenderOffset.getOffsetY();

            if (widget.isMouseOver(offsetX, offsetY, mouseX, mouseY)) {
                if (widget.onMouseClicked(absMouseX, absMouseY, mouseX, mouseY, widgetRenderOffset.add(renderOffset), mouseButton)) {
                    return true;
                }
            }
            x += widget.getMarginLeft() + widget.getXSize() + widget.getMarginRight();
        }

        return false;
    }

    @Override
    public boolean onMouseReleased(final int absMouseX, final int absMouseY, final int mouseX, final int mouseY, final RenderOffset renderOffset) {
        int x = 0;

        int ySize = getYSize();

        for (final DynamicWidget widget : widgets) {
            if (widget.isDisabled()) {
                continue;
            }
            RenderOffset widgetRenderOffset = getWidgetRenderOffset(widget, ySize, x);
            if (widgetRenderOffset == null) {
                continue;
            }
            if (widget.onMouseReleased(absMouseX, absMouseY, mouseX, mouseY, widgetRenderOffset.add(renderOffset))) {
                return true;
            }
            x += widget.getMarginLeft() + widget.getXSize() + widget.getMarginRight();
        }
        return false;
    }

    @Override
    public boolean onMouseDWheel(final int absMouseX, final int absMouseY,
                                 final int mouseX, final int mouseY,
                                 final RenderOffset renderOffset,
                                 final int wheel) {
        int x = 0;

        int ySize = getYSize();

        for (final DynamicWidget widget : widgets) {
            if (widget.isDisabled()) {
                continue;
            }
            RenderOffset widgetRenderOffset = getWidgetRenderOffset(widget, ySize, x);
            if (widgetRenderOffset == null) {
                continue;
            }
            if (widget.onMouseDWheel(absMouseX, absMouseY, mouseX, mouseY, widgetRenderOffset.add(renderOffset), wheel)) {
                return true;
            }
            x += widget.getMarginLeft() + widget.getXSize() + widget.getMarginRight();
        }
        return false;
    }

    @Override
    public boolean onKeyTyped(final char typedChar, final int keyCode) {
        for (final DynamicWidget widget : widgets) {
            if (widget.isDisabled()) {
                continue;
            }
            if (widget.onKeyTyped(typedChar, keyCode)) {
                return true;
            }
        }
        return false;
    }

    // CustomEventHandlers

    @Override
    public boolean onGuiEvent(final GuiEvent event) {
        for (final DynamicWidget widget : widgets) {
            if (widget.isDisabled()) {
                continue;
            }
            if (widget.onGuiEvent(event)) {
                return true;
            }
        }
        return false;
    }

    // Utils

    public RenderOffset getWidgetRenderOffset(DynamicWidget widget, int ySize, int x) {
        int xOffset;
        int yOffset;

        if (isCenterAligned()) {
            xOffset = x + widget.getMarginLeft();
            yOffset = (ySize - (widget.getMarginUp() + widget.getYSize() + widget.getMarginDown())) / 2;
        } else if (upAligned) {
            xOffset = x + widget.getMarginLeft();
            yOffset = widget.getMarginUp();
        } else if (downAligned) {
            xOffset = x + widget.getMarginLeft();
            yOffset = ySize - (widget.getYSize() + widget.getMarginDown());
        } else {
            // Where does it align?
            return null;
        }

        return new RenderOffset(xOffset, yOffset);
    }

    // X/Y Size

    @Override
    public int getXSize() {
        int xSize = 0;
        for (final DynamicWidget widget : widgets) {
            xSize += widget.getMarginLeft() + widget.getXSize() + widget.getMarginRight();
        }
        return xSize;
    }

    @Override
    public DynamicWidget setXSize(final int xSize) {
        // It's dynamic, so ignore it.
        return this;
    }

    @Override
    public int getYSize() {
        int maxY = 0;
        for (final DynamicWidget widget : widgets) {
            int ySize = widget.getMarginUp() + widget.getYSize() + widget.getMarginDown();
            if (ySize > maxY) {
                maxY = ySize;
            }
        }
        return maxY;
    }

    @Override
    public DynamicWidget setYSize(final int ySize) {
        // It's dynamic, so ignore it.
        return this;
    }

    // Align

    public boolean isUpAligned() {
        return upAligned;
    }

    public Row setUpAligned(final boolean upAligned) {
        this.downAligned = !upAligned;
        this.upAligned = upAligned;
        return this;
    }

    public boolean isDownAligned() {
        return downAligned;
    }

    public Row setDownAligned(final boolean downAligned) {
        this.upAligned = !downAligned;
        this.downAligned = downAligned;
        return this;
    }

    public Row setCenterAligned(final boolean centerAligned) {
        if (centerAligned) {
            this.upAligned = true;
            this.downAligned = true;
            return this;
        }
        // Default setting is up aligned.
        this.upAligned = true;
        this.downAligned = false;
        return this;
    }

    public boolean isCenterAligned() {
        return this.upAligned && this.downAligned;
    }
}
