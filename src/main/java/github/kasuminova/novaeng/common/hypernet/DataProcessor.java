package github.kasuminova.novaeng.common.hypernet;

import crafttweaker.annotations.ZenRegister;
import github.kasuminova.mmce.common.event.recipe.FactoryRecipeTickEvent;
import github.kasuminova.mmce.common.event.recipe.RecipeCheckEvent;
import github.kasuminova.mmce.common.helper.IDynamicPatternInfo;
import github.kasuminova.mmce.common.upgrade.MachineUpgrade;
import github.kasuminova.novaeng.common.hypernet.upgrade.ProcessorModuleCPU;
import github.kasuminova.novaeng.common.hypernet.upgrade.ProcessorModuleRAM;
import github.kasuminova.novaeng.common.registry.RegistryHyperNet;
import github.kasuminova.novaeng.common.util.RandomUtils;
import hellfirepvp.modularmachinery.ModularMachinery;
import hellfirepvp.modularmachinery.common.lib.RequirementTypesMM;
import hellfirepvp.modularmachinery.common.machine.IOType;
import hellfirepvp.modularmachinery.common.machine.factory.FactoryRecipeThread;
import hellfirepvp.modularmachinery.common.modifier.RecipeModifier;
import hellfirepvp.modularmachinery.common.tiles.TileFactoryController;
import hellfirepvp.modularmachinery.common.tiles.base.TileMultiblockMachineController;
import io.netty.util.internal.shaded.org.jctools.queues.atomic.MpscLinkedAtomicQueue;
import net.minecraft.nbt.NBTTagCompound;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenMethod;
import stanhebben.zenscript.annotations.ZenSetter;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

@ZenRegister
@ZenClass("novaeng.hypernet.DataProcessor")
public class DataProcessor extends NetNode {
    private final MpscLinkedAtomicQueue<Long> recentEnergyUsage = new MpscLinkedAtomicQueue<>();
    private final MpscLinkedAtomicQueue<Float> recentCalculation = new MpscLinkedAtomicQueue<>();

    private final DataProcessorType type;
    private final LinkedList<Float> computationalLoadHistory = new LinkedList<>();

    private final List<ProcessorModuleCPU> moduleCPUS = new CopyOnWriteArrayList<>();
    private final List<ProcessorModuleRAM> moduleRAMS = new CopyOnWriteArrayList<>();

    private volatile int dynamicPatternSize = 0;

    private volatile int circuitDurability = 0;

    private volatile float maxGeneration = 0;
    private volatile float generated = 0;

    private int storedHU = 0;
    private boolean overheat = false;
    private float computationalLoadHistoryCache = 0;
    private float computationalLoad = 0;

    public DataProcessor(final TileMultiblockMachineController owner) {
        super(owner);
        this.type = RegistryHyperNet.getDataProcessorType(
                Objects.requireNonNull(owner.getFoundMachine()).getRegistryName().getPath()
        );
    }

    @ZenMethod
    public void onRecipeCheck(RecipeCheckEvent event) {
        if (centerPos == null || center == null) {
            event.setFailed("未连接至计算网络！");
            return;
        }

        if (overheat) {
            event.setFailed("处理器过热！");
            return;
        }

        if (circuitDurability < type.getCircuitDurability() * 0.05F) {
            event.setFailed("主电路板耐久过低，无法正常工作！");
            return;
        }

        if (moduleCPUS.isEmpty() && moduleRAMS.isEmpty()) {
            event.setFailed("未找到处理器和内存模块！");
            return;
        }

        if (moduleCPUS.isEmpty()) {
            event.setFailed("至少需要安装一个 CPU 或 GPU 模块！");
            return;
        }

        if (moduleRAMS.isEmpty()) {
            event.setFailed("至少需要安装一个内存模块！");
        }
    }

    @ZenMethod
    public void onDurabilityFixRecipeCheck(RecipeCheckEvent event, int durability) {
        if (circuitDurability + durability > type.getCircuitDurability()) {
            event.setFailed("novaeng.hypernet.craftcheck.durability.failed");
        }
    }

