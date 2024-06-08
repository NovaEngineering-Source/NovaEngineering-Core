package github.kasuminova.novaeng.client.gui.widget.singularitycore;

import github.kasuminova.mmce.client.gui.util.MousePos;
import github.kasuminova.mmce.client.gui.util.RenderPos;
import github.kasuminova.mmce.client.gui.util.RenderSize;
import github.kasuminova.mmce.client.gui.widget.Button4State;
import github.kasuminova.mmce.client.gui.widget.MultiLineLabel;
import github.kasuminova.mmce.client.gui.widget.base.WidgetGui;
import github.kasuminova.mmce.client.gui.widget.container.Column;
import github.kasuminova.mmce.client.gui.widget.container.Row;
import github.kasuminova.novaeng.client.gui.GuiSingularityCore;
import github.kasuminova.novaeng.client.gui.widget.InputBox;
import github.kasuminova.novaeng.client.gui.widget.ProgressBar;
import github.kasuminova.novaeng.common.crafttweaker.util.NovaEngUtils;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;

import java.util.Collections;

public class MultiplierControlPanel extends Column {

    public static final int WIDTH = 93;
    public static final int HEIGHT = 53;
    public static final int TEX_X = 0;
    public static final int TEX_Y = 37;

    private static final ResourceLocation BG_TEX_RES = GuiSingularityCore.GUI_SUBASSEMBLY;
    private static final ResourceLocation BUTTON_TEX_RES = GuiSingularityCore.GUI_BUTTON;

    protected final GuiSingularityCore coreGUI;

    public MultiplierControlPanel(final GuiSingularityCore coreGUI) {
        this.coreGUI = coreGUI;
        this.width = WIDTH;
        this.height = HEIGHT;
        this.addWidgets(
                new MultiLineLabel(Collections.singletonList("§c工作倍率设置"))
                        .setAutoWrap(false)
                        .setAutoRecalculateSize(false)
                        .setWidthHeight(70, 10)
                        .setMargin(20, 0, 4, 3),
                new ProgressBar()
                        // 0.25 ~ 1.75
                        .setMaxProgress(1.5f)
                        .setProgress(.75f)
                        .setTooltipFunction(progressBar ->
                                Collections.singletonList(String.format("§c工作倍率：%s%%",
                                        NovaEngUtils.formatDouble((0.25f + progressBar.getProgress()) * 100, 2))
                                )
                        )
                        .setWidthHeight(87, 15)
                        .setMarginLeft(3),
                new Row()
                        .addWidgets(
                                new InputBox()
                                        .setEnableBackground(false)
                                        .setInputType(InputBox.InputType.NUMBER)
                                        .setTooltipFunction(inputBox ->
                                                Collections.singletonList("§c在此处输入新的工作倍率（1.0 = 100%）。")
                                        )
                                        .setWidthHeight(45, 10)
                                        .setMargin(6, 5, 6, 0),
                                new Button4State()
                                        .setMouseDownTextureXY(65, 19)
                                        .setHoveredTextureXY(102, 0)
                                        .setTextureXY(65, 0)
                                        .setUnavailableTextureXY(102, 19)
                                        .setTextureLocation(BUTTON_TEX_RES)
                                        .setTooltipFunction(button -> Collections.singletonList("§c单击应用新数值。"))
                                        .setWidthHeight(36, 18)
                                        .setMargin(0, 0, 2, 0)
                        )
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
