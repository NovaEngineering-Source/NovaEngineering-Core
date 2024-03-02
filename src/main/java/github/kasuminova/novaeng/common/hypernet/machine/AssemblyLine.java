package github.kasuminova.novaeng.common.hypernet.machine;

import github.kasuminova.mmce.common.event.machine.MachineStructureUpdateEvent;
import github.kasuminova.mmce.common.event.recipe.FactoryRecipeFailureEvent;
import github.kasuminova.mmce.common.event.recipe.FactoryRecipeFinishEvent;
import github.kasuminova.mmce.common.event.recipe.FactoryRecipeTickEvent;
import github.kasuminova.mmce.common.event.recipe.RecipeCheckEvent;
import github.kasuminova.mmce.common.helper.IDynamicPatternInfo;
import github.kasuminova.novaeng.common.crafttweaker.expansion.RecipePrimerAssemblyLine;
import github.kasuminova.novaeng.common.crafttweaker.hypernet.HyperNetHelper;
import github.kasuminova.novaeng.common.hypernet.NetNodeCache;
import github.kasuminova.novaeng.common.hypernet.NetNodeImpl;
import github.kasuminova.novaeng.common.registry.RegistryHyperNet;
import hellfirepvp.modularmachinery.ModularMachinery;
import hellfirepvp.modularmachinery.common.crafting.ActiveMachineRecipe;
import hellfirepvp.modularmachinery.common.crafting.MachineRecipe;
import hellfirepvp.modularmachinery.common.crafting.helper.ComponentRequirement;
import hellfirepvp.modularmachinery.common.crafting.helper.CraftingStatus;
import hellfirepvp.modularmachinery.common.crafting.requirement.RequirementIngredientArray;
import hellfirepvp.modularmachinery.common.crafting.requirement.RequirementItem;
import hellfirepvp.modularmachinery.common.integration.crafttweaker.event.MMEvents;
import hellfirepvp.modularmachinery.common.machine.DynamicMachine;
import hellfirepvp.modularmachinery.common.machine.MachineRegistry;
import hellfirepvp.modularmachinery.common.machine.RecipeThread;
import hellfirepvp.modularmachinery.common.tiles.TileFactoryController;
import hellfirepvp.modularmachinery.common.tiles.base.TileMultiblockMachineController;
import io.netty.util.collection.IntObjectHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.util.ResourceLocation;

public class AssemblyLine extends NetNodeImpl {
    public static final String MACHINE_NAME = "assembly_line";

    protected final ChipManager chipManager = new ChipManager();

    private final Object2IntOpenHashMap<MachineRecipe> recipeItemLengths = new Object2IntOpenHashMap<>();

    public AssemblyLine(final TileMultiblockMachineController owner) {
        super(owner);
        recipeItemLengths.defaultReturnValue(-1);
    }

    public static void registerNetNode() {
        ResourceLocation registryName = new ResourceLocation(ModularMachinery.MODID, MACHINE_NAME);
        RegistryHyperNet.registerHyperNetNode(registryName, AssemblyLine.class);
        MMEvents.WAIT_FOR_MODIFY.add(() -> {
            DynamicMachine machine = MachineRegistry.getRegistry().getMachine(registryName);
            if (machine != null) {
                HyperNetHelper.addControllerGUIHyperNetInfo(machine, AssemblyLine.class);
            }
        });
        MMEvents.onStructureUpdate(MACHINE_NAME, event -> {
            AssemblyLine assemblyLine = NetNodeCache.getCache(event.getController(), AssemblyLine.class);
            if (assemblyLine != null) assemblyLine.onAssemblyLineStructureUpdate(event);
        });
    }

    public static boolean isNotAssemblyLine(final TileMultiblockMachineController ctrl) {
        DynamicMachine foundMachine = ctrl.getFoundMachine();
        if (foundMachine == null) {
            return false;
        }
        return !RecipePrimerAssemblyLine.ASSEMBLY_LINE.equals(foundMachine.getRegistryName());
    }

    @Override
    public void onMachineTick() {
        super.onMachineTick();
    }

    public void onAssemblyLineRecipeCheck(final RecipeCheckEvent event) {
        if (!(event.getController() instanceof TileFactoryController)) {
            event.setFailed("装配线不支持普通控制器！");
        }
        if (chipManager.isLocked(0)) {
            event.setFailed("其他线程正在使用装配线片 #0！");
        }
    }

