package github.kasuminova.novaeng.client.gui;

import github.kasuminova.mmce.client.gui.GuiContainerDynamic;
import github.kasuminova.mmce.client.gui.widget.base.WidgetController;
import github.kasuminova.mmce.client.gui.widget.base.WidgetGui;
import github.kasuminova.novaeng.NovaEngineeringCore;
import github.kasuminova.novaeng.common.container.ContainerEStorageController;
import github.kasuminova.novaeng.common.tile.estorage.EStorageController;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

public class GuiEStorageController extends GuiContainerDynamic<ContainerEStorageController> {
    public static final ResourceLocation TEXTURES_BACKGROUND = new ResourceLocation(
            NovaEngineeringCore.MOD_ID, "textures/gui/estorage_controller.png");

    public GuiEStorageController(final EStorageController controller, final EntityPlayer opening) {
        super(new ContainerEStorageController(controller, opening));
        this.xSize = 256;
        this.ySize = 207;
        this.widgetController = new WidgetController(WidgetGui.of(this));
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(final float partialTicks, final int mouseX, final int mouseY) {
        GlStateManager.color(1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(TEXTURES_BACKGROUND);
        final int x = (this.width - this.xSize) / 2;
        final int y = (this.height - this.ySize) / 2;
        Gui.drawModalRectWithCustomSizedTexture(x, y, 0, 0, 256, 207, 256, 256);
        super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
    }

}
