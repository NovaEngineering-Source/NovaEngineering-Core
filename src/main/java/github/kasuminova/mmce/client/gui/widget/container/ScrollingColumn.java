package github.kasuminova.mmce.client.gui.widget.container;

import github.kasuminova.mmce.client.gui.util.MousePos;
import github.kasuminova.mmce.client.gui.util.RenderPos;
import github.kasuminova.mmce.client.gui.util.RenderSize;
import github.kasuminova.mmce.client.gui.widget.Scrollbar;
import github.kasuminova.mmce.client.gui.widget.base.DynamicWidget;
import github.kasuminova.mmce.client.gui.widget.event.GuiEvent;
import net.minecraft.client.gui.inventory.GuiContainer;

@SuppressWarnings("unused")
public class ScrollingColumn extends Column {
    protected final Scrollbar scrollbar = new Scrollbar();

    @Override
    protected void preRenderInternal(final GuiContainer gui, final RenderSize renderSize, final RenderPos renderPos, final MousePos mousePos) {
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
                widget.preRender(gui, new RenderSize(widget.getWidth(), widget.getHeight()), absRenderPos, mousePos.relativeTo(widgetRenderPos));
            }
            y += widget.getMarginUp() + widget.getHeight() + widget.getMarginDown();
        }
    }

    @Override
    protected void postRenderInternal(final GuiContainer gui, final RenderSize renderSize, final RenderPos renderPos, final MousePos mousePos) {
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
                widget.postRender(gui, new RenderSize(widget.getWidth(), widget.getHeight()), absRenderPos, mousePos.relativeTo(widgetRenderPos));
            }
            y += widget.getMarginUp() + widget.getHeight() + widget.getMarginDown();
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
                if (widget.isMouseOver(offsetX, offsetY, mousePos.mouseX(), mousePos.mouseY())) {
                    RenderPos absRenderPos = widgetRenderPos.add(renderPos);
                    if (widget.onMouseClicked(mousePos.relativeTo(widgetRenderPos), absRenderPos, mouseButton)) {
                        return true;
                    }
                }
            }
            y += widget.getMarginUp() + widget.getHeight() + widget.getMarginDown();
        }

        RenderPos scrollbarRenderPos = new RenderPos(
                width - (scrollbar.getMarginLeft() + scrollbar.getWidth() + scrollbar.getMarginRight()),
                height - (scrollbar.getMarginUp() + scrollbar.getHeight() + scrollbar.getMarginDown()));
        if (scrollbar.isMouseOver(scrollbarRenderPos.posX(), scrollbarRenderPos.posY())) {
            return scrollbar.onMouseClicked(mousePos.relativeTo(scrollbarRenderPos), renderPos.add(scrollbarRenderPos), mouseButton);
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
        if (isMouseOver(mousePos.mouseX(), mousePos.mouseY())) {
            return scrollbar.onMouseDWheel(mousePos.relativeTo(scrollbarRenderPos), renderPos.add(scrollbarRenderPos), wheel);
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
