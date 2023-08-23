package github.kasuminova.novaeng.common.hypernet;

import crafttweaker.annotations.ZenRegister;
import github.kasuminova.mmce.common.event.recipe.FactoryRecipeTickEvent;
import github.kasuminova.mmce.common.event.recipe.RecipeCheckEvent;
import github.kasuminova.mmce.common.upgrade.MachineUpgrade;
import github.kasuminova.novaeng.common.hypernet.upgrade.ProcessorModuleCPU;
import github.kasuminova.novaeng.common.hypernet.upgrade.ProcessorModuleRAM;
import github.kasuminova.novaeng.common.registry.RegistryHyperNet;
import github.kasuminova.novaeng.common.util.RandomUtils;
import hellfirepvp.modularmachinery.common.lib.RequirementTypesMM;
import hellfirepvp.modularmachinery.common.machine.IOType;
import hellfirepvp.modularmachinery.common.machine.factory.FactoryRecipeThread;
import hellfirepvp.modularmachinery.common.modifier.RecipeModifier;
import hellfirepvp.modularmachinery.common.tiles.TileFactoryController;
import hellfirepvp.modularmachinery.common.tiles.base.TileMultiblockMachineController;
import hellfirepvp.modularmachinery.common.util.MiscUtils;
import io.netty.util.internal.shaded.org.jctools.queues.SpscLinkedQueue;
import net.minecraft.nbt.NBTTagCompound;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenMethod;
import stanhebben.zenscript.annotations.ZenSetter;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@ZenRegister
@ZenClass("novaeng.hypernet.DataProcessor")
public class DataProcessor extends NetNode {
    private final SpscLinkedQueue<Long> recentEnergyUsage = new SpscLinkedQueue<>();
    private final SpscLinkedQueue<Float> recentCalculation = new SpscLinkedQueue<>();

    private final DataProcessorType type;
    private final LinkedList<Float> computationalLoadHistory = new LinkedList<>();

    private int circuitDurability = 0;
    private int storedHU = 0;
    private boolean overheat = false;
    private float computationalLoadHistoryCache = 0;
    private float computationalLoad = 0;
    private float maxGeneration = 0;

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

        Map<String, List<MachineUpgrade>> upgrades = owner.getFoundUpgrades();
        if (upgrades.isEmpty()) {
            event.setFailed("未找到处理器和内存模块！");
            return;
        }

        List<MachineUpgrade> flattened = MiscUtils.flatten(upgrades.values());
        if (ProcessorModuleRAM.filter(flattened).isEmpty()) {
            event.setFailed("至少需要安装一个内存模块！");
            return;
        }

        if (ProcessorModuleCPU.filter(flattened).isEmpty()) {
            event.setFailed("至少需要安装一个 CPU 或 GPU 模块！");
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

        if (energyUsage == 0) {
            event.getRecipeThread().removeModifier("energy");
        } else {
            float mul = (float) ((double) (energyUsage + baseEnergyUsage) / baseEnergyUsage);
            event.getRecipeThread().addModifier("energy", new RecipeModifier(
                    RequirementTypesMM.REQUIREMENT_ENERGY,
                    IOType.INPUT, mul, 1, false
            ));
        }

        float totalCalculation = 0;
        Float calculation;
        while((calculation = recentCalculation.poll()) != null) {
            totalCalculation += calculation;
        }

        computationalLoadHistory.addFirst(totalCalculation);
        computationalLoadHistoryCache += totalCalculation;
        if (computationalLoadHistory.size() > 100) {
            computationalLoadHistoryCache -= computationalLoadHistory.pollLast();
        }

        computationalLoad = computationalLoadHistoryCache / computationalLoadHistory.size();
    }

    @ZenMethod
    public void fixCircuit(int durability) {
        circuitDurability = Math.min(circuitDurability + durability, type.getCircuitDurability());
        writeNBT();
    }

    @ZenMethod
    public void onMachineTick() {
        super.onMachineTick();

        if (!isWorking()) {
            computationalLoad = 0;
            computationalLoadHistory.clear();
        }

        if (owner.getTicksExisted() % 20 == 0) {
            maxGeneration = getComputationPointProvision(0xFFFFFF);
            writeNBT();
        }

        if (storedHU > 0) {
            storedHU -= Math.min(type.getHeatDistribution(), storedHU);
            if (storedHU <= 0) {
                overheat = false;
            }
            maxGeneration = getComputationPointProvision(0xFFFFFF);
            writeNBT();
        }
    }

    @Override
    public float requireComputationPoint(final float maxGeneration, final boolean doCalculate) {
        if (!isConnected() || center == null || !isWorking()) {
            return 0F;
        }

        float generation = calculateComputationPointProvision(maxGeneration, doCalculate);

        if (doCalculate) {
            consumeCircuitDurability();
            doHeatGeneration(generation);
            writeNBT();
        }

        return generation;
    }

    private void consumeCircuitDurability() {
        if (owner.getTicksExisted() % 20 != 0) {
            return;
        }
        if (!(RandomUtils.nextFloat() <= (type.getCircuitConsumeChance() / getEfficiency()))) {
            return;
        }

        int min = type.getMinCircuitConsumeAmount();
        int max = type.getMaxCircuitConsumeAmount();

        circuitDurability -= Math.min(min + RandomUtils.nextInt(max - min), circuitDurability);
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
        return overHeatPercent >= 0.95F ? 1.0F - (overHeatPercent - 0.5F) * 2F : 1F;
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
        List<MachineUpgrade> flattened = MiscUtils.flatten(upgrades.values());

        List<ProcessorModuleRAM> moduleRAMs = ProcessorModuleRAM.filter(flattened);
        if (moduleRAMs.isEmpty()) {
            return 0;
        }

        List<ProcessorModuleCPU> moduleCPUs = ProcessorModuleCPU.filter(flattened);
        if (moduleCPUs.isEmpty()) {
            return 0;
        }

        long totalEnergyConsumption = 0;
        float maxGen = maxGeneration * getEfficiency();

        float generationLimit = 0F;
        for (ProcessorModuleRAM ram : moduleRAMs) {
            float generated = ram.calculate(doCalculate, maxGen - generationLimit);
            generationLimit += generated;
            if (doCalculate) {
                totalEnergyConsumption += (long) ((double) (generated / ram.getComputationPointGenerationLimit()) * ram.getEnergyConsumption());
            }
        }

        float totalGenerated = 0F;
        for (final ProcessorModuleCPU cpu : moduleCPUs) {
            float generated = cpu.calculate(doCalculate, generationLimit - totalGenerated);
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
    }

    @ZenGetter("overheat")
    public boolean isOverheat() {
        return overheat;
    }
}
