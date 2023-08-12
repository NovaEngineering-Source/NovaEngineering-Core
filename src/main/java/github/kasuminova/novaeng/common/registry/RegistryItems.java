package github.kasuminova.novaeng.common.registry;

import hellfirepvp.modularmachinery.common.item.ItemDynamicColor;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static hellfirepvp.modularmachinery.common.registry.RegistryItems.pendingDynamicColorItems;

@SuppressWarnings({"MethodMayBeStatic", "UnusedReturnValue"})
public class RegistryItems {
    public static final List<Item> ITEM_BLOCKS_TO_REGISTER = new ArrayList<>();
    public static final List<Item> ITEM_MODEL_TO_REGISTER  = new ArrayList<>();

    @SubscribeEvent
    public void registerItems(RegistryEvent.Register<Item> event) {
        GenericRegistryPrimer.INSTANCE.wipe(event.getGenericType());

        registerItemBlocks();
        registerItemModels();

        GenericRegistryPrimer.INSTANCE.fillRegistry(event.getRegistry().getRegistrySuperType(), event.getRegistry());
    }

    public static void registerItemBlocks() {
        ITEM_BLOCKS_TO_REGISTER.forEach(RegistryItems::registerItem);
        ITEM_BLOCKS_TO_REGISTER.clear();
    }

    public static void registerItemModels() {
        ITEM_MODEL_TO_REGISTER.forEach(RegistryItems::registryItemModel);
        ITEM_MODEL_TO_REGISTER.clear();
    }

    public static <T extends Item> T registerItem(T item) {
        ITEM_MODEL_TO_REGISTER.add(item);
        GenericRegistryPrimer.INSTANCE.register(item);
        if (item instanceof ItemDynamicColor) {
            pendingDynamicColorItems.add((ItemDynamicColor) item);
        }
        return item;
    }

    public static void registryItemModel(final Item item) {
        NonNullList<ItemStack> list = NonNullList.create();
        ResourceLocation registryName = Objects.requireNonNull(item.getRegistryName());

        item.getSubItems(Objects.requireNonNull(item.getCreativeTab()), list);
        if (list.isEmpty()) {
            ModelLoader.setCustomModelResourceLocation(
                    item, 0, new ModelResourceLocation(registryName, "inventory"));
        } else {
            list.forEach(stack -> ModelLoader.setCustomModelResourceLocation(
                    item, stack.getItemDamage(), new ModelResourceLocation(registryName, "inventory")));
        }
    }
}