    public void onAssemblyLineRecipePreTick(final FactoryRecipeTickEvent event) {
        ActiveMachineRecipe activeRecipe = event.getActiveRecipe();
        RecipeThread recipeThread = event.getRecipeThread();

        int using = getRequiredChipIndex(activeRecipe);

        if (!chipManager.lock(recipeThread, using)) {
            event.setFailed(false, String.format("装配线片 #%s 已被锁定！", using));
        }
    }

    public void onAssemblyLineRecipePostTick(final FactoryRecipeTickEvent event) {
        RecipeThread recipeThread = event.getRecipeThread();
        ActiveMachineRecipe activeRecipe = event.getActiveRecipe();
        int using = getRequiredChipIndex(activeRecipe);

        if (!recipeThread.getStatus().isCrafting()) {
            chipManager.unlock(recipeThread);
        } else {
            recipeThread
                    .setStatus(CraftingStatus.SUCCESS)
                    .setStatusInfo(String.format("工作中...（已锁定装配线片 #%s）", using));
        }
    }

    public void onAssemblyLineRecipeFinished(final FactoryRecipeFinishEvent event) {
        chipManager.unlock(event.getRecipeThread());
    }

    public void onAssemblyLineRecipeFailure(final FactoryRecipeFailureEvent event) {
        chipManager.unlock(event.getRecipeThread());
    }

    public void onAssemblyLineStructureUpdate(final MachineStructureUpdateEvent event) {
        TileMultiblockMachineController ctrl = event.getController();
        IDynamicPatternInfo pattern = ctrl.getDynamicPattern("line");
        if (pattern != null) ctrl.setExtraThreadCount(pattern.getSize());
        chipManager.clean();
    }

    protected int getRecipeLength(final MachineRecipe recipe) {
        int recipeLength = recipeItemLengths.getInt(recipe);
        if (recipeLength == -1) {
            recipeLength = 0;
            for (final ComponentRequirement<?, ?> req : recipe.getCraftingRequirements()) {
                if (req instanceof RequirementItem || req instanceof RequirementIngredientArray) {
                    recipeLength++;
                }
            }
            recipeItemLengths.put(recipe, recipeLength);
        }
        return recipeLength;
    }

    protected int getRequiredChipIndex(final ActiveMachineRecipe activeRecipe) {
        int recipeLength = getRecipeLength(activeRecipe.getRecipe());
        float progress = ((float) activeRecipe.getTick()) / ((float) activeRecipe.getTotalTick());

        return (int) (recipeLength * progress);
    }

    /**
     * 装配线片管理器。
     */
    public static class ChipManager {
        private final IntObjectHashMap<RecipeThread> threadChipUsing = new IntObjectHashMap<>();
        private final Object2IntOpenHashMap<RecipeThread> chipThreadUsing = new Object2IntOpenHashMap<>();

        public ChipManager() {
            chipThreadUsing.defaultReturnValue(-1);
        }

        /**
         * 配方线程尝试占用一个装配线片。
         *
         * @param thread 配方线程
         * @param index 装配线片位置
         * @return true 为占用成功或目标已经被自己占用，false 则代表目标片已被其他线程占用。
         */
        public boolean lock(final RecipeThread thread, final int index) {
            // 获取当前线程所在的片位置。
            int prevUsing = chipThreadUsing.getInt(thread);

            // 如果新位置和当前位置不一样，则尝试移动至新位置，否则直接返回 true。
            if (prevUsing == index) {
                return true;
            }

            // 检查目标片是否已经有线程正在使用。
            RecipeThread usingThread = threadChipUsing.get(index);
            if (usingThread != null && usingThread != thread && usingThread.getStatus().isCrafting()) {
                return false;
            }

            // 解除对先前片的占用。
            threadChipUsing.remove(prevUsing);
            threadChipUsing.put(index, thread);
            // 移动到新位置。
            chipThreadUsing.put(thread, index);
            return true;
        }

        /**
         * 配方线程解除对装配线片的占用。
         *
         * @param thread 配方线程
         */
        public void unlock(final RecipeThread thread) {
            int using = chipThreadUsing.removeInt(thread);
            if (using != -1) {
                threadChipUsing.remove(using);
            }
        }

        /**
         * 检查目标片是否被占用。
         * @param index 片位置
         * @return true 为被占用，false 为未被占用。
         */
        public boolean isLocked(final int index) {
            return threadChipUsing.containsKey(index);
        }

        public void clean() {
            chipThreadUsing.clear();
            threadChipUsing.clear();
        }
    }
}
