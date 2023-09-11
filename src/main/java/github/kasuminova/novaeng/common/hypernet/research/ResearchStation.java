package github.kasuminova.novaeng.common.hypernet.research;

import crafttweaker.annotations.ZenRegister;
import github.kasuminova.mmce.common.event.recipe.FactoryRecipeTickEvent;
import github.kasuminova.mmce.common.event.recipe.RecipeCheckEvent;
import github.kasuminova.novaeng.common.crafttweaker.util.NovaEngUtils;
import github.kasuminova.novaeng.common.hypernet.Database;
import github.kasuminova.novaeng.common.hypernet.NetNode;
import github.kasuminova.novaeng.common.registry.RegistryHyperNet;
import hellfirepvp.modularmachinery.ModularMachinery;
import hellfirepvp.modularmachinery.common.crafting.ActiveMachineRecipe;
import hellfirepvp.modularmachinery.common.crafting.helper.CraftingStatus;
import hellfirepvp.modularmachinery.common.machine.factory.FactoryRecipeThread;
import hellfirepvp.modularmachinery.common.tiles.TileFactoryController;
import hellfirepvp.modularmachinery.common.tiles.base.TileMultiblockMachineController;
import net.minecraft.nbt.NBTTagCompound;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenMethod;

import java.util.Collection;
import java.util.Objects;

@ZenRegister
@ZenClass("novaeng.hypernet.ResearchStation")
public class ResearchStation extends NetNode {
    private final ResearchStationType type;
    private ResearchCognitionData currentResearching = null;
    private double completedPoints = 0;
    private float consumption = 0;

