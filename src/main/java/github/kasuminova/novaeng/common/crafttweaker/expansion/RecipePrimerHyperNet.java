package github.kasuminova.novaeng.common.crafttweaker.expansion;

import crafttweaker.annotations.ZenRegister;
import github.kasuminova.novaeng.common.crafttweaker.util.NovaEngUtils;
import github.kasuminova.novaeng.common.hypernet.old.NetNodeCache;
import github.kasuminova.novaeng.common.hypernet.old.NetNodeImpl;
import github.kasuminova.novaeng.common.hypernet.old.research.ResearchCognitionData;
import github.kasuminova.novaeng.common.registry.RegistryHyperNet;
import hellfirepvp.modularmachinery.common.integration.crafttweaker.RecipePrimer;
import hellfirepvp.modularmachinery.common.tiles.base.TileMultiblockMachineController;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.FMLCommonHandler;
import stanhebben.zenscript.annotations.ZenExpansion;
import stanhebben.zenscript.annotations.ZenMethod;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

@ZenRegister
@ZenExpansion("mods.modularmachinery.RecipePrimer")
public class RecipePrimerHyperNet {

    /**
     * 为一个配方添加算力要求。
     * 用法：{@code requireComputationPoint(1.0F);}
     */
    @ZenMethod
    public static RecipePrimer requireComputationPoint(final RecipePrimer primer,
                                                       final float required)
    {
        return requireComputationPoint(primer, required, false);
    }

    @ZenMethod
    public static RecipePrimer requireComputationPoint(final RecipePrimer primer,
                                                       final float required,
                                                       final boolean triggerFailure)
    {
        if (FMLCommonHandler.instance().getSide().isClient()) {
            primer.addRecipeTooltip(
                    I18n.format("novaeng.hypernet.computation_point_required.tip",
                            NovaEngUtils.formatFLOPS(required)).intern()
            );
        }

        return primer.addPreCheckHandler(event -> {
            TileMultiblockMachineController ctrl = event.getController();
            NetNodeImpl node = NetNodeCache.getCache(ctrl, NetNodeImpl.class);
            if (node != null) {
                node.checkComputationPoint(event, required);
            }
        }).addStartHandler(event -> {
            TileMultiblockMachineController ctrl = event.getController();
            NetNodeImpl node = NetNodeCache.getCache(ctrl, NetNodeImpl.class);
            if (node != null) {
                node.onRecipeStart(event, required);
            }
        }).addFactoryStartHandler(event -> {
            TileMultiblockMachineController ctrl = event.getController();
            NetNodeImpl node = NetNodeCache.getCache(ctrl, NetNodeImpl.class);
            if (node != null) {
                node.onRecipeStart(event, required);
            }
        }).addPreTickHandler(event -> {
            TileMultiblockMachineController ctrl = event.getController();
            NetNodeImpl node = NetNodeCache.getCache(ctrl, NetNodeImpl.class);
            if (node != null) {
                node.onRecipePreTick(event, required, triggerFailure);
            }
        }).addFactoryPreTickHandler(event -> {
            TileMultiblockMachineController ctrl = event.getController();
            NetNodeImpl node = NetNodeCache.getCache(ctrl, NetNodeImpl.class);
            if (node != null) {
                node.onRecipePreTick(event, required, triggerFailure);
            }
        }).addFactoryFinishHandler(event -> {
            TileMultiblockMachineController ctrl = event.getController();
            NetNodeImpl node = NetNodeCache.getCache(ctrl, NetNodeImpl.class);
            if (node != null) {
                node.onRecipeFinished(event.getRecipeThread());
            }
        }).addFinishHandler(event -> {
            TileMultiblockMachineController ctrl = event.getController();
            NetNodeImpl node = NetNodeCache.getCache(ctrl, NetNodeImpl.class);
            if (node != null) {
                node.onRecipeFinished(event.getRecipeThread());
            }
        });
    }

    /**
     * 为一个配方添加研究认知要求。
     * 用法：{@code requireResearch("research_name_a", "research_name_b")}
     */
    @ZenMethod
    public static RecipePrimer requireResearch(final RecipePrimer primer,
                                               final String... researchNames)
    {
        return requireResearch(primer, Arrays.stream(researchNames)
                .map(RegistryHyperNet::getResearchCognitionData)
                .filter(Objects::nonNull)
                .toArray(ResearchCognitionData[]::new));
    }

    @ZenMethod
    public static RecipePrimer requireResearch(final RecipePrimer primer,
                                               final ResearchCognitionData... researchRequired)
    {
        if (FMLCommonHandler.instance().getSide().isClient()) {
            String researchTip = Arrays.stream(researchRequired)
                    .map(ResearchCognitionData::getTranslatedName)
                    .collect(Collectors.joining(TextFormatting.RESET + ", "));
            primer.addRecipeTooltip(I18n.format("novaeng.hypernet.research_required.tip", researchTip).intern());
        }

        return primer.addPostCheckHandler(event -> {
            NetNodeImpl cache = NetNodeCache.getCache(event.getController(), NetNodeImpl.class);
            if (cache != null) {
                cache.checkResearch(event, researchRequired);
            }
        });
    }
}
