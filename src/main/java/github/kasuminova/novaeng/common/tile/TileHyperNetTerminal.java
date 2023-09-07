package github.kasuminova.novaeng.common.tile;

import github.kasuminova.mmce.common.event.Phase;
import github.kasuminova.novaeng.NovaEngineeringCore;
import github.kasuminova.novaeng.common.block.BlockHyperNetTerminal;
import github.kasuminova.novaeng.common.hypernet.HyperNetTerminal;
import hellfirepvp.modularmachinery.ModularMachinery;
import hellfirepvp.modularmachinery.common.block.BlockController;
import hellfirepvp.modularmachinery.common.crafting.helper.CraftingStatus;
import hellfirepvp.modularmachinery.common.crafting.helper.ProcessingComponent;
import hellfirepvp.modularmachinery.common.machine.IOType;
import hellfirepvp.modularmachinery.common.machine.MachineRegistry;
import hellfirepvp.modularmachinery.common.util.IEnergyHandlerAsync;
import hellfirepvp.modularmachinery.common.util.IOInventory;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class TileHyperNetTerminal extends TileCustomController {
    public static final int NETWORK_CONNECT_CARD_SLOT = 0;
    public static final int ENERGY_USAGE = 2_000;

    private final HyperNetTerminal nodeProxy = new HyperNetTerminal(this);
    private final List<IEnergyHandlerAsync> energyHandlers = new ArrayList<>();

    private IOInventory cardInventory;

    public TileHyperNetTerminal() {
        this.cardInventory = (IOInventory) new IOInventory(this, new int[0], new int[0]).setMiscSlots(NETWORK_CONNECT_CARD_SLOT);
        this.cardInventory.setStackLimit(1, NETWORK_CONNECT_CARD_SLOT);
        this.parentMachine = MachineRegistry.getRegistry().getMachine(new ResourceLocation(ModularMachinery.MODID, "hypernet_terminal"));
    }

    @Override
    public void doControllerTick() {
        tickExecutor = ModularMachinery.EXECUTE_MANAGER.addTask(() -> {
            if (!doStructureCheck() || !isStructureFormed()) {
                return;
            }

            onMachineTick(Phase.START);

            if (consumeEnergy()) {
                nodeProxy.onMachineTick();
            }

            onMachineTick(Phase.END);
        }, usedTimeAvg());
    }

    public boolean consumeEnergy() {
        if (energyHandlers.isEmpty()) {
            controllerStatus = CraftingStatus.failure("component.missing.modularmachinery.energy.input");
            return false;
        }

        int energyUsage = nodeProxy.isConnected() ? ENERGY_USAGE : ENERGY_USAGE / 10;
        for (final IEnergyHandlerAsync handler : energyHandlers) {
            if (handler.extractEnergy(energyUsage)) {
                controllerStatus = CraftingStatus.working();
                return true;
            }
        }

        controllerStatus = CraftingStatus.failure("craftcheck.failure.energy.input");
        return false;
    }

    @Override
    protected void updateComponents() {
        super.updateComponents();

        energyHandlers.clear();
        for (ProcessingComponent<?> foundComponent : foundComponents) {
            if (foundComponent.getComponent().getIOType() == IOType.INPUT) {
                Object providedComponent = foundComponent.getProvidedComponent();
                if (providedComponent instanceof IEnergyHandlerAsync) {
                    IEnergyHandlerAsync iEnergyHandlerAsync = (IEnergyHandlerAsync) providedComponent;
                    energyHandlers.add(iEnergyHandlerAsync);
                }
            }
        }
    }

    @Override
    protected void checkRotation() {
        IBlockState state = getWorld().getBlockState(getPos());
        if (state.getBlock() instanceof BlockHyperNetTerminal) {
            controllerRotation = state.getValue(BlockController.FACING);
        } else {
            // wtf, where is the controller?
            NovaEngineeringCore.log.warn("Invalid controller block at " + getPos() + " !");
            controllerRotation = EnumFacing.NORTH;
        }
    }

    @Override
    public void readCustomNBT(final NBTTagCompound compound) {
        super.readCustomNBT(compound);

        nodeProxy.readNBT();

        if (compound.hasKey("cardInventory")) {
            cardInventory = IOInventory.deserialize(this, compound.getCompoundTag("cardInventory"));
        }
        if (compound.hasKey("controllerStatus")) {
            controllerStatus = CraftingStatus.deserialize(compound.getCompoundTag("controllerStatus"));
        }
    }

    @Override
    public void writeCustomNBT(final NBTTagCompound compound) {
        nodeProxy.writeNBT();

        super.writeCustomNBT(compound);

        compound.setTag("cardInventory", cardInventory.writeNBT());
        compound.setTag("controllerStatus", controllerStatus.serialize());
    }

    public IOInventory getCardInventory() {
        return cardInventory;
    }

    public HyperNetTerminal getNodeProxy() {
        return nodeProxy;
    }

    @Nullable
    @Override
    @SuppressWarnings("unchecked")
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return (T) cardInventory;
        }
        return super.getCapability(capability, facing);
    }

    // NO-OP

    @Override
    public boolean isWorking() {
        return isStructureFormed();
    }

}
