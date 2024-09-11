package github.kasuminova.novaeng.common.hypernet.old;

import crafttweaker.annotations.ZenRegister;
import github.kasuminova.mmce.common.event.recipe.RecipeCheckEvent;
import github.kasuminova.mmce.common.helper.IMachineController;
import github.kasuminova.novaeng.common.crafttweaker.hypernet.HyperNetHelper;
import github.kasuminova.novaeng.common.handler.HyperNetEventHandler;
import github.kasuminova.novaeng.common.hypernet.old.misc.ConnectResult;
import github.kasuminova.novaeng.common.registry.RegistryHyperNet;
import github.kasuminova.novaeng.common.util.RandomUtils;
import hellfirepvp.modularmachinery.ModularMachinery;
import hellfirepvp.modularmachinery.common.machine.factory.FactoryRecipeThread;
import hellfirepvp.modularmachinery.common.tiles.TileFactoryController;
import hellfirepvp.modularmachinery.common.tiles.base.TileMultiblockMachineController;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenMethod;
import stanhebben.zenscript.annotations.ZenSetter;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

@ZenRegister
@ZenClass("novaeng.hypernet.ComputationCenter")
public class ComputationCenter {
    private static final Map<TileMultiblockMachineController, ComputationCenter> CACHED_COMPUTATION_CENTER = new WeakHashMap<>();

    private final TileMultiblockMachineController owner;
    private final Map<Class<?>, Map<BlockPos, NetNode>> nodes = new ConcurrentHashMap<>();

    private final ComputationCenterType type;

    // 计算点计数器，计算当前 Tick 总共消耗了多少算力。
    private final AtomicReference<Double> computationPointCounter = new AtomicReference<>(0D);

    private UUID networkOwner = null;

    private int circuitDurability = 0;

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
        ComputationCenter computationCenter = CACHED_COMPUTATION_CENTER.get(ctrl);
        if (computationCenter == null) {
            synchronized (CACHED_COMPUTATION_CENTER) {
                computationCenter = CACHED_COMPUTATION_CENTER.get(ctrl);
                if (computationCenter == null) {
                    CACHED_COMPUTATION_CENTER.put(ctrl, computationCenter = new ComputationCenter(ctrl, ctrl.getCustomDataTag()));
                }
            }
        }
        return computationCenter;
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
    public void onDurabilityFixRecipeCheck(RecipeCheckEvent event, int durability) {
        if (circuitDurability + durability > type.getCircuitDurability()) {
            event.setFailed("novaeng.hypernet.craftcheck.durability.failed");
        }
    }

    @ZenMethod
    public void onWorkingTick() {
        checkNodeConnection();
        consumeCircuitDurability();
        HyperNetEventHandler.addTickStartAction(this::resetComputationPointCounter);
    }

    @ZenMethod
    public void fixCircuit(int durability) {
        circuitDurability = Math.min(circuitDurability + durability, type.getCircuitDurability());
        writeNBT();
    }

    @ZenMethod
    public void onMachineTick() {

    }

    private void consumeCircuitDurability() {
        if (owner.getTicksExisted() % 20 != 0) {
            return;
        }
        float consumeChance =
                (float) ((double) getConnectedMachineryCount() / type.getMaxConnections() +
                                        getComputationPointGeneration() / type.getMaxComputationPointCarrying());
        if (!(RandomUtils.nextFloat() <= Math.max(type.getCircuitConsumeChance() * consumeChance, 0.01F))) {
            return;
        }

        int min = type.getMinCircuitConsumeAmount();
        int max = type.getMaxCircuitConsumeAmount();

        circuitDurability -= min + RandomUtils.nextInt(max - min);
        writeNBT();
    }

