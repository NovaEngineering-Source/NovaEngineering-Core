package github.kasuminova.novaeng.common.network;

import github.kasuminova.novaeng.common.container.ContainerHyperNetTerminal;
import github.kasuminova.novaeng.common.hypernet.old.ComputationCenter;
import github.kasuminova.novaeng.common.hypernet.old.Database;
import github.kasuminova.novaeng.common.hypernet.old.HyperNetTerminal;
import github.kasuminova.novaeng.common.hypernet.old.research.ResearchCognitionData;
import github.kasuminova.novaeng.common.hypernet.old.research.ResearchStation;
import github.kasuminova.novaeng.common.registry.RegistryHyperNet;
import github.kasuminova.novaeng.common.tile.TileHyperNetTerminal;
import hellfirepvp.modularmachinery.ModularMachinery;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.Collection;
import java.util.Optional;

public class PktResearchTaskProvideCreative implements IMessage, IMessageHandler<PktResearchTaskProvideCreative, IMessage> {

    private ResearchCognitionData researchTask = null;

    public PktResearchTaskProvideCreative() {
    }

    public PktResearchTaskProvideCreative(final ResearchCognitionData researchTask) {
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
    public IMessage onMessage(final PktResearchTaskProvideCreative message, final MessageContext ctx) {
        ResearchCognitionData researchTask = message.researchTask;

        if (researchTask == null) {
            return null;
        }

        EntityPlayerMP player = ctx.getServerHandler().player;
        Container container = player.openContainer;
        if (!(container instanceof ContainerHyperNetTerminal)) {
            return null;
        }
        if (!player.isCreative() || !player.canUseCommand(2, "")) {
            return null;
        }

        TileHyperNetTerminal terminal = ((ContainerHyperNetTerminal) container).getOwner();

        HyperNetTerminal nodeProxy = terminal.getNodeProxy();
        ComputationCenter center = nodeProxy.getCenter();
        if (center == null) {
            return null;
        }

        Collection<Database> databases = center.getNode(Database.class);
        dependency:
        for (final ResearchCognitionData dependency : researchTask.getDependencies()) {
            for (final Database database : databases) {
                if (database.hasResearchCognition(dependency)) {
                    continue dependency;
                }
            }
            return null;
        }

        Collection<ResearchStation> stations = center.getNode(ResearchStation.class);
        Optional<ResearchStation> first = stations.stream().findFirst();
        //noinspection SimplifyOptionalCallChains
        if (!first.isPresent()) {
            return null;
        }

        ResearchStation researchStation = first.get();

        ModularMachinery.EXECUTE_MANAGER.addSyncTask(() -> {
            researchStation.provideTask(researchTask, ctx.getServerHandler().player);
            researchStation.setCompletedPoints(researchTask.getRequiredPoints());
        });
        return null;
    }

}
