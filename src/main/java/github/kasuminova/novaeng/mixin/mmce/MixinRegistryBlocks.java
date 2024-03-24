package github.kasuminova.novaeng.mixin.mmce;

import github.kasuminova.novaeng.common.block.BlockHyperNetTerminal;
import github.kasuminova.novaeng.common.block.estorage.BlockEStorageController;
import hellfirepvp.modularmachinery.ModularMachinery;
import hellfirepvp.modularmachinery.common.block.BlockController;
import hellfirepvp.modularmachinery.common.machine.DynamicMachine;
import hellfirepvp.modularmachinery.common.registry.RegistryBlocks;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(RegistryBlocks.class)
public class MixinRegistryBlocks {

    @Redirect(
            method = "registerCustomControllers",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/List;add(Ljava/lang/Object;)Z",
                    remap = false
            ),
            remap = false
    )
    private static boolean filterSpecialMachine(final List<Object> instance, final Object e) {
        DynamicMachine machine = (DynamicMachine) e;
        ResourceLocation registryName = machine.getRegistryName();
        if (registryName.equals(new ResourceLocation(ModularMachinery.MODID, "hypernet_terminal"))) {
            BlockController.MACHINE_CONTROLLERS.put(machine, BlockHyperNetTerminal.INSTANCE);
            return true;
        }
        if (BlockEStorageController.REGISTRY.containsKey(registryName)) {
            BlockController.MACHINE_CONTROLLERS.put(machine, BlockEStorageController.REGISTRY.get(registryName));
            return true;
        }
        instance.add(e);
        return true;
    }

}
