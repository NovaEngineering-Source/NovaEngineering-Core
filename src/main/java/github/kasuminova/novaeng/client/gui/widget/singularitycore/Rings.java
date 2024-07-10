package github.kasuminova.novaeng.client.gui.widget.singularitycore;

import github.kasuminova.mmce.client.gui.util.MousePos;
import github.kasuminova.mmce.client.gui.util.RenderPos;
import github.kasuminova.mmce.client.gui.util.RenderSize;
import github.kasuminova.mmce.client.gui.widget.base.DynamicWidget;
import github.kasuminova.mmce.client.gui.widget.base.WidgetGui;
import github.kasuminova.novaeng.client.gui.GuiSingularityCore;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class Rings extends DynamicWidget {

    public static final int WIDTH = 144;
    public static final int HEIGHT = 144;

    public static final int RING_1_SIZE = 85;
    public static final int RING_2_SIZE = 115;
    public static final int RING_3_SIZE = 144;

    public static final int PLATE_SIZE = 70;
    public static final int PLATE_TEX_X = 0;
    public static final int PLATE_TEX_Y = 134;

    public static final int HOLA_SIZE = 40;
    public static final int HOLA_TEX_X = 71;
    public static final int HOLA_TEX_Y = 164;

    private static final ResourceLocation BUTTON = GuiSingularityCore.GUI_BUTTON;
    private static final ResourceLocation RING_1_OFF = GuiSingularityCore.GUI_RING_1_OFF;
    private static final ResourceLocation RING_1_ON = GuiSingularityCore.GUI_RING_1_ON;
    private static final ResourceLocation RING_2_OFF = GuiSingularityCore.GUI_RING_2_OFF;
    private static final ResourceLocation RING_2_ON = GuiSingularityCore.GUI_RING_2_ON;
    private static final ResourceLocation RING_3_OFF = GuiSingularityCore.GUI_RING_3_OFF;
    private static final ResourceLocation RING_3_ON = GuiSingularityCore.GUI_RING_3_ON;

    protected final GuiSingularityCore coreGUI;
    protected final long startTime = System.currentTimeMillis();

    public Rings(final GuiSingularityCore coreGUI) {
        this.coreGUI = coreGUI;
    }

    @Override
    public void render(final WidgetGui widgetGui, final RenderSize renderSize, final RenderPos renderPos, final MousePos mousePos) {
        GuiScreen gui = widgetGui.getGui();

        long currentTime = System.currentTimeMillis();
        long elapsedTime = currentTime - startTime;

        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);

        // 3 环
        // 计算旋转角度
        float angle = (elapsedTime % 20000) / 20000.0f * 360.0f;
        GlStateManager.pushMatrix();
        GlStateManager.translate(renderPos.posX(), renderPos.posY(), 0.0f);
        // 移动到图片中心
        GlStateManager.translate((float) WIDTH / 2, (float) HEIGHT / 2, 0.0f);
        // 旋转
        GlStateManager.rotate(-angle, 0.0f, 0.0f, 1.0f);
        // 渲染
        gui.mc.getTextureManager().bindTexture(RING_3_ON);
        gui.drawTexturedModalRect(-(RING_3_SIZE / 2), -(RING_3_SIZE / 2), 0, 0, RING_3_SIZE, RING_3_SIZE);
        GlStateManager.popMatrix();

        // 2 环
        GlStateManager.pushMatrix();
        GlStateManager.translate(renderPos.posX(), renderPos.posY(), 0.0f);
        // 计算旋转角度
        angle = (elapsedTime % 10000) / 10000.0f * 360.0f;
        // 移动到图片中心
        GlStateManager.translate((float) WIDTH / 2, (float) HEIGHT / 2, 0.0f);
        // 旋转
        GlStateManager.rotate(-angle, 0.0f, 0.0f, 1.0f);
        // 渲染
        gui.mc.getTextureManager().bindTexture(RING_2_ON);
        gui.drawTexturedModalRect(-(RING_2_SIZE / 2), -(RING_2_SIZE / 2), 0, 0, RING_2_SIZE, RING_2_SIZE);
        GlStateManager.popMatrix();

        // 1 环
        GlStateManager.pushMatrix();
        GlStateManager.translate(renderPos.posX(), renderPos.posY(), 0.0f);
        // 计算旋转角度
        angle = (elapsedTime % 5000) / 5000.0f * 360.0f;
        // 移动到图片中心
        GlStateManager.translate((float) WIDTH / 2, (float) HEIGHT / 2, 0.0f);
        // 旋转
        GlStateManager.rotate(angle, 0.0f, 0.0f, 1.0f);
        // 渲染
        gui.mc.getTextureManager().bindTexture(RING_1_ON);
        gui.drawTexturedModalRect(-(RING_1_SIZE / 2), -(RING_1_SIZE / 2), 0, 0, RING_1_SIZE, RING_1_SIZE);
        GlStateManager.popMatrix();

        // 吸积盘
        GlStateManager.pushMatrix();
        GlStateManager.translate(renderPos.posX(), renderPos.posY(), 0.0f);
        // 计算旋转角度
        angle = (elapsedTime % 2500) / 2500.0f * 360.0f;
        // 移动到图片中心
        GlStateManager.translate((float) WIDTH / 2, (float) HEIGHT / 2, 0.0f);
        // 旋转
        GlStateManager.rotate(angle, 0.0f, 0.0f, 1.0f);
        // 渲染
        gui.mc.getTextureManager().bindTexture(BUTTON);
        gui.drawTexturedModalRect(-(PLATE_SIZE / 2), -(PLATE_SIZE / 2), PLATE_TEX_X, PLATE_TEX_Y, PLATE_SIZE, PLATE_SIZE);

        // 黑洞
        gui.drawTexturedModalRect(-(HOLA_SIZE / 2), -(HOLA_SIZE / 2), HOLA_TEX_X, HOLA_TEX_Y, HOLA_SIZE, HOLA_SIZE);
        GlStateManager.popMatrix();
    }

}
