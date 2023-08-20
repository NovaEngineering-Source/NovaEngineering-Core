package github.kasuminova.novaeng.client.handler;

import github.kasuminova.novaeng.common.registry.RegistryHyperNet;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

@SuppressWarnings("MethodMayBeStatic")
public class HyperNetClientEventHandler {
    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onItemTooltip(ItemTooltipEvent event) {
        Item matches = RegistryHyperNet.getHyperNetConnectCard();
        if (matches == null) {
            return;
        }

        ItemStack stack = event.getItemStack();
        if (stack.getItem() != matches) {
            return;
        }
        NBTTagCompound tag = stack.getTagCompound();


        List<String> tips = event.getToolTip();
        tips.add(I18n.format("item.hypernet_connect_card.tooltip.1"));
        tips.add(I18n.format("item.hypernet_connect_card.tooltip.2"));
        tips.add(I18n.format("item.hypernet_connect_card.tooltip.3"));
        tips.add(I18n.format("item.hypernet_connect_card.tooltip.4"));

        if (tag == null || !tag.hasKey("pos")) {
            tips.add(I18n.format("item.hypernet_connect_card.tooltip.no_pos.tip.0"));
            tips.add(I18n.format("item.hypernet_connect_card.tooltip.no_pos.tip.1"));
        } else {
            BlockPos pos = BlockPos.fromLong(tag.getLong("pos"));
            tips.add(I18n.format("item.hypernet_connect_card.tooltip.pos.tip.0", pos.getX(), pos.getY(), pos.getZ()));
            tips.add(I18n.format("item.hypernet_connect_card.tooltip.pos.tip.1"));
        }
    }
}
