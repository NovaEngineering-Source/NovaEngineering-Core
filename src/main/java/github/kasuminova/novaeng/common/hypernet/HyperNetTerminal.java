package github.kasuminova.novaeng.common.hypernet;

import github.kasuminova.novaeng.common.crafttweaker.hypernet.HyperNetHelper;
import github.kasuminova.novaeng.common.hypernet.misc.HyperNetConnectCardInfo;
import github.kasuminova.novaeng.common.tile.TileHyperNetTerminal;
import hellfirepvp.modularmachinery.common.tiles.base.TileMultiblockMachineController;
import net.minecraft.item.ItemStack;

public class HyperNetTerminal extends NetNode {
    protected final TileMultiblockMachineController terminal;

    public HyperNetTerminal(final TileMultiblockMachineController owner) {
        super(owner);
        this.terminal = owner;
    }

    @Override
    public void onMachineTick() {
        if (terminal.getTicksExisted() % 20 == 0 && terminal instanceof TileHyperNetTerminal) {
            ItemStack stack = ((TileHyperNetTerminal) terminal).getCardInventory().getStackInSlot(0);
            HyperNetConnectCardInfo info = HyperNetHelper.readConnectCardInfo(terminal, stack);

            if (info != null) {
                centerPos = info.getPos();
            } else {
                centerPos = null;
            }

            writeNBT();
        }

        super.onMachineTick();
    }

    @Override
    public int getNodeMaxPresences() {
        return 1;
    }
}
