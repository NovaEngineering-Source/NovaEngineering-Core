package github.kasuminova.novaeng.common.hypernet;

import crafttweaker.annotations.ZenRegister;
import github.kasuminova.mmce.common.event.recipe.*;
import github.kasuminova.novaeng.common.hypernet.research.ResearchCognitionData;
import hellfirepvp.modularmachinery.common.machine.RecipeThread;
import hellfirepvp.modularmachinery.common.tiles.base.TileMultiblockMachineController;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import net.minecraft.nbt.NBTTagCompound;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import java.util.Arrays;
import java.util.Collection;

@ZenRegister
@ZenClass("novaeng.hypernet.NetNodeImpl")
public class NetNodeImpl extends NetNode {
    private final Object2FloatOpenHashMap<RecipeThread> recipeConsumers = new Object2FloatOpenHashMap<>();
    private float computationPointConsumption = 0;

    public NetNodeImpl(final TileMultiblockMachineController owner) {
        super(owner);
    }

    @Override
    public void onMachineTick() {
        super.onMachineTick();
        if (isWorking()) {
            if (owner.getTicksExisted() % 10 == 0) {
                float total = 0;
                for (final Float value : recipeConsumers.values()) {
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
                                      final float pointRequired,
                                      final ResearchCognitionData... researchRequired)
    {
        if (centerPos == null || center == null) {
            event.setFailed("未连接至计算网络！");
            return;
        }

        if (center.getComputationPointGeneration() < pointRequired) {
            event.setFailed("算力不足！预期：" + pointRequired + "T FloPS，当前：" + center.getComputationPointGeneration() + "T FloPS");
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

        Arrays.stream(researchRequired)
                .filter(research -> nodes.stream()
                        .noneMatch(database -> database.hasResearchCognition(research)))
                .findFirst()
                .ifPresent(research -> event.setFailed("缺失研究：" + research.getResearchName() + "！"));
    }

    public void onRecipeStart(final RecipeStartEvent event, final float computation) {
        recipeConsumers.put(event.getRecipeThread(), computation * event.getActiveRecipe().getParallelism());
    }

    public void onRecipeStart(final FactoryRecipeStartEvent event, final float computation) {
        recipeConsumers.put(event.getRecipeThread(), computation * event.getActiveRecipe().getParallelism());
    }

    public void onRecipePreTick(final RecipeTickEvent event, final float computation) {
        if (centerPos == null || center == null) {
            event.setFailed(true, "未连接至计算网络！");
            return;
        }
        float required = computation * event.getActiveRecipe().getParallelism();
        recipeConsumers.put(event.getRecipeThread(), required);

        float consumed = center.consumeComputationPoint(required);
        if (consumed < required) {
            event.preventProgressing(
                    "算力不足！预期：" + required + "T FloPS，当前：" + consumed + "T FloPS");
        }
    }

    public void onRecipePreTick(final FactoryRecipeTickEvent event, final float computation) {
        if (centerPos == null || center == null) {
            event.setFailed(true, "未连接至计算网络！");
            return;
        }
        float required = computation * event.getActiveRecipe().getParallelism();
        recipeConsumers.put(event.getRecipeThread(), required);

        float consumed = center.consumeComputationPoint(required);
        if (consumed < required) {
            event.preventProgressing(
                    "算力不足！预期：" + required + "T FloPS，当前：" + consumed + "T FloPS");
        }
    }

    public void onRecipeFinished(final RecipeThread thread) {
        recipeConsumers.removeFloat(thread);
    }

    @Override
    public void readNBT(final NBTTagCompound customData) {
        super.readNBT(customData);
        this.computationPointConsumption = customData.getInteger("c");
    }

    @Override
    public void writeNBT() {
        super.writeNBT();
        NBTTagCompound tag = owner.getCustomDataTag();
        tag.setFloat("c", computationPointConsumption);
    }

    @Override
    public float getComputationPointConsumption() {
        return computationPointConsumption;
    }
}
