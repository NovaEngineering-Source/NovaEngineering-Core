package github.kasuminova.novaeng.mixin.minecraft;

import net.minecraft.client.gui.FontRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(FontRenderer.class)
public interface IFontRenderer {

    @Invoker
    void callLoadGlyphTexture(int page);

    @Accessor
    float getRed();

    @Accessor
    float getGreen();

    @Accessor
    float getBlue();

    @Accessor
    float getAlpha();

    @Accessor
    void setRed(float red);

    @Accessor
    void setGreen(float green);

    @Accessor
    void setBlue(float blue);

    @Accessor
    void setAlpha(float alpha);

}
