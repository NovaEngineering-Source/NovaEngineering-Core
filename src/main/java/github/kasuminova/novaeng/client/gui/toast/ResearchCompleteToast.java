package github.kasuminova.novaeng.client.gui.toast;

import github.kasuminova.novaeng.common.hypernet.old.research.ResearchCognitionData;
import net.minecraft.client.gui.toasts.GuiToast;
import net.minecraft.client.gui.toasts.IToast;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class ResearchCompleteToast implements IToast {
    private final ResearchCognitionData researchData;
    private final ItemStack itemStack;
    private long firstDrawTime;
    private boolean newDisplay;

    public ResearchCompleteToast(ResearchCognitionData researchData) {
        this.researchData = researchData;
        this.itemStack = researchData.getPreviewStack();
    }

    @Nonnull
    public Visibility draw(@Nonnull GuiToast toastGui, long delta) {
        if (this.newDisplay) {
            this.firstDrawTime = delta;
            this.newDisplay = false;
        }
        var minecraft = toastGui.getMinecraft();
        var fontRenderer = minecraft.fontRenderer;

        // Texture
        minecraft.getTextureManager().bindTexture(TEXTURE_TOASTS);
        GlStateManager.color(1.0F, 1.0F, 1.0F);
        toastGui.drawTexturedModalRect(0, 0, 0, 32, 160, 32);

        // Text
        var title = I18n.format("gui.toast.research.complete");
        fontRenderer.drawString(title, 30, 7, -11534256);
        fontRenderer.drawString(researchData.getTranslatedName(), 30, 18, -16777216);

        // Item
        RenderHelper.enableGUIStandardItemLighting();
        minecraft.getRenderItem().renderItemAndEffectIntoGUI(null, itemStack, 8, 8);

        return delta - this.firstDrawTime < 5000L ? Visibility.SHOW : Visibility.HIDE;
    }
}
