package github.kasuminova.novaeng.client.gui.widget.msa;

import github.kasuminova.mmce.client.gui.widget.container.Column;

public class AssemblyInvManager extends Column {
    private AssemblyInv currentOpened = null;

    public void openInv(final AssemblyInv inv) {
        if (currentOpened != inv) {
            if (currentOpened != null) {
                currentOpened.closeInv();
            }
            inv.openInv();
            currentOpened = inv;
        }
    }

    public AssemblyInvManager addInv(final AssemblyInv inv) {
        addWidget(inv);
        return this;
    }

}