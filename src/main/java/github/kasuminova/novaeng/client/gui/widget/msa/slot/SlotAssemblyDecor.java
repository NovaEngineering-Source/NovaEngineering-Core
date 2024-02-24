package github.kasuminova.novaeng.client.gui.widget.msa.slot;

import github.kasuminova.mmce.client.gui.util.MousePos;
import github.kasuminova.mmce.client.gui.util.RenderPos;
import github.kasuminova.mmce.client.gui.util.RenderSize;
import github.kasuminova.mmce.client.gui.widget.base.WidgetGui;
import github.kasuminova.novaeng.common.container.slot.AssemblySlotManager;
import github.kasuminova.novaeng.common.container.slot.SlotConditionItemHandler;
import net.minecraft.client.gui.Gui;

@SuppressWarnings("unused")
public abstract class SlotAssemblyDecor<T extends SlotConditionItemHandler> extends SlotAssembly<T> {
    protected int overlayX;
    protected int overlayY;
    protected int overlayWidth;
    protected int overlayHeight;

    protected int decorOverlayX;
    protected int decorOverlayY;
    protected int decorOverlayWidth;
    protected int decorOverlayHeight;

    public SlotAssemblyDecor(final int slotID, final AssemblySlotManager slotManager) {
        super(slotID, slotManager);
    }

    @Override
    public void render(final WidgetGui gui, final RenderSize renderSize, final RenderPos renderPos, final MousePos mousePos) {
        if (isVisible() && texLocation != null && isAvailable()) {
            gui.getGui().mc.getTextureManager().bindTexture(texLocation);

            int renderPosX = renderPos.posX();
            int renderPosY = renderPos.posY();

            // decor overlay
            int texX = decorOverlayX;
            int texY = decorOverlayY;
            int width = decorOverlayWidth;
            int height = decorOverlayHeight;
            Gui.drawScaledCustomSizeModalRect(renderPosX, renderPosY, texX, texY, width, height, this.width, this.height, 256, 256);

            // simple overlay
            texX = overlayX;
            texY = overlayY;
            width = overlayWidth;
            height = overlayHeight;
            Gui.drawScaledCustomSizeModalRect(renderPosX, renderPosY, texX, texY, width, height, this.width, this.height, 256, 256);
        }
    }

    public int getOverlayX() {
        return overlayX;
    }

    public SlotAssemblyDecor<T> setOverlayX(final int overlayX) {
        this.overlayX = overlayX;
        return this;
    }

    public int getOverlayY() {
        return overlayY;
    }

    public SlotAssemblyDecor<T> setOverlayY(final int overlayY) {
        this.overlayY = overlayY;
        return this;
    }

    public int getOverlayWidth() {
        return overlayWidth;
    }

    public SlotAssemblyDecor<T> setOverlayWidth(final int overlayWidth) {
        this.overlayWidth = overlayWidth;
        return this;
    }

    public int getOverlayHeight() {
        return overlayHeight;
    }

    public SlotAssemblyDecor<T> setOverlayHeight(final int overlayHeight) {
        this.overlayHeight = overlayHeight;
        return this;
    }

    public int getDecorOverlayX() {
        return decorOverlayX;
    }

    public SlotAssemblyDecor<T> setDecorOverlayX(final int decorOverlayX) {
        this.decorOverlayX = decorOverlayX;
        return this;
    }

    public int getDecorOverlayY() {
        return decorOverlayY;
    }

    public SlotAssemblyDecor<T> setDecorOverlayY(final int decorOverlayY) {
        this.decorOverlayY = decorOverlayY;
        return this;
    }

    public int getDecorOverlayWidth() {
        return decorOverlayWidth;
    }

    public SlotAssemblyDecor<T> setDecorOverlayWidth(final int decorOverlayWidth) {
        this.decorOverlayWidth = decorOverlayWidth;
        return this;
    }

    public int getDecorOverlayHeight() {
        return decorOverlayHeight;
    }

    public SlotAssemblyDecor<T> setDecorOverlayHeight(final int decorOverlayHeight) {
        this.decorOverlayHeight = decorOverlayHeight;
        return this;
    }
}
