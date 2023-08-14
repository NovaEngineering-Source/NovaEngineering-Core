package github.kasuminova.novaeng.common.hypernet;

import github.kasuminova.novaeng.common.tile.TileHyperNetTerminal;
import net.minecraft.item.ItemStack;

public class HyperNetTerminal extends NetNode {
    protected final TileHyperNetTerminal terminal;

    public HyperNetTerminal(final TileHyperNetTerminal owner) {
        super(owner);
        this.terminal = owner;
    }

    @Override
    public void onMachineTick() {
        if (terminal.getTicksExisted() % 10 == 0) {
            ItemStack stack = terminal.getCardInventory().getStackInSlot(0);
            centerPos = HyperNetHelper.readConnectCardInfo(terminal, stack);
            writeNBT();
        }

        super.onMachineTick();
    }

    @Override
    public int getNodeMaxPresences() {
        return 1;
    }
}
