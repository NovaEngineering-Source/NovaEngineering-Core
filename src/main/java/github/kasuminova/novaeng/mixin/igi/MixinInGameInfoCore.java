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
    private Framebuffer novaEngineering_Core$framebuffer = null;
    @Unique
    private boolean novaEngineering_Core$refreshBuffer = true;
    @Unique
    private int novaEngineering_Core$tickCounter = 0;
    @Unique
    private int novaEngineering_Core$displayWidth = 0;
    @Unique
    private int novaEngineering_Core$displayHeight = 0;

    @Shadow(remap = false)
    public abstract void onTickRender();

    @Inject(method = "onTickClient", at = @At("HEAD"), remap = false)
    private void onTickClient(final CallbackInfo ci) {
        novaEngineering_Core$tickCounter++;
        if (novaEngineering_Core$tickCounter % 2 == 0) {
            novaEngineering_Core$refreshBuffer = true;
        }
    }

    @Inject(method = "onTickRender", at = @At("HEAD"), cancellable = true, remap = false)
    private void onTickRenderPre(final CallbackInfo ci) {
        if (!OpenGlHelper.framebufferSupported) {
            return;
        }

        Minecraft minecraft = Minecraft.getMinecraft();

        if (novaEngineering_Core$framebuffer == null) {
            novaEngineering_Core$displayWidth = minecraft.displayWidth;
            novaEngineering_Core$displayHeight = minecraft.displayHeight;
            novaEngineering_Core$framebuffer = new Framebuffer(novaEngineering_Core$displayWidth, novaEngineering_Core$displayHeight, false);
            novaEngineering_Core$framebuffer.framebufferColor[0] = 0.0F;
            novaEngineering_Core$framebuffer.framebufferColor[1] = 0.0F;
            novaEngineering_Core$framebuffer.framebufferColor[2] = 0.0F;
        }
        if (novaEngineering_Core$refreshBuffer) {
            if (novaEngineering_Core$displayWidth != minecraft.displayWidth || novaEngineering_Core$displayHeight != minecraft.displayHeight) {
                novaEngineering_Core$displayWidth = minecraft.displayWidth;
                novaEngineering_Core$displayHeight = minecraft.displayHeight;
                novaEngineering_Core$framebuffer.createBindFramebuffer(novaEngineering_Core$displayWidth, novaEngineering_Core$displayHeight);
            } else {
                novaEngineering_Core$framebuffer.framebufferClear();
            }
            novaEngineering_Core$framebuffer.bindFramebuffer(false);
            GlStateManager.disableBlend();
            GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        } else {
            RenderUtils.renderFramebuffer(minecraft, novaEngineering_Core$framebuffer);
            ci.cancel();
        }
    }

    @Inject(method = "onTickRender", at = @At("RETURN"), remap = false)
    private void onTickRenderPost(final CallbackInfo ci) {
        if (!OpenGlHelper.framebufferSupported) {
            return;
        }
        if (novaEngineering_Core$refreshBuffer) {
            novaEngineering_Core$refreshBuffer = false;
            Minecraft.getMinecraft().getFramebuffer().bindFramebuffer(false);
            onTickRender();
        }
    }
}
