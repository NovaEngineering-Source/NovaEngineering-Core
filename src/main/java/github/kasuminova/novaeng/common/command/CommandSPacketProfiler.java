package github.kasuminova.novaeng.common.command;

import github.kasuminova.novaeng.common.profiler.SPacketProfiler;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

import javax.annotation.Nonnull;

public class CommandSPacketProfiler extends CommandBase {
    public static final CommandSPacketProfiler INSTANCE = new CommandSPacketProfiler();

    private CommandSPacketProfiler() {
    }

    @Nonnull
    @Override
    public String getName() {
        return "spacket_profiler";
    }

    @Nonnull
    @Override
    public String getUsage(@Nonnull final ICommandSender sender) {
        return "Usage: /spacket_profiler";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public void execute(@Nonnull final MinecraftServer server,
                        @Nonnull final ICommandSender sender,
                        @Nonnull final String[] args)
    {
        SPacketProfiler.getProfilerMessages().stream().map(TextComponentString::new).forEach(sender::sendMessage);
    }

}
