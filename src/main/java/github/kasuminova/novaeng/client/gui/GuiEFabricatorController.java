package github.kasuminova.novaeng.client.gui;

import github.kasuminova.mmce.client.gui.GuiContainerDynamic;
import github.kasuminova.mmce.client.gui.widget.base.WidgetController;
import github.kasuminova.mmce.client.gui.widget.base.WidgetGui;
import github.kasuminova.novaeng.NovaEngineeringCore;
import github.kasuminova.novaeng.client.gui.widget.efabricator.ControlPanel;
import github.kasuminova.novaeng.client.gui.widget.efabricator.CraftingStatusPanel;
import github.kasuminova.novaeng.client.gui.widget.efabricator.HeatStatisticPanel;
import github.kasuminova.novaeng.client.gui.widget.efabricator.TotalCraftedLabel;
import github.kasuminova.novaeng.client.gui.widget.efabricator.event.EFGUIDataUpdateEvent;
import github.kasuminova.novaeng.common.container.ContainerEFabricatorController;
import github.kasuminova.novaeng.common.container.data.EFabricatorData;
import github.kasuminova.novaeng.common.tile.efabricator.EFabricatorController;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

public class GuiEFabricatorController extends GuiContainerDynamic<ContainerEFabricatorController> {

    public static final ResourceLocation TEXTURES_BACKGROUND_LIGHT = new ResourceLocation(
            NovaEngineeringCore.MOD_ID, "textures/gui/efabricator_light.png");
    public static final ResourceLocation TEXTURES_BACKGROUND_DARK = new ResourceLocation(
            NovaEngineeringCore.MOD_ID, "textures/gui/efabricator_dark.png");
    public static final ResourceLocation TEXTURES_INVENTORY = new ResourceLocation(
            NovaEngineeringCore.MOD_ID, "textures/gui/efabricator_inventory.png");
    public static final ResourceLocation TEXTURES_ELEMENTS = new ResourceLocation(
            NovaEngineeringCore.MOD_ID, "textures/gui/efabricator_elements.png");

    private EFabricatorData data = null;

    public GuiEFabricatorController(final EFabricatorController controller, final EntityPlayer opening) {
        super(new ContainerEFabricatorController(controller, opening));
        this.xSize = 243;
        this.ySize = 202;
        this.widgetController = new WidgetController(WidgetGui.of(this));
        this.widgetController.addWidget(new TotalCraftedLabel().setAbsXY(5, 1));
        this.widgetController.addWidget(new CraftingStatusPanel().setAbsXY(7, 15));
        this.widgetController.addWidget(new ControlPanel().setAbsXY(7, 77));
        this.widgetController.addWidget(new HeatStatisticPanel().setAbsXY(184, 117));
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(final float partialTicks, final int mouseX, final int mouseY) {
        GlStateManager.color(1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(TEXTURES_BACKGROUND_DARK);
        final int x = (this.width - this.xSize) / 2;
        final int y = (this.height - this.ySize) / 2;
        Gui.drawModalRectWithCustomSizedTexture(x, y, 0, 0, 256, 207, 256, 256);
        this.mc.getTextureManager().bindTexture(TEXTURES_INVENTORY);
        Gui.drawModalRectWithCustomSizedTexture(x + 17, y + 118, 1, 120, 162, 76, 256, 256);
        super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
    }

    public void onDataUpdate(final EFabricatorData data) {
        this.data = data;
        this.widgetController.postGuiEvent(new EFGUIDataUpdateEvent(this));
    }

    public EFabricatorData getData() {
        return data;
    }

}
