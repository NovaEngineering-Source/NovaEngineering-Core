package github.kasuminova.novaeng.mixin.eio;

import com.enderio.core.common.util.NNList;
import crazypants.enderio.conduits.conduit.item.ItemConduit;
import github.kasuminova.novaeng.mixin.util.CachedItemConduit;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import zone.rong.mixinextras.injector.ModifyReturnValue;

@Mixin(ItemConduit.class)
public abstract class MixinItemConduit implements CachedItemConduit {

    @Unique
    private ItemStack novaeng$cachedStack = ItemStack.EMPTY;

    @Inject(method = "writeToNBT", at = @At("TAIL"), remap = false)
    public void onWriteToNBT(final NBTTagCompound nbtRoot, final CallbackInfo ci) {
        if (!novaeng$cachedStack.isEmpty()) {
            NBTTagCompound cachedStackTag = new NBTTagCompound();

            novaeng$cachedStack.writeToNBT(cachedStackTag);
            if (novaeng$cachedStack.getCount() > 64) {
                cachedStackTag.setInteger("Count", novaeng$cachedStack.getCount());
            }

            nbtRoot.setTag("cachedStack", cachedStackTag);
        }
    }

    @Inject(method = "readFromNBT", at = @At("TAIL"), remap = false)
    public void onReadFromNBT(final NBTTagCompound nbtRoot, final CallbackInfo ci) {
        if (nbtRoot.hasKey("cachedStack")) {
            NBTTagCompound cachedStackTag = nbtRoot.getCompoundTag("cachedStack");
            novaeng$cachedStack = new ItemStack(cachedStackTag);
            novaeng$cachedStack.setCount(cachedStackTag.getInteger("Count"));
        }
    }

    @ModifyReturnValue(method = "getDrops", at = @At("RETURN"))
    public NNList<ItemStack> getDrops(NNList<ItemStack> res) {
        if (!novaeng$cachedStack.isEmpty()) {
            res.add(novaeng$cachedStack);
        }
        return res;
    }

    @Override
    @SuppressWarnings("AddedMixinMembersNamePattern")
    public ItemStack getCachedStack() {
        return novaeng$cachedStack;
    }

    @Override
    @SuppressWarnings("AddedMixinMembersNamePattern")
    public void setCachedStack(final ItemStack cachedStack) {
        this.novaeng$cachedStack = cachedStack;
    }

}
