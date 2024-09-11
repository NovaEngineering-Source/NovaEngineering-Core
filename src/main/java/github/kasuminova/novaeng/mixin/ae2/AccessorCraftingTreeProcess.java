package github.kasuminova.novaeng.mixin.ae2;

import appeng.crafting.CraftingTreeNode;
import appeng.crafting.CraftingTreeProcess;
import it.unimi.dsi.fastutil.objects.Object2LongArrayMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = CraftingTreeProcess.class, remap = false)
public interface AccessorCraftingTreeProcess {

    @Accessor
    Object2LongArrayMap<CraftingTreeNode> getNodes();

}
