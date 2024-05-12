package github.kasuminova.novaeng.mixin.astralsorcery;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.constellation.perk.PlayerAttributeMap;
import hellfirepvp.astralsorcery.common.constellation.perk.attribute.PerkAttributeModifier;
import hellfirepvp.astralsorcery.common.constellation.perk.attribute.PerkAttributeType;
import hellfirepvp.astralsorcery.common.util.log.LogCategory;
import io.netty.util.internal.ThrowableUtil;
import net.minecraftforge.fml.relauncher.Side;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;
import java.util.Map;

@Mixin(PlayerAttributeMap.class)
public class MixinPlayerAttributeMap {

    @Shadow(remap = false)
    private Map<PerkAttributeType, List<PerkAttributeModifier>> attributes;

    @Shadow(remap = false)
    private Side side;

    /**
     * @author Kasumi_Nova
     * @reason No Crashes.
     */
    @Overwrite(remap = false)
    void assertConvertersModifiable() {
        Thread thread = Thread.currentThread();
        if (!thread.getName().equalsIgnoreCase("Server thread") && !thread.getName().equalsIgnoreCase("Client thread")) {
            try {
                throw new RuntimeException("Detected invalid thread to modify converters!");
            } catch (RuntimeException e) {
                AstralSorcery.log.warn(ThrowableUtil.stackTraceToString(e));
            }
        }

        int appliedModifiers = 0;
        for (List<PerkAttributeModifier> modifiers : this.attributes.values()) {
            appliedModifiers += modifiers.size();
        }
        if (appliedModifiers > 0) {
            LogCategory.PERKS.warn(() -> "Following modifiers are still applied on " + side.name() + " while trying to modify converters:");
            for (List<PerkAttributeModifier> modifiers : this.attributes.values()) {
                for (PerkAttributeModifier modifier : modifiers) {
                    LogCategory.PERKS.warn(() -> "Modifier: " + modifier.getId());
                }
            }

            AstralSorcery.log.warn("Trying to modify PerkConverters while modifiers are applied!");
        }
    }
    
}
