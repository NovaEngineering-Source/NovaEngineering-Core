package github.kasuminova.novaeng.common.hypernet.proc.server;

import github.kasuminova.novaeng.common.container.slot.AssemblySlotManager;
import github.kasuminova.novaeng.common.hypernet.proc.CalculateReply;
import github.kasuminova.novaeng.common.hypernet.proc.CalculateRequest;
import github.kasuminova.novaeng.common.hypernet.proc.CalculateType;
import github.kasuminova.novaeng.common.hypernet.proc.CalculateTypes;
import github.kasuminova.novaeng.common.hypernet.proc.server.assembly.*;
import github.kasuminova.novaeng.common.hypernet.proc.server.exception.EnergyDeficitException;
import github.kasuminova.novaeng.common.hypernet.proc.server.exception.EnergyOverloadException;
import github.kasuminova.novaeng.common.hypernet.proc.server.exception.ModularServerException;
import github.kasuminova.novaeng.common.hypernet.proc.server.module.ServerModule;
import github.kasuminova.novaeng.common.hypernet.proc.server.module.base.ServerModuleBase;
import github.kasuminova.novaeng.common.registry.ServerModuleRegistry;
import github.kasuminova.novaeng.common.util.ServerModuleInv;
import hellfirepvp.modularmachinery.common.tiles.base.TileEntitySynchronized;
import hellfirepvp.modularmachinery.common.util.ItemUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.function.Consumer;

public class ModularServer extends CalculateServer implements ServerInvProvider {
    protected final AssemblySlotManager slotManager = new AssemblySlotManager(this);

    protected final TileEntitySynchronized owner;

    protected final Properties prop = new Properties();

    protected final List<ServerModule> modules = new ArrayList<>();
    protected final List<Extension> extensions = new LinkedList<>();

    protected final Map<CalculateType, TreeSet<Calculable>> calculableTypeSet = new HashMap<>();

    protected final Map<Class<?>, List<ServerModule>> typeModulesCache = new HashMap<>();
    protected final Map<ServerModuleBase<?>, List<ServerModule>> baseModulesCache = new HashMap<>();

    protected Consumer<ModularServer> onServerInvChangedListener = null;

    protected boolean started = false;

    protected long energyCap = 0;
    protected long maxEnergyCap = 0;

    protected long energyConsumed = 0;
    protected long maxEnergyConsumption = 0;
    protected long maxEnergyProvision = 0;

    protected int heatGenerated = 0;

    protected int totalHardwareBandwidth = 0;
    protected int usedHardwareBandwidth = 0;

    protected ItemStack cachedStack;

    protected ServerModuleInv assemblyCPUInv = null;
    protected ServerModuleInv assemblyCalculateCardInv = null;
    protected ServerModuleInv assemblyExtensionInv = null;
    protected ServerModuleInv assemblyHeatRadiatorInv = null;
    protected ServerModuleInv assemblyPowerInv = null;

    public ModularServer(final TileEntitySynchronized owner, final ItemStack stack) {
        this.owner = owner;
        this.cachedStack = stack;
    }

    public boolean requiresUpdate(final ItemStack newStack) {
        return newStack != cachedStack && !ItemUtils.matchStacks(cachedStack, newStack);
    }

    public ItemStack getCachedStack() {
        return cachedStack;
    }

    public void setCachedStack(final ItemStack cachedStack) {
        this.cachedStack = cachedStack;
    }

    @Override
    public CalculateReply calculate(final CalculateRequest request) {
        if (!started) {
            return new CalculateReply(0);
        }
        TreeSet<Calculable> calculableSet = calculableTypeSet.get(request.type());
        if (calculableSet == null) {
            return new CalculateReply(0);
        }

        double totalGenerated = 0;
        double maxRequired = request.maxRequired();
        for (final Extension extension : extensions) {
            extension.onCalculate(request);
        }
        for (final Calculable calculable : calculableSet) {
            try {
                totalGenerated += calculable.calculate(request.subtractMaxRequired(totalGenerated));
            } catch (ModularServerException e) {
                started = false;
                return new CalculateReply(0);
            }
            if (totalGenerated >= maxRequired) {
                break;
            }
        }

        return new CalculateReply(totalGenerated);
    }

