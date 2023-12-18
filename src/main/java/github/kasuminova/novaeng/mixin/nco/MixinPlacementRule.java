package github.kasuminova.novaeng.mixin.nco;

import nc.util.I18nHelper;
import nc.util.Lang;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

@Mixin(I18nHelper.class)
public class MixinPlacementRule {
    @Unique
    private static final ScriptEngine JS_ENGINE = new ScriptEngineManager().getEngineByName("JavaScript");

    @Inject(method = "getPluralRule", remap = false, at = @At("HEAD"), cancellable = true)
    private static void onGetPluralRule(final int count, final CallbackInfoReturnable<Integer> cir) throws ScriptException {
        cir.setReturnValue((int) JS_ENGINE.eval(Lang.localise("nc.sf.plural_rule", count)));
    }

}
