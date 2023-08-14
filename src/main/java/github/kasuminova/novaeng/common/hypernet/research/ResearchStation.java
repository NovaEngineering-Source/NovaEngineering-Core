package github.kasuminova.novaeng.common.hypernet.research;

import crafttweaker.annotations.ZenRegister;
import github.kasuminova.mmce.common.event.recipe.FactoryRecipeTickEvent;
import github.kasuminova.mmce.common.event.recipe.RecipeCheckEvent;
import github.kasuminova.mmce.common.helper.IMachineController;
import github.kasuminova.novaeng.common.hypernet.Database;
import github.kasuminova.novaeng.common.hypernet.NetNode;
import github.kasuminova.novaeng.common.registry.RegistryHyperNet;
import hellfirepvp.modularmachinery.ModularMachinery;
import hellfirepvp.modularmachinery.common.crafting.ActiveMachineRecipe;
import hellfirepvp.modularmachinery.common.machine.factory.FactoryRecipeThread;
import hellfirepvp.modularmachinery.common.tiles.base.TileMultiblockMachineController;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
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
                        database.getType().getMaxResearchCognitionStoreSize() > database.getStoredResearchCognition().size())) {
            event.setFailed("网络中所有的数据库存储已满！");
        }
    }

    @ZenMethod
    public void onWorkingTick(final FactoryRecipeTickEvent event) {
        if (centerPos == null) {
            event.setFailed(true, "未连接至计算网络！");
            return;
        }
        if (currentResearching == null) {
            event.setFailed(true, "终端尚未发配任务！");
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
            ModularMachinery.EXECUTE_MANAGER.addSyncTask(this::writeResearchProgressToDatabase);
        }
        writeNBT();
    }

    public void completeRecipe(FactoryRecipeThread thread) {
        ActiveMachineRecipe recipe = thread.getActiveRecipe();
        recipe.setTick(recipe.getTotalTick() + 1);

        Collection<Database> nodes = center.getNode(Database.class);
        if (nodes.isEmpty()) {
            return;
        }

        if (currentResearching == null) {
            return;
        }

        ModularMachinery.EXECUTE_MANAGER.addSyncTask(() -> {
            nodes.stream()
                    .filter(node -> node.getType().getMaxResearchCognitionStoreSize() > node.getStoredResearchCognition().size())
                    .filter(node -> node.getAllResearchingCognition().containsKey(currentResearching))
                    .findFirst()
                    .ifPresent(node -> node.storeResearchCognitionData(currentResearching));
            nodes.forEach(node -> node.getAllResearchingCognition().removeDouble(currentResearching));
            resetResearchTask();
        });
    }

    public void writeResearchProgressToDatabase() {
        if (currentResearching == null) {
            return;
        }

        Collection<Database> nodes = center.getNode(Database.class);
        if (nodes.isEmpty()) {
            return;
        }

        for (Database database : nodes) {
            Object2DoubleOpenHashMap<ResearchCognitionData> map = database.getAllResearchingCognition();
            if (database.getType().getMaxResearchCognitionStoreSize() < database.getStoredResearchCognition().size()) {
                continue;
            }
            if (map.computeIfPresent(currentResearching, (k, v) -> currentResearchingProgress) != null) {
                database.writeNBT();
                break;
            }
        }
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
        double progress = center.getNode(Database.class)
                .stream()
                .filter(database -> database.getType().getMaxResearchCognitionStoreSize() > database.getStoredResearchCognition().size())
                .map(database -> database.getAllResearchingCognition().getOrDefault(data, -1D))
                .filter(d -> d != -1D)
                .findFirst()
                .orElse(-1D);

        if (progress == -1) {
            ModularMachinery.EXECUTE_MANAGER.addSyncTask(() -> center.getNode(Database.class)
                    .stream()
                    .filter(database -> database.getType().getMaxResearchCognitionStoreSize() > database.getStoredResearchCognition().size())
                    .findFirst()
                    .ifPresent(database -> {
                        database.getAllResearchingCognition().put(data, 0D);
                        database.writeNBT();
                    }));
            progress = 0D;
        }

        currentResearchingProgress = progress;
        writeNBT();
    }

    @Override
    public void readNBT(final NBTTagCompound customData) {
        super.readNBT(customData);
        this.computationPointConsumption = customData.getFloat("computationPointConsumption");
        this.currentResearching = RegistryHyperNet.getResearchCognitionData(customData.getString("researching"));
        this.currentResearchingProgress = customData.getDouble("researchProgress");
    }

    @Override
    public void writeNBT() {
        super.writeNBT();
        NBTTagCompound tag = owner.getCustomDataTag();

        tag.setFloat("computationPointConsumption", computationPointConsumption);
        if (currentResearching != null) {
            tag.setString("researching", currentResearching.getResearchName());
            tag.setDouble("researchProgress", currentResearchingProgress);
        }
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

    @ZenGetter("currentResearching")
    public ResearchCognitionData getCurrentResearching() {
        return currentResearching;
    }

    @ZenGetter("currentResearchingProgress")
    public double getCurrentResearchingProgress() {
        return currentResearchingProgress;
    }
}
