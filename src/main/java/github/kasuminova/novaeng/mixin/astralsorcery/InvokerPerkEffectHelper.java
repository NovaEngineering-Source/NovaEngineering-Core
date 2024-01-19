package github.kasuminova.novaeng.mixin.astralsorcery;

import hellfirepvp.astralsorcery.common.constellation.perk.PerkEffectHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(PerkEffectHelper.class)
public interface InvokerPerkEffectHelper {

    @Invoker(remap = false)
    void invokeHandlePerkModification(EntityPlayer player, Side side, boolean remove);

}
