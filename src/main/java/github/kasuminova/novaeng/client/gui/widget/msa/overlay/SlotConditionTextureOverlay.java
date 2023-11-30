package github.kasuminova.novaeng.client.gui.widget.msa.overlay;

import github.kasuminova.mmce.client.gui.util.MousePos;
import github.kasuminova.mmce.client.gui.util.RenderPos;
import github.kasuminova.mmce.client.gui.util.RenderSize;
import github.kasuminova.mmce.client.gui.widget.TextureOverlay;
import github.kasuminova.novaeng.client.gui.widget.msa.slot.SlotAssembly;
import hellfirepvp.modularmachinery.client.ClientScheduler;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;

public class SlotConditionTextureOverlay extends TextureOverlay {
    protected final SlotAssembly<?> slotAssembly;

    public SlotConditionTextureOverlay(final SlotAssembly<?> slotAssembly) {
        this.slotAssembly = slotAssembly;
    }

    @Override
    public void postRender(final GuiContainer gui, final RenderSize renderSize, final RenderPos renderPos, final MousePos mousePos) {
        float partialTickTime = ClientScheduler.getClientTick();

        if (isVisible() && texLocation != null) {
            float alpha;

            if (slotAssembly.isInstalled()) {
                alpha = 1F;
            } else if (slotAssembly.isHovered()) {
                float period = 40F;
                alpha = (float) Math.sin((partialTickTime % period) / period * (2.0 * Math.PI)) * 0.5F + 0.5F;
            } else {
                return;
            }

            GlStateManager.color(1F, 1F, 1F, alpha);

            gui.mc.getTextureManager().bindTexture(texLocation);
            gui.drawTexturedModalRect(renderPos.posX(), renderPos.posY(), textureX, textureY, width, height);

            GlStateManager.color(1F, 1F, 1F, 1F);
        }
    }

    @Override
    public boolean isVisible() {
        return slotAssembly.isAvailable();
    }
}
