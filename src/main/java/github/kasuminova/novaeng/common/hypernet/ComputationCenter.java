package github.kasuminova.novaeng.common.hypernet;

import crafttweaker.annotations.ZenRegister;
import github.kasuminova.mmce.common.event.recipe.RecipeCheckEvent;
import github.kasuminova.mmce.common.helper.IMachineController;
import github.kasuminova.novaeng.common.handler.HyperNetEventHandler;
import github.kasuminova.novaeng.common.registry.RegistryHyperNet;
import github.kasuminova.novaeng.common.util.RandomUtils;
import hellfirepvp.modularmachinery.ModularMachinery;
import hellfirepvp.modularmachinery.common.tiles.base.TileMultiblockMachineController;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenMethod;
import stanhebben.zenscript.annotations.ZenSetter;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@ZenRegister
@ZenClass("novaeng.hypernet.ComputationCenter")
public class ComputationCenter {
    private static final Map<TileMultiblockMachineController, ComputationCenter> CACHED_COMPUTATION_CENTER = new WeakHashMap<>();

    private final TileMultiblockMachineController owner;
    private final Map<Class<?>, Map<BlockPos, NetNode>> nodes = new ConcurrentHashMap<>();

    private final ComputationCenterType type;
    private int circuitDurability = 0;

    // 计算点计数器，计算当前 Tick 总共消耗了多少算力。
    private volatile float computationPointCounter = 0;

    // 存储上一个 Tick 消耗的总算力，用于能源消耗计算。
    private float lastComputationPointConsumed = 0;

    public ComputationCenter(final TileMultiblockMachineController owner, final NBTTagCompound customData) {
        this.owner = owner;
        this.type = RegistryHyperNet.getComputationCenterType(
                Objects.requireNonNull(owner.getFoundMachine()).getRegistryName().getPath()
        );
        readNBT(customData);
    }

    @ZenMethod
    public static ComputationCenter from(final IMachineController machine) {
        TileMultiblockMachineController ctrl = machine.getController();
        return CACHED_COMPUTATION_CENTER.computeIfAbsent(ctrl, v ->
                new ComputationCenter(ctrl, ctrl.getCustomDataTag()));
    }

    public static void clearCache() {
        CACHED_COMPUTATION_CENTER.clear();
    }

    @ZenMethod
    public void onRecipeCheck(RecipeCheckEvent event) {
        if (circuitDurability < type.getCircuitDurability() * 0.05F) {
            event.setFailed("电路板耐久过低，无法正常工作！");
        }
    }

    @ZenMethod
    public void onWorkingTick() {
        checkNodeConnection();
        consumeCircuitDurability();
        HyperNetEventHandler.addTickStartAction(this::resetComputationPointCounter);
    }

    @ZenMethod
    public void onMachineTick() {

    }

    private void consumeCircuitDurability() {
        if (owner.getTicksExisted() % 20 != 0) {
            return;
        }
        float consumeChance =
                (float) getConnectedMachineryCount() / type.getMaxConnections() +
                        getComputationPointGeneration() / type.getMaxComputationPointCarrying();
        if (!(RandomUtils.nextFloat() <= Math.max(type.getCircuitConsumeChance() * consumeChance, 0.01F))) {
            return;
        }

        int min = type.getMinCircuitConsumeAmount();
        int max = type.getMaxCircuitConsumeAmount();

        circuitDurability -= min + RandomUtils.nextInt(max - min);
        writeNBT();
    }

