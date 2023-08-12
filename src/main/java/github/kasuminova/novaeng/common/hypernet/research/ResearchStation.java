package github.kasuminova.novaeng.common.hypernet.research;

import crafttweaker.annotations.ZenRegister;
import github.kasuminova.mmce.common.event.recipe.FactoryRecipeTickEvent;
import github.kasuminova.mmce.common.event.recipe.RecipeCheckEvent;
import github.kasuminova.mmce.common.helper.IMachineController;
import github.kasuminova.novaeng.common.hypernet.Database;
import github.kasuminova.novaeng.common.hypernet.NetNode;
import github.kasuminova.novaeng.common.registry.RegistryHyperNet;
import hellfirepvp.modularmachinery.common.crafting.ActiveMachineRecipe;
import hellfirepvp.modularmachinery.common.machine.factory.FactoryRecipeThread;
import hellfirepvp.modularmachinery.common.tiles.base.TileMultiblockMachineController;
import net.minecraft.nbt.NBTTagCompound;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenMethod;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;

@ZenRegister
@ZenClass("novaeng.hypernet.ResearchStation")
public class ResearchStation extends NetNode {
    private static final Map<TileMultiblockMachineController, ResearchStation> CACHED_RESEARCH_STATION = new WeakHashMap<>();
    private final ResearchStationType type;
    private ResearchCognitionData currentResearching = null;
    private double currentResearchingProgress = 0;
    private float computationPointConsumption = 0;

    public ResearchStation(final TileMultiblockMachineController owner, final NBTTagCompound customData) {
        super(owner);
        this.type = RegistryHyperNet.getResearchStationType(
                Objects.requireNonNull(owner.getFoundMachine()).getRegistryName().getPath()
        );

        readNBT(customData);
    }

    public static void completeRecipe(FactoryRecipeThread thread) {
        ActiveMachineRecipe recipe = thread.getActiveRecipe();
        recipe.setTick(recipe.getTotalTick());
    }

    @ZenMethod
    public static ResearchStation from(final IMachineController machine) {
        TileMultiblockMachineController ctrl = machine.getController();
        return CACHED_RESEARCH_STATION.computeIfAbsent(ctrl, v ->
                new ResearchStation(ctrl, ctrl.getCustomDataTag()));
    }

    public static void clearCache() {
        CACHED_RESEARCH_STATION.clear();
    }

    @ZenMethod
    public void onRecipeCheck(final RecipeCheckEvent event) {
        if (centerPos == null || center == null) {
            event.setFailed("未连接至计算网络！");
            return;
        }

        if (currentResearching == null) {
            event.setFailed("终端尚未发配任务！");
            return;
        }

        float techLevel = currentResearching.getTechLevel();
        if (type.getMaxTechLevel() < techLevel) {
            event.setFailed("科技等级不足！（研究站等级：" + type.getMaxTechLevel() + "，要求：" + techLevel + "）");
            return;
        }

        float pointPerTick = currentResearching.getMinComputationPointPerTick();
        if (center.getComputationPointGeneration() < pointPerTick) {
            event.setFailed("算力不足！（预期算力：" + pointPerTick + " TFloPS，当前算力：" + center.getComputationPointGeneration() + " TFloPS）");
            return;
        }

        Collection<Database> nodes = center.getNode(Database.class);
        if (nodes.isEmpty()) {
            event.setFailed("计算网络中未找到数据库！");
            return;
        }

        if (nodes.stream()
                .map(Database.class::cast)
                .noneMatch(database ->
                        database.getStoredResearchCognition().size() < database.getType().getMaxResearchCognitionStoreSize())) {
            event.setFailed("网络中所有的数据库存储已满！");
        }
    }

    @ZenMethod
    public void onWorkingTick(final FactoryRecipeTickEvent event) {
        if (centerPos == null) {
            event.setFailed(true, "未连接至计算网络！");
            return;
        }
        if (center == null) {
            event.setFailed(false, "未连接至计算网络！");
            return;
        }

        double left = currentResearching.getRequiredPoints() - currentResearchingProgress;
        computationPointConsumption = (float) Math.min(currentResearching.getMinComputationPointPerTick(), left);

        if (left <= 0) {
            completeRecipe(event.getRecipeThread());
            return;
        }

        float consumed = center.consumeComputationPoint(computationPointConsumption);
        if (consumed < computationPointConsumption) {
            event.preventProgressing(
                    "算力不足！（预期算力：" + computationPointConsumption + " TFloPS，当前算力：" + consumed + " TFloPS）");
        } else {
            currentResearchingProgress += consumed;
            writeResearchProgressToDatabase();
        }
        writeNBT();
    }

    public void writeResearchProgressToDatabase() {
        if (currentResearching == null) {
            return;
        }

        Collection<Database> nodes = center.getNode(Database.class);
        if (nodes.isEmpty()) {
            return;
        }

        nodes.stream()
                .filter(node -> node.getStoredResearchCognition().size() >= node.getType().getMaxResearchCognitionStoreSize())
                .map(Database::getAllResearchingCognition)
                .filter(researching -> researching.containsKey(currentResearching.getResearchName()))
                .findFirst()
                .ifPresent(researching -> researching.put(currentResearching.getResearchName(), currentResearchingProgress));
    }

    @ZenMethod
    public void onRecipeFinished() {
        Collection<Database> nodes = center.getNode(Database.class);
        if (nodes.isEmpty()) {
            return;
        }

        if (currentResearching == null) {
            return;
        }

        nodes.forEach(node -> node.getAllResearchingCognition().removeDouble(currentResearching.getResearchName()));
        nodes.stream()
                .map(Database.class::cast)
                .filter(database -> database.getStoredResearchCognition().size() < database.getType().getMaxResearchCognitionStoreSize())
                .findFirst()
                .ifPresent(database -> database.storeResearchCognitionData(currentResearching));

        resetResearchTask();
    }

    public void resetResearchTask() {
        currentResearching = null;
        currentResearchingProgress = 0;
        computationPointConsumption = 0;
    }

    @Override
    @ZenMethod
    public void onMachineTick() {
        super.onMachineTick();

        if (!owner.isWorking()) {
            computationPointConsumption = 0;
        }
    }

    public void provideTask(ResearchCognitionData data) {
        currentResearching = data;
        computationPointConsumption = data.getMinComputationPointPerTick();
        currentResearchingProgress = center.getNode(Database.class)
                .stream()
                .map(database -> database.getAllResearchingCognition().getOrDefault(data.getResearchName(), -1D))
                .filter(progress -> progress != -1D)
                .findFirst()
                .orElse(0D);
    }

    @Override
    public void readNBT(final NBTTagCompound customData) {
        super.readNBT(customData);
        this.computationPointConsumption = customData.getFloat("computationPointConsumption");
    }

    @Override
    public void writeNBT() {
        super.writeNBT();
        NBTTagCompound tag = owner.getCustomDataTag();
        tag.setFloat("computationPointConsumption", computationPointConsumption);
    }

    @Override
    public int getNodeMaxPresences() {
        return 1;
    }

    @Override
    public float getComputationPointConsumption() {
        return computationPointConsumption;
    }

    @ZenGetter("type")
    public ResearchStationType getType() {
        return type;
    }

}
