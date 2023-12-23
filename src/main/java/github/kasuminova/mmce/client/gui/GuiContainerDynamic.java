package github.kasuminova.mmce.client.gui;

import github.kasuminova.mmce.client.gui.util.MousePos;
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
    protected void setWidthHeight() {
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        widgetController.update();
    }

    @Override
    public void initGui() {
        super.initGui();
        widgetController.init();
    }

    @Override
    public void onGuiClosed() {
        widgetController.onGUIClosed();
        super.onGuiClosed();
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(final float partialTicks, final int mouseX, final int mouseY) {
        widgetController.render(new MousePos(mouseX, mouseY));
    }

    @Override
    protected void drawGuiContainerForegroundLayer(final int mouseX, final int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        widgetController.postRender(new MousePos(mouseX, mouseY));
        widgetController.renderTooltip(new MousePos(mouseX, mouseY));
    }

    @Override
    public void handleMouseInput() throws IOException {
        final int mouseX = Mouse.getEventX() * this.width / this.mc.displayWidth;
        final int mouseY = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;
        if (widgetController.onMouseInput(new MousePos(mouseX, mouseY))) {
            return;
        }
        super.handleMouseInput();
    }

    @Override
    protected void mouseClicked(final int mouseX, final int mouseY, final int mouseButton) throws IOException {
        if (widgetController.onMouseClicked(new MousePos(mouseX, mouseY), mouseButton)) {
            return;
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void mouseReleased(final int mouseX, final int mouseY, final int state) {
        if (widgetController.onMouseReleased(new MousePos(mouseX, mouseY))) {
            return;
        }
        super.mouseReleased(mouseX, mouseY, state);
    }

    @Override
    protected void keyTyped(final char typedChar, final int keyCode) throws IOException {
        if (widgetController.onKeyTyped(typedChar, keyCode)) {
            return;
        }
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    public void drawHoveringText(@Nonnull final List<String> textLines, final int x, final int y) {
        super.drawHoveringText(textLines, x, y);
    }

}
