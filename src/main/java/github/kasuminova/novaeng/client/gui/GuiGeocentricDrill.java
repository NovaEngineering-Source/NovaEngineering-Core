package github.kasuminova.novaeng.client.gui;

import github.kasuminova.mmce.client.gui.GuiContainerDynamic;
import github.kasuminova.mmce.client.gui.util.TextureProperties;
import github.kasuminova.mmce.client.gui.widget.Button4State;
import github.kasuminova.mmce.client.gui.widget.base.WidgetController;
import github.kasuminova.mmce.client.gui.widget.base.WidgetGui;
import github.kasuminova.novaeng.NovaEngineeringCore;
import github.kasuminova.novaeng.client.gui.widget.geocentricdrill.DrillMonitor;
import github.kasuminova.novaeng.client.gui.widget.geocentricdrill.OreControlList;
import github.kasuminova.novaeng.common.container.ContainerGeocentricDrill;
import github.kasuminova.novaeng.common.machine.GeocentricDrill;
import github.kasuminova.novaeng.common.network.PktGeocentricDrillControl;
import github.kasuminova.novaeng.common.tile.machine.GeocentricDrillController;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;

import java.util.Collections;

public class GuiGeocentricDrill extends GuiContainerDynamic<ContainerGeocentricDrill> {

    public static final ResourceLocation GUI_TEXTURE =
            new ResourceLocation(NovaEngineeringCore.MOD_ID, "textures/gui/guigeocentricdrill.png");

    private final OreControlList oreControlList = new OreControlList();
    
    private final GeocentricDrillController owner;

    private final Button4State dive = new Button4State();
    private final Button4State ascend = new Button4State();

    public GuiGeocentricDrill(final GeocentricDrillController controller, final EntityPlayer opening) {
        super(new ContainerGeocentricDrill(controller, opening));
        this.xSize = 176;
        this.ySize = 213;
        this.guiLeft = (this.width - this.xSize) / 2;
        this.guiTop = (this.height - this.ySize) / 2;
        this.owner = controller;

        this.widgetController = new WidgetController(WidgetGui.of(this, this.xSize, this.ySize, guiLeft, guiTop));
        this.widgetController.addWidget(new DrillMonitor(controller).setAbsXY(7, 7));

        this.dive.setWidthHeight(12, 12);
        this.dive.setAbsXY(142, 55);
        this.dive.setMouseDownTexture(TextureProperties.of(GUI_TEXTURE, 200, 20, 12, 12))
                .setHoveredTexture(TextureProperties.of(GUI_TEXTURE, 188, 20, 12, 12))
                .setUnavailableTexture(TextureProperties.of(GUI_TEXTURE, 212, 20, 12, 12))
                .setTexture(TextureProperties.of(GUI_TEXTURE, 176, 20, 12, 12));
        this.dive.setTooltipFunction(button -> {
            boolean isControlDown = Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_RCONTROL);
            if (isControlDown) {
                return Collections.singletonList(I18n.format("gui.geocentric_drill.dive.ctrl_down"));
            }
            return Collections.singletonList(I18n.format("gui.geocentric_drill.dive"));
        });
        this.dive.setOnClickedListener((button) -> {
            boolean isControlDown = Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_RCONTROL);
            int newDepth = Math.min(owner.getTargetDepth() + (isControlDown ? 1000 : 100), GeocentricDrill.MAX_DEPTH);
            NovaEngineeringCore.NET_CHANNEL.sendToServer(new PktGeocentricDrillControl(PktGeocentricDrillControl.Type.SET_TARGET_DEPTH, newDepth));
            this.dive.setAvailable(newDepth < GeocentricDrill.MAX_DEPTH);
        });
        this.ascend.setWidthHeight(12, 12);
        this.ascend.setAbsXY(157, 55);
        this.ascend.setMouseDownTexture(TextureProperties.of(GUI_TEXTURE, 200, 34, 12, 12))
                .setHoveredTexture(TextureProperties.of(GUI_TEXTURE, 188, 34, 12, 12))
                .setUnavailableTexture(TextureProperties.of(GUI_TEXTURE, 212, 34, 12, 12))
                .setTexture(TextureProperties.of(GUI_TEXTURE, 176, 34, 12, 12));
        this.ascend.setTooltipFunction(button -> {
            boolean isControlDown = Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_RCONTROL);
            if (isControlDown) {
                return Collections.singletonList(I18n.format("gui.geocentric_drill.ascend.ctrl_down"));
            }
            return Collections.singletonList(I18n.format("gui.geocentric_drill.ascend"));
        });
        this.ascend.setOnClickedListener((button) -> {
            boolean isControlDown = Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_RCONTROL);
            int newDepth = Math.max(owner.getTargetDepth() - (isControlDown ? 1000 : 100), GeocentricDrill.MIN_DEPTH);
            NovaEngineeringCore.NET_CHANNEL.sendToServer(new PktGeocentricDrillControl(PktGeocentricDrillControl.Type.SET_TARGET_DEPTH, newDepth));
            this.ascend.setAvailable(newDepth > GeocentricDrill.MIN_DEPTH);
        });

        this.widgetController.addWidget(this.dive);
        this.widgetController.addWidget(this.ascend);

        this.oreControlList.setAbsXY(7, 70);
        this.oreControlList.setWidthHeight(162, 54);
        this.widgetController.addWidget(this.oreControlList);

        updateData();
    }

    @Override
    protected void drawGuiContainerForegroundLayer(final int mouseX, final int mouseY) {
        FontRenderer fr = Minecraft.getMinecraft().fontRenderer;
        fr.drawStringWithShadow(I18n.format("gui.geocentric_drill.ore_control"), 7, 52 + 4, 0xFFFFFF);
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(final float partialTicks, final int mouseX, final int mouseY) {
        GlStateManager.color(1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(GUI_TEXTURE);
        final int x = (this.width - this.xSize) / 2;
        final int y = (this.height - this.ySize) / 2;
        Gui.drawModalRectWithCustomSizedTexture(x, y, 0, 0, 176, 213, 256, 256);
        super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
    }

    public void updateData() {
        this.oreControlList.setStackList(GeocentricDrill.GEOCENTRIC_DRILL.getRawOres(), owner.getAccelerateOres());
        this.dive.setAvailable(owner.getTargetDepth() < GeocentricDrill.MAX_DEPTH);
        this.ascend.setAvailable(owner.getTargetDepth() > GeocentricDrill.MIN_DEPTH);
    }

}
