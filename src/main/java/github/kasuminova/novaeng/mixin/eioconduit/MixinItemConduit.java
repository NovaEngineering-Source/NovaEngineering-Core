package github.kasuminova.novaeng.mixin.eioconduit;

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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

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

    @Inject(method = "getDrops", at = @At("RETURN"), remap = false)
    public void onGetDrops(final CallbackInfoReturnable<NNList<ItemStack>> cir) {
        if (!novaeng$cachedStack.isEmpty()) {
            cir.getReturnValue().add(novaeng$cachedStack);
        }
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