    // Modules

    public void initModules() {
        modules.clear();
        extensions.clear();
        calculableTypeSet.clear();
        typeModulesCache.clear();
        baseModulesCache.clear();

        scanInvModules(assemblyCPUInv);
        scanInvModules(assemblyCalculateCardInv);
        scanInvModules(assemblyExtensionInv);
        scanInvModules(assemblyPowerInv);
        scanInvModules(assemblyHeatRadiatorInv);

        recalculateHardwareBandwidth();
    }

    protected void scanInvModules(ServerModuleInv moduleInv) {
        moduleInv.getAvailableSlotsStream().forEach(slot -> {
            if (!slotManager.getSlot(moduleInv.getInvName(), slot).isAvailable()) {
                return;
            }
            ItemStack stackInSlot = moduleInv.getStackInSlot(slot);
            if (stackInSlot.isEmpty()) {
                return;
            }
            ServerModuleBase<?> module = ServerModuleRegistry.getModule(stackInSlot);
            if (module == null) {
                return;
            }

            ServerModule moduleInstance = module.createInstance(this, stackInSlot);
            modules.add(moduleInstance);
            baseModulesCache.computeIfAbsent(module, v -> new ArrayList<>()).add(moduleInstance);

            if (moduleInstance instanceof Extension) {
                addExtension((Extension) moduleInstance);
            }
            if (moduleInstance instanceof Calculable) {
                addCalculable((Calculable) moduleInstance);
            }
        });
    }

    public void addExtension(@Nonnull final Extension extension) {
        extensions.add(extension);
    }

    public void addCalculable(@Nonnull final Calculable calculable) {
        for (CalculateType calculateType : CalculateTypes.getAvailableTypes().values()) {
            if (calculable.getCalculateTypeEfficiency(calculateType) <= 0D) {
                continue;
            }
            calculableTypeSet.computeIfAbsent(calculateType, v -> new TreeSet<>((o1, o2) -> {
                double efficiencyLeft = o1.getCalculateTypeEfficiency(calculateType);
                double efficiencyRight = o2.getCalculateTypeEfficiency(calculateType);
                if (efficiencyLeft == efficiencyRight) {
                    return 0;
                }
                return efficiencyLeft < efficiencyRight ? 1 : -1;
            })).add(calculable);
        }
    }

    // Hardware Bandwidth

    public void recalculateHardwareBandwidth() {
        int totalProvided = 0;
        int totalConsumed = 0;

        for (final ServerModule module : modules) {
            if (module instanceof HardwareBandwidthProvider provider) {
                totalProvided += provider.getHardwareBandwidthProvision();
            } else if (module instanceof HardwareBandwidthConsumer consumer) {
                totalConsumed += consumer.getHardwareBandwidth();
            }
        }

        totalHardwareBandwidth = totalProvided;
        usedHardwareBandwidth = totalConsumed;
    }

    public int getTotalHardwareBandwidth() {
        return totalHardwareBandwidth;
    }

    public int getUsedHardwareBandwidth() {
        return usedHardwareBandwidth;
    }

    // Energy System

    public long provideEnergy(long amount) {
        long maxCanProvide = Math.min(Math.min(maxEnergyCap - energyCap, maxEnergyProvision), amount);
        energyCap += maxCanProvide;
        return maxCanProvide;
    }

    public long consumeEnergy(long amount) throws ModularServerException {
        if (amount > energyCap) {
            energyConsumed += energyCap;
            energyCap = 0;
            throw new EnergyDeficitException();
        }

        long maxCanConsume = Math.min(maxEnergyConsumption - energyConsumed, amount);
        energyConsumed += maxCanConsume;
        energyCap -= maxCanConsume;

        if (amount > maxCanConsume) {
            throw new EnergyOverloadException();
        }
        return maxCanConsume;
    }

