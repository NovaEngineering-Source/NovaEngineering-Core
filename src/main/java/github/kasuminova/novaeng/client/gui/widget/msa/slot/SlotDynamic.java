package github.kasuminova.novaeng.client.gui.widget.msa.slot;

import github.kasuminova.mmce.client.gui.util.MousePos;
import github.kasuminova.mmce.client.gui.util.RenderPos;
import github.kasuminova.mmce.client.gui.util.RenderSize;
import github.kasuminova.mmce.client.gui.widget.base.DynamicWidget;
import github.kasuminova.mmce.client.gui.widget.event.GuiEvent;
import github.kasuminova.novaeng.client.gui.widget.msa.event.ModularServerUpdateEvent;
import github.kasuminova.novaeng.common.container.slot.SlotConditionItemHandler;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.util.ResourceLocation;

public abstract class SlotDynamic<T extends SlotConditionItemHandler> extends DynamicWidget {
    protected final int slotID;

    protected T slot = null;

    protected ResourceLocation texLocation = null;
    protected ResourceLocation unavailableTexLocation = null;

    protected int textureX = 0;
    protected int textureY = 0;

    protected int unavailableTextureX = 0;
    protected int unavailableTextureY = 0;

    public SlotDynamic(final int slotID) {
        this.slotID = slotID;
        this.width = 18;
        this.height = 18;
    }

    @Override
    public void initWidget(final GuiContainer gui) {
        this.slot = getSlot();
    }

    @Override
    public void preRender(final GuiContainer gui, final RenderSize renderSize, final RenderPos renderPos, final MousePos mousePos) {
        if (slot != null) {
            if (isAvailable() && slot.isEnabled()) {
                slot.xPos = renderPos.posX() + 1;
                slot.yPos = renderPos.posY() + 1;
            }
        }
    }

    @Override
    public void render(final GuiContainer gui, final RenderSize renderSize, final RenderPos renderPos, final MousePos mousePos) {
        if (isVisible() && unavailableTexLocation != null && texLocation != null) {
            if (slot != null) {
                slot.setHovered(isMouseOver(mousePos));
            }

            int texX;
            int texY;
            if (isAvailable()) {
                texX = textureX;
                texY = textureY;
                gui.mc.getTextureManager().bindTexture(texLocation);
            } else {
                texX = unavailableTextureX;
                texY = unavailableTextureY;
                gui.mc.getTextureManager().bindTexture(unavailableTexLocation);
            }

            gui.drawTexturedModalRect(renderPos.posX(), renderPos.posY(), texX, texY, width, height);
        }
    }

    protected abstract T getSlot();

    public boolean isAvailable() {
        return true;
    }

    @Override
    public boolean onGuiEvent(final GuiEvent event) {
        if (event instanceof ModularServerUpdateEvent) {
            this.slot = getSlot();
        }
        return false;
    }

    // Getters / Setters

    public ResourceLocation getTexLocation() {
        return texLocation;
    }

    public SlotDynamic<T> setTexLocation(final ResourceLocation texLocation) {
        this.texLocation = texLocation;
        return this;
    }

    public ResourceLocation getUnavailableTexLocation() {
        return unavailableTexLocation;
    }

    public SlotDynamic<T> setUnavailableTexLocation(final ResourceLocation unavailableTexLocation) {
        this.unavailableTexLocation = unavailableTexLocation;
        return this;
    }

    public int getTextureX() {
        return textureX;
    }

    public SlotDynamic<T> setTextureX(final int textureX) {
        this.textureX = textureX;
        return this;
    }

    public int getTextureY() {
        return textureY;
    }

    public SlotDynamic<T> setTextureY(final int textureY) {
        this.textureY = textureY;
        return this;
    }

    public int getUnavailableTextureX() {
        return unavailableTextureX;
    }

    public SlotDynamic<T> setUnavailableTextureX(final int unavailableTextureX) {
        this.unavailableTextureX = unavailableTextureX;
        return this;
    }

    public int getUnavailableTextureY() {
        return unavailableTextureY;
    }

    public SlotDynamic<T> setUnavailableTextureY(final int unavailableTextureY) {
        this.unavailableTextureY = unavailableTextureY;
        return this;
    }

    public boolean isHovered() {
        return slot != null && slot.isHovered();
    }
}
