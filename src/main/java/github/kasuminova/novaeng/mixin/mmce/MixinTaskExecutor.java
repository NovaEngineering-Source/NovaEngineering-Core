package github.kasuminova.novaeng.mixin.mmce;

import com.google.common.collect.Sets;
import github.kasuminova.mmce.common.concurrent.TaskExecutor;
import github.kasuminova.novaeng.mixin.util.ITaskExecutor;
import net.minecraft.tileentity.TileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;

@Mixin(TaskExecutor.class)
public class MixinTaskExecutor implements ITaskExecutor {

    @Unique
    private final Set<TileEntity> novaeng$requireMarkDirtyTEQueue = Sets.newIdentityHashSet();

    @Inject(method = "updateTileEntity", at = @At("HEAD"), remap = false)
    public void injectUpdateTE(final CallbackInfo ci) {
        for (final TileEntity te : novaeng$requireMarkDirtyTEQueue) {
            te.markDirty();
        }
        novaeng$requireMarkDirtyTEQueue.clear();
    }

    @Unique
    @Override
    @SuppressWarnings("AddedMixinMembersNamePattern")
    public void addTEMarkTask(final TileEntity te) {
        novaeng$requireMarkDirtyTEQueue.add(te);
    }
}
