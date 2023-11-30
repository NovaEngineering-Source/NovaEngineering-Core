package github.kasuminova.novaeng.client.gui.widget.msa.slot;

import github.kasuminova.mmce.client.gui.util.MousePos;
import github.kasuminova.mmce.client.gui.util.RenderPos;
import github.kasuminova.mmce.client.gui.util.RenderSize;
import github.kasuminova.mmce.client.gui.widget.event.GuiEvent;
import github.kasuminova.novaeng.client.gui.widget.msa.event.AssemblyInvCloseEvent;
import github.kasuminova.novaeng.client.gui.widget.msa.event.AssemblyInvOpenEvent;
import github.kasuminova.novaeng.common.container.slot.AssemblySlotManager;
import github.kasuminova.novaeng.common.container.slot.SlotConditionItemHandler;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public abstract class SlotAssembly<T extends SlotConditionItemHandler> extends SlotDynamic<T> {
    protected final AssemblySlotManager slotManager;

    protected final List<SlotAssembly<?>> dependencies = new LinkedList<>();
    protected final List<SlotAssembly<?>> dependents = new LinkedList<>();

    public SlotAssembly(final int slotID, final AssemblySlotManager slotManager) {
        super(slotID);
        this.slotManager = slotManager;
    }

    @Override
    public void update(final GuiContainer gui) {
        super.update(gui);
        if (slot != null) {
            slot.setEnabled(isAvailable());
        }
    }

    @Override
    public void postRender(final GuiContainer gui, final RenderSize renderSize, final RenderPos renderPos, final MousePos mousePos) {
        super.postRender(gui, renderSize, renderPos, mousePos);

        for (final SlotAssembly<?> dependency : dependencies) {
            if (dependency.isHovered()) {
                Gui.drawRect(renderPos.posX() + 1, renderPos.posY() + 1,
                        renderPos.posX() + 17, renderPos.posY() + 17,
                        0x8096FF96
                );
                GlStateManager.color(1F, 1F, 1F, 1F);
                return;
            }
        }

        for (final SlotAssembly<?> dependent : dependents) {
            if (dependent.isHovered()) {
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
                return;
            }
        }
    }

    public <SLOT extends SlotAssembly<?>> SlotAssembly<T> dependsOn(SLOT dependency) {
        dependencies.add(dependency);
        dependency.addDependent(this);
        return this;
    }

    public void addDependent(SlotAssembly<?> dependent) {
        dependents.add(dependent);
    }

    @Override
    public boolean onGuiEvent(final GuiEvent event) {
        if (slot != null) {
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
        if (slot == null) {
            return false;
        }
        for (final SlotAssembly<?> dependency : dependencies) {
            if (!dependency.isInstalled()) {
                return false;
            }
        }
        return true;
    }

    public boolean isInstalled() {
        return slot != null && slot.isEnabled() && slot.getHasStack();
    }

    @Override
    public List<String> getHoverTooltips(final MousePos mousePos) {
        if (isInstalled()) {
            return Collections.emptyList();
        }

        String slotDesc = getSlotDescription();
        List<String> tooltips = new LinkedList<>();
        tooltips.add(slotDesc);

        if (!dependencies.isEmpty()) {
            tooltips.add(I18n.format("gui.modular_server_assembler.assembly.dependencies"));
            for (final SlotAssembly<?> dependency : dependencies) {
                if (dependency.isInstalled()) {
                    tooltips.add(dependency.getSlotDescription() + I18n.format("gui.modular_server_assembler.assembly.dependencies.installed"));
                } else {
                    tooltips.add(dependency.getSlotDescription() + I18n.format("gui.modular_server_assembler.assembly.dependencies.uninstalled"));
                }
            }
        }
        if (!dependents.isEmpty()) {
            tooltips.add(I18n.format("gui.modular_server_assembler.assembly.dependents"));
            for (final SlotAssembly<?> dependent : dependents) {
                tooltips.add(dependent.getSlotDescription());
            }
        }

        return tooltips;
    }

    public String getSlotDescription() {
        return "";
    }
}
