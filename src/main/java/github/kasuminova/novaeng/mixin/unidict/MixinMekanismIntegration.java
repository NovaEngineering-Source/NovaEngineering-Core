package github.kasuminova.novaeng.mixin.unidict;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import mekanism.common.recipe.RecipeHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 修复矿辞统一对 MEKCEU 失效的问题。
 */
@SuppressWarnings({"rawtypes", "unchecked"})
@Mixin(targets = "wanion.unidict.integration.MekanismIntegration")
public class MixinMekanismIntegration {

    @Unique
    private final Map<Map, Map> novaeng_core$mapping = new Reference2ObjectOpenHashMap<>();

    @Redirect(
            method = "call()Ljava/lang/String;",
            at = @At(
                    value = "INVOKE",
                    target = "Lmekanism/common/recipe/RecipeHandler$Recipe;get()Ljava/util/HashMap;",
                    remap = false
            ),
            remap = false
    )
    private HashMap redirectCallGet(final RecipeHandler.Recipe<?, ?, ?> instance) {
        Object2ObjectOpenHashMap original = instance.get();
        HashMap newMap = new HashMap<>(original);
        novaeng_core$mapping.put(newMap, original);
        return newMap;
    }

    @Redirect(
            method = {"fixMachineRecipes", "fixSawmillRecipes"},
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/Map;putAll(Ljava/util/Map;)V",
                    remap = false
            ),
            remap = false
    )
    private void redirectPutAllToOriginal(final Map instance, final Map toPut) {
        novaeng_core$applyModification(instance, toPut);
    }

    @Redirect(
            method = {"fixCrystallizerRecipes", "fixPRCRecipes"},
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/HashMap;putAll(Ljava/util/Map;)V",
                    remap = false
            ),
            remap = false
    )
    private void redirectHashMapPutAllToOriginal(final HashMap instance, final Map toPut) {
        novaeng_core$applyModification(instance, toPut);
    }

    @Unique
    private void novaeng_core$applyModification(final Map instance, final Map toPut) {
        Map original = novaeng_core$mapping.get(instance);
        Iterator it = original.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            if (!instance.containsKey(entry.getKey())) {
                it.remove();
            }
        }
        original.putAll(toPut);
    }

}
