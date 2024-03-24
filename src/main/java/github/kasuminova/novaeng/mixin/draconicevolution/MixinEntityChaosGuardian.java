package github.kasuminova.novaeng.mixin.draconicevolution;

import com.brandon3055.draconicevolution.entity.EntityChaosGuardian;
import com.brandon3055.draconicevolution.entity.EntityDragonOld;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.DimensionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@SuppressWarnings("MethodMayBeStatic")
@Mixin(EntityChaosGuardian.class)
public class MixinEntityChaosGuardian extends EntityDragonOld {

    public MixinEntityChaosGuardian() {
        super(null);
    }

    @Inject(method = "destroyBlocksInAABB", at = @At("HEAD"), cancellable = true, remap = false)
    private void preventDestroy(final AxisAlignedBB par1AxisAlignedBB, final CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(false);
    }

    @Inject(method = "onLivingUpdate", at = @At("HEAD"))
    private void injectLivingUpdate(final CallbackInfo ci) {
        if (world.provider.getDimensionType() != DimensionType.THE_END) {
            setHealth(0);
            setDead();
        }
    }

}
