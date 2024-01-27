package github.kasuminova.novaeng.mixin.minecraft;

import github.kasuminova.novaeng.common.profiler.PacketProfiler;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.*;
import net.minecraftforge.fml.common.FMLCommonHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;
import java.util.List;

@Mixin(NettyPacketDecoder.class)
@SuppressWarnings("MethodMayBeStatic")
public class MixinNettyPacketDecoder {

    @Inject(method = "decode", at = @At("HEAD"), cancellable = true)
    public void onDecode(final ChannelHandlerContext p_decode_1_, final ByteBuf p_decode_2_, final List<Object> p_decode_3_, final CallbackInfo ci) throws Exception {
        // Only for Client.
        if (FMLCommonHandler.instance().getSide().isServer()) {
            return;
        }

        if (p_decode_2_.readableBytes() != 0) {
            PacketBuffer packetbuffer = new PacketBuffer(p_decode_2_);
            int i = packetbuffer.readVarInt();
            Packet<?> packet = p_decode_1_.channel().attr(NetworkManager.PROTOCOL_ATTRIBUTE_KEY).get().getPacket(EnumPacketDirection.CLIENTBOUND, i);

            if (packet == null) {
                throw new IOException("Bad packet id " + i);
            }
            // 记录先前下标
            final int currentIndex = packetbuffer.readerIndex();
            // 读取网络包
            packet.readPacketData(packetbuffer);
            // 记录读取后下标
            PacketProfiler.onPacketReceived(packet, packetbuffer.readerIndex() - currentIndex);

            if (packetbuffer.readableBytes() > 0) {
                throw new IOException("Packet " + p_decode_1_.channel().attr(NetworkManager.PROTOCOL_ATTRIBUTE_KEY).get().getId() + "/" + i + " (" + packet.getClass().getSimpleName() + ") was larger than I expected, found " + packetbuffer.readableBytes() + " bytes extra whilst reading packet " + i);
            } else {
                p_decode_3_.add(packet);
            }
        }

        ci.cancel();
    }

}
