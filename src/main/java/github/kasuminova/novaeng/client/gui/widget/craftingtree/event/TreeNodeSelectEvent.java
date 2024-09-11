package github.kasuminova.novaeng.client.gui.widget.craftingtree.event;

import github.kasuminova.mmce.client.gui.widget.event.GuiEvent;
import github.kasuminova.novaeng.client.gui.widget.craftingtree.TreeNode;

public class TreeNodeSelectEvent extends GuiEvent {

    private final TreeNode selectedNode;

    public TreeNodeSelectEvent(final TreeNode selectedNode) {
        super(null);
        this.selectedNode = selectedNode;
    }

    public TreeNode getSelectedNode() {
        return selectedNode;
    }

}
