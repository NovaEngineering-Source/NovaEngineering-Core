package github.kasuminova.novaeng.mixin.minecraft.forge;

import github.kasuminova.novaeng.common.profiler.CPacketProfiler;
import github.kasuminova.novaeng.common.profiler.SPacketProfiler;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.INetHandler;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.NetworkManager;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleIndexedCodec;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(SimpleIndexedCodec.class)
public class MixinSimpleIndexedCodec {

    @Redirect(method = "decodeInto(Lio/netty/channel/ChannelHandlerContext;Lio/netty/buffer/ByteBuf;Lnet/minecraftforge/fml/common/network/simpleimpl/IMessage;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraftforge/fml/common/network/simpleimpl/IMessage;fromBytes(Lio/netty/buffer/ByteBuf;)V",
                    remap = false),
            remap = false
    )
    private void onDecodePre(final IMessage message, final ByteBuf byteBuf, final ChannelHandlerContext ctx) {
        final int prevIndex = byteBuf.readerIndex();

        message.fromBytes(byteBuf);

        NetworkManager networkManager;
        INetHandler netHandler = ctx.channel().attr(NetworkRegistry.NET_HANDLER).get();
        if (netHandler instanceof NetHandlerPlayServer handlerServer) {
            networkManager = handlerServer.getNetworkManager();
            SPacketProfiler.onPacketReceived(networkManager, message);
        } else if (netHandler instanceof NetHandlerPlayClient) {
            CPacketProfiler.onPacketReceived(message, byteBuf.readerIndex() - prevIndex);
        }
    }

}
