package github.kasuminova.mmce.client.gui.util;

import github.kasuminova.mmce.client.gui.widget.base.DynamicWidget;
import net.minecraft.client.gui.inventory.GuiContainer;

public interface RenderFunction {

    void doRender(DynamicWidget dynamicWidget, GuiContainer guiContainer, RenderSize renderSize, RenderPos renderPos, MousePos mousePos);

}
