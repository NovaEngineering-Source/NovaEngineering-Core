package github.kasuminova.novaeng.client.handler;

import github.kasuminova.novaeng.common.hypernet.server.module.ServerModule;
import github.kasuminova.novaeng.common.hypernet.server.module.base.ServerModuleBase;
import github.kasuminova.novaeng.common.registry.RegistryHyperNet;
import github.kasuminova.novaeng.common.registry.ServerModuleRegistry;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

@SuppressWarnings("MethodMayBeStatic")
public class HyperNetClientEventHandler {
    public static final HyperNetClientEventHandler INSTANCE = new HyperNetClientEventHandler();

    protected final Map<ItemStack, ServerModule> moduleCache = new WeakHashMap<>();

    private HyperNetClientEventHandler() {
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onItemTooltip(ItemTooltipEvent event) {
        Item matches = RegistryHyperNet.getHyperNetConnectCard();
        if (matches == Items.AIR) {
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

    @SubscribeEvent
    @SuppressWarnings("unchecked")
    public void onServerModuleItemTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        if (stack.isEmpty()) {
            return;
        }

        ServerModule cachedModule = moduleCache.get(stack);
        if (cachedModule == null) {
            ServerModuleBase<ServerModule> moduleBase = (ServerModuleBase<ServerModule>) ServerModuleRegistry.getModule(stack);
            if (moduleBase == null) {
                return;
            }
            cachedModule = moduleBase.createInstance(null, stack);
        }

        moduleCache.put(stack, cachedModule);

        ServerModuleBase<ServerModule> moduleBase = (ServerModuleBase<ServerModule>) cachedModule.getModuleBase();
        event.getToolTip().addAll(moduleBase.getTooltip(cachedModule));
    }
}
