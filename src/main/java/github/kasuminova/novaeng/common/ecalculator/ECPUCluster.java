package github.kasuminova.novaeng.common.ecalculator;

import github.kasuminova.novaeng.common.block.ecotech.ecalculator.prop.Levels;
import github.kasuminova.novaeng.common.tile.ecotech.ecalculator.ECalculatorController;
import github.kasuminova.novaeng.common.tile.ecotech.ecalculator.ECalculatorThreadCore;

import javax.annotation.Nullable;

public interface ECPUCluster {

    void novaeng_ec$setAvailableStorage(final long availableStorage);

    void novaeng_ec$setAccelerators(final int accelerators);

    ECalculatorThreadCore novaeng_ec$getController();

    void novaeng_ec$setThreadCore(final ECalculatorThreadCore threadCore);

    void novaeng_ec$setVirtualCPUOwner(@Nullable final ECalculatorController isVirtualCPUOwner);

    Levels novaeng_ec$getControllerLevel();

    long novaeng_ec$getUsedExtraStorage();

    void novaeng_ec$setUsedExtraStorage(final long usedExtraStorage);

}
