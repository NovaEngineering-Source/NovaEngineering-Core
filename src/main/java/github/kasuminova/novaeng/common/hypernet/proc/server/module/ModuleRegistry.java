package github.kasuminova.novaeng.common.hypernet.proc.server.module;

import com.google.common.base.Preconditions;
import github.kasuminova.novaeng.common.hypernet.proc.server.module.base.ServerModuleBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModuleRegistry {

    private static final Map<String, ServerModuleBase<?>> MODULE_BASE_REGISTRY = new HashMap<>();
    private static final Map<Item, ItemModuleRegistry> MODULE_ITEM_REGISTRY = new HashMap<>();

    public static void addModuleRegistry(final ServerModuleBase<?> module) {
        Preconditions.checkNotNull(module);
        MODULE_BASE_REGISTRY.put(module.getRegistryName(), module);
    }

    public static void addMatchItemStack(final ItemStack match, final ServerModuleBase<?> module) {
        Preconditions.checkNotNull(match);
        Preconditions.checkNotNull(module);
        MODULE_ITEM_REGISTRY.computeIfAbsent(match.getItem(), v -> new ItemModuleRegistry(module)).addMatch(match);
    }

    public static ServerModuleBase<?> getModule(final String moduleRegistryName) {
        return MODULE_BASE_REGISTRY.get(moduleRegistryName);
    }

    public static ServerModuleBase<?> getModule(final ItemStack toMatch) {
        ItemModuleRegistry itemModuleRegistry = MODULE_ITEM_REGISTRY.get(toMatch.getItem());
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
