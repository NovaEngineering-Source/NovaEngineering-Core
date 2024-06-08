package github.kasuminova.novaeng.mixin.ic2;

import com.llamalad7.mixinextras.sugar.Local;
import ic2.core.network.GrowingBuffer;
import ic2.core.network.NetworkManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.zip.DeflaterOutputStream;

/**
 * 这个鬼东西是怎么工作的？？？
 */
@SuppressWarnings("MethodMayBeStatic")
@Mixin(NetworkManager.class)
public class MixinNetworkManager {

    @Unique
    private static final DeflaterOutputStream STELLAR_CORE$UNUSED = new DeflaterOutputStream(new ByteArrayOutputStream());

    @Redirect(
            method = "sendLargePacket",
            at = @At(
                    value = "NEW",
                    target = "(Ljava/io/OutputStream;)Ljava/util/zip/DeflaterOutputStream;",
                    remap = false
            ),
            remap = false
    )
    private DeflaterOutputStream redirectSendLargePacketNewDeflaterInst(final OutputStream outputStream) {
        return STELLAR_CORE$UNUSED;
    }

    @Redirect(
            method = "sendLargePacket",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/zip/DeflaterOutputStream;close()V",
                    remap = false
            ),
            remap = false
    )
    private void redirectSendLargePacketDeflateClose(final DeflaterOutputStream instance) {
        // do nothing
    }

    @Redirect(
            method = "sendLargePacket",
            at = @At(
                    value = "INVOKE",
                    target = "Lic2/core/network/GrowingBuffer;writeTo(Ljava/io/OutputStream;)V",
                    remap = false
            ),
            remap = false
    )
    private void redirectSendLargePacketDataWriteTo(final GrowingBuffer data, final OutputStream os, @Local(name = "buffer") GrowingBuffer buffer) {
        // direct write data, do not compress.
        data.writeTo(buffer);
    }

}
