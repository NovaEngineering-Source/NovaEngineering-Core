package github.kasuminova.novaeng.common.tile.efabricator;

import appeng.api.AEApi;
import appeng.api.config.Actionable;
import appeng.api.config.PowerMultiplier;
import appeng.api.networking.energy.IEnergyGrid;
import appeng.api.storage.IMEMonitor;
import appeng.api.storage.channels.IItemStorageChannel;
import appeng.api.storage.data.IAEItemStack;
import appeng.me.GridAccessException;
import appeng.me.helpers.AENetworkProxy;
import appeng.tile.inventory.AppEngInternalInventory;
import appeng.util.Platform;
import appeng.util.item.ItemList;
import github.kasuminova.mmce.client.util.ItemStackUtils;
import github.kasuminova.mmce.common.helper.IDynamicPatternInfo;
import github.kasuminova.mmce.common.world.MMWorldEventListener;
import github.kasuminova.mmce.common.world.MachineComponentManager;
import github.kasuminova.novaeng.NovaEngineeringCore;
import github.kasuminova.novaeng.client.util.BlockModelHider;
import github.kasuminova.novaeng.common.block.efabricator.BlockEFabricatorController;
import github.kasuminova.novaeng.common.block.efabricator.prop.Levels;
import github.kasuminova.novaeng.common.block.prop.FacingProp;
import github.kasuminova.novaeng.common.network.PktEFabricatorGUIData;
import github.kasuminova.novaeng.common.tile.TileCustomController;
import github.kasuminova.novaeng.common.tile.efabricator.EFabricatorParallelProc.Modifier;
import github.kasuminova.novaeng.common.util.MachineCoolants;
import hellfirepvp.modularmachinery.ModularMachinery;
import hellfirepvp.modularmachinery.client.ClientProxy;
import hellfirepvp.modularmachinery.common.machine.MachineRegistry;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static github.kasuminova.novaeng.common.block.efabricator.BlockEFabricatorController.*;

@SuppressWarnings({"FieldAccessedSynchronizedAndUnsynchronized", "unused"})
public class EFabricatorController extends TileCustomController {

    public static final int MAX_COOLANT_CACHE = 100_000;
    public static final int WORK_DELAY = 20;

    public static final List<BlockPos> HIDE_POS_LIST = Arrays.asList(
            // Center
            new BlockPos(0, 1, 0),
            new BlockPos(0, -1, 0),

            new BlockPos(0, 1, 1),
            new BlockPos(0, 0, 1),
            new BlockPos(0, -1, 1),

            // Left
            new BlockPos(1, 1, 0),
            new BlockPos(1, 0, 0),
            new BlockPos(1, -1, 0),

            new BlockPos(1, 1, 1),
            new BlockPos(1, 0, 1),
            new BlockPos(1, -1, 1),

            // Right
            new BlockPos(-1, 1, 0),
            new BlockPos(-1, 0, 0),
            new BlockPos(-1, -1, 0),

            new BlockPos(-1, 1, 1),
            new BlockPos(-1, 0, 1),
            new BlockPos(-1, -1, 1)
    );

    protected final List<EFabricatorPart> fabricatorParts = new ArrayList<>();
    protected final List<EFabricatorWorker> fabricatorWorkers = new ArrayList<>();
    protected final List<EFabricatorPatternBus> fabricatorPatternBuses = new ArrayList<>();
    protected final List<EFabricatorParallelProc> fabricatorParallelProcs = new ArrayList<>();

    protected final List<IFluidHandler> coolantInputHandlers = new ArrayList<>();
    protected final List<IFluidHandler> coolantOutputHandlers = new ArrayList<>();

