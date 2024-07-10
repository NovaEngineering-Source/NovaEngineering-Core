package github.kasuminova.novaeng.client.gui;

import com.cleanroommc.client.shader.ShaderManager;
import github.kasuminova.mmce.client.gui.GuiContainerDynamic;
import github.kasuminova.mmce.client.gui.widget.base.WidgetController;
import github.kasuminova.mmce.client.gui.widget.base.WidgetGui;
import github.kasuminova.mmce.client.gui.widget.container.Column;
import github.kasuminova.novaeng.NovaEngineeringCore;
import github.kasuminova.novaeng.client.gui.widget.singularitycore.MultiplierControlPanel;
import github.kasuminova.novaeng.client.gui.widget.singularitycore.Rings;
import github.kasuminova.novaeng.client.gui.widget.singularitycore.StartStopPanel;
import github.kasuminova.novaeng.client.gui.widget.singularitycore.StatusPanel;
import github.kasuminova.novaeng.common.container.ContainerSingularityCore;
import github.kasuminova.novaeng.common.tile.machine.SingularityCore;
import gregtech.client.shader.Shaders;
import gregtech.client.shader.postprocessing.BloomEffect;
import gregtech.client.utils.DepthTextureUtil;
import gregtech.client.utils.RenderUtil;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class GuiSingularityCore extends GuiContainerDynamic<ContainerSingularityCore> {

    public static final ResourceLocation GUI_BACKGROUND = new ResourceLocation(NovaEngineeringCore.MOD_ID, "textures/gui/singularity_core_gui.png");
    public static final ResourceLocation GUI_BACKGROUND_BLOOM = new ResourceLocation(NovaEngineeringCore.MOD_ID, "textures/gui/singularity_core_gui_bloom.png");

    public static final ResourceLocation GUI_SUBASSEMBLY = new ResourceLocation(NovaEngineeringCore.MOD_ID, "textures/gui/singularity_core_subassembly.png");
    public static final ResourceLocation GUI_SUBASSEMBLY_2 = new ResourceLocation(NovaEngineeringCore.MOD_ID, "textures/gui/singularity_core_subassembly_2.png");
    public static final ResourceLocation GUI_SUBASSEMBLY_3 = new ResourceLocation(NovaEngineeringCore.MOD_ID, "textures/gui/singularity_core_subassembly_3.png");

    public static final ResourceLocation GUI_BUTTON = new ResourceLocation(NovaEngineeringCore.MOD_ID, "textures/gui/singularity_core_button.png");

    public static final ResourceLocation GUI_RING_1_OFF = new ResourceLocation(NovaEngineeringCore.MOD_ID, "textures/gui/singularity_core_rings_1_off.png");
    public static final ResourceLocation GUI_RING_1_ON = new ResourceLocation(NovaEngineeringCore.MOD_ID, "textures/gui/singularity_core_rings_1_on.png");
    public static final ResourceLocation GUI_RING_2_OFF = new ResourceLocation(NovaEngineeringCore.MOD_ID, "textures/gui/singularity_core_rings_2_off.png");
    public static final ResourceLocation GUI_RING_2_ON = new ResourceLocation(NovaEngineeringCore.MOD_ID, "textures/gui/singularity_core_rings_2_on.png");
    public static final ResourceLocation GUI_RING_3_OFF = new ResourceLocation(NovaEngineeringCore.MOD_ID, "textures/gui/singularity_core_rings_3_off.png");
    public static final ResourceLocation GUI_RING_3_ON = new ResourceLocation(NovaEngineeringCore.MOD_ID, "textures/gui/singularity_core_rings_3_on.png");

    private static Framebuffer bloomFBO = null;

    public GuiSingularityCore(final SingularityCore controller, final EntityPlayer opening) {
        super(new ContainerSingularityCore(controller, opening));
        this.xSize = 429;
        this.ySize = 268;
        this.widgetController = new WidgetController(WidgetGui.of(this));
        this.widgetController.addWidget(
                new Column()
                        .addWidgets(
                                new StatusPanel(this).setMarginDown(2),
                                new StartStopPanel(this).setMarginDown(2),
                                new MultiplierControlPanel(this)
                        )
                        .setAbsXY(18, 28)
        );
        this.widgetController.addWidget(
                new Rings(this).setAbsXY(127, 28)
        );
    }

    @Override
    public void initGui() {
        super.initGui();
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(final float partialTicks, final int mouseX, final int mouseY) {
        GlStateManager.color(1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(GUI_BACKGROUND);
        final int x = (this.width - this.xSize) / 2;
        final int y = (this.height - this.ySize) / 2;
        Gui.drawModalRectWithCustomSizedTexture(x, y, 0, 0, this.xSize, this.ySize, 512, 512);
        if (!ShaderManager.isOptifineShaderPackLoaded() && OpenGlHelper.framebufferSupported) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(0, 0, 1f);
            preRenderBloom();
            renderBloom();
            postRenderBloom();
            GlStateManager.popMatrix();
        }
        super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(final int mouseX, final int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
    }

    public void preRenderBloom() {
        Framebuffer fbo = mc.getFramebuffer();
        
        if (bloomFBO == null ||
            bloomFBO.framebufferWidth != fbo.framebufferWidth ||
            bloomFBO.framebufferHeight != fbo.framebufferHeight ||
            (fbo.isStencilEnabled() && !bloomFBO.isStencilEnabled()))
        {
            if (bloomFBO == null) {
                bloomFBO = new Framebuffer(fbo.framebufferWidth, fbo.framebufferHeight, false);
                bloomFBO.setFramebufferColor(0, 0, 0, 0);
            } else {
                bloomFBO.createBindFramebuffer(fbo.framebufferWidth, fbo.framebufferHeight);
            }

            if (fbo.isStencilEnabled() && !bloomFBO.isStencilEnabled()) {
                bloomFBO.enableStencil();
            }

            if (DepthTextureUtil.isLastBind() && DepthTextureUtil.isUseDefaultFBO()) {
                RenderUtil.hookDepthTexture(bloomFBO, DepthTextureUtil.framebufferDepthTexture);
            } else {
                RenderUtil.hookDepthBuffer(bloomFBO, fbo.depthBuffer);
            }

            bloomFBO.setFramebufferFilter(GL11.GL_LINEAR);
        }

        GlStateManager.depthMask(true);

        bloomFBO.framebufferClear();
        bloomFBO.bindFramebuffer(true);
    }

    public void postRenderBloom() {
        Framebuffer fbo = mc.getFramebuffer();
        GlStateManager.depthMask(false);
        // blend bloom + transparent
        fbo.bindFramebufferTexture();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_DST_ALPHA, GL11.GL_ZERO);
        Shaders.renderFullImageInFBO(bloomFBO, Shaders.IMAGE_F, null);
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        // render unreal
        BloomEffect.renderUnreal(bloomFBO, fbo);

        // render bloom blend result to fbo
        GlStateManager.disableBlend();
        Shaders.renderFullImageInFBO(fbo, Shaders.IMAGE_F, null);
    }

    public void renderBloom() {
        GlStateManager.color(1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(GUI_BACKGROUND_BLOOM);
        final int x = (this.width - this.xSize) / 2;
        final int y = (this.height - this.ySize) / 2;
        Gui.drawModalRectWithCustomSizedTexture(x, y, 0, 0, this.xSize, this.ySize, 512, 512);
    }

}
