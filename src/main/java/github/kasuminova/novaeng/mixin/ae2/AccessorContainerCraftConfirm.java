package github.kasuminova.novaeng.mixin.ae2;

import appeng.api.networking.crafting.ICraftingJob;
import appeng.container.implementations.ContainerCraftConfirm;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = ContainerCraftConfirm.class, remap = false)
public interface AccessorContainerCraftConfirm {

    @Accessor
    ICraftingJob getResult();

}
