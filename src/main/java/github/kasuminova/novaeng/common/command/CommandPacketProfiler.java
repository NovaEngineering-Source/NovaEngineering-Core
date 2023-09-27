package github.kasuminova.novaeng.common.command;

import github.kasuminova.novaeng.common.profiler.PacketProfiler;
import github.kasuminova.novaeng.common.profiler.TEUpdatePacketProfiler;
import hellfirepvp.modularmachinery.common.util.MiscUtils;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Tuple;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CommandPacketProfiler extends CommandBase {
    public static final CommandPacketProfiler INSTANCE = new CommandPacketProfiler();

    private CommandPacketProfiler() {
    }

    @Nonnull
    @Override
    public String getName() {
        return "packet_profiler";
    }

    @Nonnull
    @Override
    public String getUsage(@Nonnull final ICommandSender sender) {
        return "Usage: /packet_profiler [reset]";
    }

    @Override
    @SuppressWarnings("SimplifyStreamApiCallChains")
    public void execute(@Nonnull final MinecraftServer server,
                        @Nonnull final ICommandSender sender,
                        @Nonnull final String[] args)
    {
        if (args.length >= 1) {
            switch (args[0]) {
                case "reset" -> {
                    PacketProfiler.PACKET_TOTAL_SIZE.clear();
                    PacketProfiler.profilerStartTime = System.currentTimeMillis();
                    TEUpdatePacketProfiler.TE_UPDATE_PACKET_TOTAL_SIZE.clear();
                    sender.sendMessage(new TextComponentString("Cleared packet profiler data."));
                    return;
                }
                case "start" -> {
                    PacketProfiler.enabled = true;
                    sender.sendMessage(new TextComponentString("Packet profiler is started."));
                    return;
                }
                case "stop" -> {
                    PacketProfiler.enabled = false;
                    sender.sendMessage(new TextComponentString("Packet profiler is stopped."));
                    return;
                }
            }
        }

        long totalPacketSize = PacketProfiler.PACKET_TOTAL_SIZE.values().stream()
                .mapToLong(Tuple::getSecond)
                .sum();
        long profileTimeExisted = System.currentTimeMillis() - PacketProfiler.profilerStartTime;
        int networkBandwidthPerSec = (int) ((totalPacketSize / (profileTimeExisted / 1000L)));

        List<Map.Entry<Class<?>, Tuple<Long, Long>>> sorted = PacketProfiler.PACKET_TOTAL_SIZE.entrySet().stream()
                .sorted((o1, o2) -> Long.compare(o2.getValue().getSecond(), o1.getValue().getSecond()))
                .limit(10)
                .collect(Collectors.toList());

        for (final Map.Entry<Class<?>, Tuple<Long, Long>> entry : sorted) {
            Class<?> pClass = entry.getKey();
            long packetTotalAmount = entry.getValue().getFirst();
            long packetTotalSize = entry.getValue().getSecond();

            sender.sendMessage(new TextComponentString(
                    String.format("Packet Class: %s, Total Packet Amount: %s",
                            TextFormatting.BLUE + pClass.getSimpleName() + TextFormatting.WHITE,
                            TextFormatting.GOLD + MiscUtils.formatDecimal(packetTotalAmount) + TextFormatting.WHITE)
            ));
            sender.sendMessage(new TextComponentString(
                    String.format("Total Packet Size: %s, Avg Packet Size: %s",
                            TextFormatting.RED + MiscUtils.formatNumber(packetTotalSize) + 'B' + TextFormatting.WHITE,
                            TextFormatting.YELLOW + MiscUtils.formatNumber(packetTotalSize / packetTotalAmount)) + 'B' + TextFormatting.WHITE
            ));
        }
        sender.sendMessage(new TextComponentString(String.format("Network BandWidth Per Second: %s",
                TextFormatting.GREEN + MiscUtils.formatNumber(networkBandwidthPerSec) + "B/s" + TextFormatting.WHITE)));

        sender.sendMessage(new TextComponentString("SPacketUpdateTileEntity stat:"));
        List<Map.Entry<Class<?>, Tuple<Long, Long>>> teSorted = TEUpdatePacketProfiler.TE_UPDATE_PACKET_TOTAL_SIZE.entrySet().stream()
                .sorted((o1, o2) -> Long.compare(o2.getValue().getSecond(), o1.getValue().getSecond()))
                .limit(10)
                .collect(Collectors.toList());

        for (final Map.Entry<Class<?>, Tuple<Long, Long>> entry : teSorted) {
            Class<?> tClass = entry.getKey();
            long packetTotalAmount = entry.getValue().getFirst();
            long packetTotalSize = entry.getValue().getSecond();

            sender.sendMessage(new TextComponentString(
                    String.format("TE Class: %s, TE Total Amount: %s",
                            TextFormatting.BLUE + tClass.getName() + TextFormatting.WHITE,
                            TextFormatting.GOLD + MiscUtils.formatDecimal(packetTotalAmount) + TextFormatting.WHITE)
            ));
            sender.sendMessage(new TextComponentString(
                    String.format("TE Update Packet Total Size: %s, TE Update Packet Avg Size: %s",
                            TextFormatting.RED + MiscUtils.formatNumber(packetTotalSize) + 'B' + TextFormatting.WHITE,
                            TextFormatting.YELLOW + MiscUtils.formatNumber(packetTotalSize / packetTotalAmount)) + 'B' + TextFormatting.WHITE
            ));
        }
    }
}
