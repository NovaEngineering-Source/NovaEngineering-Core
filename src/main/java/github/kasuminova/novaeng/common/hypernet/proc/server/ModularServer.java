package github.kasuminova.novaeng.common.hypernet.proc.server;

import github.kasuminova.novaeng.common.container.slot.AssemblySlotManager;
import github.kasuminova.novaeng.common.hypernet.proc.CalculateReply;
import github.kasuminova.novaeng.common.hypernet.proc.CalculateRequest;
import github.kasuminova.novaeng.common.hypernet.proc.CalculateType;
import github.kasuminova.novaeng.common.hypernet.proc.CalculateTypes;
import github.kasuminova.novaeng.common.hypernet.proc.server.assembly.*;
import github.kasuminova.novaeng.common.util.ServerModuleInv;
import hellfirepvp.modularmachinery.common.tiles.base.TileEntitySynchronized;
import hellfirepvp.modularmachinery.common.util.ItemUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nonnull;
import java.util.*;

public class ModularServer extends CalculateServer implements ServerInvProvider {
    protected final AssemblySlotManager slotManager = new AssemblySlotManager(this);

    protected final TileEntitySynchronized owner;

    protected final Map<CalculateType, TreeSet<Calculable>> calculableTypeSet = new HashMap<>();
    protected final List<Extension> extensions = new LinkedList<>();

    protected long energyCap = 0;
    protected long maxEnergyCap = 0;

    protected long energyConsumed = 0;
    protected long maxEnergyConsume = 0;

    protected int heatGenerated = 0;

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
        return !ItemUtils.matchStacks(cachedStack, newStack);
    }

    @Override
    public CalculateReply calculate(final CalculateRequest request) {
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
            totalGenerated += calculable.calculate(request.subtractMaxRequired(totalGenerated));
            if (totalGenerated >= maxRequired) {
                break;
            }
        }

        return new CalculateReply(totalGenerated);
    }

    public long provideEnergy(long amount) {
        long maxCanProvide = maxEnergyCap - energyCap;
        if (amount > maxCanProvide) {
            energyCap = maxEnergyCap;
            return maxCanProvide;
        } else {
            energyCap += amount;
            return amount;
        }
    }

    public long consumeEnergy(long amount) {
        long maxCanConsume = Math.min(Math.min(maxEnergyConsume - energyConsumed, energyCap), amount);
        energyConsumed += maxCanConsume;
        energyCap -= maxCanConsume;
        return maxCanConsume;
    }

    public void generateHeat(int amount) {
        heatGenerated += amount;
    }

    public int removeHeat() {
        int prev = heatGenerated;
        heatGenerated = 0;
        return prev;
    }

    public void addExtension(@Nonnull final Extension extension) {
        extensions.add(extension);
    }

    public void addCalculable(@Nonnull final Calculable calculable) {
        for (CalculateType calculateType : CalculateTypes.getAvailableTypes().values()) {
            if (calculable.getCalculateTypeEfficiency(calculateType) <= 0) {
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

    // NBT read / write

    public void readFullInvNBT(final NBTTagCompound stackTag) {
        readAssemblyCPUInv(stackTag.getCompoundTag("cpuInv"));
        readAssemblyCalculateCardInv(stackTag.getCompoundTag("calInv"));
        readAssemblyExtensionInv(stackTag.getCompoundTag("extInv"));
        readAssemblyHeatRadiatorInv(stackTag.getCompoundTag("heatInv"));
        readAssemblyPowerInv(stackTag.getCompoundTag("powerInv"));

        slotManager.initSlots();
    }

    public void initInv() {
        createDefaultAssemblyCPUInv();
        createDefaultAssemblyCalculateCardInv();
        createDefaultAssemblyExtensionInv();
        createDefaultAssemblyHeatRadiatorInv();
        createDefaultAssemblyPowerInv();

        slotManager.initSlots();
    }

    public void readAssemblyCPUInv(final NBTTagCompound stackTag) {
        if (stackTag.isEmpty()) {
            createDefaultAssemblyCPUInv();
            return;
        }
        assemblyCPUInv.readNBT(stackTag);
    }

    public void createDefaultAssemblyCPUInv() {
        assemblyCPUInv = ServerModuleInv.create(owner, AssemblyInvCPUConst.INV_SIZE);
    }

    public void readAssemblyCalculateCardInv(final NBTTagCompound stackTag) {
        if (stackTag.isEmpty()) {
            createDefaultAssemblyCalculateCardInv();
            return;
        }
        assemblyCalculateCardInv.readNBT(stackTag);
    }

    public void createDefaultAssemblyCalculateCardInv() {
        assemblyCalculateCardInv = ServerModuleInv.create(owner, AssemblyInvCalculateCardConst.INV_SIZE);
    }

    public void readAssemblyExtensionInv(final NBTTagCompound stackTag) {
        if (stackTag.isEmpty()) {
            createDefaultAssemblyExtensionInv();
            return;
        }
        assemblyExtensionInv.readNBT(stackTag);
    }

    public void createDefaultAssemblyExtensionInv() {
        assemblyExtensionInv = ServerModuleInv.create(owner, AssemblyInvExtensionConst.INV_SIZE);
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
        assemblyHeatRadiatorInv = ServerModuleInv.create(owner, AssemblyInvHeatRadiatorConst.INV_SIZE);
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
        assemblyPowerInv = ServerModuleInv.create(owner, AssemblyInvPowerConst.INV_SIZE);
    }

    public NBTTagCompound writeNBT() {
        NBTTagCompound tag = new NBTTagCompound();

        tag.setTag("cpuInv", assemblyCPUInv.writeNBT());
        tag.setTag("calInv", assemblyCalculateCardInv.writeNBT());
        tag.setTag("extInv", assemblyExtensionInv.writeNBT());
        tag.setTag("heatInv", assemblyHeatRadiatorInv.writeNBT());
        tag.setTag("powerInv", assemblyPowerInv.writeNBT());

        return tag;
    }

    public AssemblySlotManager getSlotManager() {
        return slotManager;
    }
}
