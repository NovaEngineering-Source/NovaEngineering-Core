package github.kasuminova.novaeng.common.registry;

import github.kasuminova.novaeng.NovaEngineeringCore;
import github.kasuminova.novaeng.client.renderer.EStorageEnergyCellItemRenderer;
import github.kasuminova.novaeng.common.item.estorage.EStorageCellFluid;
import github.kasuminova.novaeng.common.item.estorage.EStorageCellItem;
import github.kasuminova.novaeng.common.item.estorage.ItemBlockEStorageEnergyCell;
import hellfirepvp.modularmachinery.common.item.ItemDynamicColor;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import static hellfirepvp.modularmachinery.common.registry.RegistryItems.pendingDynamicColorItems;

@SuppressWarnings({"MethodMayBeStatic", "UnusedReturnValue"})
public class RegistryItems {
    public static final List<Item> ITEMS_TO_REGISTER = new LinkedList<>();
    public static final List<Item> ITEMS_TO_REGISTER_CT = new LinkedList<>();
    public static final List<Item> ITEM_MODELS_TO_REGISTER = new LinkedList<>();

    @SubscribeEvent
    public void registerItems(RegistryEvent.Register<Item> event) {
        GenericRegistryPrimer.INSTANCE.wipe(event.getGenericType());

        ITEMS_TO_REGISTER.add(EStorageCellItem.LEVEL_A);
        ITEMS_TO_REGISTER.add(EStorageCellItem.LEVEL_B);
        ITEMS_TO_REGISTER.add(EStorageCellItem.LEVEL_C);
        ITEMS_TO_REGISTER.add(EStorageCellFluid.LEVEL_A);
        ITEMS_TO_REGISTER.add(EStorageCellFluid.LEVEL_B);
        ITEMS_TO_REGISTER.add(EStorageCellFluid.LEVEL_C);

        registerItems();

        GenericRegistryPrimer.INSTANCE.fillRegistry(event.getRegistry().getRegistrySuperType(), event.getRegistry());
    }

    public static void registerItems() {
        ITEMS_TO_REGISTER.forEach(RegistryItems::registerItem);
        ITEMS_TO_REGISTER.clear();
        ITEMS_TO_REGISTER_CT.forEach(RegistryItems::registerItem);
        ITEMS_TO_REGISTER_CT.clear();
    }

    public static void registerItemModels() {
        if (FMLCommonHandler.instance().getSide().isServer()) {
            return;
        }
        ITEM_MODELS_TO_REGISTER.forEach(RegistryItems::registryItemModel);
        ITEM_MODELS_TO_REGISTER.clear();
        setMeshDef();
    }

    @SideOnly(Side.CLIENT)
    private static void setMeshDef() {
        ModelLoader.setCustomMeshDefinition(ItemBlockEStorageEnergyCell.L4, new EStorageEnergyCellItemRenderer("l4"));
        ModelLoader.setCustomMeshDefinition(ItemBlockEStorageEnergyCell.L6, new EStorageEnergyCellItemRenderer("l6"));
        ModelLoader.setCustomMeshDefinition(ItemBlockEStorageEnergyCell.L9, new EStorageEnergyCellItemRenderer("l9"));
    }

    public static <T extends Item> T registerItem(T item) {
        ITEM_MODELS_TO_REGISTER.add(item);
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

        NovaEngineeringCore.log.debug("REGISTERED ITEM MODEL: " + registryName);
    }
}
