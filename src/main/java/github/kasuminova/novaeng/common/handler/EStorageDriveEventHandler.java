package github.kasuminova.novaeng.common.handler;

import appeng.tile.inventory.AppEngCellInventory;
import github.kasuminova.novaeng.common.tile.estorage.EStorageCellDrive;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class EStorageDriveEventHandler {

    public static final EStorageDriveEventHandler INSTANCE = new EStorageDriveEventHandler();

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        World world = event.getWorld();
        if (world.isRemote) {
            return;
        }

        EntityPlayer player = event.getEntityPlayer();

        EntityEquipmentSlot hand = EntityEquipmentSlot.MAINHAND;
        ItemStack stackInHand = player.getHeldItemMainhand();
        if (stackInHand.isEmpty()) {
            stackInHand = player.getHeldItemOffhand();
            hand = EntityEquipmentSlot.OFFHAND;
        }

        TileEntity te = world.getTileEntity(event.getPos());
        if (!(te instanceof final EStorageCellDrive drive)) {
            return;
        }

        AppEngCellInventory inv = drive.getDriveInv();
        ItemStack stackInSlot = inv.getStackInSlot(0);
        if (stackInSlot.isEmpty()) {
            if (stackInHand.isEmpty()) {
                return;
            }
            inv.setStackInSlot(0, stackInHand.copy());
            stackInHand.setCount(0);
            return;
        }

        if (!player.isSneaking()) {
            return;
        }

        inv.setStackInSlot(0, stackInHand);
        player.setItemStackToSlot(hand, stackInSlot);
    }

}
