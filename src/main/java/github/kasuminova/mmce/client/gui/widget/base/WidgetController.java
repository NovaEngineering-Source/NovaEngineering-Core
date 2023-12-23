package github.kasuminova.mmce.client.gui.widget.base;

import github.kasuminova.mmce.client.gui.util.MousePos;
import github.kasuminova.mmce.client.gui.util.RenderPos;
import github.kasuminova.mmce.client.gui.util.RenderSize;
import github.kasuminova.mmce.client.gui.widget.container.WidgetContainer;
import github.kasuminova.mmce.client.gui.widget.event.GuiEvent;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.input.Mouse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WidgetController {

    protected final GuiContainer gui;
    protected final List<WidgetContainer> containers = new ArrayList<>();

    private boolean initialized = false;

    public WidgetController(final GuiContainer gui) {
        this.gui = gui;
    }

    public void addWidgetContainer(final WidgetContainer widgetContainer) {
        containers.add(widgetContainer);
    }

    public void render(final MousePos mousePos) {
        GuiContainer gui = this.gui;

        final int guiLeft = (gui.width - gui.getXSize()) / 2;
        final int guiTop = (gui.height - gui.getYSize()) / 2;

        GlStateManager.pushMatrix();
        GlStateManager.translate(guiLeft, guiTop, 0F);

        for (final WidgetContainer container : containers) {
            RenderPos renderPos = new RenderPos(guiLeft + container.getAbsX(), guiTop + container.getAbsY());
            RenderPos relativeRenderPos = renderPos.subtract(new RenderPos(guiLeft, guiTop));
            MousePos relativeMousePos = mousePos.relativeTo(renderPos);
            RenderSize renderSize = new RenderSize(container.getWidth(), container.getHeight());
            container.preRender(gui, renderSize, relativeRenderPos, relativeMousePos);
        }
        for (final WidgetContainer container : containers) {
            RenderPos renderPos = new RenderPos(guiLeft + container.getAbsX(), guiTop + container.getAbsY());
            RenderPos relativeRenderPos = renderPos.subtract(new RenderPos(guiLeft, guiTop));
            MousePos relativeMousePos = mousePos.relativeTo(renderPos);
            RenderSize renderSize = new RenderSize(container.getWidth(), container.getHeight());
            container.render(gui, renderSize, relativeRenderPos, relativeMousePos);
        }

        GlStateManager.popMatrix();
    }

    public void postRender(final MousePos mousePos) {
        GuiContainer gui = this.gui;

        final int guiLeft = (gui.width - gui.getXSize()) / 2;
        final int guiTop = (gui.height - gui.getYSize()) / 2;

        GlStateManager.pushMatrix();
//        GlStateManager.translate(guiLeft, guiTop, 0F);

        for (final WidgetContainer container : containers) {
            RenderPos renderPos = new RenderPos(guiLeft + container.getAbsX(), guiTop + container.getAbsY());
            RenderPos relativeRenderPos = renderPos.subtract(new RenderPos(guiLeft, guiTop));
            MousePos relativeMousePos = mousePos.relativeTo(renderPos);
            RenderSize renderSize = new RenderSize(container.getWidth(), container.getHeight());
            container.postRender(gui, renderSize, relativeRenderPos, relativeMousePos);
        }

        GlStateManager.popMatrix();
    }

    public void renderTooltip(final MousePos mousePos) {
        final int guiLeft = (gui.width - gui.getXSize()) / 2;
        final int guiTop = (gui.height - gui.getYSize()) / 2;

        List<String> hoverTooltips = getHoverTooltips(mousePos);
        if (!hoverTooltips.isEmpty()) {
            MousePos relativeMousePos = mousePos.relativeTo(new RenderPos(guiLeft, guiTop));
            gui.drawHoveringText(hoverTooltips, relativeMousePos.mouseX(), relativeMousePos.mouseY());
        }
    }

    public void init() {
        if (!initialized) {
            GuiContainer gui = this.gui;
            containers.forEach(container -> container.initWidget(gui));
        }
        this.initialized = true;
    }

    public void update() {
        GuiContainer gui = this.gui;
        containers.forEach(container -> container.update(gui));
    }

    public void onGUIClosed() {
        GuiContainer gui = this.gui;
        containers.forEach(container -> container.onGUIClosed(gui));
    }

    public void postGuiEvent(GuiEvent event) {
        for (final WidgetContainer container : containers) {
            if (container.onGuiEvent(event)) {
                break;
            }
        }
    }

    public boolean onMouseClicked(final MousePos mousePos, final int mouseButton) {
        GuiContainer gui = this.gui;

        final int x = (gui.width - gui.getXSize()) / 2;
        final int y = (gui.height - gui.getYSize()) / 2;

        for (final WidgetContainer container : containers) {
            RenderPos renderPos = new RenderPos(x + container.getAbsX(), y + container.getAbsY());
            RenderPos relativeRenderPos = renderPos.subtract(new RenderPos(x, y));
            MousePos relativeMousePos = mousePos.relativeTo(renderPos);

            if (container.isMouseOver(relativeMousePos)) {
                if (container.onMouseClicked(relativeMousePos, relativeRenderPos, mouseButton)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean onMouseReleased(final MousePos mousePos) {
        GuiContainer gui = this.gui;

        final int x = (gui.width - gui.getXSize()) / 2;
        final int y = (gui.height - gui.getYSize()) / 2;

        for (final WidgetContainer container : containers) {
            RenderPos renderPos = new RenderPos(x + container.getAbsX(), y + container.getAbsY());
            RenderPos relativeRenderPos = renderPos.subtract(new RenderPos(x, y));
            MousePos relativeMousePos = mousePos.relativeTo(renderPos);

            if (container.onMouseReleased(relativeMousePos, relativeRenderPos)) {
                return true;
            }
        }
        return false;
    }

    public boolean onMouseInput(final MousePos mousePos) {
        final int wheel = Mouse.getEventDWheel();
        if (wheel == 0) {
            return false;
        }
        GuiContainer gui = this.gui;

        final int x = (gui.width - gui.getXSize()) / 2;
        final int y = (gui.height - gui.getYSize()) / 2;

        for (final WidgetContainer container : containers) {
            RenderPos renderPos = new RenderPos(x + container.getAbsX(), y + container.getAbsY());
            RenderPos relativeRenderPos = renderPos.subtract(new RenderPos(x, y));
            MousePos relativeMousePos = mousePos.relativeTo(renderPos);

            if (container.onMouseDWheel(relativeMousePos, relativeRenderPos, wheel)) {
                return true;
            }
        }
        return false;
    }

    public boolean onKeyTyped(final char typedChar, final int keyCode) {
        for (final WidgetContainer container : containers) {
            if (container.onKeyTyped(typedChar, keyCode)) {
                return true;
            }
        }
        return false;
    }

    public List<String> getHoverTooltips(final MousePos mousePos) {
        GuiContainer gui = this.gui;

        final int x = (gui.width - gui.getXSize()) / 2;
        final int y = (gui.height - gui.getYSize()) / 2;

        List<String> tooltips = null;
        for (final WidgetContainer container : containers) {
            RenderPos renderPos = new RenderPos(x + container.getAbsX(), y + container.getAbsY());
            MousePos relativeMousePos = mousePos.relativeTo(renderPos);

            if (container.isMouseOver(relativeMousePos)) {
                List<String> hoverTooltips = container.getHoverTooltips(relativeMousePos);
                if (!hoverTooltips.isEmpty()) {
                    tooltips = hoverTooltips;
                    break;
                }
            }
        }

        return tooltips != null ? tooltips : Collections.emptyList();
    }

    public GuiContainer getGui() {
        return gui;
    }

    public List<WidgetContainer> getContainers() {
        return containers;
    }

}
