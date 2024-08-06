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
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;

import java.util.Collections;

public class StartStopPanel extends Column {

    public static final int WIDTH = 93;
    public static final int HEIGHT = 35;
    public static final int TEX_X = 0;
    public static final int TEX_Y = 1;

    private static final ResourceLocation BG_TEX_RES = GuiSingularityCore.GUI_SUBASSEMBLY;
    private static final ResourceLocation BUTTON_TEX_RES = GuiSingularityCore.GUI_BUTTON;

    protected final GuiSingularityCore coreGUI;

    public StartStopPanel(final GuiSingularityCore coreGUI) {
        this.coreGUI = coreGUI;
        this.width = WIDTH;
        this.height = HEIGHT;
        addWidgets(
                new MultiLineLabel(Collections.singletonList("§c状态：§a已启动§6（环数：3）"))
                        .setVerticalCentering(true)
                        .setAutoWrap(false)
                        .setAutoRecalculateSize(false)
                        .setWidthHeight(84, 10)
                        .setMargin(6, 0, 4, 3),
                new Row().addWidgets(
                        new Button4State()
                                .setMouseDownTexture(1, 32)
                                .setHoveredTexture(1, 16)
                                .setTexture(1, 0)
                                .setUnavailableTexture(1, 48)
                                .setTextureLocation(BUTTON_TEX_RES)
                                .setTooltipFunction(button -> Collections.singletonList("§c单击停止工作进程。"))
                                .setWidthHeight(31, 15)
                                .setMarginRight(3),
                        new Button4State()
                                .setMouseDownTexture(33, 32)
                                .setHoveredTexture(33, 16)
                                .setTexture(33, 0)
                                .setUnavailableTexture(33, 48)
                                .setTextureLocation(BUTTON_TEX_RES)
                                .setTooltipFunction(button -> Collections.singletonList("§a单击开始启动进程。"))
                                .setWidthHeight(31, 15)
                ).setMarginLeft(25)
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
