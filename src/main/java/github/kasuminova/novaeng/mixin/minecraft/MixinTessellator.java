package github.kasuminova.novaeng.mixin.minecraft;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Tessellator.class)
public class MixinTessellator {

    @Redirect(method = "<init>", at = @At(value = "NEW", target = "(I)Lnet/minecraft/client/renderer/BufferBuilder;"))
    private BufferBuilder redirectNewBuffer(final int bufferSizeIn) {
        return new BufferBuilder(bufferSizeIn);
//        return new BufferBuilder(bufferSizeIn) {
//
//            private boolean isDrawing;
//            private StackTraceElement[] currentStackTrace = null;
//
//            @Override
//            public void begin(final int glMode, @Nonnull final VertexFormat format) {
//                if (isDrawing) {
//                    if (currentStackTrace != null) {
//                        for (final StackTraceElement traceElement : currentStackTrace) {
//                            NovaEngineeringCore.log.error(traceElement.toString());
//                        }
//                    }
//                    throw new IllegalStateException("Already building!");
//                }
//                isDrawing = true;
//                currentStackTrace = Thread.currentThread().getStackTrace();
//
//                super.begin(glMode, format);
//            }
//
//            @Override
//            public void finishDrawing() {
//                if (!isDrawing) {
//                    if (currentStackTrace != null) {
//                        for (final StackTraceElement traceElement : currentStackTrace) {
//                            NovaEngineeringCore.log.error(traceElement.toString());
//                        }
//                    }
//                    throw new IllegalStateException("Not building!");
//                }
//                isDrawing = false;
//                currentStackTrace = null;
//
//                super.finishDrawing();
//            }
//        };
    }

}
