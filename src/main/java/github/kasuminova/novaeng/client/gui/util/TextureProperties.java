package github.kasuminova.novaeng.client.gui.util;

import com.github.bsideup.jabel.Desugar;
import github.kasuminova.mmce.client.gui.util.RenderPos;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;

@Desugar
public record TextureProperties(ResourceLocation texRes, int texX, int texY, int width, int height) {

    public void bind(final TextureManager textureManager) {
        textureManager.bindTexture(texRes);
    }

    public void render(final RenderPos renderPos, final GuiScreen gui) {
        bind(gui.mc.getTextureManager());
        gui.drawTexturedModalRect(renderPos.posX(), renderPos.posY(), texX, texY, width, height);
    }

}
