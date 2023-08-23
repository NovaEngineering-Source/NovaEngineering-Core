package github.kasuminova.novaeng.common.hypernet;

import crafttweaker.annotations.ZenRegister;
import github.kasuminova.mmce.common.event.recipe.RecipeCheckEvent;
import github.kasuminova.novaeng.common.hypernet.research.ResearchCognitionData;
import hellfirepvp.modularmachinery.common.tiles.base.TileMultiblockMachineController;
import net.minecraft.nbt.NBTTagCompound;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;
import stanhebben.zenscript.annotations.ZenSetter;

import java.util.Arrays;
import java.util.Collection;

@ZenRegister
@ZenClass("novaeng.hypernet.NetNodeImpl")
public class NetNodeImpl extends NetNode {
    private float computationPointProvision = 0;
    private float computationPointConsumption = 0;

    public NetNodeImpl(final TileMultiblockMachineController owner) {
        super(owner);
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
            event.setFailed("算力不足！（预期算力：" + pointRequired + "T FloPS，当前算力：" + center.getComputationPointGeneration() + "T FloPS）");
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

    @Override
    public void readNBT(final NBTTagCompound customData) {
        super.readNBT(customData);
        this.computationPointProvision = customData.getInteger("p");
        this.computationPointConsumption = customData.getInteger("c");
    }

    @Override
    public void writeNBT() {
        super.writeNBT();
        NBTTagCompound tag = owner.getCustomDataTag();
        tag.setFloat("p", computationPointProvision);
        tag.setFloat("c", computationPointConsumption);
    }

    @ZenSetter("computationPointProvision")
    public void setComputationPointProvision(final int computationPointProvision) {
        this.computationPointProvision = computationPointProvision;
    }

    @ZenSetter("computationPointConsumption")
    public void setComputationPointConsumption(final int computationPointConsumption) {
        this.computationPointConsumption = computationPointConsumption;
    }

    @Override
    public float getComputationPointProvision(final float maxGeneration) {
        return Math.min(computationPointProvision, maxGeneration);
    }

    @Override
    public float getComputationPointConsumption() {
        return computationPointConsumption;
    }
}
