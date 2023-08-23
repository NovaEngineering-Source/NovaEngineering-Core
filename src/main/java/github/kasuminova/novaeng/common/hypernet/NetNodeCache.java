package github.kasuminova.novaeng.common.hypernet;

import crafttweaker.annotations.ZenRegister;
import github.kasuminova.mmce.common.helper.IMachineController;
import github.kasuminova.novaeng.NovaEngineeringCore;
import github.kasuminova.novaeng.common.hypernet.research.ResearchStation;
import github.kasuminova.novaeng.common.registry.RegistryHyperNet;
import hellfirepvp.modularmachinery.common.machine.DynamicMachine;
import hellfirepvp.modularmachinery.common.tiles.base.TileMultiblockMachineController;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.WeakHashMap;

@ZenRegister
@ZenClass("novaeng.hypernet.NetNodeCache")
public class NetNodeCache {
    private static final Map<TileMultiblockMachineController, NetNode> CACHED_NODES = new WeakHashMap<>();

    @ZenMethod
    public static DataProcessor getDataProcessor(IMachineController ctrl) {
        return getCache(ctrl.getController(), DataProcessor.class);
    }

    @ZenMethod
    public static Database getDatabase(IMachineController ctrl) {
        return getCache(ctrl.getController(), Database.class);
    }

    @ZenMethod
    public static ResearchStation getResearchStation(IMachineController ctrl) {
        return getCache(ctrl.getController(), ResearchStation.class);
    }

    public static <T extends NetNode> T getCache(TileMultiblockMachineController ctrl, Class<T> type) {
        DynamicMachine machine = ctrl.getFoundMachine();
        if (type == null || machine == null) {
            return null;
        }

        NetNode node = CACHED_NODES.get(ctrl);
        if (type.isInstance(node)) {
            return type.cast(node);
        } else {
            Class<? extends NetNode> ctrlType = RegistryHyperNet.getNodeType(machine);
            if (type != ctrlType) {
                throw new IllegalStateException(String.format(
                        "Try to get node type %s, but controller type is %s.",
                        type.getSimpleName(), ctrlType.getSimpleName()
                ));
            }
            CACHED_NODES.remove(ctrl);
        }

        try {
            Constructor<T> constructor = type.getConstructor(TileMultiblockMachineController.class);
            T instance = constructor.newInstance(ctrl);
            instance.readNBT();

            CACHED_NODES.put(ctrl, instance);
            return instance;
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(
                    "Unable to find single parameter constructor in class! Please report this issue to the developers.",
                    e
            );
        } catch (Exception e) {
            NovaEngineeringCore.log.warn(e);
            return null;
        }
    }
}