    private void checkNodeConnection() {
        if (owner.getTicksExisted() % 20 != 0) {
            return;
        }

        World world = owner.getWorld();

        nodes.forEach((clazz, nodes) -> {
            Iterator<Map.Entry<BlockPos, NetNode>> it = nodes.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<BlockPos, NetNode> next = it.next();

                BlockPos pos = next.getKey();
                NetNode node = next.getValue();

                if (node.getOwner().isInvalid()) {
                    it.remove();
                    continue;
                }

                if (world.isBlockLoaded(pos)) {
                    TileEntity te = world.getTileEntity(pos);
                    if (!(te instanceof TileMultiblockMachineController)) {
                        it.remove();
                    }
                } else {
                    ModularMachinery.EXECUTE_MANAGER.addSyncTask(() -> {
                        TileEntity te = world.getTileEntity(pos);
                        if (!(te instanceof TileMultiblockMachineController)) {
                            nodes.remove(pos);
                        }
                    });
                }
            }
        });
    }

    public boolean onConnect(final TileMultiblockMachineController machinery, final NetNode node) {
        if (!owner.isWorking()) {
            return false;
        }

        Map<BlockPos, NetNode> connected = nodes.computeIfAbsent(node.getClass(), v -> new ConcurrentHashMap<>());
        BlockPos pos = machinery.getPos();

        if (connected.computeIfPresent(pos, (k, v) -> node) != null) {
            node.onConnected(this);
            return true;
        }

        if (getConnectedMachineryCount() >= type.getMaxConnections()
                || !HyperNetHelper.supportsHyperNet(machinery)
                || connected.size() >= node.getNodeMaxPresences()) {
            return false;
        }

        connected.put(pos, node);
        node.onConnected(this);
        return true;
    }

    public void onDisconnect(final TileMultiblockMachineController machinery, final NetNode node) {
        nodes.computeIfAbsent(node.getClass(), v -> new ConcurrentHashMap<>()).remove(machinery.getPos());
    }

    public Collection<NetNode> getNode(Class<?> type) {
        return nodes.computeIfAbsent(type, v -> new ConcurrentHashMap<>()).values();
    }

    /**
     * 消耗计算点，返回已消耗的数量。
     */
    public synchronized float consumeComputationPoint(final float amount) {
        if (!owner.isWorking() || type.getMaxComputationPointCarrying() < amount || computationPointCounter < amount) {
            return 0;
        }

        float required = amount;
        float totalGenerated = 0F;

        calculate:
        for (Map<BlockPos, NetNode> nodes : nodes.values()) {
            for (NetNode node : nodes.values()) {
                float generated = node.requireComputationPoint(required, true);
                required -= generated;
                totalGenerated += generated;
                if (required <= 0F) {
                    break calculate;
                }
            }
        }

        computationPointCounter -= totalGenerated;
        return totalGenerated;
    }

    public void resetComputationPointCounter() {
        lastComputationPointConsumed = computationPointCounter;
        computationPointCounter = type.getMaxComputationPointCarrying();
    }

    @ZenMethod
    public void readNBT() {
        readNBT(owner.getCustomDataTag());
    }

    public void readNBT(final NBTTagCompound customData) {
        if (customData.hasKey("circuitDurability")) {
            this.circuitDurability = customData.getInteger("circuitDurability");
        } else {
            this.circuitDurability = type.getCircuitDurability();
        }
    }

    @ZenMethod
    public void writeNBT() {
        NBTTagCompound tag = owner.getCustomDataTag();
        tag.setInteger("circuitDurability", circuitDurability);
    }

    @ZenGetter("circuitDurability")
    public int getCircuitDurability() {
        return circuitDurability;
    }

    @ZenSetter("circuitDurability")
    public void setCircuitDurability(final int circuitDurability) {
        this.circuitDurability = circuitDurability;
        writeNBT();
    }

    @ZenGetter("connectedMachineryCount")
    public int getConnectedMachineryCount() {
        return nodes.values().stream().mapToInt(Map::size).sum();
    }

    @ZenGetter("type")
    public ComputationCenterType getType() {
        return type;
    }

    @ZenGetter("computationPointGeneration")
    public float getComputationPointGeneration() {
        float maxCarry = type.getMaxComputationPointCarrying();
        float totalGeneration = 0F;
        for (Map<BlockPos, NetNode> nodes : nodes.values()) {
            for (NetNode node : nodes.values()) {
                float generation = node.getComputationPointProvision(maxCarry);
                maxCarry -= generation;
                totalGeneration += generation;
            }
        }
        return totalGeneration;
    }

    @ZenGetter("computationPointConsumption")
    public float getComputationPointConsumption() {
        return (float) nodes.values()
                .stream()
                .flatMap(map -> map.values().stream())
                .mapToDouble(NetNode::getComputationPointConsumption)
                .sum();
    }

    public TileMultiblockMachineController getOwner() {
        return owner;
    }
}
