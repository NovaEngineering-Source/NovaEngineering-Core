package github.kasuminova.novaeng.common.hypernet.old;

import crafttweaker.annotations.ZenRegister;
import github.kasuminova.mmce.common.event.recipe.*;
import github.kasuminova.novaeng.common.crafttweaker.util.NovaEngUtils;
import github.kasuminova.novaeng.common.hypernet.old.research.ResearchCognitionData;
import hellfirepvp.modularmachinery.common.machine.RecipeThread;
import hellfirepvp.modularmachinery.common.tiles.base.TileMultiblockMachineController;
import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import net.minecraft.nbt.NBTTagCompound;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import java.util.Collection;

@ZenRegister
@ZenClass("novaeng.hypernet.NetNodeImpl")
public class NetNodeImpl extends NetNode {
    protected final Object2DoubleMap<RecipeThread> recipeConsumers = new Object2DoubleOpenHashMap<>();
    protected double computationPointConsumption = 0;

    public NetNodeImpl(final TileMultiblockMachineController owner) {
        super(owner);
    }

    @Override
    public void onMachineTick() {
        super.onMachineTick();
        if (isWorking()) {
            if (owner.getTicksExisted() % 10 == 0) {
                double total = 0;
                for (final double value : recipeConsumers.values()) {
                    total += value;
                }
                computationPointConsumption = total;
                recipeConsumers.clear();
            }
        } else {
            computationPointConsumption = 0;
        }
    }

    @ZenMethod
    public void checkComputationPoint(final RecipeCheckEvent event,
                                      final double pointRequired,
                                      final ResearchCognitionData... researchRequired)
    {
        if (centerPos == null || center == null) {
            event.setFailed("未连接至计算网络！");
            return;
        }

        double generation = center.getComputationPointGeneration() - center.getComputationPointConsumption();
        if (generation < pointRequired) {
            event.setFailed("算力不足！预期："
                    + NovaEngUtils.formatFLOPS(pointRequired) + "，当前："
                    + NovaEngUtils.formatFLOPS(generation));
            return;
        }

        int currentParallelism = event.getActiveRecipe().getMaxParallelism();
        if (currentParallelism > 1) {
            int max = (int) Math.min(currentParallelism, generation / pointRequired);
            event.getActiveRecipe().setMaxParallelism(max);
        }
    }

    @ZenMethod
    public void checkResearch(final RecipeCheckEvent event,
                              final ResearchCognitionData... researchRequired)
    {
        if (centerPos == null || center == null) {
            event.setFailed("未连接至计算网络！");
            return;
        }

        Collection<Database> nodes = center.getNode(Database.class);
        if (nodes.isEmpty()) {
            event.setFailed("计算网络中未找到数据库！");
            return;
        }

        for (ResearchCognitionData researchCognitionData : researchRequired) {
            if (nodes.stream().noneMatch(database -> database.hasResearchCognition(researchCognitionData))) {
                event.setFailed("缺失研究：" + researchCognitionData.getTranslatedName() + "！");
                break;
            }
        }
    }

    public void onRecipeStart(final RecipeStartEvent event, final double computation) {
        recipeConsumers.put(event.getRecipeThread(), computation * event.getActiveRecipe().getParallelism());
    }

    public void onRecipeStart(final FactoryRecipeStartEvent event, final double computation) {
        recipeConsumers.put(event.getRecipeThread(), computation * event.getActiveRecipe().getParallelism());
    }

    public void onRecipePreTick(final RecipeTickEvent event, final double computation, final boolean triggerFailure) {
        if (centerPos == null) {
            event.setFailed(true, "未连接至计算网络！");
            return;
        }
        if (center == null) {
            event.preventProgressing("未连接至计算网络！");
            return;
        }
        double required = computation * event.getActiveRecipe().getParallelism();
        recipeConsumers.put(event.getRecipeThread(), required);

        double consumed = center.consumeComputationPoint(required);
        if (consumed < required) {
            String failureMessage = String.format("算力不足！预期：%s，当前：%s",
                    NovaEngUtils.formatFLOPS(required), NovaEngUtils.formatFLOPS(consumed));

            if (triggerFailure) {
                event.setFailed(event.getActiveRecipe().getRecipe().doesCancelRecipeOnPerTickFailure(), failureMessage);
            } else {
                event.preventProgressing(failureMessage);
            }
        }
    }

    public void onRecipePreTick(final FactoryRecipeTickEvent event, final double computation, final boolean triggerFailure) {
        if (centerPos == null) {
            event.setFailed(true, "未连接至计算网络！");
            return;
        }
        if (center == null) {
            event.preventProgressing("未连接至计算网络！");
            return;
        }
        double required = computation * event.getActiveRecipe().getParallelism();
        recipeConsumers.put(event.getRecipeThread(), required);

        double consumed = center.consumeComputationPoint(required);
        if (consumed < required) {
            String failureMessage = String.format("算力不足！预期：%s，当前：%s",
                    NovaEngUtils.formatFLOPS(required), NovaEngUtils.formatFLOPS(consumed));

            if (triggerFailure) {
                event.setFailed(event.getActiveRecipe().getRecipe().doesCancelRecipeOnPerTickFailure(), failureMessage);
            } else {
                event.preventProgressing(failureMessage);
            }
        }
    }

    public void onRecipeFinished(final RecipeThread thread) {
        recipeConsumers.removeDouble(thread);
    }

    @Override
    public void readNBT(final NBTTagCompound customData) {
        super.readNBT(customData);
        this.computationPointConsumption = customData.getDouble("c");
    }

    @Override
    public void writeNBT() {
        super.writeNBT();
        NBTTagCompound tag = owner.getCustomDataTag();
        tag.setDouble("c", computationPointConsumption);
    }

    @Override
    public double getComputationPointConsumption() {
        return computationPointConsumption;
    }
}
