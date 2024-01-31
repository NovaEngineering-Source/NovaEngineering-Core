package github.kasuminova.novaeng.common.command;

import github.kasuminova.novaeng.client.handler.ClientEventHandler;
import github.kasuminova.novaeng.common.profiler.PacketProfiler;
import github.kasuminova.novaeng.common.profiler.TEUpdatePacketProfiler;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

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
    public List<String> getAliases() {
        return Arrays.asList("reset", "start", "stop", "debug_limit", "te_debug_limit");
    }

    @Nonnull
    @Override
    public List<String> getTabCompletions(@Nonnull final MinecraftServer server,
                                          @Nonnull final ICommandSender sender,
                                          @Nonnull final String[] args,
                                          @Nullable final BlockPos targetPos) {
        if (args.length == 1) {
            return getListOfStringsMatchingLastWord(args, "reset", "start", "stop", "debug_limit", "te_debug_limit");
        }
        return super.getTabCompletions(server, sender, args, targetPos);
    }

    @Nonnull
    @Override
    public String getUsage(@Nonnull final ICommandSender sender) {
        return "Usage: /packet_profiler [reset|start|stop|debug_limit <limit>|te_debug_limit <limit>]";
    }

    @Override
    public boolean checkPermission(@Nonnull final MinecraftServer server, @Nonnull final ICommandSender sender) {
        return sender instanceof EntityPlayer;
    }

    @Override
    public void execute(@Nonnull final MinecraftServer server,
                        @Nonnull final ICommandSender sender,
                        @Nonnull final String[] args) {
        if (args.length == 0) {
            for (final String message : PacketProfiler.getProfilerMessages(10)) {
                sender.sendMessage(new TextComponentString(message));
            }
            for (final String message : TEUpdatePacketProfiler.getProfilerMessages(10)) {
                sender.sendMessage(new TextComponentString(message));
            }
            return;
        }

        if (args.length >= 2) {
            try {
                switch (args[0]) {
                    case "debug_limit" -> ClientEventHandler.debugPacketProfilerMessageLimit = Integer.parseInt(args[1]);
                    case "te_debug_limit" -> ClientEventHandler.debugTEPacketProfilerMessageLimit = Integer.parseInt(args[1]);
                }
            } catch (Exception e) {
                sender.sendMessage(new TextComponentString("Invalid parameter " + args[1]));
            }
        }

        switch (args[0]) {
            case "reset" -> {
                PacketProfiler.PACKET_TOTAL_SIZE.clear();
                PacketProfiler.TOTAL_RECEIVED_DATA_SIZE.set(0);
                PacketProfiler.profilerStartTime = System.currentTimeMillis();
                TEUpdatePacketProfiler.TE_UPDATE_PACKET_TOTAL_SIZE.clear();
                sender.sendMessage(new TextComponentString("Cleared packet profiler data."));
            }
            case "start" -> {
                PacketProfiler.enabled = true;
                PacketProfiler.profilerStartTime += System.currentTimeMillis() - PacketProfiler.profilerStopTime;
                sender.sendMessage(new TextComponentString("Packet profiler is started."));
            }
            case "stop" -> {
                PacketProfiler.enabled = false;
                PacketProfiler.profilerStopTime = System.currentTimeMillis();
                sender.sendMessage(new TextComponentString("Packet profiler is stopped."));
            }
        }
    }
}
