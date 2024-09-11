package github.kasuminova.novaeng.client.gui.widget.vanilla;

import appeng.client.gui.widgets.ITooltip;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButtonImage;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class GuiButtonImageExt extends GuiButtonImage implements ITooltip {
    
    private String message = "";

    public GuiButtonImageExt(final int buttonId, final int xIn, final int yIn, final int widthIn, final int heightIn, final int textureOffestX, final int textureOffestY, final int p_i47392_8_, final ResourceLocation resource) {
        super(buttonId, xIn, yIn, widthIn, heightIn, textureOffestX, textureOffestY, p_i47392_8_, resource);
    }

    @Override
    public void drawButton(final Minecraft mc, final int mouseX, final int mouseY, final float partialTicks) {
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.color(1f, 1f, 1f, 1f);
        super.drawButton(mc, mouseX, mouseY, partialTicks);
    }

    public GuiButtonImageExt setMessage(final String message) {
        this.message = message;
        return this;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public int xPos() {
        return x;
    }

    @Override
    public int yPos() {
        return y;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public boolean isVisible() {
        return visible;
    }

}
