package github.kasuminova.novaeng.common.hypernet.old.base;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IIngredient;
import net.minecraft.util.Tuple;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@ZenRegister
@ZenClass("novaeng.hypernet.NetNodeTypeRepairable")
public abstract class NetNodeTypeRepairable extends NetNodeType {
    public static final String FIX_THREAD_NAME = "novaeng.hypernet.maintenance_controller";

    protected final List<Tuple<List<IIngredient>, Integer>> fixIngredientList = new ArrayList<>();

    public NetNodeTypeRepairable(final String typeName,
                                 final long energyUsage)
    {
        super(typeName, energyUsage);
    }

    public List<Tuple<List<IIngredient>, Integer>> getFixIngredientList() {
        return fixIngredientList;
    }

    @ZenMethod
    public NetNodeTypeRepairable addFixIngredient(final int durability, final IIngredient... ingredients) {
        fixIngredientList.add(new Tuple<>(Arrays.asList(ingredients), durability));
        return this;
    }
}
