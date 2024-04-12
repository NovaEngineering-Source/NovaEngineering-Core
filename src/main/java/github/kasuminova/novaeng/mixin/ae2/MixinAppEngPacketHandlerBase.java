package github.kasuminova.novaeng.mixin.ae2;

import appeng.core.sync.AppEngPacket;
import appeng.core.sync.AppEngPacketHandlerBase;
import appeng.core.sync.network.AppEngServerPacketHandler;
import com.llamalad7.mixinextras.sugar.Local;
import github.kasuminova.novaeng.common.profiler.CPacketProfiler;
import github.kasuminova.novaeng.common.profiler.SPacketProfiler;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.lang.reflect.InvocationTargetException;

@Mixin(AppEngServerPacketHandler.class)
public class MixinAppEngPacketHandlerBase {

    @Redirect(
            method = "onPacketData",
            at = @At(
                    value = "INVOKE",
                    target = "Lappeng/core/sync/AppEngPacketHandlerBase$PacketTypes;parsePacket(Lio/netty/buffer/ByteBuf;)Lappeng/core/sync/AppEngPacket;",
                    remap = false
            ),
            remap = false
    )
    private AppEngPacket redirectParsePacket(final AppEngPacketHandlerBase.PacketTypes instance, 
                                             final ByteBuf in,
                                             @Local(name = "player") EntityPlayer player) throws InvocationTargetException, InstantiationException, IllegalAccessException
    {
        int prevIndex = in.readerIndex();
        AppEngPacket packet = instance.parsePacket(in);

        if (player != null) {
            SPacketProfiler.onPacketReceived(player, packet);
        } else {
            CPacketProfiler.onPacketReceived(packet, in.readerIndex() - prevIndex);
        }

        return packet;
    }

}
