package github.kasuminova.novaeng.mixin.minecraft;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.ArrayDeque;

@Mixin(ParticleManager.class)
public interface AccessorParticleManager {

    @Accessor
    ArrayDeque<Particle>[][] getFxLayers();

}
