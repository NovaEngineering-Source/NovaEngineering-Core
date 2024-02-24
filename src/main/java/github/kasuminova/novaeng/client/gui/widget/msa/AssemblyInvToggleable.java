package github.kasuminova.novaeng.client.gui.widget.msa;

import github.kasuminova.mmce.client.gui.util.MousePos;
import github.kasuminova.mmce.client.gui.util.RenderPos;
import github.kasuminova.mmce.client.gui.util.RenderSize;
import github.kasuminova.mmce.client.gui.widget.Button5State;
import github.kasuminova.mmce.client.gui.widget.base.DynamicWidget;
import github.kasuminova.mmce.client.gui.widget.base.WidgetController;
import github.kasuminova.mmce.client.gui.widget.base.WidgetGui;
import github.kasuminova.mmce.client.gui.widget.container.Column;
import github.kasuminova.novaeng.client.gui.widget.msa.event.AssemblyInvCloseEvent;
import github.kasuminova.novaeng.client.gui.widget.msa.event.AssemblyInvOpenEvent;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;

import java.util.Collections;

public abstract class AssemblyInvToggleable extends AssemblyInv {
    protected final Column toggleButtonColumn = new Column();
    protected final Button5State toggleFirst = new Button5State();
    protected final Button5State toggleSecond = new Button5State();

    protected final Column secondSlotColumn = new Column();

    protected ResourceLocation secondOpenedBgTexLocation = null;

    protected Column currentColumn = slotColumn;

    protected int secondOpenedInvBgTexOffsetX = 0;
    protected int secondOpenedInvBgTexOffsetY = 0;
    protected int secondOpenedInvBgTexWidth = 0;
    protected int secondOpenedInvBgTexHeight = 0;

    public AssemblyInvToggleable(final AssemblyInvManager assemblyInvManager, final WidgetController widgetController) {
        super(assemblyInvManager, widgetController);
        // AssemblyInvToggleable 使用不太一样的渲染方式。
        this.widgets.clear();

        this.toggleFirst.setOnClickedListener(button -> {
            if (this.currentColumn == this.slotColumn) {
                ((Button5State) button).setClicked(true);
                return;
            }
            toggleColumn(this.slotColumn);
            this.toggleSecond.setClicked(false);
        });
        this.toggleFirst.setTooltipFunction(button -> Collections.singletonList(I18n.format("gui.modular_server_assembler.assembly.inv.change.main")));
        this.toggleFirst.setClicked(true);

        this.toggleSecond.setOnClickedListener(button -> {
            if (this.currentColumn == this.secondSlotColumn) {
                ((Button5State) button).setClicked(true);
                return;
            }
            toggleColumn(this.secondSlotColumn);
            this.toggleFirst.setClicked(false);
        });
        this.toggleSecond.setTooltipFunction(button -> Collections.singletonList(I18n.format("gui.modular_server_assembler.assembly.inv.change.heat")));

        this.toggleButtonColumn.setMarginLeft(7).setMarginUp(7);
        this.toggleButtonColumn.addWidget(this.toggleFirst);
        this.toggleButtonColumn.addWidget(this.toggleSecond);

        this.addWidget(this.open);
        this.addWidget(this.toggleButtonColumn);
        this.addWidget(this.slotColumn);
        this.addWidget(this.secondSlotColumn);

        this.slotColumn.setDisabled(true);
        this.toggleButtonColumn.setDisabled(true);
        this.secondSlotColumn.setDisabled(true);
    }

    protected static void openInv(final Column column) {
        column.setEnabled(true);
        column.onGuiEvent(new AssemblyInvOpenEvent(null));
    }

    protected static void closeInv(final Column column) {
        column.onGuiEvent(new AssemblyInvCloseEvent(null));
        column.setDisabled(true);
    }

    @Override
    protected void preRenderInternal(final WidgetGui gui, final RenderSize renderSize, final RenderPos renderPos, final MousePos mousePos) {
        doRender(gui, renderSize, renderPos, mousePos, DynamicWidget::preRender);

        if (this.open.isEnabled()) {
            if (closedBgTexLocation != null) {
                gui.getGui().mc.getTextureManager().bindTexture(closedBgTexLocation);
                gui.getGui().drawTexturedModalRect(renderPos.posX(), renderPos.posY(),
                        closedInvBgTexOffsetX, closedInvBgTexOffsetY,
                        closedInvBgTexWidth, closedInvBgTexHeight
                );
            }
        } else {
            ResourceLocation openedBgTexLocation;
            int openedInvBgTexOffsetX;
            int openedInvBgTexOffsetY;
            int openedInvBgTexWidth;
            int openedInvBgTexHeight;

            if (this.currentColumn == this.slotColumn) {
                openedBgTexLocation   = this.openedBgTexLocation;
                openedInvBgTexOffsetX = this.openedInvBgTexOffsetX;
                openedInvBgTexOffsetY = this.openedInvBgTexOffsetY;
                openedInvBgTexWidth   = this.openedInvBgTexWidth;
                openedInvBgTexHeight  = this.openedInvBgTexHeight;
            } else if (this.currentColumn == this.secondSlotColumn) {
                openedBgTexLocation   = this.secondOpenedBgTexLocation;
                openedInvBgTexOffsetX = this.secondOpenedInvBgTexOffsetX;
                openedInvBgTexOffsetY = this.secondOpenedInvBgTexOffsetY;
                openedInvBgTexWidth   = this.secondOpenedInvBgTexWidth;
                openedInvBgTexHeight  = this.secondOpenedInvBgTexHeight;
            } else {
                return;
            }

            if (openedBgTexLocation != null) {
                gui.getGui().mc.getTextureManager().bindTexture(openedBgTexLocation);
                gui.getGui().drawTexturedModalRect(renderPos.posX(), renderPos.posY(),
                        openedInvBgTexOffsetX, openedInvBgTexOffsetY,
                        openedInvBgTexWidth, openedInvBgTexHeight
                );
            }
        }
    }

    protected void toggleColumn(final Column column) {
        if (currentColumn != column) {
            closeInv();
            this.currentColumn = column;
            openInv();
        }
    }

    @Override
    public void openInv() {
        this.open.setDisabled(true);
        this.toggleButtonColumn.setEnabled(true);
        openInv(currentColumn);
    }

    @Override
    public void closeInv() {
        this.open.setEnabled(true);
        this.toggleButtonColumn.setDisabled(true);
        closeInv(currentColumn);
    }
}
