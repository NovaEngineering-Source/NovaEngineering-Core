package github.kasuminova.novaeng.mixin.ae2;

import appeng.api.networking.crafting.ICraftingJob;
import appeng.api.storage.data.IAEItemStack;
import appeng.container.implementations.ContainerCraftConfirm;
import appeng.crafting.CraftingJob;
import appeng.crafting.CraftingTreeNode;
import appeng.crafting.CraftingTreeProcess;
import github.kasuminova.novaeng.NovaEngineeringCore;
import it.unimi.dsi.fastutil.objects.Object2LongArrayMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.concurrent.Future;

@Mixin(ContainerCraftConfirm.class)
public abstract class MixinContainerCraftConfirm {

//    @Shadow(remap = false)
//    protected abstract Future<ICraftingJob> getJob();
//
//    @Inject(
//            method = "detectAndSendChanges",
//            at = @At(
//                    value = "INVOKE",
//                    target = "Ljava/util/concurrent/Future;isDone()Z",
//                    remap = false
//            )
//    )
//    private void injectDetectAndSendChangesIsDone(final CallbackInfo ci) {
//        if (this.getJob().isDone()) {
//            ICraftingJob job;
//            try {
//                job = getJob().get();
//            } catch (Throwable e) {
//                return;
//            }
//
//            if (!(job instanceof CraftingJob craftingJob)) {
//                return;
//            }
//            novaeng_core$printTree(craftingJob.getTree(), 0);
//        }
//    }
//
//    @Unique
//    private static void novaeng_core$printTree(final CraftingTreeNode root, int depth) {
//        AccessorCraftingTreeNode accessor = (AccessorCraftingTreeNode) root;
//        IAEItemStack output = accessor.getWhat();
//        List<CraftingTreeProcess> nodes = accessor.getNodes();
//
//        NovaEngineeringCore.log.info("{}Output: {}, Requestable: {}, Missing: {}", novaeng_core$space(depth), output, output.getCountRequestable(), accessor.getMissing());
//        if (!nodes.isEmpty()) {
//            NovaEngineeringCore.log.info("{}Inputs:", novaeng_core$space(depth));
//            nodes.stream()
//                    .map(AccessorCraftingTreeProcess.class::cast)
//                    .map(AccessorCraftingTreeProcess::getNodes)
//                    .forEach(subNodes -> subNodes.keySet()
//                            .forEach(treeNode -> novaeng_core$printTree(treeNode, depth + 1)));
//        }
//    }
//
//    @Unique
//    @SuppressWarnings("StringRepeatCanBeUsed")
//    private static String novaeng_core$space(int depth) {
//        StringBuilder sb = new StringBuilder();
//        for (int i = 0; i < depth; i++) {
//            sb.append("  ");
//        }
//        return sb.toString();
//    }

}
