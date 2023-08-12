package github.kasuminova.novaeng.common.network;

import github.kasuminova.novaeng.common.hypernet.ComputationCenter;
import github.kasuminova.novaeng.common.hypernet.Database;
import github.kasuminova.novaeng.common.hypernet.HyperNetTerminal;
import github.kasuminova.novaeng.common.hypernet.research.ResearchCognitionData;
import github.kasuminova.novaeng.common.registry.RegistryHyperNet;
import github.kasuminova.novaeng.common.tile.TileHyperNetTerminal;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PktTerminalGuiData implements IMessage, IMessageHandler<PktTerminalGuiData, IMessage> {
    private static final List<ResearchCognitionData> UNLOCKED_DATA = new ArrayList<>();
    private static final Object2DoubleOpenHashMap<ResearchCognitionData> RESEARCHING_DATA = new Object2DoubleOpenHashMap<>();

    // Client Only
    private final List<ResearchCognitionData> unlockedData = new ArrayList<>();
    private final Object2DoubleOpenHashMap<ResearchCognitionData> researchingData = new Object2DoubleOpenHashMap<>();

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

    @Override
    public void fromBytes(final ByteBuf buf) {
        unlockedData.clear();

        NBTTagCompound tag = ByteBufUtils.readTag(buf);
        if (tag == null) {
            return;
        }

        NBTTagList unlocked = tag.getTagList("unlockedData", Constants.NBT.TAG_STRING);
        IntStream.range(0, unlocked.tagCount())
                .mapToObj(unlocked::getStringTagAt)
                .map(RegistryHyperNet::getResearchCognitionData)
                .filter(Objects::nonNull)
                .forEach(unlockedData::add);

        NBTTagList researching = tag.getTagList("researchingData", Constants.NBT.TAG_COMPOUND);
        IntStream.range(0, researching.tagCount()).mapToObj(researching::getCompoundTagAt).forEach(tagAt -> {
            String researchName = tagAt.getString("researchName");
            ResearchCognitionData data = RegistryHyperNet.getResearchCognitionData(researchName);
            if (data == null) {
                return;
            }
            researchingData.put(data, tagAt.getDouble("progress"));
        });
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

        Set<String> researchCognition = databases.stream()
                .flatMap(database -> database.getStoredResearchCognition().stream())
                .collect(Collectors.toSet());

        NBTTagList unlocked = new NBTTagList();
        researchCognition.stream().map(NBTTagString::new).forEach(unlocked::appendTag);
        tag.setTag("unlockedData", unlocked);

        Object2DoubleOpenHashMap<String> researchingData = new Object2DoubleOpenHashMap<>();
        databases.forEach(database -> database.getAllResearchingCognition()
                .forEach((researchName, progress) -> researchingData.put(researchName, progress.doubleValue())));
        NBTTagList researching = new NBTTagList();
        researchingData.forEach((key, value) -> {
            NBTTagCompound research = new NBTTagCompound();
            research.setString("researchName", key);
            research.setDouble("progress", value);
            researching.appendTag(research);
        });
        tag.setTag("researchingData", researching);

        ByteBufUtils.writeTag(buf, tag);
    }

    @Override
    public IMessage onMessage(final PktTerminalGuiData message, final MessageContext ctx) {
        UNLOCKED_DATA.clear();
        UNLOCKED_DATA.addAll(message.unlockedData);
        RESEARCHING_DATA.clear();
        RESEARCHING_DATA.putAll(message.researchingData);
        return null;
    }
}
