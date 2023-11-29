package github.kasuminova.novaeng.client.gui.widget.msa.slot;

import github.kasuminova.mmce.client.gui.util.MousePos;
import github.kasuminova.novaeng.NovaEngineeringCore;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;

import java.util.Collections;
import java.util.List;

public class SlotRAM extends SlotCondition {
    public static final ResourceLocation TEX_LOCATION = new ResourceLocation(NovaEngineeringCore.MOD_ID, "textures/gui/msa_cpu.png");
    public static final int TEX_X = 122;
    public static final int TEX_Y = 18;

    public static final int UNAVAILABLE_TEX_X = 25;
    public static final int UNAVAILABLE_TEX_Y = 7;

    protected final int displayID;

    public SlotRAM(final int displayID) {
        this.displayID = displayID;
        this.texLocation = TEX_LOCATION;
        this.unavailableTexLocation = TEX_LOCATION;
        this.textureX = TEX_X;
        this.textureY = TEX_Y;
        this.unavailableTextureX = UNAVAILABLE_TEX_X;
        this.unavailableTextureY = UNAVAILABLE_TEX_Y;
    }

    @Override
    public SlotRAM dependsOn(SlotExtension dependency) {
        return (SlotRAM) super.dependsOn(dependency);
    }

    @Override
    public List<String> getHoverTooltips(final MousePos mousePos) {
        return Collections.singletonList(I18n.format("gui.modular_server_assembler.assembly.ram.name", displayID));
    }
}
