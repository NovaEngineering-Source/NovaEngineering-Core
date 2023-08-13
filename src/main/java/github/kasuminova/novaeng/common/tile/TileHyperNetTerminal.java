package github.kasuminova.novaeng.common.tile;

import github.kasuminova.mmce.common.event.Phase;
import github.kasuminova.novaeng.NovaEngineeringCore;
import github.kasuminova.novaeng.common.block.BlockHyperNetTerminal;
import github.kasuminova.novaeng.common.hypernet.HyperNetTerminal;
import hellfirepvp.modularmachinery.ModularMachinery;
import hellfirepvp.modularmachinery.common.block.BlockController;
import hellfirepvp.modularmachinery.common.crafting.ActiveMachineRecipe;
import hellfirepvp.modularmachinery.common.crafting.helper.CraftingStatus;
import hellfirepvp.modularmachinery.common.crafting.helper.ProcessingComponent;
import hellfirepvp.modularmachinery.common.machine.IOType;
import hellfirepvp.modularmachinery.common.machine.MachineRegistry;
import hellfirepvp.modularmachinery.common.machine.RecipeThread;
import hellfirepvp.modularmachinery.common.modifier.RecipeModifier;
import hellfirepvp.modularmachinery.common.tiles.base.TileMultiblockMachineController;
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
import java.util.List;
import java.util.stream.Collectors;

public class TileHyperNetTerminal extends TileMultiblockMachineController {
    public static final int NETWORK_CONNECT_CARD_SLOT = 0;
    public static final int ENERGY_USAGE = 1_000;

    private final HyperNetTerminal nodeProxy = new HyperNetTerminal(this);
    private CraftingStatus controllerStatus = CraftingStatus.IDLE;

    private IOInventory cardInventory;

    public TileHyperNetTerminal() {
        this.cardInventory = (IOInventory) new IOInventory(this, new int[0], new int[0]).setMiscSlots(NETWORK_CONNECT_CARD_SLOT);
        this.cardInventory.setStackLimit(1, NETWORK_CONNECT_CARD_SLOT);
        this.parentMachine = MachineRegistry.getRegistry().getMachine(new ResourceLocation(ModularMachinery.MODID, "hypernet_terminal"));
    }

    @Override
    public void doControllerTick() {
        tickExecutor = ModularMachinery.EXECUTE_MANAGER.addParallelAsyncTask(() -> {
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
        List<IEnergyHandlerAsync> energyHandlers = foundComponents.stream()
                .filter(foundComponent -> foundComponent.getComponent().getIOType() == IOType.INPUT)
                .map(ProcessingComponent::getProvidedComponent)
                .filter(IEnergyHandlerAsync.class::isInstance)
                .map(IEnergyHandlerAsync.class::cast)
                .collect(Collectors.toList());

        if (energyHandlers.isEmpty()) {
            controllerStatus = CraftingStatus.failure("component.missing.modularmachinery.energy.input");
            return false;
        }

        for (final IEnergyHandlerAsync handler : energyHandlers) {
            if (handler.extractEnergy(ENERGY_USAGE)) {
                controllerStatus = CraftingStatus.working();
                return true;
            }
        }

        controllerStatus = CraftingStatus.failure("craftcheck.failure.energy.input");
        return false;
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

        nodeProxy.readNBT(compound);

        if (compound.hasKey("cardInventory")) {
            cardInventory = IOInventory.deserialize(this, compound.getCompoundTag("cardInventory"));
        }
        if (compound.hasKey("controllerStatus")) {
            controllerStatus = CraftingStatus.deserialize(compound.getCompoundTag("controllerStatus"));
        }
    }

    @Override
    protected void readMachineNBT(NBTTagCompound compound) {
        if (compound.hasKey("parentMachine")) {
            ResourceLocation rl = new ResourceLocation(compound.getString("parentMachine"));
            parentMachine = MachineRegistry.getRegistry().getMachine(rl);
        }

        super.readMachineNBT(compound);
    }

    @Override
    public void writeCustomNBT(final NBTTagCompound compound) {
        super.writeCustomNBT(compound);

        nodeProxy.writeNBT(compound);

        compound.setTag("cardInventory", cardInventory.writeNBT());
        compound.setTag("controllerStatus", controllerStatus.serialize());
    }

    public IOInventory getCardInventory() {
        return cardInventory;
    }

    public HyperNetTerminal getNodeProxy() {
        return nodeProxy;
    }

    @Override
    public CraftingStatus getControllerStatus() {
        return controllerStatus;
    }

    @Override
    public void setControllerStatus(final CraftingStatus status) {
        this.controllerStatus = status;
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
    public void flushContextModifier() {

    }

    @Nullable
    @Override
    public ActiveMachineRecipe getActiveRecipe() {
        return null;
    }

    @Override
    public ActiveMachineRecipe[] getActiveRecipeList() {
        return new ActiveMachineRecipe[0];
    }

    @Override
    public RecipeThread[] getRecipeThreadList() {
        return new RecipeThread[0];
    }

    @Override
    public boolean isWorking() {
        return true;
    }

    @Override
    public void addModifier(final String key, final RecipeModifier modifier) {

    }

    @Override
    public void removeModifier(final String key) {

    }

    @Override
    public void overrideStatusInfo(final String newInfo) {

    }

}
