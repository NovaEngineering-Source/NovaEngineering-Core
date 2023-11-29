package github.kasuminova.novaeng.client.gui.widget.msa.overlay;

import github.kasuminova.mmce.client.gui.util.MousePos;
import github.kasuminova.mmce.client.gui.util.RenderPos;
import github.kasuminova.mmce.client.gui.util.RenderSize;
import github.kasuminova.mmce.client.gui.widget.base.DynamicWidget;
import github.kasuminova.novaeng.NovaEngineeringCore;
import github.kasuminova.novaeng.client.gui.widget.msa.slot.SlotCPU;
import hellfirepvp.modularmachinery.client.ClientScheduler;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class OverlayCPU extends DynamicWidget {
    public static final ResourceLocation TEX_LOCATION = new ResourceLocation(NovaEngineeringCore.MOD_ID, "textures/gui/msa_elements.png");

    public static final int TEX_X = 234;
    public static final int TEX_Y = 0;

    public static final int WIDTH = 22;
    public static final int HEIGHT = 14;

    protected ResourceLocation texLocation = null;

    protected final SlotCPU slotCPU;

    protected int textureX = 0;
    protected int textureY = 0;

    public OverlayCPU(final SlotCPU slotCPU) {
        this.slotCPU = slotCPU;
        this.texLocation = TEX_LOCATION;
        this.textureX = TEX_X;
        this.textureY = TEX_Y;
        this.width = WIDTH;
        this.height = HEIGHT;
    }

    @Override
    public void postRender(final GuiContainer gui, final RenderSize renderSize, final RenderPos renderPos, final MousePos mousePos) {
        float partialTickTime = ClientScheduler.getClientTick();

        if (isVisible() && texLocation != null) {
            float period = 50F;
            float alpha = (float) Math.sin((partialTickTime % period) / period * (2.0 * Math.PI)) * 0.5F + 0.5F;

            GlStateManager.color(1F, 1F, 1F, alpha);

            gui.mc.getTextureManager().bindTexture(texLocation);
            gui.drawTexturedModalRect(renderPos.posX(), renderPos.posY(), textureX, textureY, width, height);

            GlStateManager.color(1F, 1F, 1F, 1F);
        }
    }

    @Override
    public boolean isVisible() {
        return slotCPU.isHovered();
    }

}
