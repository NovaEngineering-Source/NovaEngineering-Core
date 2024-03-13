package github.kasuminova.novaeng.common.crafttweaker.expansion;

import crafttweaker.annotations.ZenRegister;
import github.kasuminova.novaeng.common.crafttweaker.util.NovaEngUtils;
import github.kasuminova.novaeng.common.machine.IllumPool;
import hellfirepvp.modularmachinery.common.integration.crafttweaker.RecipePrimer;
import stanhebben.zenscript.annotations.ZenExpansion;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenRegister
@ZenExpansion("mods.modularmachinery.RecipePrimer")
public class RecipePrimerIllumPool {

    @ZenMethod
    public static RecipePrimer addIllumPoolManaAddHandler(RecipePrimer primer, int amount) {
        primer.addPreCheckHandler(event -> IllumPool.onAddManaRecipeCheck(event, amount));
        primer.addFactoryPreTickHandler(event -> IllumPool.onAddManaRecipeTick(event, amount));
        primer.addRecipeTooltip(String.format("向辉光魔力池注入魔力，总计 §b%s§f 点。", NovaEngUtils.formatDecimal(amount)));
        return primer;
    }

    @ZenMethod
    public static RecipePrimer addIllumPoolIllumAddHandler(RecipePrimer primer, int amount) {
        primer.addPreCheckHandler(event -> IllumPool.onAddIllumRecipeCheck(event, amount));
        primer.addFactoryPreTickHandler(event -> IllumPool.onAddIllumRecipeTick(event, amount));
        primer.addRecipeTooltip(String.format("向辉光魔力池注入辉光魔力，总计 §e%s§f 点。", NovaEngUtils.formatDecimal(amount)));
        return primer;
    }

}