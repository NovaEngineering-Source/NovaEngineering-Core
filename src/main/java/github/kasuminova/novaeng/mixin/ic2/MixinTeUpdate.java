package github.kasuminova.novaeng.mixin.ic2;

import github.kasuminova.novaeng.NovaEngineeringCore;
import ic2.core.network.GrowingBuffer;
import ic2.core.network.NetworkManager;
import net.minecraft.entity.player.EntityPlayerMP;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(targets = "ic2.core.network.TeUpdate")
public class MixinTeUpdate {

    @Redirect(
            method = "send",
            at = @At(
                    value = "INVOKE",
                    target = "Lic2/core/network/NetworkManager;sendLargePacket(Lnet/minecraft/entity/player/EntityPlayerMP;ILic2/core/network/GrowingBuffer;)V"
            ),
            remap = false)
    private static void redirectSendLargePacket(final NetworkManager instance, final EntityPlayerMP e, final int state, final GrowingBuffer growingBuffer) {
        NovaEngineeringCore.PARALLEL_NETWORK_MANAGER.offerAction(instance, () -> {
            ((InvokerNetworkManager) instance).invokeSendLargePacket(e, state, growingBuffer);
        });
    }

}
