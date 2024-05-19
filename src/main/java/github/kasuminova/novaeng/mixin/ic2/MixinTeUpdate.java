package github.kasuminova.novaeng.mixin.ic2;

import github.kasuminova.novaeng.NovaEngineeringCore;
import ic2.core.WorldData;
import ic2.core.network.NetworkManager;
import io.netty.util.internal.ThrowableUtil;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.network.FMLEventChannel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;

/**
 * 这个鬼东西是怎么工作的？？？
 */
@Mixin(targets = "ic2.core.network.TeUpdate")
public abstract class MixinTeUpdate {

    @Shadow(remap = false)
    public static void send(final WorldData worldData, final NetworkManager network) throws IOException {
    }

    @Unique
    private static volatile FMLEventChannel novaeng_core$channel = null;

    @Inject(method = "send", at = @At("HEAD"), cancellable = true, remap = false)
    private static void injectSend(final WorldData worldData, final NetworkManager network, final CallbackInfo ci) {
        if (!FMLCommonHandler.instance().getMinecraftServerInstance().isCallingFromMinecraftThread()) {
            return;
        }
        NovaEngineeringCore.PARALLEL_NETWORK_MANAGER.offerAction(novaeng_core$getChannel(), () -> {
            try {
                send(worldData, network);
            } catch (IOException e) {
                NovaEngineeringCore.log.warn(ThrowableUtil.stackTraceToString(e));
            }
        });
        ci.cancel();
    }

    @Unique
    private static FMLEventChannel novaeng_core$getChannel() {
        if (novaeng_core$channel == null) {
            synchronized (MixinTeUpdate.class) {
                if (novaeng_core$channel == null) {
                    novaeng_core$channel = ObfuscationReflectionHelper.getPrivateValue(NetworkManager.class, null, "channel");
                }
            }
        }
        return novaeng_core$channel;
    }

}
