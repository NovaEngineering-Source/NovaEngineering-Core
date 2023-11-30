package github.kasuminova.novaeng.client.gui.widget.msa.slot;

import github.kasuminova.novaeng.NovaEngineeringCore;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.items.SlotItemHandler;

public class SlotDisabled extends SlotDynamic<SlotItemHandler> {
    public static final ResourceLocation TEX_LOCATION = new ResourceLocation(NovaEngineeringCore.MOD_ID, "textures/gui/msa_cpu.png");
    public static final int TEX_X = 7;
    public static final int TEX_Y = 7;

    public SlotDisabled() {
        super(-1);
        this.texLocation = TEX_LOCATION;
        this.unavailableTexLocation = TEX_LOCATION;
        this.textureX = TEX_X;
        this.textureY = TEX_Y;
        this.unavailableTextureX = TEX_X;
        this.unavailableTextureY = TEX_Y;
    }

    @Override
    protected SlotItemHandler getSlot() {
        return null;
    }

    @Override
    public boolean isAvailable() {
        return false;
    }
}
