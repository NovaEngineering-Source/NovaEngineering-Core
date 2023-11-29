package github.kasuminova.novaeng.client.gui.widget.msa;

import github.kasuminova.mmce.client.gui.widget.base.WidgetController;
import github.kasuminova.mmce.client.gui.widget.container.Column;
import github.kasuminova.mmce.client.gui.widget.container.Row;
import github.kasuminova.novaeng.NovaEngineeringCore;
import github.kasuminova.novaeng.client.gui.GuiModularServerAssembler;
import github.kasuminova.novaeng.client.gui.widget.msa.overlay.OverlayCalculateCardExt;
import github.kasuminova.novaeng.client.gui.widget.msa.slot.SlotCalculateCard;
import github.kasuminova.novaeng.client.gui.widget.msa.slot.SlotCalculateCardExtension;
import net.minecraft.util.ResourceLocation;

public class AssemblyInvCalculateCard extends AssemblyInv {

    public static final int CLOSED_WIDTH = 27;
    public static final int CLOSED_HEIGHT = 26;

    public static final int OPENED_WIDTH = 104;
    public static final int OPENED_HEIGHT = 86;

    public static final int BUTTON_TEX_X = 37;

    public AssemblyInvCalculateCard(final AssemblyInvManager assemblyInvManager, final WidgetController widgetController) {
        super(assemblyInvManager, widgetController);
        this.width = CLOSED_WIDTH;
        this.height = CLOSED_HEIGHT;

        this.openedBgTexLocation = new ResourceLocation(NovaEngineeringCore.MOD_ID, "textures/gui/msa_calculate_card.png");
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
        this.open.setTextureXY(BUTTON_TEX_X, 237);
        this.open.setHoveredTextureXY(37, 219);

        SlotCalculateCardExtension ext_0_0 = new SlotCalculateCardExtension(0);
        SlotCalculateCard slot_0_0 = new SlotCalculateCard(0).dependsOn(ext_0_0);
        SlotCalculateCard slot_0_1 = new SlotCalculateCard(1).dependsOn(ext_0_0);
        SlotCalculateCard slot_0_2 = new SlotCalculateCard(2).dependsOn(ext_0_0);
        SlotCalculateCard slot_0_3 = new SlotCalculateCard(3).dependsOn(ext_0_0);

        SlotCalculateCardExtension ext_1_0 = new SlotCalculateCardExtension(1);
        SlotCalculateCard slot_1_0 = new SlotCalculateCard(4).dependsOn(ext_1_0);
        SlotCalculateCard slot_1_1 = new SlotCalculateCard(5).dependsOn(ext_1_0);
        SlotCalculateCard slot_1_2 = new SlotCalculateCard(6).dependsOn(ext_1_0);
        SlotCalculateCard slot_1_3 = new SlotCalculateCard(7).dependsOn(ext_1_0);
        
        SlotCalculateCardExtension ext_2_0 = new SlotCalculateCardExtension(2);
        SlotCalculateCard slot_2_0 = new SlotCalculateCard(8).dependsOn(ext_2_0);
        SlotCalculateCard slot_2_1 = new SlotCalculateCard(9).dependsOn(ext_2_0);
        SlotCalculateCard slot_2_2 = new SlotCalculateCard(10).dependsOn(ext_2_0);
        SlotCalculateCard slot_2_3 = new SlotCalculateCard(11).dependsOn(ext_2_0);
        
        SlotCalculateCardExtension ext_3_0 = new SlotCalculateCardExtension(3);
        SlotCalculateCard slot_3_0 = new SlotCalculateCard(12).dependsOn(ext_3_0);
        SlotCalculateCard slot_3_1 = new SlotCalculateCard(13).dependsOn(ext_3_0);
        SlotCalculateCard slot_3_2 = new SlotCalculateCard(14).dependsOn(ext_3_0);
        SlotCalculateCard slot_3_3 = new SlotCalculateCard(15).dependsOn(ext_3_0);

        slotColum.addWidgets(new Row().addWidgets(ext_0_0, slot_0_0, slot_0_1, slot_0_2, slot_0_3).setMarginLeft(7).setMarginUp(7));
        slotColum.addWidgets(new Row().addWidgets(ext_1_0, slot_1_0, slot_1_1, slot_1_2, slot_1_3).setMarginLeft(7));
        slotColum.addWidgets(new Row().addWidgets(ext_2_0, slot_2_0, slot_2_1, slot_2_2, slot_2_3).setMarginLeft(7));
        slotColum.addWidgets(new Row().addWidgets(ext_3_0, slot_3_0, slot_3_1, slot_3_2, slot_3_3).setMarginLeft(7));

        Column slotCalculateCardExtOverlay = new Column();
        slotCalculateCardExtOverlay.setAbsX(147).setAbsY(59).addWidgets(
                new OverlayCalculateCardExt(ext_0_0),
                new OverlayCalculateCardExt(ext_1_0),
                new OverlayCalculateCardExt(ext_2_0),
                new OverlayCalculateCardExt(ext_3_0)
        );
        widgetController.addWidgetContainer(slotCalculateCardExtOverlay);
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
