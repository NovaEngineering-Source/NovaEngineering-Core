package gregtech.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import gregtech.client.utils.BloomEffectUtil;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.Entity;
import net.minecraft.util.BlockRenderLayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
@SuppressWarnings("MethodMayBeStatic")
public class MixinEntityRenderer {

    @Inject(
            method = "renderWorldPass",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/RenderGlobal;renderBlockLayer(Lnet/minecraft/util/BlockRenderLayer;DILnet/minecraft/entity/Entity;)I",
                    shift = At.Shift.AFTER,
                    ordinal = 3
            )
    )
    @SuppressWarnings("DataFlowIssue")
    private void injectBloomRenderer(final int pass,
                                     final float partialTicks,
                                     final long finishTimeNano,
                                     final CallbackInfo ci,
                                     @Local(name = "renderglobal") RenderGlobal renderglobal,
                                     @Local(name = "entity") Entity entity)
    {
        BloomEffectUtil.renderBloomBlockLayer(renderglobal, BlockRenderLayer.TRANSLUCENT, partialTicks, pass, entity);
    }

}
