package github.kasuminova.novaeng.client.gui.widget.msa.slot;

import github.kasuminova.mmce.client.gui.util.MousePos;
import github.kasuminova.mmce.client.gui.util.RenderPos;
import github.kasuminova.mmce.client.gui.util.RenderSize;
import github.kasuminova.mmce.client.gui.widget.base.WidgetGui;
import github.kasuminova.mmce.client.gui.widget.event.GuiEvent;
import github.kasuminova.novaeng.client.gui.widget.msa.event.AssemblerInvUpdateEvent;
import github.kasuminova.novaeng.client.gui.widget.msa.event.AssemblyInvCloseEvent;
import github.kasuminova.novaeng.client.gui.widget.msa.event.AssemblyInvOpenEvent;
import github.kasuminova.novaeng.common.container.slot.AssemblySlotManager;
import github.kasuminova.novaeng.common.container.slot.SlotConditionItemHandler;
import github.kasuminova.novaeng.common.hypernet.server.ModularServer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;

import java.util.Collections;
import java.util.List;

public abstract class SlotAssembly<T extends SlotConditionItemHandler> extends SlotDynamic<T> {
    protected AssemblySlotManager slotManager;

    public SlotAssembly(final int slotID, final AssemblySlotManager slotManager) {
        super(slotID);
        this.slotManager = slotManager;
    }

    @Override
    public void update(final WidgetGui gui) {
        super.update(gui);
        if (slot != null) {
            slot.setEnabled(isAvailable());
        }
    }

    @Override
    public void postRender(final WidgetGui gui, final RenderSize renderSize, final RenderPos renderPos, final MousePos mousePos) {
        super.postRender(gui, renderSize, renderPos, mousePos);

        if (slot == null) {
            return;
        }

        if (isAvailable() && slot.isInvalid()) {
            Gui.drawRect(renderPos.posX() + 1, renderPos.posY() + 1,
                    renderPos.posX() + 17, renderPos.posY() + 17,
                    0x80FF9696
            );
            return;
        }

        for (final SlotConditionItemHandler dependency : slot.getDependencies()) {
            if (renderDependiceColorOverlay(renderPos, dependency)) return;
        }
        for (final SlotConditionItemHandler dependent : slot.getDependents()) {
            if (renderDependencyColorOverlay(renderPos, dependent)) return;
        }
        for (final SlotConditionItemHandler dependency : slot.getSoftDependencies()) {
            if (renderDependiceColorOverlay(renderPos, dependency)) return;
        }
        for (final SlotConditionItemHandler dependent : slot.getSoftDependents()) {
            if (renderDependencyColorOverlay(renderPos, dependent)) return;
        }
    }

    private boolean renderDependencyColorOverlay(final RenderPos renderPos, final SlotConditionItemHandler dependent) {
        if (dependent.isHovered()) {
            GlStateManager.colorMask(true, true, true, false);

            if (isInstalled()) {
                Gui.drawRect(renderPos.posX() + 1, renderPos.posY() + 1,
                        renderPos.posX() + 17, renderPos.posY() + 17,
                        0x80FFFF96
                );
            } else {
                Gui.drawRect(renderPos.posX() + 1, renderPos.posY() + 1,
                        renderPos.posX() + 17, renderPos.posY() + 17,
                        0x80FF9696
                );
            }
            GlStateManager.color(1F, 1F, 1F, 1F);

            GlStateManager.colorMask(true, true, true, true);
            return true;
        }
        return false;
    }

    private static boolean renderDependiceColorOverlay(final RenderPos renderPos, final SlotConditionItemHandler dependency) {
        if (dependency.isHovered()) {
            GlStateManager.colorMask(true, true, true, false);

            Gui.drawRect(renderPos.posX() + 1, renderPos.posY() + 1,
                    renderPos.posX() + 17, renderPos.posY() + 17,
                    0x8096FF96
            );
            GlStateManager.color(1F, 1F, 1F, 1F);

            GlStateManager.colorMask(true, true, true, true);
            return true;
        }
        return false;
    }

    @Override
    public boolean onGuiEvent(final GuiEvent event) {
        if (event instanceof AssemblerInvUpdateEvent serverUpdateEvent) {
            ModularServer server = serverUpdateEvent.getServer();
            this.slotManager = server == null ? null : server.getSlotManager();
        } else if (slot != null) {
            if (event instanceof AssemblyInvCloseEvent) {
                slot.setEnabled(false);
            } else if (event instanceof AssemblyInvOpenEvent) {
                slot.setEnabled(true);
            }
        }
        return super.onGuiEvent(event);
    }

    @Override
    public boolean isAvailable() {
        return slot != null && slot.isAvailable();
    }

    public boolean isInstalled() {
        return slot != null && slot.getHasStack();
    }

    @Override
    public List<String> getHoverTooltips(final MousePos mousePos) {
        return slot == null ? Collections.emptyList() : slot.getHoverTooltips();
    }
}
