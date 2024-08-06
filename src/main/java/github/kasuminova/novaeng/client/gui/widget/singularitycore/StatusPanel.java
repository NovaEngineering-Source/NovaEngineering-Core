package github.kasuminova.novaeng.client.gui.widget.singularitycore;

import github.kasuminova.mmce.client.gui.util.MousePos;
import github.kasuminova.mmce.client.gui.util.RenderPos;
import github.kasuminova.mmce.client.gui.util.RenderSize;
import github.kasuminova.mmce.client.gui.util.TextureProperties;
import github.kasuminova.mmce.client.gui.widget.base.WidgetGui;
import github.kasuminova.mmce.client.gui.widget.container.Row;
import github.kasuminova.novaeng.client.gui.GuiSingularityCore;
import github.kasuminova.novaeng.client.gui.widget.ProgressBar;
import github.kasuminova.novaeng.common.crafttweaker.util.NovaEngUtils;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;

import java.util.Collections;

public class StatusPanel extends Row {

    public static final int WIDTH = 93;
    public static final int HEIGHT = 129;
    public static final int TEX_X = 163;
    public static final int TEX_Y = 0;
    
    public static final int BAR_WIDTH = 14;
    public static final int BAR_HEIGHT = 108;

    public static final int LEFT_BAR_TEX_X = 46;
    public static final int MID_BAR_TEX_X = 61;
    public static final int RIGHT_BAR_TEX_X = 76;

    public static final int BAR_TEX_Y = 169;

    private static final ResourceLocation BG_TEX_RES = GuiSingularityCore.GUI_SUBASSEMBLY;
    private static final ResourceLocation BAR_TEX_RES = GuiSingularityCore.GUI_SUBASSEMBLY_2;

    protected final GuiSingularityCore coreGUI;

    public StatusPanel(final GuiSingularityCore coreGUI) {
        this.coreGUI = coreGUI;
        this.width = WIDTH;
        this.height = HEIGHT;
        this.addWidgets(
                new ProgressBar()
                        .setVertical(true)
                        .setDownToUp(true)
                        .addForegroundTexture(new TextureProperties(BAR_TEX_RES, LEFT_BAR_TEX_X, BAR_TEX_Y, BAR_WIDTH, BAR_HEIGHT))
                        .setProgress(0.75f) // For testing
                        .setTooltipFunction(progressBar ->
                                Collections.singletonList(String.format("§c内部能源存储：%s%%",
                                        NovaEngUtils.formatDouble(progressBar.getProgressPercent() * 100, 2))
                                )
                        )
                        .setWidthHeight(BAR_WIDTH, BAR_HEIGHT)
                        .setMargin(8, 3, 3, 0),
                new ProgressBar()
                        .setVertical(true)
                        .setDownToUp(true)
                        .addForegroundTexture(new TextureProperties(BAR_TEX_RES, MID_BAR_TEX_X, BAR_TEX_Y, BAR_WIDTH, BAR_HEIGHT))
                        .setProgress(0.4f) // For testing
                        .setTooltipFunction(progressBar ->
                                Collections.singletonList(String.format("§c奇点质量：%s%%",
                                        NovaEngUtils.formatDouble(progressBar.getProgressPercent() * 100, 2))
                                )
                        )
                        .setWidthHeight(BAR_WIDTH, BAR_HEIGHT)
                        .setMargin(0, 3, 3, 0),
                new ProgressBar()
                        .setVertical(true)
                        .setDownToUp(true)
                        .addForegroundTexture(new TextureProperties(BAR_TEX_RES, MID_BAR_TEX_X, BAR_TEX_Y, BAR_WIDTH, BAR_HEIGHT))
                        .setProgress(0.35f) // For testing
                        .setTooltipFunction(progressBar ->
                                Collections.singletonList(String.format("§c奇点引力场强度：%s%%",
                                        NovaEngUtils.formatDouble(progressBar.getProgressPercent() * 100, 2))
                                )
                        )
                        .setWidthHeight(BAR_WIDTH, BAR_HEIGHT)
                        .setMargin(0, 3, 3, 0),
                new ProgressBar()
                        .setVertical(true)
                        .setDownToUp(true)
                        .addForegroundTexture(new TextureProperties(BAR_TEX_RES, MID_BAR_TEX_X, BAR_TEX_Y, BAR_WIDTH, BAR_HEIGHT))
                        .setProgress(0.35f) // For testing
                        .setTooltipFunction(progressBar ->
                                Collections.singletonList(String.format("§c控制力场强度：%s%%",
                                        NovaEngUtils.formatDouble(progressBar.getProgressPercent() * 100, 2))
                                )
                        )
                        .setWidthHeight(BAR_WIDTH, BAR_HEIGHT)
                        .setMargin(0, 3, 3, 0),
                new ProgressBar()
                        .setVertical(true)
                        .setDownToUp(true)
                        .addForegroundTexture(new TextureProperties(BAR_TEX_RES, RIGHT_BAR_TEX_X, BAR_TEX_Y, BAR_WIDTH, BAR_HEIGHT))
                        .setProgress(0.46f) // For testing
                        .setTooltipFunction(progressBar ->
                                Collections.singletonList(String.format("§c核心负载：%s%%",
                                        NovaEngUtils.formatDouble(progressBar.getProgressPercent() * 100, 2))
                                )
                        )
                        .setWidthHeight(BAR_WIDTH, BAR_HEIGHT)
                        .setMargin(0, 0, 3, 0)
        );
    }

    @Override
    protected void preRenderInternal(final WidgetGui widgetGui, final RenderSize renderSize, final RenderPos renderPos, final MousePos mousePos) {
        GuiScreen gui = widgetGui.getGui();
        gui.mc.getTextureManager().bindTexture(BG_TEX_RES);
        gui.drawTexturedModalRect(
                renderPos.posX(), renderPos.posY(),
                TEX_X, TEX_Y,
                WIDTH, HEIGHT
        );
        super.preRenderInternal(widgetGui, renderSize, renderPos, mousePos);
    }

    @Override
    public int getWidth() {
        return this.width;
    }

    @Override
    public int getHeight() {
        return this.height;
    }

}
