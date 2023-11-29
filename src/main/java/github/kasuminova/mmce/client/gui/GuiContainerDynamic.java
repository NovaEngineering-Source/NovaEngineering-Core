package github.kasuminova.mmce.client.gui;

import github.kasuminova.mmce.client.gui.util.MousePos;
import github.kasuminova.mmce.client.gui.util.RenderPos;
import github.kasuminova.mmce.client.gui.widget.base.WidgetController;
import hellfirepvp.modularmachinery.client.gui.GuiContainerBase;
import hellfirepvp.modularmachinery.common.container.ContainerBase;
import org.lwjgl.input.Mouse;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.List;

public abstract class GuiContainerDynamic<T extends ContainerBase<?>> extends GuiContainerBase<T> {

    protected final WidgetController widgetController = new WidgetController(this);

    public GuiContainerDynamic(final T container) {
        super(container);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(final int mouseX, final int mouseY) {
        final int x = (this.width - this.xSize) / 2;
        final int y = (this.height - this.ySize) / 2;

        RenderPos renderPos = new RenderPos(x, y);
        MousePos mousePos = new MousePos(mouseX, mouseY).relativeTo(renderPos);
        widgetController.render(mousePos);
    }

    @Override
    public void handleMouseInput() throws IOException {
        final int x = (this.width - this.xSize) / 2;
        final int y = (this.height - this.ySize) / 2;

        final int mouseX = Mouse.getEventX() * this.width / this.mc.displayWidth;
        final int mouseY = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;

        RenderPos renderPos = new RenderPos(x, y);
        MousePos mousePos = new MousePos(mouseX, mouseY).relativeTo(renderPos);
        widgetController.onMouseInput(mousePos);

        super.handleMouseInput();
    }

    @Override
    protected void mouseClicked(final int mouseX, final int mouseY, final int mouseButton) throws IOException {
        final int x = (this.width - this.xSize) / 2;
        final int y = (this.height - this.ySize) / 2;

        RenderPos renderPos = new RenderPos(x, y);
        MousePos mousePos = new MousePos(mouseX, mouseY).relativeTo(renderPos);
        widgetController.onMouseClicked(mousePos, mouseButton);

        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void mouseReleased(final int mouseX, final int mouseY, final int state) {
        final int x = (this.width - this.xSize) / 2;
        final int y = (this.height - this.ySize) / 2;

        RenderPos renderPos = new RenderPos(x, y);
        MousePos mousePos = new MousePos(mouseX, mouseY).relativeTo(renderPos);
        widgetController.onMouseReleased(mousePos);

        super.mouseReleased(mouseX, mouseY, state);
    }

    @Override
    protected void keyTyped(final char typedChar, final int keyCode) throws IOException {
        widgetController.onKeyTyped(typedChar, keyCode);

        super.keyTyped(typedChar, keyCode);
    }

    @Override
    public void drawHoveringText(@Nonnull final List<String> textLines, final int x, final int y) {
        super.drawHoveringText(textLines, x, y);
    }

}
