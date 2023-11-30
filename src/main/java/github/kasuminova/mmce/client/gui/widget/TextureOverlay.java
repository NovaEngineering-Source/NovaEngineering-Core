package github.kasuminova.mmce.client.gui.widget;

import github.kasuminova.mmce.client.gui.util.MousePos;
import github.kasuminova.mmce.client.gui.util.RenderPos;
import github.kasuminova.mmce.client.gui.util.RenderSize;
import github.kasuminova.mmce.client.gui.widget.base.DynamicWidget;
import hellfirepvp.modularmachinery.client.ClientScheduler;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class TextureOverlay extends DynamicWidget {

    protected ResourceLocation texLocation = null;

    protected int textureX = 0;
    protected int textureY = 0;

    @Override
    public void postRender(final GuiContainer gui, final RenderSize renderSize, final RenderPos renderPos, final MousePos mousePos) {
        float partialTickTime = ClientScheduler.getClientTick();

        if (isVisible() && texLocation != null) {
            float period = 40F;
            float alpha = (float) Math.sin((partialTickTime % period) / period * (2.0 * Math.PI)) * 0.5F + 0.5F;

            GlStateManager.color(1F, 1F, 1F, alpha);

            gui.mc.getTextureManager().bindTexture(texLocation);
            gui.drawTexturedModalRect(renderPos.posX(), renderPos.posY(), textureX, textureY, width, height);

            GlStateManager.color(1F, 1F, 1F, 1F);
        }
    }

}
