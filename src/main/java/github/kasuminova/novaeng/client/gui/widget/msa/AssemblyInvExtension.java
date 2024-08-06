package github.kasuminova.novaeng.client.gui.widget.msa;

import github.kasuminova.mmce.client.gui.widget.base.WidgetController;
import github.kasuminova.mmce.client.gui.widget.container.Column;
import github.kasuminova.mmce.client.gui.widget.container.Row;
import github.kasuminova.novaeng.NovaEngineeringCore;
import github.kasuminova.novaeng.client.gui.GuiModularServerAssembler;
import github.kasuminova.novaeng.client.gui.widget.msa.overlay.OverlayExtensionCard;
import github.kasuminova.novaeng.client.gui.widget.msa.overlay.OverlayExtensionCardExtension;
import github.kasuminova.novaeng.client.gui.widget.msa.slot.SlotExtensionCard;
import github.kasuminova.novaeng.client.gui.widget.msa.slot.SlotExtensionCardExtension;
import github.kasuminova.novaeng.client.gui.widget.msa.slot.SlotExtensionCardHeatRadiator;
import github.kasuminova.novaeng.common.container.slot.AssemblySlotManager;
import net.minecraft.util.ResourceLocation;

public class AssemblyInvExtension extends AssemblyInvToggleable {
    public static final ResourceLocation WIDGET_TEX_LOCATION = new ResourceLocation(NovaEngineeringCore.MOD_ID, "textures/gui/msa_extension.png");

    public static final int CLOSED_WIDGET_WIDTH = 27;
    public static final int CLOSED_WIDGET_HEIGHT = 26;

    public static final int OPENED_WIDGET_WIDTH = 120;
    public static final int OPENED_WIDGET_HEIGHT = 86;

    public static final int OPENED_HEAT_RADIATOR_COLUMN_WIDGET_WIDTH = 102;
    public static final int OPENED_HEAT_RADIATOR_COLUMN_WIDGET_HEIGHT = 86;

    public static final int BUTTON_TEX_X = 55;

    public static final int TOGGLE_FIRST_BUTTON_TEX_X = 134;
    public static final int TOGGLE_SECOND_BUTTON_TEX_X = 121;

    public static final int TOGGLE_BUTTON_TEX_Y = 1;
    public static final int TOGGLE_BUTTON_HOVERED_TEX_Y = 13;
    public static final int TOGGLE_BUTTON_CLICKED_TEX_Y = 25;

    public static final int TOGGLE_BUTTON_WIDTH = 12;
    public static final int TOGGLE_BUTTON_HEIGHT = 12;

