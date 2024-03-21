package github.kasuminova.novaeng.mixin.cofh;

import cofh.core.network.PacketHandler;
import net.minecraftforge.fml.common.network.FMLEmbeddedChannel;
import net.minecraftforge.fml.relauncher.Side;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.EnumMap;

@Mixin(PacketHandler.class)
public interface AccessorPacketHandler {

    @Accessor(remap = false)
    EnumMap<Side, FMLEmbeddedChannel> getChannels();

}
