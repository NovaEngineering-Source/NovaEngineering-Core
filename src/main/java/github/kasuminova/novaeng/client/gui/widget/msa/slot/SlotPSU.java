package github.kasuminova.novaeng.client.gui.widget.msa.slot;

import github.kasuminova.novaeng.NovaEngineeringCore;
import github.kasuminova.novaeng.common.container.slot.AssemblySlotManager;
import github.kasuminova.novaeng.common.container.slot.SlotConditionItemHandler;
import github.kasuminova.novaeng.common.container.slot.SlotPSUItemHandler;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;

public class SlotPSU extends SlotAssembly<SlotPSUItemHandler> {
    public static final ResourceLocation TEX_LOCATION = new ResourceLocation(NovaEngineeringCore.MOD_ID, "textures/gui/msa_power.png");
    public static final int TEX_X = 7;
    public static final int TEX_Y = 7;

    public static final int UNAVAILABLE_TEX_X = 7;
    public static final int UNAVAILABLE_TEX_Y = 7;

    protected final int displayID;

    public SlotPSU(final int displayID, final int slotID, final AssemblySlotManager slotManager) {
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
    protected SlotPSUItemHandler getSlot() {
        SlotConditionItemHandler slot = slotManager.getSlot("power", slotID);
        return slot instanceof SlotPSUItemHandler ? (SlotPSUItemHandler) slot : null;
    }

    @Override
    public <SLOT extends SlotAssembly<?>> SlotPSU dependsOn(SLOT dependency) {
        return (SlotPSU) super.dependsOn(dependency);
    }

    @Override
    public String getSlotDescription() {
        return I18n.format("gui.modular_server_assembler.assembly.psu.name", displayID);
    }

}
