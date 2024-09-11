package github.kasuminova.novaeng.common.network;

import github.kasuminova.novaeng.client.gui.toast.ResearchCompleteToast;
import github.kasuminova.novaeng.common.hypernet.old.research.ResearchCognitionData;
import github.kasuminova.novaeng.common.registry.RegistryHyperNet;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PktResearchTaskComplete implements IMessage, IMessageHandler<PktResearchTaskComplete, IMessage> {
    private ResearchCognitionData researchTask = null;

    public PktResearchTaskComplete() {
    }

    public PktResearchTaskComplete(ResearchCognitionData researchTask) {
        this.researchTask = researchTask;
    }

    @Override
    public void fromBytes(final ByteBuf buf) {
        NBTTagCompound tag = ByteBufUtils.readTag(buf);
        if (tag == null || !tag.hasKey("researchTask")) {
            return;
        }
        this.researchTask = RegistryHyperNet.getResearchCognitionData(tag.getString("researchTask"));
    }

    @Override
    public void toBytes(final ByteBuf buf) {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setString("researchTask", this.researchTask.getResearchName());
        ByteBufUtils.writeTag(buf, tag);
    }

    @Override
    public IMessage onMessage(final PktResearchTaskComplete message, final MessageContext ctx) {
        showToast(message.researchTask);
        return null;
    }

    @SideOnly(Side.CLIENT)
    private static void showToast(ResearchCognitionData researchData) {
        Minecraft.getMinecraft().getToastGui().add(new ResearchCompleteToast(researchData));
    }

}