    public long getEnergyCap() {
        return energyCap;
    }

    // Heat System

    public void generateHeat(int amount) {
        heatGenerated += amount;
    }

    public int removeHeat() {
        int prev = heatGenerated;
        heatGenerated = 0;
        return prev;
    }

    // typeModules

    public List<ServerModule> getModulesByType(Class<? extends ServerModule> type) {
        List<ServerModule> cache = typeModulesCache.get(type);
        if (cache != null) {
            return cache;
        }

        List<ServerModule> matched = new ArrayList<>();
        for (final ServerModule module : modules) {
            if (module.getClass().isAssignableFrom(type)) {
                matched.add(module);
            }
        }

        typeModulesCache.put(type, matched);
        return matched;
    }

    // baseModules

    public List<ServerModule> getModulesByBase(ServerModuleBase<?> base) {
        return baseModulesCache.getOrDefault(base, Collections.emptyList());
    }

    // NBT read / write

    public void readFullInvNBT(final NBTTagCompound stackTag) {
        readAssemblyCPUInv(stackTag.getCompoundTag("cpuInv"));
        readAssemblyCalculateCardInv(stackTag.getCompoundTag("calInv"));
        readAssemblyExtensionInv(stackTag.getCompoundTag("extInv"));
        readAssemblyHeatRadiatorInv(stackTag.getCompoundTag("heatInv"));
        readAssemblyPowerInv(stackTag.getCompoundTag("powerInv"));

        prop.readNBT(stackTag.getCompoundTag("prop"));

        slotManager.initSlots();
    }

    public NBTTagCompound writeNBT() {
        NBTTagCompound tag = new NBTTagCompound();

        tag.setTag("cpuInv", assemblyCPUInv.writeNBT());
        tag.setTag("calInv", assemblyCalculateCardInv.writeNBT());
        tag.setTag("extInv", assemblyExtensionInv.writeNBT());
        tag.setTag("heatInv", assemblyHeatRadiatorInv.writeNBT());
        tag.setTag("powerInv", assemblyPowerInv.writeNBT());
        tag.setTag("prop", prop.writeNBT());

        return tag;
    }

    // ServerModule inv

    public void initInv() {
        createDefaultAssemblyCPUInv();
        createDefaultAssemblyCalculateCardInv();
        createDefaultAssemblyExtensionInv();
        createDefaultAssemblyHeatRadiatorInv();
        createDefaultAssemblyPowerInv();

        slotManager.initSlots();
    }

    public void onAssemblyInvUpdate(final ServerModuleInv changedInv) {

    }

    public void readAssemblyCPUInv(final NBTTagCompound stackTag) {
        if (stackTag.isEmpty()) {
            createDefaultAssemblyCPUInv();
            return;
        }
        assemblyCPUInv.readNBT(stackTag);
    }

    public void createDefaultAssemblyCPUInv() {
        assemblyCPUInv = ServerModuleInv.create(owner, AssemblyInvCPUConst.INV_SIZE, "cpu")
                .setOnChangedListener(this::onAssemblyInvUpdate);
    }

    public void readAssemblyCalculateCardInv(final NBTTagCompound stackTag) {
        if (stackTag.isEmpty()) {
            createDefaultAssemblyCalculateCardInv();
            return;
        }
        assemblyCalculateCardInv.readNBT(stackTag);
    }

    public void createDefaultAssemblyCalculateCardInv() {
        assemblyCalculateCardInv = ServerModuleInv.create(owner, AssemblyInvCalculateCardConst.INV_SIZE, "calculate_card")
                .setOnChangedListener(this::onAssemblyInvUpdate);
    }

