package github.kasuminova.novaeng.common.registry;

import com.google.common.base.Preconditions;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import github.kasuminova.novaeng.common.hypernet.server.module.base.ServerModuleBase;
import github.kasuminova.novaeng.common.item.ItemServerModule;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ZenRegister
@ZenClass("novaeng.hypernet.server.module.ServerModuleRegistry")
public class ServerModuleRegistry {

    private static final Map<String, ServerModuleBase<?>> MODULE_BASE_REGISTRY = new HashMap<>();
    private static final Map<Item, ItemModuleRegistry> MODULE_ITEM_REGISTRY = new HashMap<>();

    @ZenMethod
    public static void registryModuleBase(final ServerModuleBase<?> module) {
        Preconditions.checkNotNull(module);
        MODULE_BASE_REGISTRY.put(module.getRegistryName(), module);
    }

    public static void registryItemStackMatch(final ItemStack match, final ServerModuleBase<?> module) {
        Preconditions.checkNotNull(match);
        Preconditions.checkNotNull(module);
        MODULE_ITEM_REGISTRY.computeIfAbsent(match.getItem(), v -> new ItemModuleRegistry(module)).addMatch(match);
    }

    @ZenMethod
    public static void registryItemStackMatch(final IItemStack matchCT, final ServerModuleBase<?> module) {
        registryItemStackMatch(CraftTweakerMC.getItemStack(matchCT), module);
    }

    @ZenMethod
    public static ServerModuleBase<?> getModule(final String moduleRegistryName) {
        return MODULE_BASE_REGISTRY.get(moduleRegistryName);
    }

    public static ServerModuleBase<?> getModule(final ItemStack toMatch) {
        Item item = toMatch.getItem();
        if (item instanceof ItemServerModule itemServerModule) {
            ServerModuleBase<?> boundedModule = itemServerModule.getBoundedModule();
            if (boundedModule != null) {
                return boundedModule;
            }
        }

        ItemModuleRegistry itemModuleRegistry = MODULE_ITEM_REGISTRY.get(item);
        return itemModuleRegistry == null ? null : itemModuleRegistry.getModule(toMatch);
    }

    public static class ItemModuleRegistry {
        private final List<ItemStack> matches = new ArrayList<>();
        private final ServerModuleBase<?> module;

        public ItemModuleRegistry(final ServerModuleBase<?> module) {
            this.module = module;
        }

        public ItemModuleRegistry addMatch(final ItemStack stack) {
            matches.add(stack);
            return this;
        }

        public ServerModuleBase<?> getModule(final ItemStack stack) {
            for (final ItemStack match : matches) {
                if (match.isItemEqual(stack)) {
                    return module;
                }
            }
            return null;
        }
    }

}
