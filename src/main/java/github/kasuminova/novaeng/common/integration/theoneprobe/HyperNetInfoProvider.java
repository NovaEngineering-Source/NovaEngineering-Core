package github.kasuminova.novaeng.common.integration.theoneprobe;

import github.kasuminova.novaeng.NovaEngineeringCore;
import github.kasuminova.novaeng.common.crafttweaker.util.NovaEngUtils;
import github.kasuminova.novaeng.common.hypernet.old.*;
import github.kasuminova.novaeng.common.hypernet.old.research.ResearchCognitionData;
import github.kasuminova.novaeng.common.hypernet.old.research.ResearchStation;
import github.kasuminova.novaeng.common.registry.RegistryHyperNet;
import hellfirepvp.modularmachinery.common.machine.DynamicMachine;
import hellfirepvp.modularmachinery.common.tiles.base.TileMultiblockMachineController;
import hellfirepvp.modularmachinery.common.util.MiscUtils;
import mcjty.theoneprobe.api.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public class HyperNetInfoProvider implements IProbeInfoProvider {
    public static final HyperNetInfoProvider INSTANCE = new HyperNetInfoProvider();

    private HyperNetInfoProvider() {
    }

    @Override
    public String getID() {
        return NovaEngineeringCore.MOD_ID + ':' + "hypernet_info_provider";
    }

    @Override
    public void addProbeInfo(final ProbeMode probeMode,
                             final IProbeInfo probeInfo,
                             final EntityPlayer player,
                             final World world,
                             final IBlockState blockState,
                             final IProbeHitData data)
    {
        if (!blockState.getBlock().hasTileEntity(blockState)) {
            return;
        }

        TileEntity te = world.getTileEntity(data.getPos());
        if (!(te instanceof final TileMultiblockMachineController ctrl)) {
            return;
        }

        DynamicMachine foundMachine = ctrl.getFoundMachine();
        if (!RegistryHyperNet.isHyperNetSupported(foundMachine)) {
            return;
        }

        processHyperNetTOP(foundMachine, ctrl, probeInfo);
    }

    private static void processHyperNetTOP(final DynamicMachine foundMachine,
                                           final TileMultiblockMachineController ctrl,
                                           final IProbeInfo info)
    {
        ResourceLocation registryName = foundMachine.getRegistryName();
        if (RegistryHyperNet.isComputationCenter(registryName)) {
            ComputationCenter center = ComputationCenter.from(ctrl);
            if (center != null) {
                processCenterTOP(center, info);
            }
            return;
        }

        NetNode node = NetNodeCache.getCache(ctrl, RegistryHyperNet.getNodeType(foundMachine));
        if (node == null) {
            return;
        }

        processNetNodeTOP(node, info);
    }

    private static IProbeInfo newBox(final IProbeInfo info) {
        return info.horizontal(info.defaultLayoutStyle().borderColor(0x801E90FF));
    }

    private static void processNetNodeTOP(final NetNode node, final IProbeInfo probeInfo) {
        ComputationCenter center = node.getCenter();

        if (center != null) {
            probeInfo.text("{*top.hypernet.online*}");
        } else {
            probeInfo.text("{*top.hypernet.offline*}");
        }

        if (node instanceof DataProcessor) {
            IProbeInfo box = newBox(probeInfo);
            processDataProcessorTOP((DataProcessor) node, newVertical(box), newVertical(box));
        } else if (node instanceof ResearchStation) {
            IProbeInfo box = newBox(probeInfo);
            processResearchStationTOP((ResearchStation) node, newVertical(box), newVertical(box), probeInfo);
        } else if (node instanceof Database) {
            IProbeInfo box = newBox(probeInfo);
            processDatabaseTOP((Database) node, newVertical(box), newVertical(box));
        } else {
            processNetNodeTOPDefault(node, probeInfo);
        }

        if (center != null) {
            IProbeInfo box = newBox(probeInfo);
            processCenterStatusTOP(center, newVertical(box), newVertical(box));
        }
    }

    private static void processNetNodeTOPDefault(final NetNode node, final IProbeInfo probeInfo) {
        double consumption = node.getComputationPointConsumption();
        double provision = node.getComputationPointProvision(0xFFFFFF);
        if (consumption <= 0 && provision <= 0) {
            return;
        }

        IProbeInfo box = newBox(probeInfo);
        IProbeInfo leftInfo = newVertical(box);
        IProbeInfo rightInfo = newVertical(box);

        if (consumption > 0) {
            leftInfo.text("{*top.hypernet.computation_point_consumption*}");
            rightInfo.text(TextFormatting.AQUA + NovaEngUtils.formatFLOPS(consumption));
        }
        if (provision > 0) {
            leftInfo.text("{*top.hypernet.computation_point_generation*}" + provision);
            rightInfo.text(TextFormatting.AQUA + NovaEngUtils.formatFLOPS(provision));
        }
    }

    private static void processDataProcessorTOP(final DataProcessor processor,
                                                final IProbeInfo leftInfo,
                                                final IProbeInfo rightInfo)
    {
        double maxGeneration = processor.getMaxGeneration();
        double load = Math.min(processor.getComputationalLoad(), maxGeneration);
        float efficiency = processor.getEfficiency();
        int storedHU = processor.getStoredHU();
        float heatPercent = processor.getOverHeatPercent();

        leftInfo.text("{*top.hypernet.processor.generation*}");
        rightInfo.text(TextFormatting.AQUA + NovaEngUtils.formatFLOPS(load) +
                " / " + NovaEngUtils.formatFLOPS(maxGeneration));

        leftInfo.text("{*top.hypernet.processor.load*}");
        rightInfo.text(TextFormatting.AQUA + NovaEngUtils.formatPercent(load, maxGeneration));

        leftInfo.text("{*top.hypernet.processor.efficiency*}");
        rightInfo.text(TextFormatting.GREEN + NovaEngUtils.formatPercent(efficiency, 1.0F));

        leftInfo.text("{*top.hypernet.processor.heat*}");
        rightInfo.text(TextFormatting.RED + MiscUtils.formatDecimal(storedHU) + "HU (" +
                NovaEngUtils.formatPercent(heatPercent, 1.0F) + ")");
    }

    private static IProbeInfo newVertical(final IProbeInfo probeInfo) {
        return probeInfo.vertical(probeInfo.defaultLayoutStyle().spacing(0));
    }

    private static void processResearchStationTOP(final ResearchStation station,
                                                  final IProbeInfo leftInfo,
                                                  final IProbeInfo rightInfo,
                                                  final IProbeInfo probeInfo)
    {
        ResearchCognitionData researching = station.getCurrentResearching();
        if (researching == null) {
            leftInfo.text("{*top.hypernet.research_station.current.empty*}");
            return;
        }

        double consumption = station.getComputationPointConsumption();

        leftInfo.text("{*top.hypernet.computation_point_consumption*}");
        rightInfo.text(TextFormatting.AQUA + NovaEngUtils.formatFLOPS(consumption));

        leftInfo.text("{*top.hypernet.research_station.current*}");
        rightInfo.text(researching.getTranslatedName());

        double completedPoints = station.getCompletedPoints();
        double requiredPoints = researching.getRequiredPoints();

        float progress = (float) (completedPoints / requiredPoints) * 100;

        // Example: 3.955K / 90K (4.39%)
        String progressStr = NovaEngUtils.formatNumber(Math.round(completedPoints))
                + " / "
                + NovaEngUtils.formatNumber(Math.round(requiredPoints))
                + " (" + NovaEngUtils.formatFloat(progress, 2) + "%)";

        newBox(probeInfo).horizontal(probeInfo.defaultLayoutStyle().alignment(ElementAlignment.ALIGN_CENTER))
                .text(TextFormatting.AQUA + "{*top.hypernet.research_station.progress*}:  ")
                .progress(Math.round(progress), 100, probeInfo.defaultProgressStyle()
                        .prefix(progressStr)
                        .filledColor(0xCC1EB4FF)
                        .alternateFilledColor(0xCC1E90FF)
                        .borderColor(0xCC1E90B9)
                        .backgroundColor(0xFF000000)
                        .numberFormat(NumberFormat.NONE)
                        .width(120)
        );
    }

    private static void processDatabaseTOP(final Database database,
                                           final IProbeInfo leftInfo,
                                           final IProbeInfo rightInfo)
    {
        if (!database.isWorking()) {
            return;
        }

        int researching = database.getAllResearchingCognition().size();
        int size = database.getStoredResearchCognition().size();
        leftInfo.text("{*top.hypernet.database.stored.research*}");
        rightInfo.text(String.format("%s%s %s(%s) / %s%d",
                TextFormatting.AQUA, size, TextFormatting.YELLOW, researching, TextFormatting.YELLOW, database.getType().getMaxResearchDataStoreSize())
        );
    }

    private static void processCenterTOP(final ComputationCenter center, final IProbeInfo probeInfo)
    {
        if (center.isWorking()) {
            probeInfo.text("{*top.hypernet.online*}");
        } else {
            probeInfo.text("{*top.hypernet.offline*}");
            return;
        }

        IProbeInfo box = newBox(probeInfo);
        processCenterStatusTOP(center, newVertical(box), newVertical(box));
    }

    private static void processCenterStatusTOP(final ComputationCenter center,
                                               final IProbeInfo leftInfo,
                                               final IProbeInfo rightInfo)
    {
        int connections = center.getConnectedMachineryCount();
        int maxConnections = center.getType().getMaxConnections();
        leftInfo.text("{*top.hypernet.connected*}");
        rightInfo.text(TextFormatting.AQUA +
                String.valueOf(connections) + " / " + TextFormatting.YELLOW + maxConnections);

        double consumption = center.getComputationPointConsumption();
        double generation = center.getComputationPointGeneration();
        leftInfo.text("{*top.hypernet.computation_point.total*}");
        rightInfo.text(TextFormatting.AQUA +
                NovaEngUtils.formatFLOPS(consumption) + " / " + NovaEngUtils.formatFLOPS(generation));
    }
}
