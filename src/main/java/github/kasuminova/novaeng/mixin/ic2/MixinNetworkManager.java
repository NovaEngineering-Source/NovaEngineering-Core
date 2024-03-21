package github.kasuminova.novaeng.mixin.ic2;

import github.kasuminova.novaeng.NovaEngineeringCore;
import ic2.core.network.GrowingBuffer;
import ic2.core.network.NetworkManager;
import net.minecraft.entity.player.EntityPlayerMP;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(NetworkManager.class)
public abstract class MixinNetworkManager {

    @Shadow(remap = false)
    protected abstract void sendPacket(final GrowingBuffer buffer, final boolean advancePos, final EntityPlayerMP player);

    @Redirect(
            method = "initiateTileEntityEvent",
            at = @At(
                    value = "INVOKE",
                    target = "Lic2/core/network/NetworkManager;sendPacket(Lic2/core/network/GrowingBuffer;ZLnet/minecraft/entity/player/EntityPlayerMP;)V"
            ),
            remap = false)
    private void redirectSendLargePacket(final NetworkManager instance, final GrowingBuffer buffer, final boolean advancePos, final EntityPlayerMP target) {
        NovaEngineeringCore.PARALLEL_NETWORK_MANAGER.offerAction(instance, () -> {
            this.sendPacket(buffer, false, target);
        });
    }

}
