package github.kasuminova.novaeng.mixin.cofh;

import cofh.core.block.TileCore;
import cofh.core.network.PacketBase;
import cofh.core.network.PacketHandler;
import cofh.core.util.helpers.ServerHelper;
import github.kasuminova.novaeng.NovaEngineeringCore;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.relauncher.Side;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TileCore.class)
public abstract class MixinTileCore extends TileEntity {

    @Shadow(remap = false)
    public abstract PacketBase getTilePacket();

    @Inject(method = "sendTilePacket", at = @At("HEAD"), cancellable = true, remap = false)
    private void sendTilePacket(final Side side, final CallbackInfo ci) {
        if (this.world != null) {
            if (side == Side.CLIENT && ServerHelper.isServerWorld(this.world)) {
                NovaEngineeringCore.PARALLEL_NETWORK_MANAGER.offerAction(PacketHandler.INSTANCE, () ->
                        PacketHandler.sendToAllAround(this.getTilePacket(), this));
                ci.cancel();
            }
        }
    }

}
