package github.kasuminova.novaeng.common.hypernet;

import github.kasuminova.novaeng.common.crafttweaker.hypernet.HyperNetHelper;
import github.kasuminova.novaeng.common.hypernet.misc.HyperNetConnectCardInfo;
import github.kasuminova.novaeng.common.tile.TileHyperNetTerminal;
import hellfirepvp.modularmachinery.common.tiles.base.TileMultiblockMachineController;
import net.minecraft.item.ItemStack;

public class HyperNetTerminal extends NetNode {

    public HyperNetTerminal(final TileMultiblockMachineController owner) {
        super(owner);
    }

    @Override
    public void onMachineTick() {
        if (owner.getTicksExisted() % 20 == 0 && owner instanceof TileHyperNetTerminal terminal) {
            ItemStack stack = terminal.getCardInventory().getStackInSlot(0);
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
