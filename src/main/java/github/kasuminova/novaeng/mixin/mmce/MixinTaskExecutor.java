package github.kasuminova.novaeng.mixin.mmce;

import com.google.common.collect.Sets;
import github.kasuminova.mmce.common.concurrent.TaskExecutor;
import github.kasuminova.novaeng.NovaEngineeringCore;
import github.kasuminova.novaeng.mixin.util.ITaskExecutor;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;

@Mixin(TaskExecutor.class)
public abstract class MixinTaskExecutor implements ITaskExecutor {

    @Shadow(remap = false) public abstract int executeActions();

    @Unique
    private final Set<TileEntity> novaeng$requireMarkDirtyTEQueue = Sets.newIdentityHashSet();

    @Inject(
            method = "onServerTick",
            at = @At(
                    value = "INVOKE_ASSIGN",
                    target = "Lgithub/kasuminova/mmce/common/concurrent/TaskExecutor;executeActions()I"
            ),
            remap = false
    )
    private void injectOnServerTickExecuteActions(final TickEvent.ServerTickEvent event, final CallbackInfo ci) {
        NovaEngineeringCore.PARALLEL_NETWORK_MANAGER.execute();
        TaskExecutor.totalExecuted += this.executeActions();
    }

    @Inject(method = "updateTileEntity", at = @At("HEAD"), remap = false)
    private void injectUpdateTE(final CallbackInfo ci) {
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
