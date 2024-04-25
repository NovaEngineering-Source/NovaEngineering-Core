package github.kasuminova.novaeng.mixin.igi;

import com.github.lunatrius.ingameinfo.handler.Ticker;
import github.kasuminova.novaeng.client.gui.hudcaching.HUDCaching;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("MethodMayBeStatic")
@Mixin(Ticker.class)
public class MixinTicker {

    @Inject(method = "onRenderTick", at = @At("HEAD"), remap = false, cancellable = true)
    private void injectOnRenderTick(final TickEvent.RenderTickEvent event, final CallbackInfo ci) {
        if (HUDCaching.enable && !HUDCaching.igiRendering) {
            ci.cancel();
        }
    }

}
