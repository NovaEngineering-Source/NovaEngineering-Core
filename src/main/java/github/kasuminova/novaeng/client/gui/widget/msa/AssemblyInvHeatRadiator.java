package github.kasuminova.novaeng.client.gui.widget.msa;

import github.kasuminova.mmce.client.gui.widget.base.WidgetController;
import github.kasuminova.novaeng.NovaEngineeringCore;
import github.kasuminova.novaeng.client.gui.GuiModularServerAssembler;
import net.minecraft.util.ResourceLocation;

public class AssemblyInvHeatRadiator extends AssemblyInv {

    public static final int CLOSED_WIDTH = 27;
    public static final int CLOSED_HEIGHT = 26;

    public static final int OPENED_WIDTH = 102;
    public static final int OPENED_HEIGHT = 90;

    public static final int BUTTON_TEX_X = 91;

    public AssemblyInvHeatRadiator(final AssemblyInvManager assemblyInvManager, final WidgetController widgetController) {
        super(assemblyInvManager, widgetController);
        this.width = CLOSED_WIDTH;
        this.height = CLOSED_HEIGHT;

        this.openedBgTexLocation = new ResourceLocation(NovaEngineeringCore.MOD_ID, "textures/gui/msa_heat_radiator.png");
        this.openedInvBgTexWidth = OPENED_WIDTH;
        this.openedInvBgTexHeight = OPENED_HEIGHT;
        this.openedInvBgTexOffsetX = 0;
        this.openedInvBgTexOffsetY = 0;

        this.closedBgTexLocation = GuiModularServerAssembler.TEXTURES_ELEMENTS;
        this.closedInvBgTexWidth = CLOSED_WIDTH;
        this.closedInvBgTexHeight = CLOSED_HEIGHT;
        this.closedInvBgTexOffsetX = 29;
        this.closedInvBgTexOffsetY = 183;

        this.open.setTextureLocation(GuiModularServerAssembler.TEXTURES_ELEMENTS);
        this.open.setMargin(5, 0, 4, 0);
        this.open.setWidth(18).setHeight(18);
        this.open.setTexture(BUTTON_TEX_X, 237);
        this.open.setHoveredTexture(BUTTON_TEX_X, 219);
    }

    @Override
    public void openInv() {
        super.openInv();

        this.width = OPENED_WIDTH;
        this.height = OPENED_HEIGHT;
    }

    @Override
    public void closeInv() {
        super.closeInv();

        this.width = CLOSED_WIDTH;
        this.height = CLOSED_HEIGHT;
    }
}
