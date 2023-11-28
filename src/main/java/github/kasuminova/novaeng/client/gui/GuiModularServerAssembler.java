package github.kasuminova.novaeng.client.gui;

import github.kasuminova.mmce.client.gui.util.MousePos;
import github.kasuminova.mmce.client.gui.util.RenderPos;
import github.kasuminova.mmce.client.gui.util.RenderSize;
import github.kasuminova.novaeng.NovaEngineeringCore;
import github.kasuminova.novaeng.client.gui.widget.msa.*;
import github.kasuminova.novaeng.common.container.ContainerModularServerAssembler;
import github.kasuminova.novaeng.common.tile.TileModularServerAssembler;
import hellfirepvp.modularmachinery.client.gui.GuiContainerBase;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

import java.io.IOException;

public class GuiModularServerAssembler extends GuiContainerBase<ContainerModularServerAssembler> {
    public static final ResourceLocation TEXTURES_BACKGROUND = new ResourceLocation(
            NovaEngineeringCore.MOD_ID, "textures/gui/modular_server_assembler.png");

    public static final ResourceLocation TEXTURES_ELEMENTS = new ResourceLocation(
            NovaEngineeringCore.MOD_ID, "textures/gui/msa_elements.png");

    public static final int MAIN_GUI_WIDTH = 329;
    public static final int MAIN_GUI_HEIGHT = 206;

    protected AssemblyInvManager assemblyInvManager = new AssemblyInvManager();

    public GuiModularServerAssembler(final TileModularServerAssembler assembler, final EntityPlayer opening) {
        super(new ContainerModularServerAssembler(assembler, opening));
        this.xSize = 433;
        this.ySize = 206;

        this.assemblyInvManager.addInv(new AssemblyInvCPU(assemblyInvManager));
        this.assemblyInvManager.addInv(new AssemblyInvCalculateCard(assemblyInvManager));
        this.assemblyInvManager.addInv(new AssemblyInvExtension(assemblyInvManager));
        this.assemblyInvManager.addInv(new AssemblyInvHeatRadiator(assemblyInvManager));
        this.assemblyInvManager.addInv(new AssemblyInvPower(assemblyInvManager));
    }

    @Override
    public void initGui() {
        super.initGui();
    }

    @Override
    protected void setWidthHeight() {

    }

    @Override
    protected void drawGuiContainerBackgroundLayer(final float partialTicks, final int mouseX, final int mouseY) {
        GlStateManager.color(1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(TEXTURES_BACKGROUND);
        final int x = (this.width - this.xSize) / 2;
        final int y = (this.height - this.ySize) / 2;
        Gui.drawModalRectWithCustomSizedTexture(x, y, 0, 0, MAIN_GUI_WIDTH, MAIN_GUI_HEIGHT, 512, 512);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(final int mouseX, final int mouseY) {
        GlStateManager.pushMatrix();

        final int x = (this.width - this.xSize) / 2;
        final int y = (this.height - this.ySize) / 2;

        RenderPos renderPos = new RenderPos(x + MAIN_GUI_WIDTH, y);
        MousePos mousePos = new MousePos(mouseX, mouseY).relativeTo(renderPos);

        assemblyInvManager.preRender(this, new RenderSize(-1, -1), renderPos.subtract(new RenderPos(x, y)), mousePos);
        assemblyInvManager.postRender(this, new RenderSize(-1, -1), renderPos.subtract(new RenderPos(x, y)), mousePos);

        GlStateManager.popMatrix();
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
    }

    @Override
    protected void mouseClicked(final int mouseX, final int mouseY, final int mouseButton) throws IOException {
        final int x = (this.width - this.xSize) / 2;
        final int y = (this.height - this.ySize) / 2;

        RenderPos renderPos = new RenderPos(x + MAIN_GUI_WIDTH, y);
        MousePos mousePos = new MousePos(mouseX, mouseY).relativeTo(renderPos);

        if (assemblyInvManager.isMouseOver(mousePos)) {
            assemblyInvManager.onMouseClicked(mousePos, renderPos, mouseButton);
        }

        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void mouseReleased(final int mouseX, final int mouseY, final int state) {
//        final int x = (this.width - this.xSize) / 2;
//        final int y = (this.height - this.ySize) / 2;

        super.mouseReleased(mouseX, mouseY, state);
    }
}
