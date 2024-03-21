package github.kasuminova.novaeng.common.tile.estorage;

import appeng.api.config.AccessRestriction;
import appeng.api.config.Actionable;
import appeng.api.config.PowerMultiplier;
import appeng.api.networking.GridFlags;
import appeng.api.networking.IGridNode;
import appeng.api.networking.energy.IAEPowerStorage;
import appeng.api.networking.security.IActionHost;
import appeng.api.networking.security.IActionSource;
import appeng.api.storage.ICellProvider;
import appeng.api.storage.IMEInventoryHandler;
import appeng.api.storage.IStorageChannel;
import appeng.api.util.AECableType;
import appeng.api.util.AEPartLocation;
import appeng.api.util.DimensionalCoord;
import appeng.me.helpers.AENetworkProxy;
import appeng.me.helpers.IGridProxyable;
import appeng.me.helpers.MachineSource;
import hellfirepvp.modularmachinery.ModularMachinery;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class EStorageMEChannel extends EStoragePart implements ICellProvider, IActionHost, IGridProxyable, IAEPowerStorage {

    protected final AENetworkProxy proxy = new AENetworkProxy(this, "aeProxy", getVisualItemStack(), true);
    protected final IActionSource source = new MachineSource(this);

    protected EStorageController storageController = null;

    protected int priority = 0;

    public EStorageMEChannel() {
        this.proxy.setIdlePowerUsage(0.0D);
        this.proxy.setFlags(GridFlags.REQUIRE_CHANNEL, GridFlags.DENSE_CAPACITY);
    }

    public IActionSource getSource() {
        return source;
    }

    @Override
    @SuppressWarnings("rawtypes")
    public List<IMEInventoryHandler> getCellArray(final IStorageChannel<?> channel) {
        if (storageController != null) {
            return storageController.getCellDrives().stream()
                    .map(drive -> drive.getHandler(channel))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    @Override
    public int getPriority() {
        return priority;
    }

    public ItemStack getVisualItemStack() {
        // TODO Change this.
        return ItemStack.EMPTY;
    }

    @Override
    public double injectAEPower(final double amt, @Nonnull final Actionable mode) {
        return 0;
    }

    @Override
    public double getAEMaxPower() {
        if (this.storageController == null) {
            return 0;
        }
        return this.storageController.getMaxEnergyStore();
    }

    @Override
    public double getAECurrentPower() {
        return 0;
    }

    @Override
    public boolean isAEPublicPowerStorage() {
        return true;
    }

    @Nonnull
    @Override
    public AccessRestriction getPowerFlow() {
        return AccessRestriction.READ_WRITE;
    }

    @Override
    public double extractAEPower(final double amt, @Nonnull final Actionable mode, @Nonnull final PowerMultiplier multiplier) {
        return multiplier.divide(storageController.extractPower(multiplier.multiply(amt), mode));
    }

    @Nonnull
    @Override
    public IGridNode getActionableNode() {
        return proxy.getNode();
    }

    @Nonnull
    @Override
    public AENetworkProxy getProxy() {
        return proxy;
    }

    @Nonnull
    @Override
    public DimensionalCoord getLocation() {
        return new DimensionalCoord(this);
    }

    @Override
    public void gridChanged() {

    }

    @Nullable
    @Override
    public IGridNode getGridNode(@Nonnull final AEPartLocation dir) {
        return proxy.getNode();
    }

    @Nonnull
    @Override
    public AECableType getCableConnectionType(@Nonnull final AEPartLocation dir) {
        return AECableType.DENSE_SMART;
    }

    @Override
    public void securityBreak() {
        getWorld().destroyBlock(getPos(), true);
    }

    @Override
    public void onChunkUnload() {
        super.onChunkUnload();
        proxy.onChunkUnload();
    }

    @Override
    public void invalidate() {
        super.invalidate();
        proxy.invalidate();
    }

    @Override
    public void validate() {
        super.validate();
        ModularMachinery.EXECUTE_MANAGER.addSyncTask(proxy::onReady);
    }
}
