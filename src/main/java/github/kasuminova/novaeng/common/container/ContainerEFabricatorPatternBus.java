package github.kasuminova.novaeng.common.container;

import appeng.container.AEBaseContainer;
import appeng.container.slot.SlotRestrictedInput;
import appeng.tile.inventory.AppEngInternalInventory;
import github.kasuminova.novaeng.common.tile.efabricator.EFabricatorPatternBus;
import net.minecraft.entity.player.EntityPlayer;

public class ContainerEFabricatorPatternBus extends AEBaseContainer {

    private final EFabricatorPatternBus owner;

    public ContainerEFabricatorPatternBus(final EFabricatorPatternBus owner, final EntityPlayer player) {
        super(player.inventory, owner);
        this.owner = owner;

        this.bindPlayerInventory(getInventoryPlayer(), 27, 150);

        AppEngInternalInventory patterns = owner.getPatterns();
        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < 12; col++) {
                this.addSlotToContainer(new SlotRestrictedInput(SlotRestrictedInput.PlacableItemType.ENCODED_PATTERN, patterns,
                        (row * 9) + col, 8 + (col * 18), 28 + (row * 18), getInventoryPlayer()));
            }
        }
    }

    public EFabricatorPatternBus getOwner() {
        return owner;
    }

}
