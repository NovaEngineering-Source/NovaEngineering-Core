package github.kasuminova.novaeng.common.command;

import com.github.bsideup.jabel.Desugar;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import github.kasuminova.novaeng.NovaEngineeringCore;
import github.kasuminova.novaeng.common.hypernet.research.SimpleResearchData;
import github.kasuminova.novaeng.common.registry.RegistryHyperNet;
import io.netty.util.internal.ThrowableUtil;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import org.apache.commons.io.FileUtils;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

public class ExportResearchDataToJson extends CommandBase {
    public static final ExportResearchDataToJson INSTANCE = new ExportResearchDataToJson();

    private ExportResearchDataToJson() {

    }

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    @Nonnull
    @Override
    public String getName() {
        return "export_research_data_to_json";
    }

    @Nonnull
    @Override
    public String getUsage(@Nonnull final ICommandSender sender) {
        return "Usage: /export_research_data_to_json";
    }

    @Override
    public void execute(@Nonnull final MinecraftServer server,
                        @Nonnull final ICommandSender sender,
                        @Nonnull final String[] args)
    {
        List<SimpleResearchData> dataList = RegistryHyperNet.getAllResearchCognitionData().stream()
                .map(SimpleResearchData::of)
                .collect(Collectors.toList());
        String jsonString = GSON.toJson(new ToSerialize(dataList));

        try {
            FileUtils.write(new File("./hypernet_research_data.json"), jsonString, StandardCharsets.UTF_8);
            sender.sendMessage(new TextComponentString(
                    TextFormatting.GREEN + "Successfully export research data to hypernet_research_data.json!"));
        } catch (IOException e) {
            NovaEngineeringCore.log.warn(ThrowableUtil.stackTraceToString(e));
            sender.sendMessage(new TextComponentString(
                    TextFormatting.RED + "Failed to export research data!"));
        }
    }

    @Desugar
    private record ToSerialize(List<SimpleResearchData> dataList) {
    }

}
