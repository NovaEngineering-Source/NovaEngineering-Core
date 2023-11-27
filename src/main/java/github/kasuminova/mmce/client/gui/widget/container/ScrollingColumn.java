package github.kasuminova.mmce.client.gui.widget.container;

import github.kasuminova.mmce.client.gui.util.RenderOffset;
import github.kasuminova.mmce.client.gui.util.RenderSize;
import github.kasuminova.mmce.client.gui.widget.Scrollbar;
import github.kasuminova.mmce.client.gui.widget.base.DynamicWidget;
import github.kasuminova.mmce.client.gui.widget.event.GuiEvent;
import net.minecraft.client.gui.inventory.GuiContainer;

public class ScrollingColumn extends Column {
    protected final Scrollbar scrollbar = new Scrollbar();

    @Override
    protected void preRenderInternal(final GuiContainer gui, final RenderSize renderSize, final RenderOffset renderOffset, final int mouseX, final int mouseY) {
        int xSize = renderSize.width();
        int ySize = renderSize.height();

        int y = getTotalHeight() > ySize ? -scrollbar.getCurrentScroll() : 0;

        for (final DynamicWidget widget : widgets) {
            if (widget.isDisabled()) {
                continue;
            }
            RenderOffset widgetRenderOffset = getWidgetRenderOffset(widget, xSize, y);
            if (widgetRenderOffset == null) {
                continue;
            }
            int offsetY = widgetRenderOffset.getOffsetY();
            if (offsetY + widget.getYSize() >= 0) {
                widget.preRender(gui, new RenderSize(widget.getXSize(), widget.getYSize()), widgetRenderOffset.add(renderOffset), mouseX, mouseY);
            }
            y += widget.getMarginUp() + widget.getYSize() + widget.getMarginDown();
        }
    }

    @Override
    protected void postRenderInternal(final GuiContainer gui, final RenderSize renderSize, final RenderOffset renderOffset, final int mouseX, final int mouseY) {
        int xSize = renderSize.width();
        int ySize = renderSize.height();

        int y = getTotalHeight() > ySize ? -scrollbar.getCurrentScroll() : 0;

        for (final DynamicWidget widget : widgets) {
            if (widget.isDisabled()) {
                continue;
            }
            RenderOffset widgetRenderOffset = getWidgetRenderOffset(widget, xSize, y);
            if (widgetRenderOffset == null) {
                continue;
            }
            int offsetY = widgetRenderOffset.getOffsetY();
            if (offsetY + widget.getYSize() >= 0) {
                widget.postRender(gui, new RenderSize(widget.getXSize(), widget.getYSize()), widgetRenderOffset.add(renderOffset), mouseX, mouseY);
            }
            y += widget.getMarginUp() + widget.getYSize() + widget.getMarginDown();
        }
    }

    @Override
    public ScrollingColumn addWidget(final DynamicWidget widget) {
        super.addWidget(widget);
        return this;
    }

    // GUI EventHandlers

    @Override
    public void update(final GuiContainer gui) {
        super.update(gui);
        scrollbar.setRange(0, Math.max(getTotalHeight() - ySize, 0)).setYSize(ySize);
    }

    @Override
    public boolean onMouseClicked(final int absMouseX, final int absMouseY, final int mouseX, final int mouseY, final RenderOffset renderOffset, final int mouseButton) {
        int xSize = this.xSize;
        int ySize = this.ySize;

        int y = getTotalHeight() > ySize ? -scrollbar.getCurrentScroll() : 0;

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

            if (offsetY + widget.getYSize() >= 0) {
                if (widget.isMouseOver(offsetX, offsetY, mouseX, mouseY)) {
                    if (widget.onMouseClicked(absMouseX, absMouseY, mouseX, mouseY, widgetRenderOffset.add(renderOffset), mouseButton)) {
                        return true;
                    }
                }
            }
            y += widget.getMarginUp() + widget.getYSize() + widget.getMarginDown();
        }

        return false;
    }

    @Override
    public boolean onMouseReleased(final int absMouseX, final int absMouseY, final int mouseX, final int mouseY, final RenderOffset renderOffset) {
        int xSize = this.xSize;
        int ySize = this.ySize;

        int y = getTotalHeight() > ySize ? -scrollbar.getCurrentScroll() : 0;

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
        int xSize = this.xSize;
        int ySize = this.ySize;

        int y = getTotalHeight() > ySize ? -scrollbar.getCurrentScroll() : 0;

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
        return super.onGuiEvent(event);
    }

    // Utils

    @Override
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
        return xSize;
    }

    @Override
    public DynamicWidget setXSize(final int xSize) {
        this.xSize = xSize;
        return this;
    }

    @Override
    public int getYSize() {
        return this.ySize;
    }

    @Override
    public DynamicWidget setYSize(final int ySize) {
        this.ySize = ySize;
        return this;
    }

    public int getTotalHeight() {
        int total = 0;
        for (final DynamicWidget widget : widgets) {
            total += widget.getMarginUp() + widget.getYSize() + widget.getMarginDown();
        }
        return total;
    }

}
