package github.kasuminova.mmce.client.gui.widget.container;

import github.kasuminova.mmce.client.gui.util.MousePos;
import github.kasuminova.mmce.client.gui.util.RenderPos;
import github.kasuminova.mmce.client.gui.util.RenderSize;
import github.kasuminova.mmce.client.gui.widget.base.DynamicWidget;
import github.kasuminova.mmce.client.gui.widget.event.GuiEvent;
import net.minecraft.client.gui.inventory.GuiContainer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("unused")
public class Column extends WidgetContainer {
    protected final List<DynamicWidget> widgets = new ArrayList<>();

    protected boolean leftAligned = true;
    protected boolean rightAligned = false;

    @Override
    protected void preRenderInternal(final GuiContainer gui, final RenderSize renderSize, final RenderPos renderPos, final MousePos mousePos) {
        int y = 0;

        int width = getWidth();

        for (final DynamicWidget widget : widgets) {
            if (widget.isDisabled()) {
                continue;
            }
            RenderPos widgetRenderPos = getWidgetRenderOffset(widget, width, y);
            if (widgetRenderPos == null) {
                continue;
            }
            RenderPos absRenderPos = widgetRenderPos.add(renderPos);
            widget.preRender(gui, new RenderSize(widget.getWidth(), widget.getHeight()), absRenderPos, mousePos.relativeTo(widgetRenderPos));
            y += widget.getMarginUp() + widget.getHeight() + widget.getMarginDown();
        }
    }

    @Override
    protected void postRenderInternal(final GuiContainer gui, final RenderSize renderSize, final RenderPos renderPos, final MousePos mousePos) {
        int y = 0;

        int width = getWidth();

        for (final DynamicWidget widget : widgets) {
            if (widget.isDisabled()) {
                continue;
            }
            RenderPos widgetRenderPos = getWidgetRenderOffset(widget, width, y);
            if (widgetRenderPos == null) {
                continue;
            }
            RenderPos absRenderPos = widgetRenderPos.add(renderPos);
            widget.postRender(gui, new RenderSize(widget.getWidth(), widget.getHeight()), absRenderPos, mousePos.relativeTo(widgetRenderPos));
            y += widget.getMarginUp() + widget.getHeight() + widget.getMarginDown();
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
    public boolean onMouseClicked(final MousePos mousePos, final RenderPos renderPos, final int mouseButton) {
        int y = 0;

        int width = getWidth();

        for (final DynamicWidget widget : widgets) {
            if (widget.isDisabled()) {
                continue;
            }
            RenderPos widgetRenderPos = getWidgetRenderOffset(widget, width, y);
            if (widgetRenderPos == null) {
                continue;
            }
            int offsetX = widgetRenderPos.posX();
            int offsetY = widgetRenderPos.posY();

            MousePos relativeMousePos = mousePos.relativeTo(widgetRenderPos);
            if (widget.isMouseOver(relativeMousePos)) {
                RenderPos absRenderPos = widgetRenderPos.add(renderPos);
                if (widget.onMouseClicked(mousePos.relativeTo(widgetRenderPos), absRenderPos, mouseButton)) {
                    return true;
                }
            }
            y += widget.getMarginUp() + widget.getHeight() + widget.getMarginDown();
        }

        return false;
    }

    @Override
    public boolean onMouseReleased(final MousePos mousePos, final RenderPos renderPos) {
        int y = 0;

        int width = getWidth();

        for (final DynamicWidget widget : widgets) {
            if (widget.isDisabled()) {
                continue;
            }
            RenderPos widgetRenderPos = getWidgetRenderOffset(widget, width, y);
            if (widgetRenderPos == null) {
                continue;
            }
            RenderPos absRenderPos = widgetRenderPos.add(renderPos);
            if (widget.onMouseReleased(mousePos.relativeTo(widgetRenderPos), absRenderPos)) {
                return true;
            }
            y += widget.getMarginUp() + widget.getHeight() + widget.getMarginDown();
        }
        return false;
    }

    @Override
    public boolean onMouseDWheel(final MousePos mousePos, final RenderPos renderPos, final int wheel) {
        int y = 0;

        int width = getWidth();

        for (final DynamicWidget widget : widgets) {
            if (widget.isDisabled()) {
                continue;
            }
            RenderPos widgetRenderPos = getWidgetRenderOffset(widget, width, y);
            if (widgetRenderPos == null) {
                continue;
            }
            RenderPos absRenderPos = widgetRenderPos.add(renderPos);
            if (widget.onMouseDWheel(mousePos.relativeTo(widgetRenderPos), absRenderPos, wheel)) {
                return true;
            }
            y += widget.getMarginUp() + widget.getHeight() + widget.getMarginDown();
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

    // Tooltips

    @Override
    public List<String> getHoverTooltips(final MousePos mousePos) {
        int y = 0;

        int width = getWidth();

        List<String> tooltips = null;

        for (final DynamicWidget widget : widgets) {
            if (widget.isDisabled()) {
                continue;
            }
            RenderPos widgetRenderPos = getWidgetRenderOffset(widget, width, y);
            if (widgetRenderPos == null) {
                continue;
            }
            int offsetX = widgetRenderPos.posX();
            int offsetY = widgetRenderPos.posY();

            MousePos relativeMousePos = mousePos.relativeTo(widgetRenderPos);
            if (widget.isMouseOver(relativeMousePos)) {
                List<String> hoverTooltips = widget.getHoverTooltips(relativeMousePos);
                if (!hoverTooltips.isEmpty()) {
                    tooltips = hoverTooltips;
                    break;
                }
            }

            y += widget.getMarginUp() + widget.getHeight() + widget.getMarginDown();
        }

        return tooltips != null ? tooltips : Collections.emptyList();
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

    public RenderPos getWidgetRenderOffset(DynamicWidget widget, int width, int y) {
        int xOffset;
        int yOffset;

        if (isCenterAligned()) {
            xOffset = (width - (widget.getMarginLeft() + widget.getWidth() + widget.getMarginRight())) / 2;
            yOffset = y + widget.getMarginUp();
        } else if (leftAligned) {
            xOffset = widget.getMarginLeft();
            yOffset = y + widget.getMarginUp();
        } else if (rightAligned) {
            xOffset = width - widget.getWidth() - widget.getMarginRight();
            yOffset = y + widget.getMarginUp();
        } else {
            // Where does it align?
            return null;
        }

        return new RenderPos(xOffset, yOffset);
    }

    // X/Y Size

    @Override
    public int getWidth() {
        int maxX = 0;
        for (final DynamicWidget widget : widgets) {
            int width = widget.getMarginLeft() + widget.getWidth() + widget.getMarginRight();
            if (width > maxX) {
                maxX = width;
            }
        }
        return maxX;
    }

    @Override
    public DynamicWidget setWidth(final int width) {
        // It's dynamic, so ignore it.
        return this;
    }

    @Override
    public int getHeight() {
        int height = 0;
        for (final DynamicWidget widget : widgets) {
            height += widget.getMarginUp() + widget.getHeight() + widget.getMarginDown();
        }
        return height;
    }

    @Override
    public DynamicWidget setHeight(final int height) {
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
