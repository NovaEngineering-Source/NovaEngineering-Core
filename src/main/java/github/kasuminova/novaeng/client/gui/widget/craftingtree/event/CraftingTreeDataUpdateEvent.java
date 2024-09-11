package github.kasuminova.novaeng.client.gui.widget.craftingtree.event;

import github.kasuminova.mmce.client.gui.widget.event.GuiEvent;
import github.kasuminova.novaeng.common.integration.ae2.data.LiteCraftTreeNode;

public class CraftingTreeDataUpdateEvent extends GuiEvent {

    private final LiteCraftTreeNode root;

    public CraftingTreeDataUpdateEvent(final LiteCraftTreeNode root) {
        super(null);
        this.root = root;
    }

    public LiteCraftTreeNode getRoot() {
        return root;
    }

}
