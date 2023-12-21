package github.kasuminova.mmce.client.gui.widget.container;

import github.kasuminova.mmce.client.gui.util.MousePos;
import github.kasuminova.mmce.client.gui.util.RenderFunction;
import github.kasuminova.mmce.client.gui.util.RenderPos;
import github.kasuminova.mmce.client.gui.util.RenderSize;
import github.kasuminova.mmce.client.gui.widget.Scrollbar;
import github.kasuminova.mmce.client.gui.widget.base.DynamicWidget;
import github.kasuminova.mmce.client.gui.widget.event.GuiEvent;
import net.minecraft.client.gui.inventory.GuiContainer;

import java.util.Collections;
import java.util.List;

@SuppressWarnings("unused")
public class ScrollingColumn extends Column {
    protected final Scrollbar scrollbar = new Scrollbar();

    @Override
    protected void doRender(final GuiContainer gui, final RenderSize renderSize, final RenderPos renderPos, final MousePos mousePos, final RenderFunction renderFunction) {
        int width = renderSize.width();
        int height = renderSize.height();

        int y = getTotalHeight() > height ? -scrollbar.getCurrentScroll() : 0;

        for (final DynamicWidget widget : widgets) {
            if (widget.isDisabled()) {
                continue;
            }
            RenderPos widgetRenderPos = getWidgetRenderOffset(widget, width, y);
            if (widgetRenderPos == null) {
                continue;
            }
            int offsetY = widgetRenderPos.posY();
            if (offsetY + widget.getHeight() >= 0) {
                RenderPos absRenderPos = widgetRenderPos.add(renderPos);
                renderFunction.doRender(widget, gui, new RenderSize(widget.getWidth(), widget.getHeight()), absRenderPos, mousePos.relativeTo(widgetRenderPos));
            }

            y += widget.getMarginUp() + widget.getHeight() + widget.getMarginDown();
            if (renderSize.isHeightLimited() && y >= renderSize.height()) {
                break;
            }
        }
    }

    @Override
    public ScrollingColumn addWidget(final DynamicWidget widget) {
        super.addWidget(widget);
        return this;
    }

    @Override
    public ScrollingColumn removeWidget(final DynamicWidget widget) {
        super.removeWidget(widget);
        return this;
    }

    // GUI EventHandlers

    @Override
    public void update(final GuiContainer gui) {
        super.update(gui);
        scrollbar.setRange(0, Math.max(getTotalHeight() - height, 0)).setHeight(height);
    }

    @Override
    public boolean onMouseClicked(final MousePos mousePos, final RenderPos renderPos, final int mouseButton) {
        int width = this.width;
        int height = this.height;

        int y = getTotalHeight() > height ? -scrollbar.getCurrentScroll() : 0;

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

            if (offsetY + widget.getHeight() >= 0) {
                MousePos relativeMousePos = mousePos.relativeTo(widgetRenderPos);
                if (widget.isMouseOver(relativeMousePos)) {
                    RenderPos absRenderPos = widgetRenderPos.add(renderPos);
                    if (widget.onMouseClicked(relativeMousePos, absRenderPos, mouseButton)) {
                        return true;
                    }
                }
            }
            y += widget.getMarginUp() + widget.getHeight() + widget.getMarginDown();
        }

        RenderPos scrollbarRenderPos = new RenderPos(
                width - (scrollbar.getMarginLeft() + scrollbar.getWidth() + scrollbar.getMarginRight()),
                height - (scrollbar.getMarginUp() + scrollbar.getHeight() + scrollbar.getMarginDown()));
        MousePos scrollbarRelativeMousePos = mousePos.relativeTo(scrollbarRenderPos);
        if (scrollbar.isMouseOver(scrollbarRelativeMousePos)) {
            return scrollbar.onMouseClicked(scrollbarRelativeMousePos, renderPos.add(scrollbarRenderPos), mouseButton);
        }

        return false;
    }

    @Override
    public boolean onMouseReleased(final MousePos mousePos, final RenderPos renderPos) {
        int width = this.width;
        int height = this.height;

        int y = getTotalHeight() > height ? -scrollbar.getCurrentScroll() : 0;

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
        int width = this.width;
        int height = this.height;

        int y = getTotalHeight() > height ? -scrollbar.getCurrentScroll() : 0;

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

        RenderPos scrollbarRenderPos = new RenderPos(
                width - (scrollbar.getMarginLeft() + scrollbar.getWidth() + scrollbar.getMarginRight()),
                height - (scrollbar.getMarginUp() + scrollbar.getHeight() + scrollbar.getMarginDown()));
        MousePos scrollbarRelativeMousePos = mousePos.relativeTo(scrollbarRenderPos);
        if (scrollbar.isMouseOver(scrollbarRelativeMousePos)) {
            return scrollbar.onMouseDWheel(scrollbarRelativeMousePos, renderPos.add(scrollbarRenderPos), wheel);
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
        int width = this.width;
        int height = this.height;

        int y = getTotalHeight() > height ? -scrollbar.getCurrentScroll() : 0;

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

        if (tooltips != null) {
            return tooltips;
        }

        RenderPos scrollbarRenderPos = new RenderPos(
                width - (scrollbar.getMarginLeft() + scrollbar.getWidth() + scrollbar.getMarginRight()),
                height - (scrollbar.getMarginUp() + scrollbar.getHeight() + scrollbar.getMarginDown()));
        MousePos scrollbarMousePos = mousePos.relativeTo(scrollbarRenderPos);
        if (scrollbar.isMouseOver(scrollbarMousePos)) {
            List<String> hoverTooltips = scrollbar.getHoverTooltips(scrollbarMousePos);
            if (!hoverTooltips.isEmpty()) {
                tooltips = hoverTooltips;
            }
        }

        return tooltips != null ? tooltips : Collections.emptyList();
    }

    // CustomEventHandlers

    @Override
    public boolean onGuiEvent(final GuiEvent event) {
        if (scrollbar.onGuiEvent(event)) {
            return true;
        }
        return super.onGuiEvent(event);
    }

    // Utils

    @Override
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
        return width;
    }

    @Override
    public DynamicWidget setWidth(final int width) {
        this.width = width;
        return this;
    }

    @Override
    public int getHeight() {
        return this.height;
    }

    @Override
    public DynamicWidget setHeight(final int height) {
        this.height = height;
        return this;
    }

    public int getTotalHeight() {
        int total = 0;
        for (final DynamicWidget widget : widgets) {
            total += widget.getMarginUp() + widget.getHeight() + widget.getMarginDown();
        }
        return total;
    }

}