    public AssemblyInvExtension(final AssemblyInvManager assemblyInvManager, final WidgetController widgetController) {
        super(assemblyInvManager, widgetController);
        this.width = CLOSED_WIDGET_WIDTH;
        this.height = CLOSED_WIDGET_HEIGHT;

        this.openedBgTexLocation = WIDGET_TEX_LOCATION;
        this.openedInvBgTexWidth = OPENED_WIDGET_WIDTH;
        this.openedInvBgTexHeight = OPENED_WIDGET_HEIGHT;
        this.openedInvBgTexOffsetX = 0;
        this.openedInvBgTexOffsetY = 0;

        this.secondOpenedBgTexLocation = WIDGET_TEX_LOCATION;
        this.secondOpenedInvBgTexWidth = OPENED_HEAT_RADIATOR_COLUMN_WIDGET_WIDTH;
        this.secondOpenedInvBgTexHeight = OPENED_HEAT_RADIATOR_COLUMN_WIDGET_HEIGHT;
        this.secondOpenedInvBgTexOffsetX = 0;
        this.secondOpenedInvBgTexOffsetY = 86;

        this.closedBgTexLocation = GuiModularServerAssembler.TEXTURES_ELEMENTS;
        this.closedInvBgTexWidth = CLOSED_WIDGET_WIDTH;
        this.closedInvBgTexHeight = CLOSED_WIDGET_HEIGHT;
        this.closedInvBgTexOffsetX = 29;
        this.closedInvBgTexOffsetY = 183;

        this.open.setTextureLocation(GuiModularServerAssembler.TEXTURES_ELEMENTS);
        this.open.setMargin(5, 0, 4, 0);
        this.open.setWidth(18).setHeight(18);
        this.open.setTexture(BUTTON_TEX_X, 237);
        this.open.setHoveredTexture(BUTTON_TEX_X, 219);

        this.toggleFirst.setTextureLocation(WIDGET_TEX_LOCATION);
        this.toggleFirst.setWidth(TOGGLE_BUTTON_WIDTH).setHeight(TOGGLE_BUTTON_HEIGHT);
        this.toggleFirst.setTexture(TOGGLE_FIRST_BUTTON_TEX_X, TOGGLE_BUTTON_TEX_Y);
        this.toggleFirst.setHoveredTexture(TOGGLE_FIRST_BUTTON_TEX_X, TOGGLE_BUTTON_HOVERED_TEX_Y);
        this.toggleFirst.setClickedTexture(TOGGLE_FIRST_BUTTON_TEX_X, TOGGLE_BUTTON_CLICKED_TEX_Y);
        this.toggleSecond.setTextureLocation(WIDGET_TEX_LOCATION);
        this.toggleSecond.setWidth(TOGGLE_BUTTON_WIDTH).setHeight(TOGGLE_BUTTON_HEIGHT);
        this.toggleSecond.setTexture(TOGGLE_SECOND_BUTTON_TEX_X, TOGGLE_BUTTON_TEX_Y);
        this.toggleSecond.setHoveredTexture(TOGGLE_SECOND_BUTTON_TEX_X, TOGGLE_BUTTON_HOVERED_TEX_Y);
        this.toggleSecond.setClickedTexture(TOGGLE_SECOND_BUTTON_TEX_X, TOGGLE_BUTTON_CLICKED_TEX_Y);

        AssemblySlotManager slotManager = assemblyInvManager.slotManager;

        SlotExtensionCardExtension ext_0_0 = new SlotExtensionCardExtension(16, slotManager);
        SlotExtensionCard ext_c_0_0 = new SlotExtensionCard(0, slotManager);
        SlotExtensionCard ext_c_0_1 = new SlotExtensionCard(1, slotManager);
        SlotExtensionCard ext_c_0_2 = new SlotExtensionCard(2, slotManager);
        SlotExtensionCard ext_c_0_3 = new SlotExtensionCard(3, slotManager);
        SlotExtensionCardHeatRadiator ext_c_heat_radiator_0_0 = new SlotExtensionCardHeatRadiator(20, slotManager);
        SlotExtensionCardHeatRadiator ext_c_heat_radiator_0_1 = new SlotExtensionCardHeatRadiator(21, slotManager);
        SlotExtensionCardHeatRadiator ext_c_heat_radiator_0_2 = new SlotExtensionCardHeatRadiator(22, slotManager);
        SlotExtensionCardHeatRadiator ext_c_heat_radiator_0_3 = new SlotExtensionCardHeatRadiator(23, slotManager);

        SlotExtensionCardExtension ext_1_0 = new SlotExtensionCardExtension(17, slotManager);
        SlotExtensionCard ext_c_1_0 = new SlotExtensionCard(4, slotManager);
        SlotExtensionCard ext_c_1_1 = new SlotExtensionCard(5, slotManager);
        SlotExtensionCard ext_c_1_2 = new SlotExtensionCard(6, slotManager);
        SlotExtensionCard ext_c_1_3 = new SlotExtensionCard(7, slotManager);
        SlotExtensionCardHeatRadiator ext_c_heat_radiator_1_0 = new SlotExtensionCardHeatRadiator(24, slotManager);
        SlotExtensionCardHeatRadiator ext_c_heat_radiator_1_1 = new SlotExtensionCardHeatRadiator(25, slotManager);
        SlotExtensionCardHeatRadiator ext_c_heat_radiator_1_2 = new SlotExtensionCardHeatRadiator(26, slotManager);
        SlotExtensionCardHeatRadiator ext_c_heat_radiator_1_3 = new SlotExtensionCardHeatRadiator(27, slotManager);

        SlotExtensionCardExtension ext_2_0 = new SlotExtensionCardExtension(18, slotManager);
        SlotExtensionCard ext_c_2_0 = new SlotExtensionCard(8, slotManager);
        SlotExtensionCard ext_c_2_1 = new SlotExtensionCard(9, slotManager);
        SlotExtensionCard ext_c_2_2 = new SlotExtensionCard(10, slotManager);
        SlotExtensionCard ext_c_2_3 = new SlotExtensionCard(11, slotManager);
        SlotExtensionCardHeatRadiator ext_c_heat_radiator_2_0 = new SlotExtensionCardHeatRadiator(28, slotManager);
        SlotExtensionCardHeatRadiator ext_c_heat_radiator_2_1 = new SlotExtensionCardHeatRadiator(29, slotManager);
        SlotExtensionCardHeatRadiator ext_c_heat_radiator_2_2 = new SlotExtensionCardHeatRadiator(30, slotManager);
        SlotExtensionCardHeatRadiator ext_c_heat_radiator_2_3 = new SlotExtensionCardHeatRadiator(31, slotManager);

        SlotExtensionCardExtension ext_3_0 = new SlotExtensionCardExtension(19, slotManager);
        SlotExtensionCard ext_c_3_0 = new SlotExtensionCard(12, slotManager);
        SlotExtensionCard ext_c_3_1 = new SlotExtensionCard(13, slotManager);
        SlotExtensionCard ext_c_3_2 = new SlotExtensionCard(14, slotManager);
        SlotExtensionCard ext_c_3_3 = new SlotExtensionCard(15, slotManager);
        SlotExtensionCardHeatRadiator ext_c_heat_radiator_3_0 = new SlotExtensionCardHeatRadiator(32, slotManager);
        SlotExtensionCardHeatRadiator ext_c_heat_radiator_3_1 = new SlotExtensionCardHeatRadiator(33, slotManager);
        SlotExtensionCardHeatRadiator ext_c_heat_radiator_3_2 = new SlotExtensionCardHeatRadiator(34, slotManager);
        SlotExtensionCardHeatRadiator ext_c_heat_radiator_3_3 = new SlotExtensionCardHeatRadiator(35, slotManager);

        slotColumn.setMarginLeft(4).setMarginUp(7);
        secondSlotColumn.setMarginLeft(4).setMarginUp(7);

        slotColumn.addWidgets(new Row().addWidgets(ext_0_0, ext_c_0_0, ext_c_0_1, ext_c_0_2, ext_c_0_3));
        slotColumn.addWidgets(new Row().addWidgets(ext_1_0, ext_c_1_0, ext_c_1_1, ext_c_1_2, ext_c_1_3));
        slotColumn.addWidgets(new Row().addWidgets(ext_2_0, ext_c_2_0, ext_c_2_1, ext_c_2_2, ext_c_2_3));
        slotColumn.addWidgets(new Row().addWidgets(ext_3_0, ext_c_3_0, ext_c_3_1, ext_c_3_2, ext_c_3_3));
        secondSlotColumn.addWidgets(new Row().addWidgets(ext_c_heat_radiator_0_0, ext_c_heat_radiator_0_1, ext_c_heat_radiator_0_2, ext_c_heat_radiator_0_3));
        secondSlotColumn.addWidgets(new Row().addWidgets(ext_c_heat_radiator_1_0, ext_c_heat_radiator_1_1, ext_c_heat_radiator_1_2, ext_c_heat_radiator_1_3));
        secondSlotColumn.addWidgets(new Row().addWidgets(ext_c_heat_radiator_2_0, ext_c_heat_radiator_2_1, ext_c_heat_radiator_2_2, ext_c_heat_radiator_2_3));
        secondSlotColumn.addWidgets(new Row().addWidgets(ext_c_heat_radiator_3_0, ext_c_heat_radiator_3_1, ext_c_heat_radiator_3_2, ext_c_heat_radiator_3_3));

        Column slotExtensionCardOverlayCol = new Column();
        slotExtensionCardOverlayCol.addWidgets(
                new Row().addWidgets(
                        new OverlayExtensionCard(ext_c_0_0).setMarginRight(2),
                        new OverlayExtensionCard(ext_c_0_1).setMarginRight(2),
                        new OverlayExtensionCard(ext_c_0_2).setMarginRight(2),
                        new OverlayExtensionCard(ext_c_0_3).setMarginRight(4),
                        new OverlayExtensionCardExtension(ext_0_0).setMarginUp(1)
                ).setMarginDown(4),
                new Row().addWidgets(
                        new OverlayExtensionCard(ext_c_1_0).setMarginRight(2),
                        new OverlayExtensionCard(ext_c_1_1).setMarginRight(2),
                        new OverlayExtensionCard(ext_c_1_2).setMarginRight(2),
                        new OverlayExtensionCard(ext_c_1_3).setMarginRight(4),
                        new OverlayExtensionCardExtension(ext_1_0).setMarginUp(1)
                ).setMarginDown(4),
                new Row().addWidgets(
                        new OverlayExtensionCard(ext_c_2_0).setMarginRight(2),
                        new OverlayExtensionCard(ext_c_2_1).setMarginRight(2),
                        new OverlayExtensionCard(ext_c_2_2).setMarginRight(2),
                        new OverlayExtensionCard(ext_c_2_3).setMarginRight(4),
                        new OverlayExtensionCardExtension(ext_2_0).setMarginUp(1)
                ).setMarginDown(4),
                new Row().addWidgets(
                        new OverlayExtensionCard(ext_c_3_0).setMarginRight(2),
                        new OverlayExtensionCard(ext_c_3_1).setMarginRight(2),
                        new OverlayExtensionCard(ext_c_3_2).setMarginRight(2),
                        new OverlayExtensionCard(ext_c_3_3).setMarginRight(4),
                        new OverlayExtensionCardExtension(ext_3_0).setMarginUp(1)
                )
        ).setAbsX(260).setAbsY(21);
        widgetController.addWidgetContainer(slotExtensionCardOverlayCol);
    }

    @Override
    public void openInv() {
        super.openInv();

        if (currentColumn == slotColumn) {
            this.width = OPENED_WIDGET_WIDTH;
            this.height = OPENED_WIDGET_HEIGHT;
        } else if (currentColumn == secondSlotColumn) {
            this.width = OPENED_HEAT_RADIATOR_COLUMN_WIDGET_WIDTH;
            this.height = OPENED_HEAT_RADIATOR_COLUMN_WIDGET_HEIGHT;
        }
    }

    @Override
    public void closeInv() {
        super.closeInv();

        this.width = CLOSED_WIDGET_WIDTH;
        this.height = CLOSED_WIDGET_HEIGHT;
    }
}
