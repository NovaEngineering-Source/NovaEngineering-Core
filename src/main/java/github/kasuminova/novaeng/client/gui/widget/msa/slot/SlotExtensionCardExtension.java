package github.kasuminova.novaeng.client.gui.widget.msa.slot;

import github.kasuminova.mmce.client.gui.util.MousePos;
import github.kasuminova.novaeng.NovaEngineeringCore;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;

import java.util.Collections;
import java.util.List;

public class SlotExtensionCardExtension extends SlotExtension {
    public static final ResourceLocation TEX_LOCATION = new ResourceLocation(NovaEngineeringCore.MOD_ID, "textures/gui/msa_extensions.png");
    public static final int TEX_X = 7;
    public static final int TEX_Y = 7;

    protected final int displayID;

    public SlotExtensionCardExtension(final int displayID) {
        this.displayID = displayID;
        this.texLocation = TEX_LOCATION;
        this.unavailableTexLocation = TEX_LOCATION;
        this.textureX = TEX_X;
        this.textureY = TEX_Y;
        this.unavailableTextureX = TEX_X;
        this.unavailableTextureY = TEX_Y;
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public List<String> getHoverTooltips(final MousePos mousePos) {
        return Collections.singletonList(I18n.format("gui.modular_server_assembler.assembly.extension_ext.name", displayID));
    }
}