    @ZenMethod
    public void onWorkingTick(FactoryRecipeTickEvent event) {
        event.getActiveRecipe().setTick(0);

        if (centerPos == null) {
            event.setFailed(true, "未连接至计算网络！");
            return;
        }
        if (center == null) {
            event.setFailed(false, "未连接至计算网络！");
            return;
        }
        if (overheat) {
            event.setFailed(true, "处理器过热！");
            return;
        }

        long baseEnergyUsage = type.getEnergyUsage();
        long energyUsage = 0;

        Long usage;
        while ((usage = recentEnergyUsage.poll()) != null) {
            energyUsage += usage;
        }

        float heatPercent = getOverHeatPercent();
        if (heatPercent <= 0.1F) {
            energyUsage += (baseEnergyUsage / 10) * dynamicPatternSize > 0 ? dynamicPatternSize : 1;
        } else if (heatPercent <= 0.5F) {
            energyUsage += (baseEnergyUsage / 5) * dynamicPatternSize > 0 ? dynamicPatternSize : 1;
        } else if (heatPercent <= 0.75F) {
            energyUsage += (baseEnergyUsage / 2) * dynamicPatternSize > 0 ? dynamicPatternSize : 1;
        } else {
            energyUsage += baseEnergyUsage * dynamicPatternSize > 0 ? dynamicPatternSize : 1;
        }

        float mul = (float) ((double) (energyUsage + baseEnergyUsage) / baseEnergyUsage);
        event.getRecipeThread().addModifier("energy", new RecipeModifier(
                RequirementTypesMM.REQUIREMENT_ENERGY,
                IOType.INPUT, mul, 1, false
        ));
    }

    @ZenMethod
    public synchronized void fixCircuit(int durability) {
        circuitDurability = Math.min(circuitDurability + durability, type.getCircuitDurability());
        writeNBT();
    }

    @ZenMethod
    public void onMachineTick() {
        super.onMachineTick();

        if (!isWorking()) {
            generated = 0F;
            computationalLoad = 0F;
            computationalLoadHistoryCache = 0F;
            computationalLoadHistory.clear();
        } else {
            float totalCalculation = 0F;
            Float calculation;
            while ((calculation = recentCalculation.poll()) != null) {
                totalCalculation += calculation;
            }

            computationalLoadHistory.addFirst(totalCalculation);
            computationalLoadHistoryCache += totalCalculation;
            if (computationalLoadHistory.size() > 100) {
                computationalLoadHistoryCache -= computationalLoadHistory.pollLast();
            }

            computationalLoad = computationalLoadHistoryCache / computationalLoadHistory.size();
            ModularMachinery.EXECUTE_MANAGER.addSyncTask(() -> generated = 0F);
        }

        if (owner.getTicksExisted() % 20 == 0) {
            maxGeneration = getComputationPointProvision(0xFFFFFF);
            IDynamicPatternInfo dynamicPattern = owner.getDynamicPattern(type.getDynamicPatternName());
            if (dynamicPattern != null) {
                dynamicPatternSize = dynamicPattern.getSize();
            } else {
                dynamicPatternSize = 0;
            }
            writeNBT();
        }

        if (storedHU > 0) {
            int heatDist = calculateHeatDist();

            storedHU -= Math.min(heatDist, storedHU);
            if (storedHU <= 0) {
                overheat = false;
            }
            maxGeneration = getComputationPointProvision(0xFFFFFF);
            writeNBT();
        }
    }

    @ZenMethod
    public synchronized void onStructureUpdate() {
        moduleCPUS.clear();
        moduleRAMS.clear();
        moduleCPUS.addAll(ProcessorModuleCPU.filter(owner.getFoundUpgrades().values()));
        moduleRAMS.addAll(ProcessorModuleRAM.filter(owner.getFoundUpgrades().values()));
    }

    private int calculateHeatDist() {
        float heatPercent = getOverHeatPercent();
        float heatDist = type.getHeatDistribution();
        if (dynamicPatternSize > 1) {
            heatDist *= dynamicPatternSize;
        }

        if (heatPercent <= 0.25F) {
            heatDist *= 0.25F;
        } else if (heatPercent <= 0.75F) {
            heatDist *= 0.25F + (heatPercent);
        } else {
            heatDist *= 1.0F;
        }

        return (int) heatDist;
    }

    @Override
    public synchronized float requireComputationPoint(final float maxGeneration, final boolean doCalculate) {
        if (!isConnected() || center == null || !isWorking()) {
            return 0F;
        }

        float generation = Math.min(this.maxGeneration - this.generated, maxGeneration);
        if (generation < 0) {
            return 0F;
        }

        float generated = calculateComputationPointProvision(generation, doCalculate);

        if (doCalculate) {
            consumeCircuitDurability();
            doHeatGeneration(generated);
            this.generated += generation;
        }

        return generated;
    }

