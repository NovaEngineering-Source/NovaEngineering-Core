package github.kasuminova.novaeng.mixin.minecraft.forge;

import github.kasuminova.novaeng.NovaEngineeringCore;
import io.netty.channel.ChannelFutureListener;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.FMLEmbeddedChannel;
import net.minecraftforge.fml.common.network.FMLOutboundHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.EnumMap;

@Mixin(SimpleNetworkWrapper.class)
@SuppressWarnings("VulnerableCodeUsages")
public class MixinSimpleNetworkWrapper {

    @Shadow(remap = false) private EnumMap<Side, FMLEmbeddedChannel> channels;

    @Inject(method = "sendToAll", at = @At("HEAD"), cancellable = true, remap = false)
    private void injectSendToAll(final IMessage message, final CallbackInfo ci) {
        if (!FMLCommonHandler.instance().getMinecraftServerInstance().isCallingFromMinecraftThread()) {
            return;
        }
        if (NovaEngineeringCore.PARALLEL_NETWORK_MANAGER.isBlacklistChannel(this)) {
            return;
        }
        NovaEngineeringCore.PARALLEL_NETWORK_MANAGER.offerAction(this, () -> {
            channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.ALL);
            channels.get(Side.SERVER).writeAndFlush(message).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
        });
        ci.cancel();
    }

    @Inject(method = "sendTo", at = @At("HEAD"), cancellable = true, remap = false)
    private void injectSendTo(final IMessage message, final EntityPlayerMP player, final CallbackInfo ci) {
        if (!FMLCommonHandler.instance().getMinecraftServerInstance().isCallingFromMinecraftThread()) {
            return;
        }
        if (NovaEngineeringCore.PARALLEL_NETWORK_MANAGER.isBlacklistChannel(this)) {
            return;
        }
        NovaEngineeringCore.PARALLEL_NETWORK_MANAGER.offerAction(this, () -> {
            channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.PLAYER);
            channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(player);
            channels.get(Side.SERVER).writeAndFlush(message).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
        });
        ci.cancel();
    }

    @Inject(method = "sendToAllAround", at = @At("HEAD"), cancellable = true, remap = false)
    private void injectSendToAllAround(final IMessage message, final NetworkRegistry.TargetPoint point, final CallbackInfo ci) {
        if (!FMLCommonHandler.instance().getMinecraftServerInstance().isCallingFromMinecraftThread()) {
            return;
        }
        if (NovaEngineeringCore.PARALLEL_NETWORK_MANAGER.isBlacklistChannel(this)) {
            return;
        }
        NovaEngineeringCore.PARALLEL_NETWORK_MANAGER.offerAction(this, () -> {
            channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.ALLAROUNDPOINT);
            channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(point);
            channels.get(Side.SERVER).writeAndFlush(message).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
        });
        ci.cancel();
    }

    @Inject(method = "sendToAllTracking(Lnet/minecraftforge/fml/common/network/simpleimpl/IMessage;Lnet/minecraftforge/fml/common/network/NetworkRegistry$TargetPoint;)V", at = @At("HEAD"), cancellable = true, remap = false)
    private void injectSendToAllTrackingPoint(final IMessage message, final NetworkRegistry.TargetPoint point, final CallbackInfo ci) {
        if (!FMLCommonHandler.instance().getMinecraftServerInstance().isCallingFromMinecraftThread()) {
            return;
        }
        if (NovaEngineeringCore.PARALLEL_NETWORK_MANAGER.isBlacklistChannel(this)) {
            return;
        }
        NovaEngineeringCore.PARALLEL_NETWORK_MANAGER.offerAction(this, () -> {
            channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.TRACKING_POINT);
            channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(point);
            channels.get(Side.SERVER).writeAndFlush(message).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
        });
        ci.cancel();
    }

    @Inject(method = "sendToAllTracking(Lnet/minecraftforge/fml/common/network/simpleimpl/IMessage;Lnet/minecraft/entity/Entity;)V", at = @At("HEAD"), cancellable = true, remap = false)
    private void injectSendToAllTracking(final IMessage message, final Entity entity, final CallbackInfo ci) {
        if (!FMLCommonHandler.instance().getMinecraftServerInstance().isCallingFromMinecraftThread()) {
            return;
        }
        if (NovaEngineeringCore.PARALLEL_NETWORK_MANAGER.isBlacklistChannel(this)) {
            return;
        }
        NovaEngineeringCore.PARALLEL_NETWORK_MANAGER.offerAction(this, () -> {
            channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.TRACKING_ENTITY);
            channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(entity);
            channels.get(Side.SERVER).writeAndFlush(message).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
        });
        ci.cancel();
    }

    @Inject(method = "sendToDimension", at = @At("HEAD"), cancellable = true, remap = false)
    private void injectSendToDimension(final IMessage message, final int dimensionId, final CallbackInfo ci) {
        if (!FMLCommonHandler.instance().getMinecraftServerInstance().isCallingFromMinecraftThread()) {
            return;
        }
        if (NovaEngineeringCore.PARALLEL_NETWORK_MANAGER.isBlacklistChannel(this)) {
            return;
        }
        NovaEngineeringCore.PARALLEL_NETWORK_MANAGER.offerAction(this, () -> {
            channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.DIMENSION);
            channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(dimensionId);
            channels.get(Side.SERVER).writeAndFlush(message).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
        });
        ci.cancel();
    }

}
