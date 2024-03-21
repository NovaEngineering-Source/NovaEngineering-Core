package github.kasuminova.novaeng.mixin.ic2;

import ic2.core.network.GrowingBuffer;
import ic2.core.network.NetworkManager;
import net.minecraft.entity.player.EntityPlayerMP;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(NetworkManager.class)
public interface InvokerNetworkManager {

    @Invoker(remap = false)
    void invokeSendLargePacket(final EntityPlayerMP player, final int state, final GrowingBuffer growingBuffer);

}
