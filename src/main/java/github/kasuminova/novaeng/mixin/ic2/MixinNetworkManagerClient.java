package github.kasuminova.novaeng.mixin.ic2;

import com.llamalad7.mixinextras.sugar.Local;
import ic2.core.network.GrowingBuffer;
import ic2.core.network.NetworkManagerClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.zip.InflaterOutputStream;

@SuppressWarnings("MethodMayBeStatic")
@Mixin(NetworkManagerClient.class)
public class MixinNetworkManagerClient {

    @Unique
    private static final InflaterOutputStream STELLAR_CORE$UNUSED = new InflaterOutputStream(new ByteArrayOutputStream());

    @Redirect(
            method = "onPacketData",
            at = @At(
                    value = "NEW",
                    target = "(Ljava/io/OutputStream;)Ljava/util/zip/InflaterOutputStream;",
                    remap = false
            ),
            remap = false
    )
    private InflaterOutputStream redirectOnPacketDataNewInflaterInst(final OutputStream outputStream) {
        return STELLAR_CORE$UNUSED;
    }

    @Redirect(
            method = "onPacketData",
            at = @At(
                    value = "INVOKE",
                    target = "Lic2/core/network/GrowingBuffer;writeTo(Ljava/io/OutputStream;)V",
                    remap = false
            ),
            remap = false
    )
    private void redirectOnPacketDataWriteTo(final GrowingBuffer input, final OutputStream os, @Local(name = "decompBuffer") GrowingBuffer decompBuffer) {
        // direct write data, do not decompress.
        input.writeTo(decompBuffer);
    }

    @Redirect(
            method = "onPacketData",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/zip/InflaterOutputStream;close()V",
                    remap = false
            ),
            remap = false
    )
    private void redirectSendLargePacketDeflateClose(final InflaterOutputStream instance) {
        // do nothing
    }

}
