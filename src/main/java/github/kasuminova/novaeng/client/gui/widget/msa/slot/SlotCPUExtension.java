package github.kasuminova.novaeng.client.gui.widget.msa.slot;

import github.kasuminova.novaeng.NovaEngineeringCore;
import github.kasuminova.novaeng.common.container.slot.AssemblySlotManager;
import github.kasuminova.novaeng.common.container.slot.SlotCPUExtItemHandler;
import github.kasuminova.novaeng.common.container.slot.SlotConditionItemHandler;
import net.minecraft.util.ResourceLocation;

public class SlotCPUExtension extends SlotAssembly<SlotCPUExtItemHandler> {
    public static final ResourceLocation TEX_LOCATION = new ResourceLocation(NovaEngineeringCore.MOD_ID, "textures/gui/msa_cpu.png");
    public static final int TEX_X = 23;
    public static final int TEX_Y = 43;

    public static final int UNAVAILABLE_TEX_X = 165;
    public static final int UNAVAILABLE_TEX_Y = 1;

    public SlotCPUExtension(final int slotID, final AssemblySlotManager slotManager) {
        super(slotID, slotManager);
        this.texLocation = TEX_LOCATION;
        this.unavailableTexLocation = TEX_LOCATION;
        this.textureX = TEX_X;
        this.textureY = TEX_Y;
        this.unavailableTextureX = UNAVAILABLE_TEX_X;
        this.unavailableTextureY = UNAVAILABLE_TEX_Y;
    }

    @Override
    protected SlotCPUExtItemHandler getSlot() {
        SlotConditionItemHandler slot = slotManager.getSlot("cpu", slotID);
        return slot instanceof SlotCPUExtItemHandler ? (SlotCPUExtItemHandler) slot : null;
    }
}
