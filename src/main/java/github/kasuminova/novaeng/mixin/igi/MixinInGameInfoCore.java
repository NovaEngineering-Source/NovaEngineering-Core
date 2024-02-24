package github.kasuminova.novaeng.mixin.igi;

import com.github.lunatrius.ingameinfo.InGameInfoCore;
import github.kasuminova.novaeng.client.util.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.shader.Framebuffer;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameInfoCore.class)
public abstract class MixinInGameInfoCore {

    @Unique
    private Framebuffer novaeng$framebuffer = null;
    @Unique
    private boolean novaeng$refreshBuffer = true;
    @Unique
    private int novaeng$tickCounter = 0;
    @Unique
    private int novaeng$displayWidth = 0;
    @Unique
    private int novaeng$displayHeight = 0;

    @Shadow(remap = false)
    public abstract void onTickRender();

    @Inject(method = "onTickClient", at = @At("HEAD"), remap = false)
    private void onTickClient(final CallbackInfo ci) {
        novaeng$tickCounter++;
        if (novaeng$tickCounter % 2 == 0) {
            novaeng$refreshBuffer = true;
        }
    }

    @Inject(method = "onTickRender", at = @At("HEAD"), cancellable = true, remap = false)
    private void onTickRenderPre(final CallbackInfo ci) {
        if (!OpenGlHelper.framebufferSupported) {
            return;
        }

        Minecraft minecraft = Minecraft.getMinecraft();

        if (novaeng$framebuffer == null) {
            novaeng$displayWidth = minecraft.displayWidth;
            novaeng$displayHeight = minecraft.displayHeight;
            novaeng$framebuffer = new Framebuffer(novaeng$displayWidth, novaeng$displayHeight, false);
            novaeng$framebuffer.framebufferColor[0] = 0.0F;
            novaeng$framebuffer.framebufferColor[1] = 0.0F;
            novaeng$framebuffer.framebufferColor[2] = 0.0F;
        }
        if (novaeng$refreshBuffer) {
            if (novaeng$displayWidth != minecraft.displayWidth || novaeng$displayHeight != minecraft.displayHeight) {
                novaeng$displayWidth = minecraft.displayWidth;
                novaeng$displayHeight = minecraft.displayHeight;
                novaeng$framebuffer.createBindFramebuffer(novaeng$displayWidth, novaeng$displayHeight);
            } else {
                novaeng$framebuffer.framebufferClear();
            }
            novaeng$framebuffer.bindFramebuffer(false);
            GlStateManager.disableBlend();
            GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        } else {
            RenderUtils.renderFramebuffer(minecraft, novaeng$framebuffer);
            ci.cancel();
        }
    }

    @Inject(method = "onTickRender", at = @At("RETURN"), remap = false)
    private void onTickRenderPost(final CallbackInfo ci) {
        if (!OpenGlHelper.framebufferSupported) {
            return;
        }
        if (novaeng$refreshBuffer) {
            novaeng$refreshBuffer = false;
            Minecraft.getMinecraft().getFramebuffer().bindFramebuffer(false);
            onTickRender();
        }
    }
}
