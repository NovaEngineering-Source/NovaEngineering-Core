package github.kasuminova.novaeng.mixin.minecraft;

import github.kasuminova.novaeng.common.profiler.CPacketProfiler;
import github.kasuminova.novaeng.common.profiler.SPacketProfiler;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(NettyPacketDecoder.class)
@SuppressWarnings("MethodMayBeStatic")
public class MixinNettyPacketDecoder {

    @Redirect(method = "decode",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/network/Packet;readPacketData(Lnet/minecraft/network/PacketBuffer;)V")
    )
    private void onDecode(final Packet<?> packet, final PacketBuffer packetBuffer, final ChannelHandlerContext ctx) throws Exception {
        final int prevIndex = packetBuffer.readerIndex();

        packet.readPacketData(packetBuffer);

        NetworkManager networkManager = ctx.pipeline().get(NetworkManager.class);
        if (networkManager == null) {
            return;
        }
        if (networkManager.getDirection() == EnumPacketDirection.CLIENTBOUND) {
            CPacketProfiler.onPacketReceived(packet, packetBuffer.readerIndex() - prevIndex);
        } else if (networkManager.getDirection() == EnumPacketDirection.SERVERBOUND) {
            SPacketProfiler.onPacketReceived(networkManager, packet);
        }
    }

}
