package github.kasuminova.mmce.client.gui.widget.base;

import github.kasuminova.mmce.client.gui.util.RenderOffset;
import github.kasuminova.mmce.client.gui.util.RenderSize;
import github.kasuminova.mmce.client.gui.widget.event.GuiEvent;
import net.minecraft.client.gui.inventory.GuiContainer;

import java.util.Collections;
import java.util.List;

public abstract class DynamicWidget {
    protected int xSize = 0;
    protected int ySize = 0;

    protected int marginLeft = 0;
    protected int marginRight = 0;
    protected int marginUp = 0;
    protected int marginDown = 0;

    protected boolean visible = true;
    protected boolean enabled = true;

    public void initWidget(GuiContainer gui) {
    }

    // Widget Render

    public void preRender(GuiContainer gui, RenderSize renderSize, RenderOffset renderOffset, int mouseX, int mouseY) {
    }

    public abstract void postRender(GuiContainer gui, RenderSize renderSize, RenderOffset renderOffset, int mouseX, int mouseY);

    // GUI EventHandlers

    public void update(GuiContainer gui) {
    }

    public boolean onMouseClicked(int absMouseX, int absMouseY, int mouseX, int mouseY, RenderOffset renderOffset, int mouseButton) {
        return false;
    }

    public boolean onMouseReleased(int absMouseX, int absMouseY, int mouseX, int mouseY, RenderOffset renderOffset) {
        return false;
    }

    public boolean onMouseDWheel(int absMouseX, int absMouseY, int mouseX, int mouseY, RenderOffset renderOffset, int wheel) {
        return false;
    }

    public boolean onKeyTyped(char typedChar, int keyCode) {
        return false;
    }

    // Custom GUIEvent Handlers

    public boolean onGuiEvent(GuiEvent event) {
        return false;
    }

    // Tooltips

    public List<String> getHoverTooltips() {
        return Collections.emptyList();
    }

    // Utils

    public boolean isMouseOver(int startX, int startY, int mouseX, int mouseY) {
        if (isInvisible()) {
            return false;
        }

        int endX = startX + xSize;
        int endY = startY + ySize;
        return mouseX >= startX && mouseX <= endX && mouseY >= startY && mouseY <= endY;
    }

    // Getter Setters

    public int getXSize() {
        return xSize;
    }

    public DynamicWidget setXSize(final int xSize) {
        this.xSize = xSize;
        return this;
    }

    public int getYSize() {
        return ySize;
    }

    public DynamicWidget setYSize(final int ySize) {
        this.ySize = ySize;
        return this;
    }

    // Padding

    public int getMarginLeft() {
        return marginLeft;
    }

    public DynamicWidget setMarginLeft(final int marginLeft) {
        this.marginLeft = marginLeft;
        return this;
    }

    public int getMarginRight() {
        return marginRight;
    }

    public DynamicWidget setMarginRight(final int marginRight) {
        this.marginRight = marginRight;
        return this;
    }

    public int getMarginUp() {
        return marginUp;
    }

    public DynamicWidget setMarginUp(final int marginUp) {
        this.marginUp = marginUp;
        return this;
    }

    public int getMarginDown() {
        return marginDown;
    }

    public DynamicWidget setMarginDown(final int marginDown) {
        this.marginDown = marginDown;
        return this;
    }

    public DynamicWidget setMarginVertical(final int marginVertical) {
        this.marginUp = marginVertical;
        this.marginDown = marginVertical;
        return this;
    }

    public DynamicWidget setMarginHorizontal(final int marginHorizontal) {
        this.marginLeft = marginHorizontal;
        this.marginRight = marginHorizontal;
        return this;
    }

    public DynamicWidget setMargin(final int margin) {
        this.marginLeft = margin;
        this.marginRight = margin;
        this.marginUp = margin;
        this.marginDown = margin;
        return this;
    }

    // Enabled / Visible

    public boolean isVisible() {
        return visible;
    }

    public DynamicWidget setVisible(final boolean visible) {
        this.visible = visible;
        return this;
    }

    public boolean isInvisible() {
        return !visible;
    }

    public DynamicWidget setInvisible(final boolean invisible) {
        this.visible = !invisible;
        return this;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public DynamicWidget setEnabled(final boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public boolean isDisabled() {
        return !enabled;
    }

    public DynamicWidget setDisabled(final boolean disabled) {
        this.enabled = !disabled;
        return this;
    }
}
