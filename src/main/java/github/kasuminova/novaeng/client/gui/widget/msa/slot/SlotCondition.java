package github.kasuminova.novaeng.client.gui.widget.msa.slot;

import java.util.LinkedList;
import java.util.List;

public class SlotCondition extends SlotDynamic {
    protected List<SlotExtension> dependencies = new LinkedList<>();

    public SlotCondition dependsOn(SlotExtension dependency) {
        dependencies.add(dependency);
        return this;
    }

    @Override
    public boolean isAvailable() {
        for (final SlotExtension dependency : dependencies) {
            if (!dependency.isInstalled()) {
                return false;
            }
        }
        return true;
    }
}
