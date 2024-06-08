package github.kasuminova.novaeng.mixin.mmce;

import github.kasuminova.novaeng.common.registry.RegistryMachineSpecial;
import hellfirepvp.modularmachinery.common.CommonProxy;
import hellfirepvp.modularmachinery.common.machine.DynamicMachine;
import hellfirepvp.modularmachinery.common.machine.MachineRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("MethodMayBeStatic")
@Mixin(CommonProxy.class)
public class MixinCommonProxy {

    @Inject(
            method = "postInit",
            at = @At(
                    value = "INVOKE",
                    target = "Lhellfirepvp/modularmachinery/common/machine/MachineRegistry;registerMachines(Ljava/util/Collection;)V",
                    ordinal = 1,
                    shift = At.Shift.AFTER
            ),
            remap = false
    )
    private void hookAfterRegisterMachine(final CallbackInfo ci) {
        RegistryMachineSpecial.getSpecialMachineRegistry().forEach((registryName, machineSpecial) -> {
            DynamicMachine machine = MachineRegistry.getRegistry().getMachine(registryName);
            if (machine != null) {
                machineSpecial.preInit(machine);
            }
        });
        RegistryMachineSpecial.getSpecialMachineRegistry().forEach((registryName, machineSpecial) -> {
            DynamicMachine machine = MachineRegistry.getRegistry().getMachine(registryName);
            if (machine != null) {
                machineSpecial.init(machine);
            }
        });
    }

}
