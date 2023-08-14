package github.kasuminova.novaeng.common.network;

import github.kasuminova.novaeng.common.hypernet.ComputationCenter;
import github.kasuminova.novaeng.common.hypernet.ComputationCenterCache;
import github.kasuminova.novaeng.common.registry.RegistryHyperNet;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PktHyperNetStatus implements IMessage, IMessageHandler<PktHyperNetStatus, IMessage> {
    private NBTTagCompound statusTag = null;
    private ComputationCenter center = null;

    public PktHyperNetStatus() {
    }

    public PktHyperNetStatus(ComputationCenter center) {
        this.center = center;
    }

    @Override
    public void fromBytes(final ByteBuf buf) {
        this.statusTag = ByteBufUtils.readTag(buf);
    }

    @Override
    public void toBytes(final ByteBuf buf) {
        NBTTagCompound statusTag = new NBTTagCompound();

        statusTag.setString("centerType", center.getType().getTypeName());
        statusTag.setInteger("totalConnected", center.getConnectedMachineryCount());
        statusTag.setFloat("computationPointGeneration", center.getComputationPointGeneration());
        statusTag.setFloat("computationPointConsumption", center.getComputationPointConsumption());

        ByteBufUtils.writeTag(buf, statusTag);
    }

    @Override
    public IMessage onMessage(final PktHyperNetStatus message, final MessageContext ctx) {
        NBTTagCompound statusTag = message.statusTag;
        if (statusTag == null) {
            return null;
        }

        if (!statusTag.hasKey("centerType")
                || !statusTag.hasKey("totalConnected")
                || !statusTag.hasKey("computationPointGeneration")
                || !statusTag.hasKey("computationPointConsumption"))
        {
            return null;
        }

        ComputationCenterCache.setType(RegistryHyperNet.getComputationCenterType(statusTag.getString("centerType")));
        ComputationCenterCache.setTotalConnected(statusTag.getInteger("totalConnected"));
        ComputationCenterCache.setComputationPointGeneration(statusTag.getInteger("computationPointGeneration"));
        ComputationCenterCache.setComputationPointConsumption(statusTag.getInteger("computationPointConsumption"));
        return null;
    }

    public NBTTagCompound getStatusTag() {
        return statusTag;
    }
}
