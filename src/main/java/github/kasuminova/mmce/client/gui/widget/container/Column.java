package github.kasuminova.mmce.client.gui.widget.container;

import github.kasuminova.mmce.client.gui.util.RenderOffset;
import github.kasuminova.mmce.client.gui.util.RenderSize;
import github.kasuminova.mmce.client.gui.widget.base.DynamicWidget;
import github.kasuminova.mmce.client.gui.widget.event.GuiEvent;
import net.minecraft.client.gui.inventory.GuiContainer;

import java.util.ArrayList;
import java.util.List;

public class Column extends WidgetContainer {
    protected final List<DynamicWidget> widgets = new ArrayList<>();

    protected boolean leftAligned = true;
    protected boolean rightAligned = false;

    @Override
    protected void preRenderInternal(final GuiContainer gui, final RenderSize renderSize, final RenderOffset renderOffset, final int mouseX, final int mouseY) {
        int y = 0;

        int xSize = getXSize();

        for (final DynamicWidget widget : widgets) {
            if (widget.isDisabled()) {
                continue;
            }
            RenderOffset widgetRenderOffset = getWidgetRenderOffset(widget, xSize, y);
            if (widgetRenderOffset == null) {
                continue;
            }

            widget.preRender(gui, new RenderSize(widget.getXSize(), widget.getYSize()), widgetRenderOffset.add(renderOffset), mouseX, mouseY);
            y += widget.getMarginUp() + widget.getYSize() + widget.getMarginDown();
        }
    }

    @Override
    protected void postRenderInternal(final GuiContainer gui, final RenderSize renderSize, final RenderOffset renderOffset, final int mouseX, final int mouseY) {
        int y = 0;

        int xSize = getXSize();

        for (final DynamicWidget widget : widgets) {
            if (widget.isDisabled()) {
                continue;
            }
            RenderOffset widgetRenderOffset = getWidgetRenderOffset(widget, xSize, y);
            if (widgetRenderOffset == null) {
                continue;
            }

            widget.postRender(gui, new RenderSize(widget.getXSize(), widget.getYSize()), widgetRenderOffset.add(renderOffset), mouseX, mouseY);
            y += widget.getMarginUp() + widget.getYSize() + widget.getMarginDown();
        }
    }

    @Override
    public List<DynamicWidget> getWidgets() {
        return widgets;
    }

    @Override
    public Column addWidget(final DynamicWidget widget) {
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
        int y = 0;

        int xSize = getXSize();

        for (final DynamicWidget widget : widgets) {
            if (widget.isDisabled()) {
                continue;
            }
            RenderOffset widgetRenderOffset = getWidgetRenderOffset(widget, xSize, y);
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
            y += widget.getMarginUp() + widget.getYSize() + widget.getMarginDown();
        }

        return false;
    }

    @Override
    public boolean onMouseReleased(final int absMouseX, final int absMouseY, final int mouseX, final int mouseY, final RenderOffset renderOffset) {
        int y = 0;

        int xSize = getXSize();

        for (final DynamicWidget widget : widgets) {
            if (widget.isDisabled()) {
                continue;
            }
            RenderOffset widgetRenderOffset = getWidgetRenderOffset(widget, xSize, y);
            if (widgetRenderOffset == null) {
                continue;
            }
            if (widget.onMouseReleased(absMouseX, absMouseY, mouseX, mouseY, widgetRenderOffset.add(renderOffset))) {
                return true;
            }
            y += widget.getMarginUp() + widget.getYSize() + widget.getMarginDown();
        }
        return false;
    }

    @Override
    public boolean onMouseDWheel(final int absMouseX, final int absMouseY,
                                 final int mouseX, final int mouseY,
                                 final RenderOffset renderOffset,
                                 final int wheel) {
        int y = 0;

        int xSize = getXSize();

        for (final DynamicWidget widget : widgets) {
            if (widget.isDisabled()) {
                continue;
            }
            RenderOffset widgetRenderOffset = getWidgetRenderOffset(widget, xSize, y);
            if (widgetRenderOffset == null) {
                continue;
            }
            if (widget.onMouseDWheel(absMouseX, absMouseY, mouseX, mouseY, widgetRenderOffset.add(renderOffset), wheel)) {
                return true;
            }
            y += widget.getMarginUp() + widget.getYSize() + widget.getMarginDown();
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

    public RenderOffset getWidgetRenderOffset(DynamicWidget widget, int xSize, int y) {
        int xOffset;
        int yOffset;

        if (isCenterAligned()) {
            xOffset = (xSize - (widget.getMarginLeft() + widget.getXSize() + widget.getMarginRight())) / 2;
            yOffset = y + widget.getMarginUp();
        } else if (leftAligned) {
            xOffset = widget.getMarginLeft();
            yOffset = y + widget.getMarginUp();
        } else if (rightAligned) {
            xOffset = xSize - widget.getXSize() - widget.getMarginRight();
            yOffset = y + widget.getMarginUp();
        } else {
            // Where does it align?
            return null;
        }

        return new RenderOffset(xOffset, yOffset);
    }

    // X/Y Size

    @Override
    public int getXSize() {
        int maxX = 0;
        for (final DynamicWidget widget : widgets) {
            int xSize = widget.getMarginLeft() + widget.getXSize() + widget.getMarginRight();
            if (xSize > maxX) {
                maxX = xSize;
            }
        }
        return maxX;
    }

    @Override
    public DynamicWidget setXSize(final int xSize) {
        // It's dynamic, so ignore it.
        return this;
    }

    @Override
    public int getYSize() {
        int ySize = 0;
        for (final DynamicWidget widget : widgets) {
            ySize += widget.getMarginUp() + widget.getYSize() + widget.getMarginDown();
        }
        return ySize;
    }

    @Override
    public DynamicWidget setYSize(final int ySize) {
        // It's dynamic, so ignore it.
        return this;
    }

    // Align

    public boolean isLeftAligned() {
        return leftAligned;
    }

    public Column setLeftAligned(final boolean leftAligned) {
        this.rightAligned = !leftAligned;
        this.leftAligned = leftAligned;
        return this;
    }

    public boolean isRightAligned() {
        return rightAligned;
    }

    public Column setRightAligned(final boolean rightAligned) {
        this.leftAligned = !rightAligned;
        this.rightAligned = rightAligned;
        return this;
    }

    public boolean isCenterAligned() {
        return this.leftAligned && this.rightAligned;
    }

    public Column setCenterAligned(final boolean centerAligned) {
        if (centerAligned) {
            this.leftAligned = true;
            this.rightAligned = true;
            return this;
        }
        // Default setting is left aligned.
        this.leftAligned = true;
        this.rightAligned = false;
        return this;
    }
}