    private synchronized void consumeCircuitDurability() {
        if (owner.getTicksExisted() % 20 != 0) {
            return;
        }
        if (!(RandomUtils.nextFloat() <= (type.getCircuitConsumeChance() / getEfficiency()))) {
            return;
        }

        int min = type.getMinCircuitConsumeAmount();
        int max = type.getMaxCircuitConsumeAmount();

        int consume = min + RandomUtils.nextInt(max - min);
        if (dynamicPatternSize > 1) {
            consume *= dynamicPatternSize;
        }

        circuitDurability -= Math.min(consume, circuitDurability);
    }

    @Override
    public boolean isWorking() {
        if (!(owner instanceof TileFactoryController)) {
            return false;
        }

        TileFactoryController factory = (TileFactoryController) owner;
        FactoryRecipeThread thread = factory.getCoreRecipeThreads().get(DataProcessorType.PROCESSOR_WORKING_THREAD_NAME);

        return thread != null && thread.isWorking();
    }

    @ZenGetter("maxGeneration")
    public float getMaxGeneration() {
        return maxGeneration;
    }

    public float getEfficiency() {
        float overHeatPercent = getOverHeatPercent();
        return overHeatPercent >= 0.85F ? (1.0F - overHeatPercent) / 0.15F : 1F;
    }

    @ZenGetter("overHeatPercent")
    public float getOverHeatPercent() {
        return overheat ? 1F : (float) storedHU / type.getOverheatThreshold();
    }

    public void doHeatGeneration(float computationPointGeneration) {
        storedHU += (int) (computationPointGeneration * 2);
        if (storedHU >= type.getOverheatThreshold()) {
            overheat = true;
        }
    }

    public float calculateComputationPointProvision(float maxGeneration, boolean doCalculate) {
        if (overheat || !isWorking()) {
            return 0;
        }

        Map<String, List<MachineUpgrade>> upgrades = owner.getFoundUpgrades();
        if (upgrades.isEmpty()) {
            return 0;
        }

        if (moduleCPUS.isEmpty()) {
            return 0;
        }

        if (moduleRAMS.isEmpty()) {
            return 0;
        }

        long totalEnergyConsumption = 0;
        float maxGen = maxGeneration * getEfficiency();

        float generationLimit = 0F;
        float totalGenerated = 0F;

        for (ProcessorModuleRAM ram : moduleRAMS) {
            float generated = ram.calculate(true, maxGen - generationLimit);
            generationLimit += generated;
            if (doCalculate) {
                totalEnergyConsumption += (long) ((double) (generated / ram.getComputationPointGenerationLimit()) * ram.getEnergyConsumption());
            }
        }
        for (final ProcessorModuleCPU cpu : moduleCPUS) {
            float generated = cpu.calculate(true, generationLimit - totalGenerated);
            totalGenerated += generated;
            if (doCalculate) {
                totalEnergyConsumption += (long) ((double) (generated / cpu.getComputationPointGeneration()) * cpu.getEnergyConsumption());
            }
        }

        if (doCalculate) {
            recentCalculation.offer(totalGenerated);
            recentEnergyUsage.offer(totalEnergyConsumption);
        }

        return totalGenerated;
    }

    @Override
    public void readNBT(final NBTTagCompound customData) {
        super.readNBT(customData);
        this.storedHU = customData.getInteger("storedHU");
        if (customData.hasKey("overheat")) {
            this.overheat = customData.getBoolean("overheat");
        }

        if (customData.hasKey("circuitDurability")) {
            this.circuitDurability = customData.getInteger("circuitDurability");
        } else {
            this.circuitDurability = type.getCircuitDurability();
        }

        this.computationalLoad = customData.getFloat("computationalLoad");
        this.maxGeneration = customData.getFloat("maxGeneration");
    }

    @Override
    public void writeNBT() {
        super.writeNBT();
        NBTTagCompound tag = owner.getCustomDataTag();
        tag.setInteger("storedHU", storedHU);
        tag.setBoolean("overheat", overheat);
        tag.setInteger("circuitDurability", circuitDurability);
        tag.setFloat("computationalLoad", computationalLoad);
        tag.setFloat("maxGeneration", maxGeneration);
    }

    @Override
    public float getComputationPointProvision(final float maxGeneration) {
        return calculateComputationPointProvision(maxGeneration, false);
    }

    @ZenGetter("computationalLoad")
    public float getComputationalLoad() {
        return computationalLoad;
    }

    @ZenGetter("type")
    public DataProcessorType getType() {
        return type;
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

    @ZenGetter("storedHU")
    public int getStoredHU() {
        return storedHU;
    }

    @ZenSetter("storedHU")
    public void setStoredHU(final int storedHU) {
        this.storedHU = storedHU;
        writeNBT();
    }

    @ZenGetter("overheat")
    public boolean isOverheat() {
        return overheat;
    }
}
