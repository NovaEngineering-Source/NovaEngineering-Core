package github.kasuminova.novaeng.mixin.cofh;

import cofh.core.network.PacketBase;
import cofh.core.network.PacketHandler;
import github.kasuminova.novaeng.NovaEngineeringCore;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.FMLOutboundHandler;
import net.minecraftforge.fml.common.network.FMLOutboundHandler.OutboundTarget;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PacketHandler.class)
@SuppressWarnings("VulnerableCodeUsages")
public class MixinPacketHandler {

    @Inject(method = "sendToAll", at = @At("HEAD"), cancellable = true, remap = false)
    private static void injectSendToAll(final PacketBase message, final CallbackInfo ci) {
        if (!FMLCommonHandler.instance().getMinecraftServerInstance().isCallingFromMinecraftThread()) {
            return;
        }
        AccessorPacketHandler instance = (AccessorPacketHandler) PacketHandler.INSTANCE;
        NovaEngineeringCore.PARALLEL_NETWORK_MANAGER.offerAction(PacketHandler.INSTANCE, () -> {
            instance.getChannels().get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(OutboundTarget.ALL);
            instance.getChannels().get(Side.SERVER).writeAndFlush(message);
        });
        ci.cancel();
    }

    @Inject(method = "sendTo(Lcofh/core/network/PacketBase;Lnet/minecraft/entity/player/EntityPlayerMP;)V", at = @At("HEAD"), cancellable = true, remap = false)
    private static void injectSendTo(final PacketBase message, final EntityPlayerMP player, final CallbackInfo ci) {
        if (!FMLCommonHandler.instance().getMinecraftServerInstance().isCallingFromMinecraftThread()) {
            return;
        }
        AccessorPacketHandler instance = (AccessorPacketHandler) PacketHandler.INSTANCE;
        NovaEngineeringCore.PARALLEL_NETWORK_MANAGER.offerAction(PacketHandler.INSTANCE, () -> {
            instance.getChannels().get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(OutboundTarget.PLAYER);
            instance.getChannels().get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(player);
            instance.getChannels().get(Side.SERVER).writeAndFlush(message);
        });
        ci.cancel();
    }

    @Inject(method = "sendTo(Lcofh/core/network/PacketBase;Lnet/minecraft/entity/player/EntityPlayer;)V", at = @At("HEAD"), cancellable = true, remap = false)
    private static void injectSendTo(final PacketBase message, final EntityPlayer player, final CallbackInfo ci) {
        if (!FMLCommonHandler.instance().getMinecraftServerInstance().isCallingFromMinecraftThread()) {
            return;
        }
        AccessorPacketHandler instance = (AccessorPacketHandler) PacketHandler.INSTANCE;
        NovaEngineeringCore.PARALLEL_NETWORK_MANAGER.offerAction(PacketHandler.INSTANCE, () -> {
            instance.getChannels().get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(OutboundTarget.PLAYER);
            instance.getChannels().get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(player);
            instance.getChannels().get(Side.SERVER).writeAndFlush(message);
        });
        ci.cancel();
    }

    @Inject(method = "sendToAllAround(Lcofh/core/network/PacketBase;Lnet/minecraftforge/fml/common/network/NetworkRegistry$TargetPoint;)V", at = @At("HEAD"), cancellable = true, remap = false)
    private static void injectSendToAllTrackingPoint(final PacketBase message, final NetworkRegistry.TargetPoint point, final CallbackInfo ci) {
        if (!FMLCommonHandler.instance().getMinecraftServerInstance().isCallingFromMinecraftThread()) {
            return;
        }
        AccessorPacketHandler instance = (AccessorPacketHandler) PacketHandler.INSTANCE;
        NovaEngineeringCore.PARALLEL_NETWORK_MANAGER.offerAction(PacketHandler.INSTANCE, () -> {
            instance.getChannels().get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(OutboundTarget.ALLAROUNDPOINT);
            instance.getChannels().get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(point);
            instance.getChannels().get(Side.SERVER).writeAndFlush(message);
        });
        ci.cancel();
    }

    @Inject(method = "sendToAllAround(Lcofh/core/network/PacketBase;Lnet/minecraft/tileentity/TileEntity;)V", at = @At("HEAD"), cancellable = true, remap = false)
    private static void injectSendToAllTracking(final PacketBase message, final TileEntity theTile, final CallbackInfo ci) {
        if (!FMLCommonHandler.instance().getMinecraftServerInstance().isCallingFromMinecraftThread()) {
            return;
        }
        AccessorPacketHandler instance = (AccessorPacketHandler) PacketHandler.INSTANCE;
        NovaEngineeringCore.PARALLEL_NETWORK_MANAGER.offerAction(PacketHandler.INSTANCE, () -> {
            instance.getChannels().get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(OutboundTarget.ALLAROUNDPOINT);
            instance.getChannels().get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGETARGS)
                    .set(
                            new NetworkRegistry.TargetPoint(
                                    theTile.getWorld().provider.getDimension(),
                                    theTile.getPos().getX(),
                                    theTile.getPos().getY(),
                                    theTile.getPos().getZ(),
                                    192.0
                            )
                    );
            instance.getChannels().get(Side.SERVER).writeAndFlush(message);
        });
        ci.cancel();
    }

    @Inject(method = "sendToAllAround(Lcofh/core/network/PacketBase;Lnet/minecraft/world/World;III)V", at = @At("HEAD"), cancellable = true, remap = false)
    private static void injectSendToAllAround(final PacketBase message, final World world, final int x, final int y, final int z, final CallbackInfo ci) {
        if (!FMLCommonHandler.instance().getMinecraftServerInstance().isCallingFromMinecraftThread()) {
            return;
        }
        AccessorPacketHandler instance = (AccessorPacketHandler) PacketHandler.INSTANCE;
        NovaEngineeringCore.PARALLEL_NETWORK_MANAGER.offerAction(PacketHandler.INSTANCE, () -> {
            instance.getChannels().get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(OutboundTarget.ALLAROUNDPOINT);
            instance.getChannels().get(Side.SERVER)
                    .attr(FMLOutboundHandler.FML_MESSAGETARGETARGS)
                    .set(new NetworkRegistry.TargetPoint(world.provider.getDimension(), x, y, z, 192.0));
            instance.getChannels().get(Side.SERVER).writeAndFlush(message);
        });
        ci.cancel();
    }

    @Inject(method = "sendToDimension", at = @At("HEAD"), cancellable = true, remap = false)
    private static void injectSendToDimension(final PacketBase message, final int dimensionId, final CallbackInfo ci) {
        if (!FMLCommonHandler.instance().getMinecraftServerInstance().isCallingFromMinecraftThread()) {
            return;
        }
        AccessorPacketHandler instance = (AccessorPacketHandler) PacketHandler.INSTANCE;
        NovaEngineeringCore.PARALLEL_NETWORK_MANAGER.offerAction(PacketHandler.INSTANCE, () -> {
            instance.getChannels().get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(OutboundTarget.DIMENSION);
            instance.getChannels().get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(dimensionId);
            instance.getChannels().get(Side.SERVER).writeAndFlush(message);
        });
        ci.cancel();
    }

}
