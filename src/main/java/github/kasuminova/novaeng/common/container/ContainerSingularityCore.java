package github.kasuminova.novaeng.common.container;

import github.kasuminova.novaeng.common.tile.machine.SingularityCore;
import hellfirepvp.modularmachinery.common.container.ContainerBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;

public class ContainerSingularityCore extends ContainerBase<SingularityCore> {

    protected int tickExisted = 0;

    public ContainerSingularityCore(final SingularityCore owner, final EntityPlayer opening) {
        super(owner, opening);
    }

    public int getTickExisted() {
        return tickExisted;
    }

    public void setTickExisted(final int tickExisted) {
        this.tickExisted = tickExisted;
    }

    @Override
    protected void addPlayerSlots(EntityPlayer opening) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                addSlotToContainer(new Slot(opening.inventory, j + i * 9 + 9, 119 + j * 18, 184 + i * 18));
            }
        }
        for (int i = 0; i < 9; i++) {
            addSlotToContainer(new Slot(opening.inventory, i, 119 + i * 18, 242));
        }
    }

}
