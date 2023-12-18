package github.kasuminova.novaeng.common.container.slot;

import github.kasuminova.novaeng.common.util.ServerModuleInv;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.SlotItemHandler;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public abstract class SlotConditionItemHandler extends SlotItemHandler {
    protected final int displayID;

    protected boolean enabled = false;
    protected boolean hovered = false;

    protected final List<SlotConditionItemHandler> dependencies = new LinkedList<>();
    protected final List<SlotConditionItemHandler> dependents = new LinkedList<>();

    public SlotConditionItemHandler(final ServerModuleInv inventoryIn, final int index, final int displayID, final int xPosition, final int yPosition) {
        super(inventoryIn, index, xPosition, yPosition);
        this.displayID = displayID;
    }

    public SlotConditionItemHandler dependsOn(SlotConditionItemHandler dependency) {
        dependencies.add(dependency);
        dependency.addDependent(this);
        return this;
    }

    public void addDependent(SlotConditionItemHandler dependent) {
        dependents.add(dependent);
    }

    public boolean isInstalled() {
        return enabled && getHasStack();
    }

    public boolean isAvailable() {
        for (final SlotConditionItemHandler dependency : dependencies) {
            if (!dependency.isInstalled()) {
                return false;
            }
        }
        return true;
    }

    @SideOnly(Side.CLIENT)
    public List<String> getHoverTooltips() {
        if (isInstalled()) {
            return Collections.emptyList();
        }

        String slotDesc = getSlotDescription();
        List<String> tooltips = new LinkedList<>();
        tooltips.add(slotDesc);

        if (!dependencies.isEmpty()) {
            tooltips.add(I18n.format("gui.modular_server_assembler.assembly.dependencies"));
            for (final SlotConditionItemHandler dependency : dependencies) {
                if (dependency.isInstalled()) {
                    tooltips.add(dependency.getSlotDescription() + I18n.format("gui.modular_server_assembler.assembly.dependencies.installed"));
                } else {
                    tooltips.add(dependency.getSlotDescription() + I18n.format("gui.modular_server_assembler.assembly.dependencies.uninstalled"));
                }
            }
        }
        if (!dependents.isEmpty()) {
            tooltips.add(I18n.format("gui.modular_server_assembler.assembly.dependents"));
            for (final SlotConditionItemHandler dependent : dependents) {
                tooltips.add(dependent.getSlotDescription());
            }
        }

        return tooltips;
    }

    public abstract String getSlotDescription();

    public List<SlotConditionItemHandler> getDependencies() {
        return dependencies;
    }

    public List<SlotConditionItemHandler> getDependents() {
        return dependents;
    }

    @Override
    public ServerModuleInv getItemHandler() {
        return (ServerModuleInv) super.getItemHandler();
    }

    public boolean isHovered() {
        return hovered;
    }

    public SlotConditionItemHandler setHovered(final boolean hovered) {
        this.hovered = hovered;
        return this;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public SlotConditionItemHandler setEnabled(final boolean enabled) {
        this.enabled = enabled;
        return this;
    }
}
