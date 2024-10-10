package github.kasuminova.novaeng.common.command;

import com.mojang.authlib.GameProfile;
import github.kasuminova.novaeng.common.profiler.CPacketProfilerDataProcessor;
import github.kasuminova.novaeng.common.profiler.SPacketProfiler;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.FMLCommonHandler;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.List;

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
    public List<String> getTabCompletions(@Nonnull final MinecraftServer server, @Nonnull final ICommandSender sender, final String[] args, @Nullable final BlockPos targetPos) {
        if (args.length == 1) {
            return getListOfStringsMatchingLastWord(args, "full", "collectClient");
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("collectClient")) {
            return getListOfStringsMatchingLastWord(args, "10", "20", "30", "40", "50");
        }
        if (args.length == 3 && args[0].equalsIgnoreCase("collectClient")) {
            return getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
        }
        return super.getTabCompletions(server, sender, args, targetPos);
    }

    @Nonnull
    @Override
    public String getUsage(@Nonnull final ICommandSender sender) {
        return "Usage: /spacket_profiler [full|collectClient [limit] [target]]";
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
        if (args.length > 0 && args[0].equalsIgnoreCase("full")) {
            SPacketProfiler.getFullProfilerMessages().stream().map(TextComponentString::new).forEach(sender::sendMessage);
            return;
        }
        if (args.length > 0 && args[0].equalsIgnoreCase("collectClient")) {
            int limit = 10;
            if (args.length > 1) {
                try {
                    limit = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    sender.sendMessage(new TextComponentString(TextFormatting.RED + "需要一个数值！"));
                    return;
                }
            }
            GameProfile target = null;
            if (args.length > 2) {
                target = getPlayer(args[2]);
                if (target == null) {
                    sender.sendMessage(new TextComponentString(TextFormatting.RED + "目标玩家不存在！"));
                    return;
                }
            }
            CPacketProfilerDataProcessor.INSTANCE.create(sender, limit, target);
            return;
        }
        SPacketProfiler.getProfilerMessages().stream().map(TextComponentString::new).forEach(sender::sendMessage);
    }

    private static GameProfile getPlayer(final String name) {
        EntityPlayerMP player = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUsername(name);
        return player == null ? null : player.getGameProfile();
    }

}
