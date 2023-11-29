package github.kasuminova.mmce.client.gui.widget.base;

import github.kasuminova.mmce.client.gui.util.MousePos;
import github.kasuminova.mmce.client.gui.util.RenderPos;
import github.kasuminova.mmce.client.gui.util.RenderSize;
import github.kasuminova.mmce.client.gui.widget.container.WidgetContainer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.input.Mouse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WidgetController {
    private final GuiContainer gui;

    private final List<WidgetContainer> containers = new ArrayList<>();

    public WidgetController(final GuiContainer gui) {
        this.gui = gui;
    }

    public void addWidgetContainer(final WidgetContainer widgetContainer) {
        containers.add(widgetContainer);
    }

    public void render(final MousePos mousePos) {
        GlStateManager.pushMatrix();

        GuiContainer gui = this.gui;

        final int x = (gui.width - gui.getXSize()) / 2;
        final int y = (gui.height - gui.getYSize()) / 2;

        for (final WidgetContainer container : containers) {
            RenderPos renderPos = new RenderPos(x + container.getAbsX(), y + container.getAbsY());
            RenderPos relativeRenderPos = renderPos.subtract(new RenderPos(x, y));
            MousePos relativeMousePos = mousePos.relativeTo(renderPos);

            container.preRender(gui, new RenderSize(-1, -1), relativeRenderPos, relativeMousePos);
        }
        for (final WidgetContainer container : containers) {
            RenderPos renderPos = new RenderPos(x + container.getAbsX(), y + container.getAbsY());
            RenderPos relativeRenderPos = renderPos.subtract(new RenderPos(x, y));
            MousePos relativeMousePos = mousePos.relativeTo(renderPos);

            container.postRender(gui, new RenderSize(-1, -1), relativeRenderPos, relativeMousePos);
        }

        List<String> hoverTooltips = getHoverTooltips(mousePos);
        if (!hoverTooltips.isEmpty()) {
            gui.drawHoveringText(hoverTooltips, mousePos.mouseX(), mousePos.mouseY());
        }

        GlStateManager.popMatrix();
    }

    public void update() {
        GuiContainer gui = this.gui;

        for (final WidgetContainer container : containers) {
            container.update(gui);
        }
    }

    public void onMouseClicked(final MousePos mousePos, final int mouseButton) {
        GuiContainer gui = this.gui;

        final int x = (gui.width - gui.getXSize()) / 2;
        final int y = (gui.height - gui.getYSize()) / 2;

        for (final WidgetContainer container : containers) {
            RenderPos renderPos = new RenderPos(x + container.getAbsX(), y + container.getAbsY());
            RenderPos relativeRenderPos = renderPos.subtract(new RenderPos(x, y));
            MousePos relativeMousePos = mousePos.relativeTo(renderPos);

            if (container.isMouseOver(relativeMousePos)) {
                if (container.onMouseClicked(relativeMousePos, relativeRenderPos, mouseButton)) {
                    break;
                }
            }
        }
    }

    public void onMouseReleased(final MousePos mousePos) {
        GuiContainer gui = this.gui;

        final int x = (gui.width - gui.getXSize()) / 2;
        final int y = (gui.height - gui.getYSize()) / 2;

        for (final WidgetContainer container : containers) {
            RenderPos renderPos = new RenderPos(x + container.getAbsX(), y + container.getAbsY());
            RenderPos relativeRenderPos = renderPos.subtract(new RenderPos(x, y));
            MousePos relativeMousePos = mousePos.relativeTo(renderPos);

            if (container.onMouseReleased(relativeMousePos, relativeRenderPos)) {
                break;
            }
        }
    }

    public void onMouseInput(final MousePos mousePos) {
        final int wheel = Mouse.getEventDWheel();
        if (wheel == 0) {
            return;
        }
        GuiContainer gui = this.gui;

        final int x = (gui.width - gui.getXSize()) / 2;
        final int y = (gui.height - gui.getYSize()) / 2;

        for (final WidgetContainer container : containers) {
            RenderPos renderPos = new RenderPos(x + container.getAbsX(), y + container.getAbsY());
            RenderPos relativeRenderPos = renderPos.subtract(new RenderPos(x, y));
            MousePos relativeMousePos = mousePos.relativeTo(renderPos);

            if (container.onMouseDWheel(relativeMousePos, relativeRenderPos, wheel)) {
                break;
            }
        }
    }

    public void onKeyTyped(final char typedChar, final int keyCode) {
        for (final WidgetContainer container : containers) {
            if (container.onKeyTyped(typedChar, keyCode)) {
                break;
            }
        }
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

}
