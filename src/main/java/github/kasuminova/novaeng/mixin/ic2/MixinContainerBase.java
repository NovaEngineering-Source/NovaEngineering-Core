package github.kasuminova.novaeng.mixin.ic2;

import github.kasuminova.novaeng.NovaEngineeringCore;
import ic2.core.ContainerBase;
import ic2.core.network.NetworkManager;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ContainerBase.class)
public class MixinContainerBase {

    @Redirect(
            method = "detectAndSendChanges",
            at = @At(
                    value = "INVOKE",
                    target = "Lic2/core/network/NetworkManager;updateTileEntityFieldTo(Lnet/minecraft/tileentity/TileEntity;Ljava/lang/String;Lnet/minecraft/entity/player/EntityPlayerMP;)V",
                    remap = false
            ),
            remap = true
    )
    private void redirectUpdateTileEntityFieldTo(final NetworkManager instance, final TileEntity te, final String field, final EntityPlayerMP player) {
        NovaEngineeringCore.PARALLEL_NETWORK_MANAGER.offerAction(instance, () -> instance.updateTileEntityFieldTo(te, field, player), 1);
    }

}
