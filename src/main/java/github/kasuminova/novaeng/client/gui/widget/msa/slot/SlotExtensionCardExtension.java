package github.kasuminova.novaeng.client.gui.widget.msa.slot;

import github.kasuminova.novaeng.NovaEngineeringCore;
import github.kasuminova.novaeng.common.container.slot.AssemblySlotManager;
import github.kasuminova.novaeng.common.container.slot.SlotConditionItemHandler;
import github.kasuminova.novaeng.common.container.slot.SlotExtensionCardExtItemHandler;
import net.minecraft.util.ResourceLocation;

public class SlotExtensionCardExtension extends SlotAssembly<SlotExtensionCardExtItemHandler> {
    public static final ResourceLocation TEX_LOCATION = new ResourceLocation(NovaEngineeringCore.MOD_ID, "textures/gui/msa_extension.png");
    public static final int TEX_X = 7;
    public static final int TEX_Y = 7;

    public SlotExtensionCardExtension(final int slotID, final AssemblySlotManager slotManager) {
        super(slotID, slotManager);
        this.texLocation = TEX_LOCATION;
        this.unavailableTexLocation = TEX_LOCATION;
        this.textureX = TEX_X;
        this.textureY = TEX_Y;
        this.unavailableTextureX = TEX_X;
        this.unavailableTextureY = TEX_Y;
    }

    @Override
    protected SlotExtensionCardExtItemHandler getSlot() {
        SlotConditionItemHandler slot = slotManager.getSlot("extension", slotID);
        return slot instanceof SlotExtensionCardExtItemHandler ? (SlotExtensionCardExtItemHandler) slot : null;
    }
}
