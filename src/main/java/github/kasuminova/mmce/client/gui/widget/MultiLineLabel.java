package github.kasuminova.mmce.client.gui.widget;

import github.kasuminova.mmce.client.gui.util.MousePos;
import github.kasuminova.mmce.client.gui.util.RenderPos;
import github.kasuminova.mmce.client.gui.util.RenderSize;
import github.kasuminova.mmce.client.gui.widget.base.DynamicWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class MultiLineLabel extends DynamicWidget {
    public static final int DEFAULT_FONT_HEIGHT = 12;

    protected final List<String> contents = new ArrayList<>();
    protected boolean leftAligned = false;
    protected boolean rightAligned = false;
    protected float scale = 1.0F;

    public MultiLineLabel(List<String> contents) {
        this.contents.addAll(contents);
        this.width = getMaxStringWidth();
        this.height = getTotalHeight();
        setMargin(2);
    }

    @Override
    public void render(final GuiContainer gui, final RenderSize renderSize, final RenderPos renderPos, final MousePos mousePos) {
        final float scale = this.scale;
        if (scale != 1F) {
            GlStateManager.pushMatrix();
            GlStateManager.scale(scale, scale, scale);
        }

        int maxWidth = renderSize.isWidthLimited() ? Math.round((float) renderSize.width() / scale) : -1;
        int maxHeight = renderSize.isHeightLimited() ? Math.round((float) renderSize.height() / scale) : Math.round(height / scale);
        int posX = Math.round((float) renderPos.posX() / scale);
        int posY = Math.round((float) renderPos.posY() / scale);

        FontRenderer fr = gui.mc.fontRenderer;

        List<String> toRender;
        if (maxWidth == -1) {
            toRender = contents;
        } else {
            toRender = new LinkedList<>();
            contents.stream().map(s -> fr.listFormattedStringToWidth(s, maxWidth)).forEach(toRender::addAll);
        }

        int offsetY = 0;
        int fontHeight = Math.round((float) DEFAULT_FONT_HEIGHT * scale);
        for (final String s : toRender) {
            fr.drawStringWithShadow(s, posX, posY + offsetY, 0xFFFFFF);
            offsetY += fontHeight;
            if (offsetY > maxHeight + fontHeight) {
                break;
            }
        }

        if (scale != 1F) {
            GlStateManager.popMatrix();
        }

        GlStateManager.color(1.0F, 1.0F, 1.0F);
    }

    // Utils

    public int getMaxStringWidth() {
        FontRenderer fr = Minecraft.getMinecraft().fontRenderer;

        int maxWidth = 0;
        for (final String content : contents) {
            int width = Math.round((float) fr.getStringWidth(content) * scale);
            if (width > maxWidth) {
                maxWidth = width;
            }
        }

        return maxWidth;
    }

    public int getTotalHeight() {
        return getTotalHeight(contents);
    }

    public int getTotalHeight(List<String> contents) {
        FontRenderer fr = Minecraft.getMinecraft().fontRenderer;

        int height = 0;
        for (final String content : contents) {
            List<String> listed = fr.listFormattedStringToWidth(content, Math.round((float) width / scale));
            height += Math.round((float) DEFAULT_FONT_HEIGHT * scale * listed.size());
        }

        return Math.round(height * scale);
    }

    // Getters / Setters

    public float getScale() {
        return scale;
    }

    public MultiLineLabel setScale(final float scale) {
        this.scale = scale;
        this.height = getTotalHeight();
        return this;
    }

    @Override
    public MultiLineLabel setWidth(final int width) {
        super.setWidth(width);
        this.height = getTotalHeight();
        return this;
    }

    public List<String> getContents() {
        return Collections.unmodifiableList(contents);
    }

    // Align

    public boolean isLeftAligned() {
        return leftAligned;
    }

    public MultiLineLabel setLeftAligned(final boolean leftAligned) {
        this.rightAligned = !leftAligned;
        this.leftAligned = leftAligned;
        return this;
    }

    public boolean isRightAligned() {
        return rightAligned;
    }

    public MultiLineLabel setRightAligned(final boolean rightAligned) {
        this.leftAligned = !rightAligned;
        this.rightAligned = rightAligned;
        return this;
    }

    public boolean isCenterAligned() {
        return this.leftAligned && this.rightAligned;
    }

    public MultiLineLabel setCenterAligned(final boolean centerAligned) {
        if (centerAligned) {
            this.leftAligned = true;
            this.rightAligned = true;
            return this;
        }
        // Default setting is left aligned.
        this.leftAligned = true;
        this.rightAligned = false;
        return this;
    }

}