    private void checkNodeConnection() {
        if (owner.getTicksExisted() % 50 != 0) {
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

    public ConnectResult onConnect(final TileMultiblockMachineController machinery, final NetNode node) {
        if (!isWorking()) {
            return ConnectResult.CENTER_NOT_WORKING;
        }

        Map<BlockPos, NetNode> connected = nodes.computeIfAbsent(node.getClass(), v -> new ConcurrentHashMap<>());
        BlockPos pos = machinery.getPos();

        if (connected.computeIfPresent(pos, (k, v) -> node) != null) {
            node.onConnected(this);
            return ConnectResult.SUCCESS;
        }

        if (getConnectedMachineryCount() >= type.getMaxConnections()) {
            return ConnectResult.CENTER_REACHED_CONNECTION_LIMIT;
        } else if (!HyperNetHelper.supportsHyperNet(machinery)) {
            return ConnectResult.UNSUPPORTED_NODE;
        } else if (connected.size() >= node.getNodeMaxPresences()) {
            return ConnectResult.NODE_TYPE_REACHED_MAX_PRESENCES;
        }

        connected.put(pos, node);
        node.onConnected(this);
        return ConnectResult.SUCCESS;
    }

    public void onDisconnect(final TileMultiblockMachineController machinery, final NetNode node) {
        nodes.computeIfAbsent(node.getClass(), v -> new ConcurrentHashMap<>()).remove(machinery.getPos());
    }

    @SuppressWarnings("unchecked")
    public <N extends NetNode> Collection<N> getNode(Class<N> type) {
        return (Collection<N>) nodes.computeIfAbsent(type, v -> new ConcurrentHashMap<>()).values();
    }

    /**
     * 消耗计算点，返回已消耗的数量。
     */
    public double consumeComputationPoint(final double required) {
        if (!isWorking() || type.getMaxComputationPointCarrying() < required || computationPointCounter.get() < required) {
            return 0;
        }

        final double[] polledCounter = {0};
        computationPointCounter.updateAndGet(counter -> {
            if (counter < required) {
                return counter;
            }
            return counter - (polledCounter[0] = required);
        });
        if (polledCounter[0] < required) {
            computationPointCounter.updateAndGet(counter -> counter + polledCounter[0]);
            return 0;
        }

        double totalGenerated = 0F;

        calculate:
        for (Map<BlockPos, NetNode> nodes : nodes.values()) {
            for (NetNode node : nodes.values()) {
                double generated = node.requireComputationPoint(required - totalGenerated, true);
                totalGenerated += generated;
                if (totalGenerated >= required) {
                    break calculate;
                }
            }
        }

        final double finalTotalGenerated = totalGenerated;
        computationPointCounter.updateAndGet(counter -> counter + (polledCounter[0] - finalTotalGenerated));

        if (required > totalGenerated) {
            // 修复精度有概率不准确的问题
            if (totalGenerated + 0.1D > required) {
                return required;
            }
        }

        return totalGenerated;
    }

    public boolean isWorking() {
        if (!(owner instanceof final TileFactoryController factory)) {
            return false;
        }
        FactoryRecipeThread thread = factory.getCoreRecipeThreads().get(ComputationCenterType.CENTER_WORKING_THREAD_NAME);
        return thread != null && thread.isWorking();
    }

    public void resetComputationPointCounter() {
        computationPointCounter.set(type.getMaxComputationPointCarrying());
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

        if (customData.hasKey("networkOwner")) {
            this.networkOwner = UUID.fromString(customData.getString("networkOwner"));
        } else {
            this.networkOwner = null;
        }
    }

    @ZenMethod
    public void writeNBT() {
        NBTTagCompound tag = owner.getCustomDataTag();
        tag.setInteger("circuitDurability", circuitDurability);

        if (networkOwner != null) {
            tag.setString("networkOwner", networkOwner.toString());
        }
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

    public UUID getNetworkOwner() {
        return networkOwner;
    }

    public void setNetworkOwner(final UUID networkOwner) {
        this.networkOwner = networkOwner;
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
    public double getComputationPointGeneration() {
        double maxCarry = type.getMaxComputationPointCarrying();
        double totalGeneration = 0F;
        for (Map<BlockPos, NetNode> nodes : nodes.values()) {
            for (NetNode node : nodes.values()) {
                double generation = node.getComputationPointProvision(maxCarry);
                maxCarry -= generation;
                totalGeneration += generation;
            }
        }
        return totalGeneration;
    }

    @ZenGetter("computationPointConsumption")
    public double getComputationPointConsumption() {
        double sum = 0F;
        for (Map<BlockPos, NetNode> map : nodes.values()) {
            for (NetNode node : map.values()) {
                sum += node.getComputationPointConsumption();
            }
        }
        return sum;
    }

    public TileMultiblockMachineController getOwner() {
        return owner;
    }
}
