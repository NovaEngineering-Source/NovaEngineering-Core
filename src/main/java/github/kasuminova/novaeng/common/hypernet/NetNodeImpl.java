package github.kasuminova.novaeng.common.hypernet;

import crafttweaker.annotations.ZenRegister;
import github.kasuminova.mmce.common.helper.IMachineController;
import hellfirepvp.modularmachinery.common.tiles.base.TileMultiblockMachineController;
import net.minecraft.nbt.NBTTagCompound;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;
import stanhebben.zenscript.annotations.ZenSetter;

import java.util.Map;
import java.util.WeakHashMap;

@ZenRegister
@ZenClass("novaeng.hypernet.NetNodeImpl")
public class NetNodeImpl extends NetNode {
    private static final Map<TileMultiblockMachineController, NetNodeImpl> CACHED_NODES = new WeakHashMap<>();

    private float computationPointProvision = 0;
    private float computationPointConsumption = 0;

    public NetNodeImpl(final TileMultiblockMachineController owner, NBTTagCompound customData) {
        super(owner);
        readNBT(customData);
    }

    @Override
    public void readNBT(final NBTTagCompound customData) {
        super.readNBT(customData);
        this.computationPointProvision = customData.getInteger("computationPointProvision");
        this.computationPointConsumption = customData.getInteger("computationPointConsumption");
    }

    @Override
    public void writeNBT() {
        super.writeNBT();
        NBTTagCompound tag = owner.getCustomDataTag();
        tag.setFloat("computationPointProvision", computationPointProvision);
        tag.setFloat("computationPointConsumption", computationPointConsumption);
    }

    @ZenMethod
    public static NetNodeImpl from(final IMachineController machine) {
        TileMultiblockMachineController ctrl = machine.getController();
        return CACHED_NODES.computeIfAbsent(ctrl, v -> new NetNodeImpl(ctrl, ctrl.getCustomDataTag()));
    }

    @ZenSetter("computationPointProvision")
    public void setComputationPointProvision(final int computationPointProvision) {
        this.computationPointProvision = computationPointProvision;
    }

    @ZenSetter("computationPointConsumption")
    public void setComputationPointConsumption(final int computationPointConsumption) {
        this.computationPointConsumption = computationPointConsumption;
    }

    @Override
    public float getComputationPointProvision(final float maxGeneration) {
        return Math.min(computationPointProvision, maxGeneration);
    }

    @Override
    public float getComputationPointConsumption() {
        return computationPointConsumption;
    }
}
