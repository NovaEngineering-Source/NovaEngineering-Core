package github.kasuminova.novaeng.mixin.igi;

import com.github.lunatrius.ingameinfo.InGameInfoCore;
import com.github.lunatrius.ingameinfo.client.gui.overlay.Info;
import github.kasuminova.novaeng.client.util.RenderUtils;
import github.kasuminova.novaeng.mixin.util.IMixinInGameInfoCore;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.shader.Framebuffer;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.LinkedList;
import java.util.List;

@Mixin(InGameInfoCore.class)
public class MixinInGameInfoCore implements IMixinInGameInfoCore {

    @Unique
    private final List<Runnable> novaeng$postDrawList = new LinkedList<>();

    @Shadow(remap = false) @Final private List<Info> info;
    @Unique
    private Framebuffer novaeng$fbo = null;
    @Unique
    private boolean novaeng$refreshFBO = true;
    @Unique
    private boolean novaeng$postDrawing = true;

    @Unique
    private int novaeng$tickCounter = 0;
    @Unique
    private int novaeng$displayWidth = 0;
    @Unique
    private int novaeng$displayHeight = 0;

    @Inject(method = "onTickClient", at = @At("HEAD"), remap = false)
    private void onTickClient(final CallbackInfo ci) {
        novaeng$tickCounter++;
        if (novaeng$tickCounter % 2 == 0) {
            novaeng$refreshFBO = true;
        }
    }

    /**
     * @author Kasumi_Nova
     * @reason 使用 FBO 优化 IGI 渲染性能，帧率越高效果越好。
     */
    @Overwrite(remap = false)
    public void onTickRender() {
        if (!OpenGlHelper.framebufferSupported) {
            return;
        }

        Minecraft minecraft = Minecraft.getMinecraft();
        novaeng$postDrawing = false;

        if (novaeng$fbo == null) {
            novaeng$displayWidth = minecraft.displayWidth;
            novaeng$displayHeight = minecraft.displayHeight;
            novaeng$fbo = new Framebuffer(novaeng$displayWidth, novaeng$displayHeight, false);
            novaeng$fbo.framebufferColor[0] = 0.0F;
            novaeng$fbo.framebufferColor[1] = 0.0F;
            novaeng$fbo.framebufferColor[2] = 0.0F;
        }

        if (novaeng$refreshFBO) {
            novaeng$postDrawList.clear();
            novaeng_core$renderToFBO(minecraft);
            novaeng$refreshFBO = false;
        }

        RenderUtils.renderFramebuffer(minecraft, novaeng$fbo);
        novaeng$postDrawing = true;
        novaeng$postDrawList.forEach(Runnable::run);
    }

    @Unique
    private void novaeng_core$renderToFBO(final Minecraft minecraft) {
        if (novaeng$displayWidth != minecraft.displayWidth || novaeng$displayHeight != minecraft.displayHeight) {
            novaeng$displayWidth = minecraft.displayWidth;
            novaeng$displayHeight = minecraft.displayHeight;
            novaeng$fbo.createBindFramebuffer(novaeng$displayWidth, novaeng$displayHeight);
        } else {
            novaeng$fbo.framebufferClear();
        }
        novaeng$fbo.bindFramebuffer(false);

        GlStateManager.disableBlend();
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        info.forEach(Info::draw);

        Minecraft.getMinecraft().getFramebuffer().bindFramebuffer(false);
    }

    @Override
    @SuppressWarnings("AddedMixinMembersNamePattern")
    public void addPostDrawAction(final Runnable action) {
        novaeng$postDrawList.add(action);
    }

    @Override
    @SuppressWarnings("AddedMixinMembersNamePattern")
    public boolean isPostDrawing() {
        return novaeng$postDrawing;
    }

}