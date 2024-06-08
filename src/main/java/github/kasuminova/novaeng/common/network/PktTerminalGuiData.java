package github.kasuminova.novaeng.common.network;

import github.kasuminova.novaeng.common.hypernet.ComputationCenter;
import github.kasuminova.novaeng.common.hypernet.Database;
import github.kasuminova.novaeng.common.hypernet.HyperNetTerminal;
import github.kasuminova.novaeng.common.hypernet.research.ResearchCognitionData;
import github.kasuminova.novaeng.common.hypernet.research.ResearchStation;
import github.kasuminova.novaeng.common.hypernet.research.ResearchStationType;
import github.kasuminova.novaeng.common.registry.RegistryHyperNet;
import github.kasuminova.novaeng.common.tile.TileHyperNetTerminal;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PktTerminalGuiData implements IMessage, IMessageHandler<PktTerminalGuiData, IMessage> {
    // Client Only Cache Data
    private static final List<ResearchCognitionData> UNLOCKED_DATA = new ArrayList<>();
    private static final Object2DoubleOpenHashMap<ResearchCognitionData> RESEARCHING_DATA = new Object2DoubleOpenHashMap<>();
    private static final List<Database.Status> DATABASES = new ArrayList<>();
    private static ResearchStationType researchStationType = null;
    
    private NBTTagCompound tag;

    // Server Only
    private TileHyperNetTerminal terminal = null;

    public PktTerminalGuiData() {

    }

    public PktTerminalGuiData(TileHyperNetTerminal terminal) {
        this.terminal = terminal;
    }

    public static List<ResearchCognitionData> getUnlockedData() {
        return UNLOCKED_DATA;
    }

    public static Object2DoubleOpenHashMap<ResearchCognitionData> getResearchingData() {
        return RESEARCHING_DATA;
    }

    public static List<Database.Status> getDatabases() {
        return DATABASES;
    }

    public static ResearchStationType getResearchStationType() {
        return researchStationType;
    }

    @Override
    public void fromBytes(final ByteBuf buf) {
        tag = ByteBufUtils.readTag(buf);
    }

    @Override
    public void toBytes(final ByteBuf buf) {
        NBTTagCompound tag = new NBTTagCompound();

        HyperNetTerminal node = terminal.getNodeProxy();
        ComputationCenter center = node.getCenter();
        if (!node.isConnected() || center == null) {
            ByteBufUtils.writeTag(buf, tag);
            return;
        }

        Collection<Database> databases = center.getNode(Database.class);

        Set<ResearchCognitionData> researchCognition = databases.stream()
                .flatMap(database -> database.getStoredResearchCognition().stream())
                .collect(Collectors.toSet());

        NBTTagList unlocked = new NBTTagList();
        researchCognition.stream()
                .map(data -> new NBTTagString(data.getResearchName()))
                .forEach(unlocked::appendTag);
        tag.setTag("unlockedData", unlocked);

        NBTTagList databasesTag = new NBTTagList();
        Object2DoubleOpenHashMap<String> researchingData = new Object2DoubleOpenHashMap<>();
        databases.forEach(database -> {
            databasesTag.appendTag(database.createStatus().writeToNBT());
            database.getAllResearchingCognition().forEach((research, progress) -> {
                String researchName = research.getResearchName();
                // 如有重复进度，取最大值。
                Double value = researchingData.computeIfPresent(researchName, (_k, v) -> v > progress ? v : progress);
                if (value == null) {
                    researchingData.put(researchName, progress.doubleValue());
                }
            });
        });
        tag.setTag("databases", databasesTag);

        NBTTagList researching = new NBTTagList();
        researchingData.forEach((key, value) -> {
            NBTTagCompound research = new NBTTagCompound();
            research.setString("researchName", key);
            research.setDouble("progress", value);
            researching.appendTag(research);
        });
        tag.setTag("researchingData", researching);

        Collection<ResearchStation> researchStations = center.getNode(ResearchStation.class);
        if (!researchStations.isEmpty()) {
            // 一个网络最多只能有一个研究站。
            ResearchStation station = researchStations.stream().findFirst().get();
            ResearchStationType type = station.getType();
            tag.setString("researchStationType", type.getTypeName());
        }

        ByteBufUtils.writeTag(buf, tag);
    }

    @Override
    public IMessage onMessage(final PktTerminalGuiData message, final MessageContext ctx) {
        if (message.tag == null) {
            return null;
        }
        if (FMLCommonHandler.instance().getSide().isClient()) {
            Minecraft.getMinecraft().addScheduledTask(() -> processPacket(message));
        }
        return null;
    }

    @SideOnly(Side.CLIENT)
    private static void processPacket(final PktTerminalGuiData message) {
        UNLOCKED_DATA.clear();
        RESEARCHING_DATA.clear();
        DATABASES.clear();

        NBTTagCompound tag = message.tag;

        NBTTagList unlocked = tag.getTagList("unlockedData", Constants.NBT.TAG_STRING);
        IntStream.range(0, unlocked.tagCount())
                .mapToObj(unlocked::getStringTagAt)
                .map(RegistryHyperNet::getResearchCognitionData)
                .filter(Objects::nonNull)
                .forEach(UNLOCKED_DATA::add);

        NBTTagList researching = tag.getTagList("researchingData", Constants.NBT.TAG_COMPOUND);
        IntStream.range(0, researching.tagCount()).mapToObj(researching::getCompoundTagAt).forEach(tagAt -> {
            String researchName = tagAt.getString("researchName");
            ResearchCognitionData data = RegistryHyperNet.getResearchCognitionData(researchName);
            if (data == null) {
                return;
            }
            RESEARCHING_DATA.put(data, tagAt.getDouble("progress"));
        });

        NBTTagList databases = tag.getTagList("databases", Constants.NBT.TAG_COMPOUND);
        IntStream.range(0, databases.tagCount())
                .mapToObj(databases::getCompoundTagAt)
                .map(Database.Status::readFromNBT)
                .filter(Objects::nonNull)
                .forEach(DATABASES::add);

        researchStationType = RegistryHyperNet.getResearchStationType(tag.getString("researchStationType"));
    }

}
