package github.kasuminova.novaeng.common.hypernet.proc.server;

import github.kasuminova.novaeng.common.hypernet.proc.CalculateReply;
import github.kasuminova.novaeng.common.hypernet.proc.CalculateRequest;
import github.kasuminova.novaeng.common.hypernet.proc.CalculateType;
import github.kasuminova.novaeng.common.hypernet.proc.CalculateTypes;
import hellfirepvp.modularmachinery.common.util.IOInventory;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.*;

public class ModularServer extends CalculateServer implements ServerInvProvider {

    protected final Map<CalculateType, TreeSet<Calculable>> calculableTypeSet = new HashMap<>();
    protected final List<Extension> extensions = new LinkedList<>();

    protected long energyCap = 0;
    protected long maxEnergyCap = 0;

    protected long energyConsumed = 0;
    protected long maxEnergyConsume = 0;

    protected int heatGenerated = 0;

    protected ItemStack cachedStack;

    protected IOInventory serverInv;
    protected IOInventory assemblyCPUInv;
    protected IOInventory assemblyCalculateCardInv;
    protected IOInventory assemblyExtensionInv;
    protected IOInventory assemblyHeatRadiatorInv;
    protected IOInventory assemblyPowerInv;

    public ModularServer(final ItemStack stack) {
        this.cachedStack = stack;
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
        if (amount > energyCap) {
            long prev = energyCap;
            energyCap = 0;
            return prev;
        } else {
            energyCap -= amount;
            return amount;
        }
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
    public IOInventory getInvByName(final String name) {
        return switch (name) {
            case "server" -> serverInv;
            case "cpu" -> assemblyCPUInv;
            case "calculate_card" -> assemblyCalculateCardInv;
            case "heat_radiator" -> assemblyHeatRadiatorInv;
            case "power" -> assemblyPowerInv;
            default -> null;
        };
    }
}
