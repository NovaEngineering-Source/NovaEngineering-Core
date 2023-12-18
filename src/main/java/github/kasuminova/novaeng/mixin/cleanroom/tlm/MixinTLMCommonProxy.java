package github.kasuminova.novaeng.mixin.cleanroom.tlm;

import com.github.tartaricacid.touhoulittlemaid.proxy.CommonProxy;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

@Mixin(CommonProxy.class)
public class MixinTLMCommonProxy {

    @Inject(method = "getScriptEngine", at = @At("HEAD"), remap = false, cancellable = true)
    private static void onGetScriptEngine(final CallbackInfoReturnable<ScriptEngine> cir) {
        cir.setReturnValue(new ScriptEngineManager().getEngineByName("JavaScript"));
    }

}
