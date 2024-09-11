package github.kasuminova.novaeng.mixin.ae2;

import appeng.client.gui.AEBaseGui;
import appeng.client.gui.implementations.GuiCraftConfirm;
import github.kasuminova.novaeng.NovaEngineeringCore;
import github.kasuminova.novaeng.client.gui.widget.vanilla.GuiButtonImageExt;
import github.kasuminova.novaeng.common.network.appeng.PktSwitchCraftingTree;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiCraftConfirm.class)
public abstract class MixinGuiCraftConfirm extends AEBaseGui {

    @Unique
    private GuiButtonImageExt novaeng_core$craftTree;

    public MixinGuiCraftConfirm() {
        super(null);
    }

    @Inject(method = "initGui", at = @At("RETURN"))
    private void injectInitGui(final CallbackInfo ci) {
        novaeng_core$craftTree = new GuiButtonImageExt(-1,
                (this.guiLeft + this.xSize) - 26, this.guiTop - 4, 26, 19,
                0, 0, 19,
                new ResourceLocation(NovaEngineeringCore.MOD_ID, "textures/gui/guicraftingtree_light.png"));
        novaeng_core$craftTree.setMessage(I18n.format("gui.crafting_tree.switch"));
        this.buttonList.add(this.novaeng_core$craftTree);
    }

    @Inject(method = "actionPerformed", at = @At("HEAD"), cancellable = true)
    private void injectActionPerformed(final GuiButton btn, final CallbackInfo ci) {
        if (btn == novaeng_core$craftTree) {
            NovaEngineeringCore.NET_CHANNEL.sendToServer(new PktSwitchCraftingTree());
            ci.cancel();
        }
    }

}
