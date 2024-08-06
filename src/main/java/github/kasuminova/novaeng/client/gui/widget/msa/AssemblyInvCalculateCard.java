package github.kasuminova.novaeng.client.gui.widget.msa;

import github.kasuminova.mmce.client.gui.widget.base.WidgetController;
import github.kasuminova.mmce.client.gui.widget.container.Column;
import github.kasuminova.mmce.client.gui.widget.container.Row;
import github.kasuminova.novaeng.NovaEngineeringCore;
import github.kasuminova.novaeng.client.gui.GuiModularServerAssembler;
import github.kasuminova.novaeng.client.gui.widget.msa.overlay.OverlayCalculateCardExt;
import github.kasuminova.novaeng.client.gui.widget.msa.slot.SlotCalculateCard;
import github.kasuminova.novaeng.client.gui.widget.msa.slot.SlotCalculateCardExtension;
import github.kasuminova.novaeng.client.gui.widget.msa.slot.SlotCalculateCardHeatRadiator;
import github.kasuminova.novaeng.common.container.slot.AssemblySlotManager;
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
        this.open.setTexture(BUTTON_TEX_X, 237);
        this.open.setHoveredTexture(37, 219);

        AssemblySlotManager slotManager = assemblyInvManager.slotManager;

        SlotCalculateCardExtension ext_0_0 = new SlotCalculateCardExtension(8, slotManager);
        SlotCalculateCard slot_0_0 = new SlotCalculateCard(0, slotManager);
        SlotCalculateCard slot_0_1 = new SlotCalculateCard(1, slotManager);
        SlotCalculateCardHeatRadiator slot_heat_radiator_0_0 = new SlotCalculateCardHeatRadiator(12, slotManager);
        SlotCalculateCardHeatRadiator slot_heat_radiator_0_1 = new SlotCalculateCardHeatRadiator(13, slotManager);

        SlotCalculateCardExtension ext_1_0 = new SlotCalculateCardExtension(9, slotManager);
        SlotCalculateCard slot_1_0 = new SlotCalculateCard(2, slotManager);
        SlotCalculateCard slot_1_1 = new SlotCalculateCard(3, slotManager);
        SlotCalculateCardHeatRadiator slot_heat_radiator_1_0 = new SlotCalculateCardHeatRadiator(14, slotManager);
        SlotCalculateCardHeatRadiator slot_heat_radiator_1_1 = new SlotCalculateCardHeatRadiator(15, slotManager);

        SlotCalculateCardExtension ext_2_0 = new SlotCalculateCardExtension(10, slotManager);
        SlotCalculateCard slot_2_0 = new SlotCalculateCard(4, slotManager);
        SlotCalculateCard slot_2_1 = new SlotCalculateCard(5, slotManager);
        SlotCalculateCardHeatRadiator slot_heat_radiator_2_0 = new SlotCalculateCardHeatRadiator(16, slotManager);
        SlotCalculateCardHeatRadiator slot_heat_radiator_2_1 = new SlotCalculateCardHeatRadiator(17, slotManager);

        SlotCalculateCardExtension ext_3_0 = new SlotCalculateCardExtension(11, slotManager);
        SlotCalculateCard slot_3_0 = new SlotCalculateCard(6, slotManager);
        SlotCalculateCard slot_3_1 = new SlotCalculateCard(7, slotManager);
        SlotCalculateCardHeatRadiator slot_heat_radiator_3_0 = new SlotCalculateCardHeatRadiator(18, slotManager);
        SlotCalculateCardHeatRadiator slot_heat_radiator_3_1 = new SlotCalculateCardHeatRadiator(19, slotManager);

        slotColumn.setMarginLeft(7).setMarginUp(7);
        slotColumn.addWidgets(new Row().addWidgets(ext_0_0, slot_0_0, slot_0_1, slot_heat_radiator_0_0, slot_heat_radiator_0_1));
        slotColumn.addWidgets(new Row().addWidgets(ext_1_0, slot_1_0, slot_1_1, slot_heat_radiator_1_0, slot_heat_radiator_1_1));
        slotColumn.addWidgets(new Row().addWidgets(ext_2_0, slot_2_0, slot_2_1, slot_heat_radiator_2_0, slot_heat_radiator_2_1));
        slotColumn.addWidgets(new Row().addWidgets(ext_3_0, slot_3_0, slot_3_1, slot_heat_radiator_3_0, slot_heat_radiator_3_1));

        Column slotCalculateCardExtOverlay = new Column();
        slotCalculateCardExtOverlay.addWidgets(
                new OverlayCalculateCardExt(ext_0_0),
                new OverlayCalculateCardExt(ext_1_0),
                new OverlayCalculateCardExt(ext_2_0),
                new OverlayCalculateCardExt(ext_3_0)
        ).setAbsX(147).setAbsY(59);
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
