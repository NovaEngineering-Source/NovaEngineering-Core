package github.kasuminova.novaeng.common.handler;

import appeng.tile.inventory.AppEngCellInventory;
import github.kasuminova.novaeng.common.estorage.EStorageCellHandler;
import github.kasuminova.novaeng.common.tile.estorage.EStorageCellDrive;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@SuppressWarnings("MethodMayBeStatic")
public class EStorageDriveEventHandler {

    public static final EStorageDriveEventHandler INSTANCE = new EStorageDriveEventHandler();

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        World world = event.getWorld();
        if (world.isRemote) {
            return;
        }

        EnumHand hand = event.getHand();
        if (hand != EnumHand.MAIN_HAND) {
            return;
        }

        EntityPlayer player = event.getEntityPlayer();
        if (player.isSneaking()) {
            return;
        }

        ItemStack stackInHand = player.getHeldItem(hand);

        TileEntity te = world.getTileEntity(event.getPos());
        if (!(te instanceof final EStorageCellDrive drive)) {
            return;
        }

        AppEngCellInventory inv = drive.getDriveInv();
        ItemStack stackInSlot = inv.getStackInSlot(0);
        if (stackInSlot.isEmpty()) {
            if (stackInHand.isEmpty() || EStorageCellHandler.getHandler(stackInHand) == null) {
                return;
            }
            player.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, inv.insertItem(0, stackInHand.copy(), false));
            event.setCanceled(true);
            return;
        }

        if (!stackInHand.isEmpty()) {
            return;
        }

        player.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, inv.extractItem(0, stackInSlot.getCount(), false));
        event.setCanceled(true);
    }

}
