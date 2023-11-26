package github.kasuminova.mmce.client.gui.widget.tab;

import github.kasuminova.mmce.client.gui.util.RenderOffset;
import github.kasuminova.mmce.client.gui.util.RenderSize;
import github.kasuminova.mmce.client.gui.widget.base.DynamicWidget;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Slot;

import java.util.ArrayList;
import java.util.List;

public class TabInventory extends DynamicWidget {

    private final List<Slot> slots = new ArrayList<>();


    @Override
    public void postRender(final GuiContainer gui, final RenderSize renderSize, final RenderOffset renderOffset, final int mouseX, final int mouseY) {

    }

}
