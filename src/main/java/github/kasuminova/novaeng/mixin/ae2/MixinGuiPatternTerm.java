package github.kasuminova.novaeng.mixin.ae2;

import appeng.client.gui.implementations.GuiMEMonitorable;
import appeng.client.gui.implementations.GuiPatternTerm;
import appeng.client.gui.widgets.GuiTabButton;
import appeng.container.implementations.ContainerPatternEncoder;
import github.kasuminova.novaeng.NovaEngineeringCore;
import github.kasuminova.novaeng.common.block.ecotech.efabricator.BlockEFabricatorController;
import github.kasuminova.novaeng.common.network.PktPatternTermUploadPattern;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiPatternTerm.class)
public class MixinGuiPatternTerm extends GuiMEMonitorable {

    @Final
    @Shadow(remap = false)
    private ContainerPatternEncoder container;

    @Unique
    private GuiTabButton novaeng_core$uploadPatternButton;

    @SuppressWarnings("DataFlowIssue")
    public MixinGuiPatternTerm() {
        super(null, null);
    }

    @Inject(method = "initGui", at = @At("RETURN"))
    private void injectInitGui(final CallbackInfo ci) {
        novaeng_core$uploadPatternButton = new GuiTabButton(
                this.guiLeft + 173, this.guiTop + this.ySize - 155,
                new ItemStack(BlockEFabricatorController.L4), I18n.format("gui.efabricator.button.upload_pattern"), this.itemRender
        );
        this.buttonList.add(this.novaeng_core$uploadPatternButton);
    }

    @Inject(method = "actionPerformed", at = @At("HEAD"), cancellable = true)
    private void injectActionPerformed(final GuiButton btn, final CallbackInfo ci) {
        if (btn == novaeng_core$uploadPatternButton) {
            NovaEngineeringCore.NET_CHANNEL.sendToServer(new PktPatternTermUploadPattern());
            ci.cancel();
        }
    }

    @Inject(method = "drawFG", at = @At("HEAD"), remap = false)
    private void injectDrawFG(final int offsetX, final int offsetY, final int mouseX, final int mouseY, final CallbackInfo ci) {
        novaeng_core$uploadPatternButton.visible = this.container.isCraftingMode();
    }

}
