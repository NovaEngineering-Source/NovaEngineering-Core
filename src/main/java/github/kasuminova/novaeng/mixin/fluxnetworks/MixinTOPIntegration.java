package github.kasuminova.novaeng.mixin.fluxnetworks;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import sonar.fluxnetworks.api.translate.Translation;
import sonar.fluxnetworks.common.integration.TOPIntegration;

@Mixin(TOPIntegration.FluxConnectorInfoProvider.class)
public class MixinTOPIntegration {

    @SuppressWarnings("MethodMayBeStatic")
    @Redirect(method = "addProbeInfo",
            at = @At(
                    value = "INVOKE",
                    target = "Lsonar/fluxnetworks/api/translate/Translation;t()Ljava/lang/String;",
                    remap = false),
            remap = false)
    public String redirectAddProbeInfoTrans(final Translation instance) {
        return "{*" + instance.key + "*}";
    }

}