    protected final IItemStorageChannel itemChannel = AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class);
    protected ItemList outputBuffer = new ItemList();

    protected BlockEFabricatorController parentController = null;
    protected double idleDrain = 64;

    protected EFabricatorMEChannel channel = null;

    protected boolean assembled = false;
    protected int length = 0;

    protected int workDelay = WORK_DELAY;
    protected int maxWorkDelay = WORK_DELAY;

    protected int parallelism = 0;
    protected int consumedParallelism = 0;

    protected int coolantCache = 0;

    protected long totalCrafted = 0;

    protected boolean speedupApplied = false;
    protected boolean overclocked = false;
    protected boolean activeCooling = false;

    protected PktEFabricatorGUIData guiDataPacket = null;

    public EFabricatorController(final ResourceLocation machineRegistryName) {
        this();
        this.parentMachine = MachineRegistry.getRegistry().getMachine(machineRegistryName);
        this.parentController = BlockEFabricatorController.REGISTRY.get(new ResourceLocation(NovaEngineeringCore.MOD_ID, machineRegistryName.getPath()));
    }

    public EFabricatorController() {
        this.workMode = WorkMode.SEMI_SYNC;
    }

    @Override
    public void doControllerTick() {
        final long tickStart = System.nanoTime();
        onSyncTick();
        timeRecorder.incrementUsedTime((int) TimeUnit.MICROSECONDS.convert(System.nanoTime() - tickStart, TimeUnit.NANOSECONDS));
    }

    protected void onSyncTick() {
        if (!this.doStructureCheck() || !this.isStructureFormed()) {
            disassemble();
            return;
        }
        assemble();

        if (channel == null || !channel.getProxy().isActive()) {
            this.tickExecutor = null;
            return;
        }

        workDelay--;
        if (workDelay > 0) {
            this.tickExecutor = null;
            return;
        }
        workDelay = maxWorkDelay;
        speedupApplied = false;
        clearOutputBuffer();
        supplyWorkerPower();
        supplyCoolantCache();
        this.tickExecutor = ModularMachinery.EXECUTE_MANAGER.addTask(this::onAsyncTick, timeRecorder.usedTimeAvg());
    }

    protected void onAsyncTick() {
        updateGUIDataPacket();
        fabricatorWorkers.forEach(worker -> worker.updateStatus(false));
        fabricatorWorkers.stream()
                .filter(EFabricatorWorker::hasWork)
                .mapToInt(EFabricatorWorker::doWork)
                .forEach(worked -> {
                    totalCrafted += worked;
                    consumedParallelism += worked <= 1 ? 0 : worked;
                });
        if (activeCooling && hasWork()) {
            convertOverflowParallelismToWorkDelay(parallelism - consumedParallelism);
        }
        consumedParallelism = 0;
        markNoUpdateSync();
    }

    protected void supplyCoolantCache() {
        if (coolantCache >= MAX_COOLANT_CACHE) {
            return;
        }

        // 写了一坨大的！
        for (final IFluidHandler inputHandler : coolantInputHandlers) {
            for (final IFluidTankProperties property : inputHandler.getTankProperties()) {
                FluidStack contents = property.getContents();
                if (contents == null || contents.amount == 0) {
                    continue;
                }

                MachineCoolants.Coolant coolant = MachineCoolants.INSTANCE.getCoolant(contents.getFluid());
                if (coolant == null) {
                    continue;
                }

                for (final IFluidHandler outputHandler : coolantOutputHandlers) {
                    int maxCanConsume = coolant.maxCanConsume(inputHandler, outputHandler);
                    if (maxCanConsume <= 0) {
                        continue;
                    }

                    int required = MAX_COOLANT_CACHE - coolantCache;
                    int mul = required / coolant.coolantUnit();
                    if (mul * coolant.coolantUnit() < required) {
                        mul++;
                    }
                    mul = Math.min(mul, maxCanConsume);

                    if (mul > 0) {
                        FluidStack input = coolant.input();
                        inputHandler.drain(new FluidStack(input, mul * input.amount), true);
                        FluidStack output = coolant.output();
                        if (output != null) {
                            outputHandler.fill(new FluidStack(output, mul * output.amount), true);
                        }
                        coolantCache += mul * coolant.coolantUnit();
                        if (coolantCache >= MAX_COOLANT_CACHE) {
                            return;
                        }
                    }
                }
            }
        }
    }

    protected void supplyWorkerPower() {
        IEnergyGrid energy;
        try {
            energy = channel.getProxy().getEnergy();
        } catch (GridAccessException ignored) {
            return;
        }

        fabricatorWorkers.stream()
                .filter(worker -> worker.getEnergyCache() < worker.getMaxEnergyCache())
                .forEach(worker -> worker.supplyEnergy(
                        (int) energy.extractAEPower(worker.getMaxEnergyCache() - worker.getEnergyCache(), Actionable.MODULATE, PowerMultiplier.CONFIG))
                );
    }

    protected void clearOutputBuffer() {
        try {
            AENetworkProxy proxy = this.channel.getProxy();
            IMEMonitor<IAEItemStack> inv = proxy.getStorage().getInventory(itemChannel);
            for (final IAEItemStack stack : outputBuffer) {
                IAEItemStack notInserted = Platform.poweredInsert(proxy.getEnergy(), inv, stack.copy(), this.channel.getSource());
                if (notInserted != null) {
                    stack.setStackSize(notInserted.getStackSize());
                } else {
                    stack.setStackSize(0);
                }
            }
        } catch (GridAccessException ignored) {
        }
    }

    @Override
    protected void updateComponents() {
        super.updateComponents();
        clearParts();
        IDynamicPatternInfo workers = getDynamicPattern("workers");
        this.length = workers != null ? workers.getSize() : 0;
        this.foundPattern.getTileBlocksArray().forEach((pos, info) -> {
            BlockPos realPos = getPos().add(pos);
            if (!this.getWorld().isBlockLoaded(realPos)) {
                return;
            }
            TileEntity te = this.getWorld().getTileEntity(realPos);
            if (!(te instanceof EFabricatorPart part)) {
                return;
            }
            part.setController(this);
            fabricatorParts.add(part);
            if (part instanceof EFabricatorWorker worker) {
                fabricatorWorkers.add(worker);
            }
            if (part instanceof EFabricatorParallelProc proc) {
                fabricatorParallelProcs.add(proc);
            }
            if (part instanceof EFabricatorPatternBus bus) {
                fabricatorPatternBuses.add(bus);
            }
            if (part instanceof EFabricatorMEChannel channel) {
                this.channel = channel;
            }
        });
        this.foundComponents.values().forEach(component -> {
            if (component.providedComponent() instanceof IFluidHandler handler) {
                switch (component.getComponent().ioType) {
                    case INPUT -> coolantInputHandlers.add(handler);
                    case OUTPUT -> coolantOutputHandlers.add(handler);
                }
            }
        });
        updateParallelism();
        updateWorkDelay();
    }

    @Override
    protected boolean canCheckStructure() {
        if (lastStructureCheckTick == -1 || (isStructureFormed() && !assembled)) {
            return true;
        }
        if (ticksExisted % 40 == 0) {
            return true;
        }
        if (isStructureFormed()) {
            BlockPos pos = getPos();
            Vec3i min = foundPattern.getMin();
            Vec3i max = foundPattern.getMax();
            return MMWorldEventListener.INSTANCE.isAreaChanged(getWorld(), pos.add(min), pos.add(max));
        }
        return ticksExisted % Math.min(structureCheckDelay + this.structureCheckCounter * 5, maxStructureCheckDelay) == 0;
    }

    protected void disassemble() {
        if (!assembled) {
            return;
        }
        assembled = false;
        this.fabricatorParts.forEach(EFabricatorPart::onDisassembled);
        clearParts();
    }

    protected void clearParts() {
        this.fabricatorParts.forEach(part -> part.setController(null));
        this.fabricatorParts.clear();
        this.fabricatorWorkers.clear();
        this.fabricatorPatternBuses.clear();
        this.fabricatorParallelProcs.clear();
        this.coolantInputHandlers.clear();
        this.coolantOutputHandlers.clear();
        this.channel = null;
        this.length = 0;
    }

    protected void updateParallelism() {
        final double[] parallelism = {0};
        Map<EFabricatorParallelProc.Type, List<Modifier>> modifierMap = fabricatorParallelProcs.stream()
                .flatMap(proc -> overclocked ? Stream.concat(proc.modifiers.stream(), proc.overclockModifiers.stream()) : proc.modifiers.stream()) // 超频额外添加超频修正器
                .filter(modifier -> modifier.isBuff() || !activeCooling) // 主动冷却移除超频的负面效果。
                .collect(Collectors.groupingBy(
                        Modifier::getType,
                        () -> new TreeMap<>(Comparator.comparingInt(EFabricatorParallelProc.Type::getPriority)),
                        Collectors.toList())
                );

        modifierMap.values().stream()
                .flatMap(Collection::stream)
                .filter(Modifier::isBuff)
                .forEach(modifier -> parallelism[0] = modifier.apply(parallelism[0]));
        modifierMap.values().stream()
                .flatMap(Collection::stream)
                .filter(Modifier::isDebuff)
                .forEach(modifier -> parallelism[0] = modifier.apply(parallelism[0]));

        this.parallelism = (int) Math.round(parallelism[0]);
    }

    public synchronized void convertOverflowParallelismToWorkDelay(final int overflow) {
        if (overflow <= 0 || speedupApplied) {
            return;
        }
        float ratio = (float) parallelism / overflow;
        int speedUp = Math.min(Math.round(ratio / 0.05f), maxWorkDelay - 1);

        double coolantUsage = parallelism * 0.04;
        int maxCanConsume = (int) (coolantCache / coolantUsage);
        speedUp = Math.min(speedUp, maxCanConsume);
        coolantCache -= (int) Math.round(speedUp * coolantUsage);

        this.workDelay = maxWorkDelay - speedUp;
        this.speedupApplied = true;
    }

    public void updateWorkDelay() {
        if (activeCooling) {
            this.maxWorkDelay = WORK_DELAY - this.fabricatorWorkers.size();
        } else {
            this.maxWorkDelay = WORK_DELAY;
        }
    }

    protected void assemble() {
        if (assembled) {
            return;
        }
        assembled = true;
        this.fabricatorParts.forEach(EFabricatorPart::onAssembled);
    }

    public void recalculateEnergyUsage() {
        double newIdleDrain = 64;
        this.idleDrain = newIdleDrain;
        if (this.channel != null) {
            this.channel.getProxy().setIdlePowerUsage(idleDrain);
        }
    }

    public boolean insertPattern(final ItemStack patternStack) {
        for (final EFabricatorPatternBus patternBus : fabricatorPatternBuses) {
            AppEngInternalInventory patternInv = patternBus.getPatterns();
            for (int i = 0; i < patternInv.getSlots(); i++) {
                if (patternInv.getStackInSlot(i).isEmpty()) {
                    patternInv.setStackInSlot(i, patternStack.copy());
                    return true;
                }
            }
        }

        return false;
    }

    public boolean offerWork(EFabricatorWorker.CraftWork work) {
        boolean success = false;
        for (EFabricatorWorker fabricatorWorker : fabricatorWorkers) {
            if (!fabricatorWorker.isFull()) {
                fabricatorWorker.offerWork(work);
                success = true;
                break;
            }
        }
        if (success && activeCooling && !speedupApplied) {
            convertOverflowParallelismToWorkDelay(parallelism);
        }
        return success;
    }

    public boolean isQueueFull() {
        for (final EFabricatorWorker worker : fabricatorWorkers) {
            if (!worker.isFull()) {
                return false;
            }
        }
        return true;
    }

    public boolean hasWork() {
        return fabricatorWorkers.stream().anyMatch(EFabricatorWorker::hasWork);
    }

    public synchronized void updateGUIDataPacket() {
        this.guiDataPacket = new PktEFabricatorGUIData(this);
    }

    public PktEFabricatorGUIData getGuiDataPacket() {
        if (guiDataPacket == null) {
            updateGUIDataPacket();
        }
        return guiDataPacket;
    }

    public long getTotalCrafted() {
        return totalCrafted;
    }

    public double getEnergyConsumePerTick() {
        return idleDrain;
    }

    public boolean isAssembled() {
        return assembled;
    }

    public int getLength() {
        return length;
    }

    public Levels getLevel() {
        if (parentController == L4) {
            return Levels.L4;
        }
        if (parentController == L6) {
            return Levels.L6;
        }
        if (parentController == L9) {
            return Levels.L9;
        }
        NovaEngineeringCore.log.warn("Invalid EFabricator controller level: {}", parentController);
        return Levels.L4;
    }

    public EFabricatorMEChannel getChannel() {
        return channel;
    }

    public List<EFabricatorPart> getFabricatorParts() {
        return fabricatorParts;
    }

    public List<EFabricatorWorker> getFabricatorWorkers() {
        return fabricatorWorkers;
    }

    public List<EFabricatorPatternBus> getFabricatorPatternBuses() {
        return fabricatorPatternBuses;
    }

    public int getParallelism() {
        return parallelism;
    }

    public int getAvailableParallelism() {
        return Math.max(0, parallelism - consumedParallelism);
    }

    public ItemList getOutputBuffer() {
        return outputBuffer;
    }

    public boolean isOverclocked() {
        return overclocked;
    }

    public EFabricatorController setOverclocked(final boolean overclocked) {
        this.overclocked = overclocked;
        updateParallelism();
        updateGUIDataPacket();
        return this;
    }

    public boolean isActiveCooling() {
        return activeCooling;
    }

    public EFabricatorController setActiveCooling(final boolean activeCooling) {
        this.activeCooling = activeCooling;
        updateParallelism();
        updateWorkDelay();
        updateGUIDataPacket();
        return this;
    }

    public int getEnergyStored() {
        return fabricatorWorkers.stream().mapToInt(EFabricatorWorker::getEnergyCache).sum();
    }

    public int getCoolantCache() {
        return coolantCache;
    }

    public void consumeCoolant(final int amount) {
        coolantCache -= amount;
    }

    public int getCoolantInputCap() {
        int total = 0;
        for (final IFluidHandler handler : coolantInputHandlers) {
            for (final IFluidTankProperties property : handler.getTankProperties()) {
                total += Math.min(property.getCapacity(), Integer.MAX_VALUE - total);
                if (total == Integer.MAX_VALUE) {
                    return Integer.MAX_VALUE;
                }
            }
        }
        return total;
    }

    public int getCoolantInputFluids() {
        int total = 0;
        for (final IFluidHandler handler : coolantInputHandlers) {
            for (final IFluidTankProperties property : handler.getTankProperties()) {
                FluidStack contents = property.getContents();
                if (contents == null || contents.amount == 0) {
                    continue;
                }
                if (MachineCoolants.INSTANCE.getCoolant(contents.getFluid()) != null) {
                    total += Math.min(contents.amount, Integer.MAX_VALUE - total);
                    if (total == Integer.MAX_VALUE) {
                        return Integer.MAX_VALUE;
                    }
                }
            }
        }
        return total;
    }

    public int getCoolantOutputCap() {
        int total = 0;
        for (final IFluidHandler handler : coolantOutputHandlers) {
            for (final IFluidTankProperties property : handler.getTankProperties()) {
                total += Math.min(property.getCapacity(), Integer.MAX_VALUE - total);
                if (total == Integer.MAX_VALUE) {
                    return Integer.MAX_VALUE;
                }
            }
        }
        return total;
    }

    public int getCoolantOutputFluids() {
        int total = 0;
        for (final IFluidHandler handler : coolantOutputHandlers) {
            for (final IFluidTankProperties property : handler.getTankProperties()) {
                FluidStack contents = property.getContents();
                if (contents == null || contents.amount == 0) {
                    continue;
                }
                total += Math.min(contents.amount, Integer.MAX_VALUE - total);
                if (total == Integer.MAX_VALUE) {
                    return Integer.MAX_VALUE;
                }
            }
        }
        return total;
    }

    @Override
    protected void checkRotation() {
        if (controllerRotation != null) {
            return;
        }
        IBlockState state = getWorld().getBlockState(getPos());
        if (state.getBlock() instanceof BlockEFabricatorController) {
            controllerRotation = state.getValue(FacingProp.HORIZONTALS);
        } else {
            NovaEngineeringCore.log.warn("Invalid estorage controller block at " + getPos() + " !");
            controllerRotation = EnumFacing.NORTH;
        }
    }

    @Override
    public void validate() {
        tileEntityInvalid = false;
        loaded = true;

        if (!FMLCommonHandler.instance().getEffectiveSide().isClient()) {
            return;
        }

        ClientProxy.clientScheduler.addRunnable(() -> {
            BlockModelHider.hideOrShowBlocks(HIDE_POS_LIST, this);
            notifyStructureFormedState(isStructureFormed());
        }, 0);
    }

    @Override
    public void invalidate() {
        tileEntityInvalid = true;
        loaded = false;
        foundComponents.forEach((te, component) -> MachineComponentManager.INSTANCE.removeOwner(te, this));
        disassemble();
        if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {
            BlockModelHider.hideOrShowBlocks(HIDE_POS_LIST, this);
        }
    }

    @Override
    public void onLoad() {
        loaded = true;
        if (!FMLCommonHandler.instance().getEffectiveSide().isClient()) {
            return;
        }
        ClientProxy.clientScheduler.addRunnable(() -> {
            BlockModelHider.hideOrShowBlocks(HIDE_POS_LIST, this);
            notifyStructureFormedState(isStructureFormed());
        }, 0);
    }

    @Override
    public void onChunkUnload() {
        super.onChunkUnload();
        disassemble();
    }

    @Override
    public void readCustomNBT(final NBTTagCompound compound) {
        boolean prevLoaded = loaded;
        loaded = false;

        super.readCustomNBT(compound);
        totalCrafted = compound.getLong("totalCrafted");
        overclocked = compound.getBoolean("overclock");
        activeCooling = compound.getBoolean("activeCooling");
        coolantCache = compound.getInteger("coolantCache");

        outputBuffer = new ItemList();
        NBTTagList list = compound.getTagList("outputBuffer", Constants.NBT.TAG_COMPOUND);
        IntStream.range(0, list.tagCount())
                .mapToObj(i -> itemChannel.createStack(ItemStackUtils.readNBTOversize(list.getCompoundTagAt(i))))
                .forEach(outputBuffer::add);

        loaded = prevLoaded;

        if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {
            ClientProxy.clientScheduler.addRunnable(() -> {
                BlockModelHider.hideOrShowBlocks(HIDE_POS_LIST, this);
                notifyStructureFormedState(isStructureFormed());
            }, 0);
        }
    }

    @Override
    public void writeCustomNBT(final NBTTagCompound compound) {
        super.writeCustomNBT(compound);
        compound.setLong("totalCrafted", totalCrafted);
        compound.setBoolean("overclock", overclocked);
        compound.setBoolean("activeCooling", activeCooling);
        compound.setInteger("coolantCache", coolantCache);

        NBTTagList list = new NBTTagList();
        synchronized (outputBuffer) {
            for (final IAEItemStack stack : outputBuffer) {
                list.appendTag(ItemStackUtils.writeNBTOversize(stack.getCachedItemStack(stack.getStackSize())));
            }
        }
        compound.setTag("outputBuffer", list);
    }

    @Override
    protected void readMachineNBT(final NBTTagCompound compound) {
        super.readMachineNBT(compound);
        if (compound.hasKey("parentMachine")) {
            ResourceLocation rl = new ResourceLocation(compound.getString("parentMachine"));
            parentMachine = MachineRegistry.getRegistry().getMachine(rl);
            if (parentMachine != null) {
                this.parentController = BlockEFabricatorController.REGISTRY.get(new ResourceLocation(NovaEngineeringCore.MOD_ID, parentMachine.getRegistryName().getPath()));
            } else {
                ModularMachinery.log.info("Couldn't find machine named " + rl + " for controller at " + getPos());
            }
        }
    }

    @Override
    public boolean isWorking() {
        return assembled;
    }

}