    public void readAssemblyExtensionInv(final NBTTagCompound stackTag) {
        if (stackTag.isEmpty()) {
            createDefaultAssemblyExtensionInv();
            return;
        }
        assemblyExtensionInv.readNBT(stackTag);
    }

    public void createDefaultAssemblyExtensionInv() {
        assemblyExtensionInv = ServerModuleInv.create(owner, AssemblyInvExtensionConst.INV_SIZE, "extension")
                .setOnChangedListener(this::onAssemblyInvUpdate);
    }

    public void readAssemblyHeatRadiatorInv(final NBTTagCompound stackTag) {
        if (stackTag.isEmpty()) {
            createDefaultAssemblyHeatRadiatorInv();
            return;
        }
        assemblyHeatRadiatorInv.readNBT(stackTag);
        initAssemblyHeatRadiatorStackLimit();
    }

    public void createDefaultAssemblyHeatRadiatorInv() {
        assemblyHeatRadiatorInv = ServerModuleInv.create(owner, AssemblyInvHeatRadiatorConst.INV_SIZE, "heat_radiator")
                .setOnChangedListener(this::onAssemblyInvUpdate);;
        initAssemblyHeatRadiatorStackLimit();
    }

    public void initAssemblyHeatRadiatorStackLimit() {
        assemblyHeatRadiatorInv.setStackLimit(AssemblyInvHeatRadiatorConst.CPU_HEAT_RADIATOR_SLOT_ID, AssemblyInvHeatRadiatorConst.CPU_HEAT_RADIATOR_AMOUNT);
        assemblyHeatRadiatorInv.setStackLimit(AssemblyInvHeatRadiatorConst.RAM_HEAT_RADIATOR_SLOT_ID, AssemblyInvHeatRadiatorConst.RAM_HEAT_RADIATOR_AMOUNT);
        assemblyHeatRadiatorInv.setStackLimit(AssemblyInvHeatRadiatorConst.CALCULATE_CARD_HEAT_RADIATOR_SLOT_ID, AssemblyInvHeatRadiatorConst.CALCULATE_CARD_HEAT_RADIATOR_AMOUNT);
        assemblyHeatRadiatorInv.setStackLimit(AssemblyInvHeatRadiatorConst.COPPER_PIPE_SLOT_ID, AssemblyInvHeatRadiatorConst.COPPER_PIPE_AMOUNT);
    }

    public void readAssemblyPowerInv(final NBTTagCompound stackTag) {
        if (stackTag.isEmpty()) {
            createDefaultAssemblyPowerInv();
            return;
        }
        assemblyPowerInv.readNBT(stackTag);
    }

    public void createDefaultAssemblyPowerInv() {
        assemblyPowerInv = ServerModuleInv.create(owner, AssemblyInvPowerConst.INV_SIZE, "power")
                .setOnChangedListener(this::onAssemblyInvUpdate);;
    }

    @Override
    public ServerModuleInv getInvByName(final String name) {
        return switch (name) {
            case "cpu" -> assemblyCPUInv;
            case "calculate_card" -> assemblyCalculateCardInv;
            case "extension" -> assemblyExtensionInv;
            case "heat_radiator" -> assemblyHeatRadiatorInv;
            case "power" -> assemblyPowerInv;
            default -> null;
        };
    }

    public void setOnServerInvChangedListener(final Consumer<ModularServer> onServerInvChangedListener) {
        this.onServerInvChangedListener = onServerInvChangedListener;
    }

    public AssemblySlotManager getSlotManager() {
        return slotManager;
    }

    public static class Properties {
        private float unlockLimit = 2.0F;

        public NBTTagCompound writeNBT() {
            NBTTagCompound tag = new NBTTagCompound();
            tag.setFloat("unlockLimit", unlockLimit);
            return tag;
        }

        public void readNBT(NBTTagCompound tag) {
            unlockLimit = tag.getFloat("unlockLimit");
        }
    }
}
