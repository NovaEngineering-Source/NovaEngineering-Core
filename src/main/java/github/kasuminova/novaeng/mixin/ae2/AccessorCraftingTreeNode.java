package github.kasuminova.novaeng.mixin.ae2;

import appeng.api.storage.data.IAEItemStack;
import appeng.crafting.CraftingTreeNode;
import appeng.crafting.CraftingTreeProcess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.ArrayList;

@Mixin(value = CraftingTreeNode.class, remap = false)
public interface AccessorCraftingTreeNode {

    @Accessor
    ArrayList<CraftingTreeProcess> getNodes();

    @Accessor
    IAEItemStack getWhat();
    
    @Accessor
    long getMissing();

}
