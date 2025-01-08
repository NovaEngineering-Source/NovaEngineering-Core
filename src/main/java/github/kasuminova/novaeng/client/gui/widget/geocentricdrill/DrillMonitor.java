package github.kasuminova.novaeng.client.gui.widget.geocentricdrill;

import github.kasuminova.mmce.client.gui.util.MousePos;
import github.kasuminova.mmce.client.gui.util.RenderPos;
import github.kasuminova.mmce.client.gui.util.RenderSize;
import github.kasuminova.mmce.client.gui.widget.base.WidgetGui;
import github.kasuminova.novaeng.client.gui.widget.SizedColumn;
import github.kasuminova.novaeng.common.crafttweaker.util.NovaEngUtils;
import github.kasuminova.novaeng.common.hypernet.old.NetNodeCache;
import github.kasuminova.novaeng.common.hypernet.old.NetNodeImpl;
import github.kasuminova.novaeng.common.machine.GeocentricDrill;
import github.kasuminova.novaeng.common.tile.machine.GeocentricDrillController;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;

import java.util.ArrayList;
import java.util.List;

public class DrillMonitor extends SizedColumn {
    
    public static final int WIDTH = 162;
    public static final int HEIGHT = 45;

    public static final int FONT_HEIGHT = 9;
    public static final float FONT_SCALE = .8F;

    public static final int TEXT_WIDTH = (int) ((WIDTH - 4) / FONT_SCALE);

    private final GeocentricDrillController controller;

    public DrillMonitor(final GeocentricDrillController controller) {
        super();
        setWidthHeight(WIDTH, HEIGHT);
        this.controller = controller;
    }

    @Override
    @SuppressWarnings("CollectionAddAllCanBeReplacedWithConstructor")
    protected void renderInternal(final WidgetGui gui, final RenderSize renderSize, final RenderPos renderPos, final MousePos mousePos) {
        int rx = renderPos.posX() + 2;
        int ry = renderPos.posY() + 2;
        FontRenderer fr = gui.getGui().mc.fontRenderer;

        GlStateManager.pushMatrix();
        GlStateManager.translate(rx, ry, 0);
        GlStateManager.scale(FONT_SCALE, FONT_SCALE, 1F);

        List<List<String>> contents = new ArrayList<>();
        String status = I18n.format("gui.controller.status") + I18n.format(controller.getControllerStatus().getUnlocMessage());
        contents.add(fr.listFormattedStringToWidth(status, TEXT_WIDTH));

        List<String> subContents = new ArrayList<>();
        String depth = I18n.format("gui.geocentric_drill.monitor.depth",
                NovaEngUtils.formatFloat(controller.getDepth(), 1),
                NovaEngUtils.formatDecimal(controller.getTargetDepth())
        );
        subContents.addAll(fr.listFormattedStringToWidth(depth, TEXT_WIDTH));
        String outputMultiplier = I18n.format("gui.geocentric_drill.monitor.output_multiplier", 
                (int) controller.getDepth() / GeocentricDrill.PARALLELISM_PER_DEPTH
        );
        subContents.addAll(fr.listFormattedStringToWidth(outputMultiplier, TEXT_WIDTH));
        contents.add(subContents);

        subContents = new ArrayList<>();
        NetNodeImpl node = NetNodeCache.getCache(controller, NetNodeImpl.class);
        if (node != null && node.isConnected()) {
            String connected = I18n.format("gui.hypernet.controller.connected");
            subContents.addAll(fr.listFormattedStringToWidth(connected, TEXT_WIDTH));
            String calculation = I18n.format("gui.hypernet.controller.computation_point_consumption") + NovaEngUtils.formatFLOPS(node.getComputationPointConsumption());
            subContents.addAll(fr.listFormattedStringToWidth(calculation, TEXT_WIDTH));
        } else {
            String disconnected = I18n.format("gui.hypernet.controller.disconnected");
            subContents.addAll(fr.listFormattedStringToWidth(disconnected, TEXT_WIDTH));
        }
        contents.add(subContents);

        ry = 0;
        for (final List<String> content : contents) {
            for (final String line : content) {
                fr.drawString(line, 0, ry, 0xFFFFFF);
                ry += FONT_HEIGHT;
            }
            ry += 3;
        }

        GlStateManager.popMatrix();
        super.renderInternal(gui, renderSize, renderPos, mousePos);
    }

    @Override
    public void update(final WidgetGui gui) {
        super.update(gui);
        NetNodeImpl node = NetNodeCache.getCache(controller, NetNodeImpl.class);
        if (node != null) {
            node.readNBT();
        }
    }

}
