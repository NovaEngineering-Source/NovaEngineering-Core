package github.kasuminova.novaeng.mixin.minecraft.forge;

import com.llamalad7.mixinextras.sugar.Local;
import github.kasuminova.novaeng.NovaEngineeringCore;
import net.minecraft.network.INetHandler;
import net.minecraftforge.fml.common.network.FMLNetworkException;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = FMLProxyPacket.class)
public class MixinFMLProxyPacket {

    @Final
    @Shadow(remap = false)
    String channel;

    @Inject(
            method = "processPacket",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraftforge/fml/common/network/handshake/NetworkDispatcher;rejectHandshake(Ljava/lang/String;)V",
                    remap = false,
                    ordinal = 1
            )
    )
    private void injectExceptionAndRELog1(final INetHandler inethandler, final CallbackInfo ci, @Local(name = "t") final Throwable t) {
        NovaEngineeringCore.log.error("[NovaEng-RELog] Caught critical exception handling a packet on channel {}, re-log exception.", channel);
        NovaEngineeringCore.log.error("[NovaEng-RELog] ", t);
    }

    @Inject(
            method = "processPacket",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraftforge/fml/common/network/handshake/NetworkDispatcher;rejectHandshake(Ljava/lang/String;)V",
                    remap = false,
                    ordinal = 0
            )
    )
    private void injectExceptionAndRELog0(final INetHandler inethandler, final CallbackInfo ci, @Local(name = "ne") final FMLNetworkException ne) {
        NovaEngineeringCore.log.error("[NovaEng-RELog] Caught critical exception handling a packet on channel {}, re-log exception.", channel);
        NovaEngineeringCore.log.error("[NovaEng-RELog] ", ne);
    }

}
