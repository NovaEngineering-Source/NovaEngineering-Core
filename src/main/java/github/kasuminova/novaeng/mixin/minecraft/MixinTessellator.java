package github.kasuminova.novaeng.mixin.minecraft;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.VertexFormat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import javax.annotation.Nonnull;

@Mixin(Tessellator.class)
public class MixinTessellator {

    @Redirect(method = "<init>", at = @At(value = "NEW", target = "(I)Lnet/minecraft/client/renderer/BufferBuilder;"))
    private BufferBuilder redirectNewBuffer(final int bufferSizeIn) {
        return new BufferBuilder(bufferSizeIn) {
            @Override
            public void begin(final int glMode, @Nonnull final VertexFormat format) {
                novaeng_core$checkClientThread();
                super.begin(glMode, format);
            }

            @Override
            public void finishDrawing() {
                novaeng_core$checkClientThread();
                super.finishDrawing();
            }
        };
    }

    @Unique
    private static void novaeng_core$checkClientThread() {
        if (!Minecraft.getMinecraft().isCallingFromMinecraftThread()) {
            throw new IllegalStateException("Using Tessellator buffer in another thread.");
        }
    }

}
