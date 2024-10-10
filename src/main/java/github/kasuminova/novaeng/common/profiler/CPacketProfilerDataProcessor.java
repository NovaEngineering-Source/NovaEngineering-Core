package github.kasuminova.novaeng.common.profiler;

import com.github.bsideup.jabel.Desugar;
import com.mojang.authlib.GameProfile;
import github.kasuminova.novaeng.NovaEngineeringCore;
import github.kasuminova.novaeng.common.handler.HyperNetEventHandler;
import github.kasuminova.novaeng.common.network.packetprofiler.PktCProfilerRequest;
import hellfirepvp.modularmachinery.common.util.MiscUtils;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.FMLCommonHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

public class CPacketProfilerDataProcessor {

    public static final CPacketProfilerDataProcessor INSTANCE = new CPacketProfilerDataProcessor();

    private final Map<GameProfile, CPacketProfilerData> receivedData = new ConcurrentHashMap<>();

    private ICommandSender sender = null;
    private UUID currentEvent = null;
    private GameProfile target = null;

    private int limit = 0;
    private long startTime = 0;

    private int players = 0;
    private int receivedPlayers = 0;

    private Future<Void> task = null;

    private CPacketProfilerDataProcessor() {}

    public void create(final ICommandSender sender, final int limit, @Nullable GameProfile target) {
        if (this.currentEvent != null) {
            NovaEngineeringCore.log.warn("Profiler collect task is already running, event ID: {}", currentEvent);
            sender.sendMessage(new TextComponentString(TextFormatting.RED + "已存在一个收集任务！事件 ID: " + TextFormatting.YELLOW + currentEvent));
            return;
        }
        List<EntityPlayerMP> players = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayers();
        if (players.isEmpty()) {
            sender.sendMessage(new TextComponentString(TextFormatting.RED + "无玩家在线，无法创建收集任务！"));
            return;
        }

        this.sender = sender;
        this.currentEvent = UUID.randomUUID();
        this.target = target;
        this.limit = limit;
        this.startTime = System.currentTimeMillis();
        requestPlayers(players, limit);
        createTask();
        this.sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "收集任务已创建，事件 ID: " + TextFormatting.YELLOW + currentEvent));
    }

    private void requestPlayers(List<EntityPlayerMP> players, final int limit) {
        this.players = players.size();
        this.receivedPlayers = 0;
        players.forEach(player -> NovaEngineeringCore.NET_CHANNEL.sendTo(new PktCProfilerRequest(currentEvent, limit), player));
    }

    @SuppressWarnings("BusyWait")
    private void createTask() {
        task = CompletableFuture.runAsync(() -> {
            while (receivedPlayers < players && System.currentTimeMillis() - startTime < 5000) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ignored) {}
            }
            if (receivedPlayers < players) {
                NovaEngineeringCore.log.warn("Profiler collect task timeout ({}ms), received players: {}, players: {}", System.currentTimeMillis() - startTime, receivedPlayers, players);
            } else {
                NovaEngineeringCore.log.info("Profiler collect task completed ({}ms), received players: {}, players: {}", System.currentTimeMillis() - startTime, receivedPlayers, players);
            }
            ProcessedData result = getProcessedData();
            HyperNetEventHandler.addTickEndAction(() -> finish(result));
        });
    }

    private void finish(ProcessedData result) {
        Map<String, CPacketProfilerData.PacketData> mergedPackets;
        Map<String, CPacketProfilerData.PacketData> mergedTileEntityPackets;

        mergedPackets = result.mergedPackets().entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .collect(LinkedHashMap::new, (map, entry) -> map.put(entry.getKey(), entry.getValue()), Map::putAll);
        mergedTileEntityPackets = result.mergedTileEntityPackets().entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .collect(LinkedHashMap::new, (map, entry) -> map.put(entry.getKey(), entry.getValue()), Map::putAll);

        List<ITextComponent> messages = new ArrayList<>();

        messages.add(new TextComponentString(TextFormatting.GREEN + "收集任务完成，事件 ID: " + TextFormatting.YELLOW + currentEvent));
        messages.add(new TextComponentString(TextFormatting.GREEN + "已收集数据: " + TextFormatting.YELLOW + receivedPlayers + "/" + players));
        messages.add(new TextComponentString(TextFormatting.GREEN + "总带宽使用: ~" + TextFormatting.AQUA + MiscUtils.formatNumber((long) result.totalBandwidthPerSecond()) + "B/s"));
        if (target == null) {
            generateDefaultMessage(messages, (long) result.maxBandwidthPerSecond(), result.maxPlayer(), mergedPackets, mergedTileEntityPackets);
        } else {
            generateTargetMessage(messages, target, mergedPackets, mergedTileEntityPackets);
        }

        messages.forEach(message -> sender.sendMessage(message));
        reset();
    }

    private void reset() {
        receivedData.clear();

        sender = null;
        currentEvent = null;
        target = null;

        limit = 0;
        startTime = 0;

        receivedPlayers = 0;
        players = 0;
        task = null;
    }

    @Nonnull
    private ProcessedData getProcessedData() {
        Map<GameProfile, CPacketProfilerData> sorted = receivedData.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .collect(LinkedHashMap::new, (map, entry) -> map.put(entry.getKey(), entry.getValue()), Map::putAll);

        double totalBandwidthPerSecond = sorted.values().stream()
                .mapToDouble(CPacketProfilerData::getNetworkBandwidthPerSecond)
                .sum();

        GameProfile maxPlayer = sorted.entrySet().stream()
                .max(Comparator.comparingDouble(entry -> entry.getValue().getNetworkBandwidthPerSecond()))
                .map(Map.Entry::getKey)
                .orElse(null);
        assert maxPlayer != null;
        double maxBandwidthPerSecond = sorted.values().stream()
                .mapToDouble(CPacketProfilerData::getNetworkBandwidthPerSecond)
                .max()
                .orElse(0);

        Map<String, CPacketProfilerData.PacketData> mergedPackets = new HashMap<>();
        Map<String, CPacketProfilerData.PacketData> mergedTileEntityPackets = new HashMap<>();

        for (CPacketProfilerData data : sorted.values()) {
            final Map<String, CPacketProfilerData.PacketData> finalMergedPackets = mergedPackets;
            data.getPackets().forEach((packetName, packetData) -> {
                CPacketProfilerData.PacketData mergedPacketData = finalMergedPackets.get(packetName);
                if (mergedPacketData == null) {
                    finalMergedPackets.put(packetName, packetData);
                } else {
                    mergedPacketData.merge(packetData);
                }
            });
            final Map<String, CPacketProfilerData.PacketData> finalMergedTileEntityPackets = mergedTileEntityPackets;
            data.getTileEntityPackets().forEach((packetName, packetData) -> {
                CPacketProfilerData.PacketData mergedPacketData = finalMergedTileEntityPackets.get(packetName);
                if (mergedPacketData == null) {
                    finalMergedTileEntityPackets.put(packetName, packetData);
                } else {
                    mergedPacketData.merge(packetData);
                }
            });
        }
        return new ProcessedData(totalBandwidthPerSecond, maxPlayer, maxBandwidthPerSecond, mergedPackets, mergedTileEntityPackets);
    }

    private static void generateDefaultMessage(final List<ITextComponent> messages, final long maxBandwidthPerSecond, final GameProfile maxPlayer, final Map<String, CPacketProfilerData.PacketData> mergedPackets, final Map<String, CPacketProfilerData.PacketData> mergedTileEntityPackets) {
        messages.add(new TextComponentString(TextFormatting.GREEN + "最大带宽使用: ~" + TextFormatting.AQUA + MiscUtils.formatNumber(maxBandwidthPerSecond) + "B/s" + TextFormatting.GREEN + "，来自: " + TextFormatting.YELLOW + maxPlayer.getName()));
        messages.add(new TextComponentString(TextFormatting.GREEN + "合并后数据: "));
        messages.add(new TextComponentString(TextFormatting.GREEN + "普通数据包: "));
        generatePktMessage(messages, mergedPackets);
        messages.add(new TextComponentString(TextFormatting.GREEN + "TileEntity 数据包: "));
        generatePktMessage(messages, mergedTileEntityPackets);
    }

    private static void generatePktMessage(final List<ITextComponent> messages, final Map<String, CPacketProfilerData.PacketData> mergedTileEntityPackets) {
        for (Map.Entry<String, CPacketProfilerData.PacketData> entry : mergedTileEntityPackets.entrySet()) {
            messages.add(new TextComponentString(
                    "PktClass: " +
                    TextFormatting.GOLD + entry.getKey() + TextFormatting.RESET + ": " +
                    TextFormatting.RED + MiscUtils.formatNumber(entry.getValue().totalSize()) + 'B' +
                    TextFormatting.WHITE + ", PktCnt: " + TextFormatting.AQUA + entry.getValue().count() +
                    TextFormatting.WHITE + ", SizeAvg: " + TextFormatting.YELLOW + MiscUtils.formatNumber(entry.getValue().totalSize() / entry.getValue().count()) + 'B'
            ));
        }
    }

    private static void generateTargetMessage(final List<ITextComponent> messages, final GameProfile target, final Map<String, CPacketProfilerData.PacketData> mergedPackets, final Map<String, CPacketProfilerData.PacketData> mergedTileEntityPackets) {
        messages.add(new TextComponentString(TextFormatting.GREEN + "目标玩家: " + TextFormatting.YELLOW + target.getName()));
        messages.add(new TextComponentString(TextFormatting.GREEN + "合并后数据: "));
        messages.add(new TextComponentString(TextFormatting.GREEN + "普通数据包: "));
        generatePktMessage(messages, mergedPackets);
        messages.add(new TextComponentString(TextFormatting.GREEN + "TileEntity 数据包: "));
        generatePktMessage(messages, mergedTileEntityPackets);
    }

    public void receive(final UUID eventId, final GameProfile player, final CPacketProfilerData data) {
        if (currentEvent == null) {
            return;
        }
        if (!currentEvent.equals(eventId)) {
            NovaEngineeringCore.log.warn("Received profiler data from {} with wrong eventId!", player.getName());
            return;
        }
        synchronized (receivedData) {
            receivedPlayers++;
            receivedData.put(player, data);
        }
        NovaEngineeringCore.log.info("Received profiler data from {}", player.getName());
    }

    @Desugar
    private record ProcessedData(double totalBandwidthPerSecond, GameProfile maxPlayer, double maxBandwidthPerSecond, Map<String, CPacketProfilerData.PacketData> mergedPackets, Map<String, CPacketProfilerData.PacketData> mergedTileEntityPackets) {
    }

}
