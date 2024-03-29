package github.kasuminova.novaeng.client.gui.widget.estorage;

import github.kasuminova.mmce.client.gui.util.AnimationValue;
import github.kasuminova.mmce.client.gui.util.MousePos;
import github.kasuminova.mmce.client.gui.util.RenderPos;
import github.kasuminova.mmce.client.gui.util.RenderSize;
import github.kasuminova.mmce.client.gui.widget.base.DynamicWidget;
import github.kasuminova.mmce.client.gui.widget.base.WidgetGui;
import github.kasuminova.novaeng.NovaEngineeringCore;
import github.kasuminova.novaeng.client.gui.GuiEStorageController;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

import java.awt.*;

public class EStorageGraphBar extends DynamicWidget {
    public static final ResourceLocation TEX_RES = new ResourceLocation(NovaEngineeringCore.MOD_ID, "textures/gui/estorage_controller_elements.png");

    public static final int BAR_TEX_X = 223;
    public static final int BAR_TEX_Y = 163;

    public static final int BAR_WIDTH = 32;
    public static final int BAR_HEIGHT = 92;

    public static final int TOP_TEX_X = 1;
    public static final int TOP_TEX_Y = 246;
    
    public static final int MID_TEX_X = 34;
    public static final int MID_TEX_Y = 250;
    public static final int MID_TEX_HEIGHT = 4;

    public static final int BOTTOM_TEX_X = 1;
    public static final int BOTTOM_TEX_Y = 246;

    public static final int BOTTOM_AND_TOP_HEIGHT = 8;
    
    protected final GuiEStorageController controllerGUI;

    protected AnimationValue percentage = AnimationValue.ofFinished(0, 500, .25, .1, .25, 1);
    protected AnimationValue ref = null;
    protected boolean reverseColor = false;

    public EStorageGraphBar(final GuiEStorageController controllerGUI) {
        this.controllerGUI = controllerGUI;
        setWidthHeight(BAR_WIDTH, BAR_HEIGHT);
    }

    public void setPercentage(final AnimationValue ref, final boolean reverseColor) {
        if (this.ref != ref) {
            this.percentage.set(ref.getTargetValue());
            this.ref = ref;
            this.reverseColor = reverseColor;
        }
    }

    @Override
    public void update(final WidgetGui gui) {
        super.update(gui);
        if (ref != null && ref.getTargetValue() != percentage.getTargetValue()) {
            percentage.set(ref.getTargetValue());
        }
    }

    @Override
    public void preRender(final WidgetGui widgetGui, final RenderSize renderSize, final RenderPos renderPos, final MousePos mousePos) {
        if (percentage.get() <= 0) {
            return;
        }

        GuiScreen gui = widgetGui.getGui();
        gui.mc.getTextureManager().bindTexture(TEX_RES);

        // 计算柱形图的高度
        int barHeight = (int) Math.round(((BAR_HEIGHT - (BOTTOM_AND_TOP_HEIGHT)) * percentage.get()));

        // 计算渐变色
        Color startColor = new Color(0, 255, 0);
        Color midColor = new Color(255, 255, 0);
        Color endColor = new Color(255, 0, 0);
        Color barColor = getGradientColor(reverseColor ? new Color[]{endColor, midColor, startColor, startColor} : new Color[]{startColor, startColor, midColor, endColor},
                (int) (255 * .75F),
                (float) percentage.get()
        );

        GlStateManager.color(
                (float) barColor.getRed() / 255,
                (float) barColor.getGreen() / 255,
                (float) barColor.getBlue() / 255,
                .75F
        );

        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);

        // 渲染顶部
        gui.drawTexturedModalRect(
                renderPos.posX(), renderPos.posY() + ((BAR_HEIGHT - (barHeight + BOTTOM_AND_TOP_HEIGHT))),
                TOP_TEX_X, TOP_TEX_Y,
                BAR_WIDTH, BOTTOM_AND_TOP_HEIGHT
        );

        // 渲染中间部分
        int yOffset = renderPos.posY() + (BAR_HEIGHT - (barHeight + BOTTOM_AND_TOP_HEIGHT / 2)) + 1;
        int finalYOffset = renderPos.posY() + BAR_HEIGHT - BOTTOM_AND_TOP_HEIGHT + (BOTTOM_AND_TOP_HEIGHT / 2) + 1;
        for (int i = yOffset; i < finalYOffset; i++) {
            gui.drawTexturedModalRect(
                    renderPos.posX(), i,
                    MID_TEX_X, MID_TEX_Y,
                    BAR_WIDTH, MID_TEX_HEIGHT
            );
        }

        // 渲染底部
        gui.drawTexturedModalRect(
                renderPos.posX(), renderPos.posY() + BAR_HEIGHT - BOTTOM_AND_TOP_HEIGHT,
                BOTTOM_TEX_X, BOTTOM_TEX_Y,
                BAR_WIDTH, BOTTOM_AND_TOP_HEIGHT
        );

        GlStateManager.color(1F, 1F, 1F, 1F);
    }

    @Override
    public void render(final WidgetGui widgetGui, final RenderSize renderSize, final RenderPos renderPos, final MousePos mousePos) {
    }

    public static Color getGradientColor(final Color[] colors,
                                         final int alpha,
                                         final float percentage) {
        float percent = Math.max(0, Math.min(1, percentage));

        // 确保颜色数组和百分比数组长度相同
        if (colors.length < 2) {
            throw new IllegalArgumentException("Colors array must contain at least two colors.");
        }

        // 计算渐变段数
        int numSegments = colors.length - 1;

        // 计算当前百分比位于哪个渐变段
        float segmentPercent = percent * numSegments;
        int segmentIndex = (int) segmentPercent;
        if (segmentIndex >= numSegments) {
            // 如果超出最大渐变段数，则取最后一个渐变段
            return colors[numSegments];
        } else if (segmentIndex < 0) {
            // 如果百分比小于 0，则取第一个渐变段 
            return colors[0];
        }

        // 获取当前渐变段的起始颜色和结束颜色
        Color startColor = colors[segmentIndex];
        Color endColor = colors[segmentIndex + 1];

        // 计算当前渐变段内的百分比
        float segmentPercentage = segmentPercent - segmentIndex;

        // 计算当前渐变段内的颜色
        int interpolatedRed = (int) (startColor.getRed() + (endColor.getRed() - startColor.getRed()) * segmentPercentage);
        int interpolatedGreen = (int) (startColor.getGreen() + (endColor.getGreen() - startColor.getGreen()) * segmentPercentage);
        int interpolatedBlue = (int) (startColor.getBlue() + (endColor.getBlue() - startColor.getBlue()) * segmentPercentage);

        // 返回计算得到的颜色，带有指定的透明度
        return new Color(interpolatedRed, interpolatedGreen, interpolatedBlue, alpha);
    }

}
