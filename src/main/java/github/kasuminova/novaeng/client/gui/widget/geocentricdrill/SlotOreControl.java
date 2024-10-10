package github.kasuminova.novaeng.client.gui.widget.geocentricdrill;

import github.kasuminova.mmce.client.gui.util.MousePos;
import github.kasuminova.mmce.client.gui.util.RenderPos;
import github.kasuminova.mmce.client.gui.util.RenderSize;
import github.kasuminova.mmce.client.gui.widget.base.WidgetGui;
import github.kasuminova.mmce.client.gui.widget.slot.SlotItemVirtualJEI;
import github.kasuminova.novaeng.NovaEngineeringCore;
import github.kasuminova.novaeng.common.crafttweaker.util.NovaEngUtils;
import github.kasuminova.novaeng.common.machine.GeocentricDrill;
import github.kasuminova.novaeng.common.network.PktGeocentricDrillControl;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;

import java.awt.*;
import java.util.List;

public class SlotOreControl extends SlotItemVirtualJEI {

    private final String oreName;
    private float chance;
    private boolean accelerated;

    public SlotOreControl(final String oreName, final ItemStack stackInSlot, float chance, boolean accelerated) {
        super(stackInSlot);
        this.oreName = oreName;
        this.chance = chance;
        this.accelerated = accelerated;
    }

    @Override
    public List<String> getHoverTooltips(final WidgetGui widgetGui, final MousePos mousePos) {
        List<String> tooltips = super.getHoverTooltips(widgetGui, mousePos);
        if (!tooltips.isEmpty()) {
            float chance = (accelerated ? this.chance * GeocentricDrill.ACCELERATE_MULTIPLIER : this.chance) * 100;
            tooltips.add(I18n.format("gui.geocentric_drill.ore_control.tooltip.chance", 
                    NovaEngUtils.formatFloat(chance, 1))
            );
            tooltips.add(accelerated 
                    ? I18n.format("gui.geocentric_drill.ore_control.tooltip.unmark") 
                    : I18n.format("gui.geocentric_drill.ore_control.tooltip.mark")
            );
        }
        return tooltips;
    }

    @Override
    public void render(final WidgetGui widgetGui, final RenderSize renderSize, final RenderPos renderPos, final MousePos mousePos) {
        super.render(widgetGui, renderSize, renderPos, mousePos);
        if (stackInSlot.isEmpty()) {
            return;
        }

        Minecraft mc = widgetGui.getGui().mc;
        final int rx = renderPos.posX() + 1;
        final int ry = renderPos.posY() + 1;
        renderChance(mc.fontRenderer, rx, ry, chance);
    }

    @Override
    protected void drawHoverOverlay(final MousePos mousePos, final int rx, final int ry) {
        if (accelerated) {
            GlStateManager.disableLighting();
//            GlStateManager.disableDepth();
            GlStateManager.colorMask(true, true, true, false);
            GuiScreen.drawRect(rx, ry, rx + 16, ry + 16, new Color(255, 255, 50, 127).getRGB());
            GlStateManager.colorMask(true, true, true, true);
            GlStateManager.enableLighting();
//            GlStateManager.enableDepth();
        } else {
            super.drawHoverOverlay(mousePos, rx, ry);
        }
    }

    public void renderChance(final FontRenderer fr, final int xPos, final int yPos, final float chance) {
        final float scale = .75F;
        float actualChance = (accelerated ? chance * GeocentricDrill.ACCELERATE_MULTIPLIER : chance) * 100;
        String content = NovaEngUtils.formatFloat(actualChance, 1) + "%";

        GlStateManager.disableLighting();
        GlStateManager.disableBlend();
        GlStateManager.pushMatrix();
        GlStateManager.scale(scale, scale, 1.0F);
        GlStateManager.translate(0.0F, 0.0F, 160F);

        boolean unicodeFlag = fr.getUnicodeFlag();
        fr.setUnicodeFlag(false);
        fr.drawStringWithShadow(content, (xPos + 16 - (fr.getStringWidth(content) * scale)) / scale, yPos / scale, accelerated ? 0x7FFF00 : 0xFFFFFF);
        fr.setUnicodeFlag(unicodeFlag);

        GlStateManager.popMatrix();
        GlStateManager.enableBlend();
    }

    public static SlotOreControl of(final String oreName, final ItemStack stackInSlot, final float chance, final boolean accelerated) {
        return new SlotOreControl(oreName, stackInSlot, chance, accelerated);
    }

    @Override
    public boolean onMouseClick(final MousePos mousePos, final RenderPos renderPos, final int mouseButton) {
        if (accelerated) {
            NovaEngineeringCore.NET_CHANNEL.sendToServer(new PktGeocentricDrillControl(PktGeocentricDrillControl.Type.REMOVE_ACCELERATE_ORE, oreName));
        } else {
            NovaEngineeringCore.NET_CHANNEL.sendToServer(new PktGeocentricDrillControl(PktGeocentricDrillControl.Type.ADD_ACCELERATE_ORE, oreName));
        }
        return true;
    }

}
