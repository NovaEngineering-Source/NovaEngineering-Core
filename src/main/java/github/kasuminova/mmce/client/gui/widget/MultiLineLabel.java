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
import java.util.stream.Collectors;

public class MultiLineLabel extends DynamicWidget {
    public static final int DEFAULT_FONT_HEIGHT = 12;

    protected boolean leftAligned = false;
    protected boolean rightAligned = false;

    protected float scale = 1.0F;

    protected final List<String> contents = new ArrayList<>();

    public MultiLineLabel(List<String> contents) {
        this.contents.addAll(contents);
        this.width = getMaxStringWidth();
        this.height = getTotalHeight();
        setMargin(2);
    }

    @Override
    public void render(final GuiContainer gui, final RenderSize renderSize, final RenderPos renderPos, final MousePos mousePos) {
        GlStateManager.scale(scale, scale, scale);

        int maxWidth;
        if (renderSize.isWidthLimited()) {
            maxWidth = Math.round((float) renderSize.width() / scale);
        } else {
            maxWidth = -1;
        }

        int offsetX = Math.round((float) renderPos.posX() / scale);
        int offsetY = Math.round((float) renderPos.posY() / scale);

        FontRenderer fr = gui.mc.fontRenderer;

        List<String> toRender = maxWidth == -1 ? contents : contents.stream()
                .flatMap(s -> fr.listFormattedStringToWidth(s, maxWidth).stream())
                .collect(Collectors.toCollection(LinkedList::new));

        for (final String s : toRender) {
            fr.drawStringWithShadow(s, offsetX, offsetY, 0xFFFFFF);
            offsetX += Math.round((float) DEFAULT_FONT_HEIGHT * scale);
        }

        GlStateManager.scale(1.0F, 1.0F, 1.0F);
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
        return Math.round((float) (contents.size() * DEFAULT_FONT_HEIGHT) * scale);
    }

    // Getters / Setters

    public float getScale() {
        return scale;
    }

    public void setScale(final float scale) {
        this.scale = scale;
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
