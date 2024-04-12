package github.kasuminova.novaeng.mixin.minecraft.forge;

import net.minecraftforge.client.GuiIngameForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(GuiIngameForge.class)
public interface AccessorGuiIngameForge {

    @Invoker(remap = false)
    void callRenderCrosshairs(float partialTicks);

}
