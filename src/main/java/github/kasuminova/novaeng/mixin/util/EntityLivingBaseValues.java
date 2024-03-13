package github.kasuminova.novaeng.mixin.util;

import github.kasuminova.novaeng.NovaEngineeringCore;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.datasync.DataParameter;

import java.lang.reflect.Field;

public class EntityLivingBaseValues {

    public static final DataParameter<Float> HEALTH;

    static {
        DataParameter<Float> health = null;
        try {
            Field filedHealth = EntityLivingBase.class.getDeclaredField("HEALTH");
            filedHealth.setAccessible(true);
            health = (DataParameter<Float>) filedHealth.get(null);
        } catch (Exception e) {
            NovaEngineeringCore.log.warn("Cannot get field EntityLivingBase.HEALTH!", e);
        }
        HEALTH = health;
    }

}
