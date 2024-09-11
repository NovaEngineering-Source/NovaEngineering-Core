package github.kasuminova.novaeng.common.integration.ae2.data;

import appeng.crafting.CraftingTreeNode;
import appeng.crafting.CraftingTreeProcess;
import com.github.bsideup.jabel.Desugar;
import github.kasuminova.novaeng.common.util.AEItemStackSet;
import github.kasuminova.novaeng.mixin.ae2.AccessorCraftingTreeProcess;
import io.netty.buffer.ByteBuf;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Desugar
public record LiteCraftTreeProc(List<LiteCraftTreeNode> inputs) implements Comparable<LiteCraftTreeProc> {

    @Nullable
    public static LiteCraftTreeProc of(final CraftingTreeProcess process) {
        AccessorCraftingTreeProcess accessor = (AccessorCraftingTreeProcess) process;
        List<LiteCraftTreeNode> inputs = new ArrayList<>();
        LiteCraftTreeProc proc = new LiteCraftTreeProc(inputs);
        for (CraftingTreeNode node : accessor.getNodes().keySet()) {
            inputs.add(LiteCraftTreeNode.of(node, proc));
        }
        // return null if no inputs
        return inputs.isEmpty() ? null : proc;
    }

    public static LiteCraftTreeProc fromBuffer(final ByteBuf buf, final AEItemStackSet stackSet) {
        int size = buf.readByte();
        List<LiteCraftTreeNode> inputs = new ArrayList<>();
        LiteCraftTreeProc proc = new LiteCraftTreeProc(inputs);
        for (int i = 0; i < size; i++) {
            inputs.add(LiteCraftTreeNode.fromBuffer(buf, stackSet, proc));
        }
        return proc;
    }

    public void writeToBuffer(final ByteBuf buf, final AEItemStackSet stackSet) {
        if (inputs.size() > Byte.MAX_VALUE) {
            throw new IllegalStateException("Too many inputs for a single node");
        }
        buf.writeByte(inputs.size());
        inputs.forEach(node -> node.writeToBuffer(buf, stackSet));
    }

    public void sort() {
        inputs.sort(Comparator.reverseOrder());
        for (final LiteCraftTreeNode input : inputs) {
            for (final LiteCraftTreeProc proc : input.inputs()) {
                proc.sort();
            }
        }
    }

    @Override
    public int compareTo(@Nonnull final LiteCraftTreeProc o) {
        return Integer.compare(diveToDeep(this, 0, new LiteCraftTreeNode.DepthRecorder()), diveToDeep(o, 0, new LiteCraftTreeNode.DepthRecorder()));
    }

    public static int diveToDeep(final LiteCraftTreeProc proc, final int depth, final LiteCraftTreeNode.DepthRecorder recorder) {
        for (final LiteCraftTreeNode node : proc.inputs) {
            for (final LiteCraftTreeProc subProc : node.inputs()) {
                int newDepth = depth + 1;
                recorder.dive(newDepth);
                diveToDeep(subProc, newDepth, recorder);
            }
        }
        return recorder.getDepth();
    }

    public int totalNodes() {
        int nodeCount = inputs.size();
        for (final LiteCraftTreeNode node : inputs) {
            for (final LiteCraftTreeProc input : node.inputs()) {
                nodeCount += input.totalNodes();
            }
        }
        return nodeCount;
    }

}
