package github.kasuminova.novaeng.common.crafttweaker.artisanworktables;

import com.codetaylor.mc.artisanworktables.modules.worktables.integration.crafttweaker.builder.IZenRecipeBuilder;
import crafttweaker.annotations.ZenRegister;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenRegister
@ZenClass("novaeng.artisanworktables.builder.RecipeBuilder")
public class RecipeBuilder {

    @ZenMethod
    public static IZenRecipeBuilder get(String table) {
        return IZenRecipeBuilder.get(table);
    }

}
