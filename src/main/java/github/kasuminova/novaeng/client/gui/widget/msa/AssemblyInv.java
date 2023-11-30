package github.kasuminova.novaeng.client.gui.widget.msa;

import github.kasuminova.mmce.client.gui.util.MousePos;
import github.kasuminova.mmce.client.gui.util.RenderPos;
import github.kasuminova.mmce.client.gui.util.RenderSize;
import github.kasuminova.mmce.client.gui.widget.Button;
import github.kasuminova.mmce.client.gui.widget.base.WidgetController;
import github.kasuminova.mmce.client.gui.widget.container.Column;
import github.kasuminova.mmce.client.gui.widget.container.Row;
import github.kasuminova.novaeng.client.gui.widget.msa.event.AssemblyInvCloseEvent;
import github.kasuminova.novaeng.client.gui.widget.msa.event.AssemblyInvOpenEvent;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.util.ResourceLocation;

public abstract class AssemblyInv extends Row {
    protected final AssemblyInvManager assemblyInvManager;

    protected final Button open = new Button();

    protected final Column slotColum = new Column();

    protected ResourceLocation closedBgTexLocation = null;
    protected ResourceLocation openedBgTexLocation = null;

    protected int closedInvBgTexWidth = 0;
    protected int closedInvBgTexOffsetX = 0;
    protected int closedInvBgTexHeight = 0;
    protected int closedInvBgTexOffsetY = 0;

    protected int openedInvBgTexWidth = 0;
    protected int openedInvBgTexOffsetX = 0;
    protected int openedInvBgTexHeight = 0;
    protected int openedInvBgTexOffsetY = 0;

    public AssemblyInv(final AssemblyInvManager assemblyInvManager, final WidgetController widgetController) {
        this.assemblyInvManager = assemblyInvManager;

        this.open.setOnClickedListener(button -> assemblyInvManager.openInv(this));
        this.addWidget(this.open);

        this.slotColum.setDisabled(true);
        this.addWidget(slotColum);
    }

    @Override
    protected void preRenderInternal(final GuiContainer gui, final RenderSize renderSize, final RenderPos renderPos, final MousePos mousePos) {
        super.preRenderInternal(gui, renderSize, renderPos, mousePos);

        if (this.open.isEnabled()) {
            if (closedBgTexLocation != null) {
                gui.mc.getTextureManager().bindTexture(closedBgTexLocation);
                gui.drawTexturedModalRect(renderPos.posX(), renderPos.posY(),
                        closedInvBgTexOffsetX, closedInvBgTexOffsetY,
                        closedInvBgTexWidth, closedInvBgTexHeight
                );
            }
        } else {
            if (openedBgTexLocation != null) {
                gui.mc.getTextureManager().bindTexture(openedBgTexLocation);
                gui.drawTexturedModalRect(renderPos.posX(), renderPos.posY(),
                        openedInvBgTexOffsetX, openedInvBgTexOffsetY,
                        openedInvBgTexWidth, openedInvBgTexHeight
                );
            }
        }
    }

    @Override
    public void onGUIClosed(final GuiContainer gui) {
        closeInv();
    }

    public void openInv() {
        this.open.setDisabled(true);
        this.slotColum.setEnabled(true);
        onGuiEvent(new AssemblyInvOpenEvent(null));
    }

    public void closeInv() {
        onGuiEvent(new AssemblyInvCloseEvent(null));
        this.open.setEnabled(true);
        this.slotColum.setDisabled(true);
    }

    // Width / Height

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public AssemblyInv setWidth(final int width) {
        this.width = width;
        return this;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public AssemblyInv setHeight(final int height) {
        this.height = height;
        return this;
    }

    // Getters / Setters

    public AssemblyInvManager getAssemblyInvManager() {
        return assemblyInvManager;
    }

    public Button getOpen() {
        return open;
    }

    public ResourceLocation getClosedBgTexLocation() {
        return closedBgTexLocation;
    }

    public AssemblyInv setClosedBgTexLocation(final ResourceLocation closedBgTexLocation) {
        this.closedBgTexLocation = closedBgTexLocation;
        return this;
    }

    public int getClosedInvBgTexWidth() {
        return closedInvBgTexWidth;
    }

    public AssemblyInv setClosedInvBgTexWidth(final int closedInvBgTexWidth) {
        this.closedInvBgTexWidth = closedInvBgTexWidth;
        return this;
    }

    public int getClosedInvBgTexHeight() {
        return closedInvBgTexHeight;
    }

    public AssemblyInv setClosedInvBgTexHeight(final int closedInvBgTexHeight) {
        this.closedInvBgTexHeight = closedInvBgTexHeight;
        return this;
    }
}