    public ResearchStation(final TileMultiblockMachineController owner) {
        super(owner);
        this.type = RegistryHyperNet.getResearchStationType(
                Objects.requireNonNull(owner.getFoundMachine()).getRegistryName().getPath()
        );
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
        if (center.getComputationPointGeneration() < Math.min(pointPerTick, getComputationLeft())) {
            event.setFailed("算力不足！预期：" + pointPerTick + "T FloPS，当前：" + center.getComputationPointGeneration() + "T FloPS");
            return;
        }

        Collection<Database> nodes = center.getNode(Database.class);
        if (nodes.isEmpty()) {
            event.setFailed("计算网络中未找到数据库！");
            return;
        }

        if (nodes.stream()
                .map(Database.class::cast)
                .noneMatch(database -> database.hasDatabaseSpace(currentResearching)))
        {
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

        if (checkCompleted(event)) {
            return;
        }

        float consumed = center.consumeComputationPoint((float) Math.min(getComputationLeft(), consumption));
        if (consumed < consumption) {
            event.preventProgressing("算力不足！预期："
                    + NovaEngUtils.formatFLOPS(consumption) + "，当前："
                    + NovaEngUtils.formatFLOPS(consumed) + "T FloPS");
        } else {
            doResearch(event, consumed);
        }
    }

    protected boolean checkCompleted(final FactoryRecipeTickEvent event) {
        double left = getComputationLeft();
        if (left <= 0) {
            completeRecipe(event.getFactoryRecipeThread());
            writeNBT();
            return true;
        }
        return false;
    }

    private double getComputationLeft() {
        if (currentResearching == null) {
            return 0D;
        }
        return currentResearching.getRequiredPoints() - completedPoints;
    }

    protected void doResearch(final FactoryRecipeTickEvent event, final float consumed) {
        if (checkCompleted(event)) {
            return;
        }

        completedPoints += consumed;
        consumption = (float) Math.min(currentResearching.getMinComputationPointPerTick(), getComputationLeft());

        ActiveMachineRecipe activeRecipe = event.getActiveRecipe();
        int totalTick = activeRecipe.getTotalTick();
        activeRecipe.setTick(Math.max((int) (getProgressPercent() * totalTick) - 1, 0));
        event.getRecipeThread().setStatus(CraftingStatus.SUCCESS).setStatusInfo("研究中...");

        ModularMachinery.EXECUTE_MANAGER.addSyncTask(this::writeResearchProgressToDatabase);
        writeNBT();
    }

    public void completeRecipe(FactoryRecipeThread thread) {
        ActiveMachineRecipe recipe = thread.getActiveRecipe();
        recipe.setTick(recipe.getTotalTick() + 1);

        Collection<Database> databases = center.getNode(Database.class);
        if (databases.isEmpty()) {
            return;
        }

        ResearchCognitionData data = currentResearching;
        if (data == null) {
            return;
        }

        ModularMachinery.EXECUTE_MANAGER.addSyncTask(() -> {
            for (final Database database : databases) {
                if (database.storeResearchCognitionData(data)) {
                    break;
                }
            }
            databases.forEach(database -> database.getAllResearchingCognition().removeDouble(data));
            resetResearchTask();
        });
    }

    public void writeResearchProgressToDatabase() {
        if (currentResearching == null) {
            return;
        }

        for (Database database : center.getNode(Database.class)) {
            if (database.writeResearchingData(currentResearching, completedPoints)) {
                break;
            }
        }
    }

    public void resetResearchTask() {
        currentResearching = null;
        completedPoints = 0;
        consumption = 0;
    }

    @Override
    @ZenMethod
    public void onMachineTick() {
        super.onMachineTick();

        if (!isWorking()) {
            consumption = 0;
        }
    }

    public void provideTask(ResearchCognitionData data) {
        currentResearching = data;
        if (data == null) {
            completedPoints = 0;
            writeNBT();
            return;
        }

        consumption = data.getMinComputationPointPerTick();
        double progress = center.getNode(Database.class)
                .stream()
                .map(database -> database.getResearchingData(data))
                .filter(d -> d != -1D)
                .findFirst()
                .orElse(-1D);

        if (progress == -1) {
            for (final Database database : center.getNode(Database.class)) {
                if (database.writeResearchingData(data, 0D)) {
                    break;
                }
            }
            progress = 0D;
        }

        completedPoints = progress;
        writeNBT();
    }

    @Override
    public boolean isWorking() {
        if (!(owner instanceof TileFactoryController)) {
            return false;
        }

        TileFactoryController factory = (TileFactoryController) owner;
        FactoryRecipeThread thread = factory.getCoreRecipeThreads().get(ResearchStationType.RESEARCH_STATION_WORKING_THREAD_NAME);

        return thread != null && thread.isWorking();
    }

    @Override
    public void readNBT(final NBTTagCompound customData) {
        super.readNBT(customData);
        this.consumption = customData.getFloat("consumption");
        this.currentResearching = RegistryHyperNet.getResearchCognitionData(customData.getString("researching"));
        this.completedPoints = customData.getDouble("completedPoints");
    }

    @Override
    public void writeNBT() {
        super.writeNBT();
        NBTTagCompound tag = owner.getCustomDataTag();

        tag.setFloat("consumption", consumption);
        if (currentResearching != null) {
            tag.setString("researching", currentResearching.getResearchName());
            tag.setDouble("completedPoints", completedPoints);
        }
    }

    @Override
    public int getNodeMaxPresences() {
        return 1;
    }

    @Override
    public float getComputationPointConsumption() {
        return consumption;
    }

    @ZenGetter("type")
    public ResearchStationType getType() {
        return type;
    }

    @ZenGetter("currentResearching")
    public ResearchCognitionData getCurrentResearching() {
        return currentResearching;
    }

    @ZenGetter("completedPoints")
    public double getCompletedPoints() {
        return completedPoints;
    }

    public ResearchStation setCompletedPoints(final double completedPoints) {
        this.completedPoints = completedPoints;
        return this;
    }

    @ZenGetter("progressPercent")
    public double getProgressPercent() {
        if (currentResearching == null) {
            return 0;
        }
        return completedPoints / currentResearching.getRequiredPoints();
    }
}
