package github.kasuminova.novaeng.client.gui.widget.msa.slot;

import github.kasuminova.novaeng.NovaEngineeringCore;
import github.kasuminova.novaeng.common.container.slot.AssemblySlotManager;
import github.kasuminova.novaeng.common.container.slot.SlotCapacitorItemHandler;
import github.kasuminova.novaeng.common.container.slot.SlotConditionItemHandler;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;

public class SlotCapacitor extends SlotAssembly<SlotCapacitorItemHandler> {
    public static final ResourceLocation TEX_LOCATION = new ResourceLocation(NovaEngineeringCore.MOD_ID, "textures/gui/msa_power.png");
    public static final int TEX_X = 7;
    public static final int TEX_Y = 25;

    public static final int UNAVAILABLE_TEX_X = 7;
    public static final int UNAVAILABLE_TEX_Y = 25;

    protected final int displayID;

    public SlotCapacitor(final int displayID, final int slotID, final AssemblySlotManager slotManager) {
        super(slotID, slotManager);
        this.displayID = displayID;
        this.texLocation = TEX_LOCATION;
        this.unavailableTexLocation = TEX_LOCATION;
        this.textureX = TEX_X;
        this.textureY = TEX_Y;
        this.unavailableTextureX = UNAVAILABLE_TEX_X;
        this.unavailableTextureY = UNAVAILABLE_TEX_Y;
    }

    @Override
    protected SlotCapacitorItemHandler getSlot() {
        SlotConditionItemHandler slot = slotManager.getSlot("power", slotID);
        return slot instanceof SlotCapacitorItemHandler ? (SlotCapacitorItemHandler) slot : null;
    }

    @Override
    public <SLOT extends SlotAssembly<?>> SlotCapacitor dependsOn(final SLOT dependency) {
        return (SlotCapacitor) super.dependsOn(dependency);
    }

    @Override
    public String getSlotDescription() {
        return I18n.format("gui.modular_server_assembler.assembly.capacitor.name", displayID);
    }

}
