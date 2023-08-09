package github.kasuminova.novaeng.common.hypernet.research;

import crafttweaker.annotations.ZenRegister;
import github.kasuminova.mmce.common.event.recipe.FactoryRecipeTickEvent;
import github.kasuminova.mmce.common.event.recipe.RecipeCheckEvent;
import github.kasuminova.mmce.common.helper.IMachineController;
import github.kasuminova.novaeng.common.hypernet.Database;
import github.kasuminova.novaeng.common.hypernet.NetNode;
import github.kasuminova.novaeng.common.registry.RegistryHyperNet;
import hellfirepvp.modularmachinery.common.tiles.base.TileMultiblockMachineController;
import net.minecraft.nbt.NBTTagCompound;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenMethod;

import java.util.*;

@ZenRegister
@ZenClass("novaeng.hypernet.ResearchStation")
public class ResearchStation extends NetNode {
    private static final Map<TileMultiblockMachineController, ResearchStation> CACHED_RESEARCH_STATION = new WeakHashMap<>();

    private final ResearchStationType type;
    private float computationPointConsumption = 0;

    public ResearchStation(final TileMultiblockMachineController owner, final NBTTagCompound customData) {
        super(owner);
        this.type = RegistryHyperNet.getResearchStationType(
                Objects.requireNonNull(owner.getFoundMachine()).getRegistryName().getPath()
        );

        readNBT(customData);
    }

    @ZenMethod
    public void onRecipeCheck(final float computationPointConsumption, final float techLevel, final RecipeCheckEvent event) {
        if (centerPos == null || center == null) {
            event.setFailed("未连接至计算网络！");
            return;
        }

        if (type.getMaxTechLevel() < techLevel) {
            event.setFailed("科技等级不足！（研究站等级：" + type.getMaxTechLevel() + "，要求：" + techLevel + "）");
            return;
        }

        if (center.getComputationPointGeneration() < computationPointConsumption) {
            event.setFailed("算力不足！（预期算力：" + computationPointConsumption + " TFloPS，当前算力：" + center.getComputationPointGeneration() + " TFloPS）");
        }

        Collection<NetNode> nodes = center.getNode(Database.class);
        if (nodes.isEmpty()) {
            event.setFailed("计算网络中未找到数据库！");
        }

        if (nodes.stream()
                .map(Database.class::cast)
                .noneMatch(database ->
                        database.getStoredResearchCognition().size() < database.getType().getMaxResearchCognitionStoreSize()))
        {
            event.setFailed("网络中所有的数据库存储已满！");
        }
    }

    @ZenMethod
    public void onWorkingTick(final float computationPointConsumption, final FactoryRecipeTickEvent event) {
        if (centerPos == null) {
            event.setFailed(true, "未连接至计算网络！");
            return;
        }
        if (center == null) {
            event.setFailed(false, "未连接至计算网络！");
        }

        this.computationPointConsumption = computationPointConsumption;
        float consumed = center.consumeComputationPoint(computationPointConsumption);
        if (consumed < computationPointConsumption) {
            event.preventProgressing(
                    "算力不足！（预期算力：" + computationPointConsumption + " TFloPS，当前算力：" + consumed + " TFloPS）");
        }
        writeNBT();
    }

    @ZenMethod
    public void onRecipeFinished(String researchName) {
        Collection<NetNode> nodes = center.getNode(Database.class);
        if (nodes.isEmpty()) {
            return;
        }

        ResearchCognitionData data = RegistryHyperNet.getResearchCognitionData(researchName);

        if (data == null) {
            return;
        }

        nodes.stream()
                .map(Database.class::cast)
                .filter(database -> database.getStoredResearchCognition().size() < database.getType().getMaxResearchCognitionStoreSize())
                .findFirst()
                .ifPresent(database -> database.storeResearchCognitionData(data));
    }

    @Override
    @ZenMethod
    public void onMachineTick() {
        super.onMachineTick();

        if (!owner.isWorking()) {
            computationPointConsumption = 0;
        }
    }

    @ZenMethod
    public static ResearchStation from(final IMachineController machine) {
        TileMultiblockMachineController ctrl = machine.getController();
        return CACHED_RESEARCH_STATION.computeIfAbsent(ctrl, v ->
                new ResearchStation(ctrl, ctrl.getCustomDataTag()));
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
    public float getComputationPointConsumption() {
        return computationPointConsumption;
    }

    @ZenGetter("type")
    public ResearchStationType getType() {
        return type;
    }

    public static void clearCache() {
        CACHED_RESEARCH_STATION.clear();
    }

}
