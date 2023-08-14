package github.kasuminova.novaeng.common.hypernet;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.world.IBlockPos;
import hellfirepvp.modularmachinery.common.tiles.base.TileMultiblockMachineController;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenMethod;

import javax.annotation.Nonnull;

@ZenRegister
@ZenClass("novaeng.hypernet.NetNode")
public abstract class NetNode {
    protected final TileMultiblockMachineController owner;
    protected ComputationCenter center = null;
    protected BlockPos centerPos = null;

    public NetNode(final TileMultiblockMachineController owner) {
        this.owner = owner;
    }

    @ZenMethod
    public void onMachineTick() {
        if (owner.getTicksExisted() % 20 != 0) {
            return;
        }

        if (isConnected()) {
            connectToCenter();
        }
    }

    @ZenMethod
    public boolean connectTo(IBlockPos pos) {
        this.centerPos = CraftTweakerMC.getBlockPos(pos);
        connectToCenter();
        writeNBT();
        return isConnected();
    }

    @ZenMethod
    protected void connectToCenter() {
        this.center = null;

        if (!owner.getWorld().isBlockLoaded(this.centerPos)) {
            return;
        }

        TileEntity te = owner.getWorld().getTileEntity(this.centerPos);
        if (!(te instanceof TileMultiblockMachineController)) {
            return;
        }

        TileMultiblockMachineController ctrl = (TileMultiblockMachineController) te;
        if (!HyperNetHelper.isComputationCenter(ctrl)) {
            return;
        }

        ComputationCenter.from(ctrl).onConnect(owner, this);

        writeNBT();
    }

    public float requireComputationPoint(final float maxGeneration, final boolean doCalculate) {
        return 0;
    }

    @ZenMethod
    public float getComputationPointProvision(final float maxGeneration) {
        return 0;
    }

    @ZenGetter("computationPointConsumption")
    public float getComputationPointConsumption() {
        return 0;
    }

    @ZenMethod
    public IItemStack getConnectCardOutput(IItemStack stackCT) {
        return center == null ? stackCT : HyperNetHelper.writeConnectCardInfo(center, stackCT);
    }

    @ZenMethod
    public void readNBT() {
        readNBT(owner.getCustomDataTag());
    }

    public void readNBT(final NBTTagCompound customData) {
        this.centerPos = null;
        this.center = null;

        if (!customData.hasKey("centerPos")) {
            return;
        }

        this.centerPos = BlockPos.fromLong(customData.getLong("centerPos"));
    }

    @ZenMethod
    public void writeNBT() {
        writeNBT(owner.getCustomDataTag());
    }

    public void writeNBT(NBTTagCompound tag) {
        if (centerPos != null) {
            tag.setLong("centerPos", centerPos.toLong());
        }
    }

    @ZenGetter("maxNodePresences")
    public int getNodeMaxPresences() {
        return 1000;
    }

    @ZenGetter("center")
    public ComputationCenter getCenter() {
        return center;
    }

    public void onConnected(@Nonnull ComputationCenter center) {
        this.center = center;
    }

    @ZenGetter("connected")
    public boolean isConnected() {
        return centerPos != null;
    }

    public TileMultiblockMachineController getOwner() {
        return owner;
    }
}
