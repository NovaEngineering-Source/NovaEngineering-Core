package github.kasuminova.novaeng.client.gui.widget.msa;

import github.kasuminova.mmce.client.gui.widget.base.WidgetController;
import github.kasuminova.mmce.client.gui.widget.container.Column;
import github.kasuminova.mmce.client.gui.widget.container.Row;
import github.kasuminova.novaeng.NovaEngineeringCore;
import github.kasuminova.novaeng.client.gui.GuiModularServerAssembler;
import github.kasuminova.novaeng.client.gui.widget.msa.overlay.OverlayCPU;
import github.kasuminova.novaeng.client.gui.widget.msa.overlay.OverlayRAM;
import github.kasuminova.novaeng.client.gui.widget.msa.slot.*;
import github.kasuminova.novaeng.common.container.slot.AssemblySlotManager;
import github.kasuminova.novaeng.common.hypernet.server.assembly.AssemblyInvCPUConst;
import net.minecraft.util.ResourceLocation;

public class AssemblyInvCPU extends AssemblyInvToggleable {

    public static final ResourceLocation WIDGET_TEX_LOCATION = new ResourceLocation(NovaEngineeringCore.MOD_ID, "textures/gui/msa_cpu.png");

    public static final int CLOSED_WIDGET_WIDTH = 27;
    public static final int CLOSED_WIDGET_HEIGHT = 26;

    public static final int OPENED_WIDGET_WIDTH = 138;
    public static final int OPENED_WIDGET_HEIGHT = 86;

    public static final int OPENED_HEAT_RADIATOR_COLUMN_WIDGET_WIDTH = 120;
    public static final int OPENED_HEAT_RADIATOR_COLUMN_WIDGET_HEIGHT = 86;

    public static final int BUTTON_TEX_X = 1;

    public static final int TOGGLE_FIRST_BUTTON_TEX_X = 152;
    public static final int TOGGLE_SECOND_BUTTON_TEX_X = 139;

    public static final int TOGGLE_BUTTON_TEX_Y = 1;
    public static final int TOGGLE_BUTTON_HOVERED_TEX_Y = 13;
    public static final int TOGGLE_BUTTON_CLICKED_TEX_Y = 25;

    public static final int TOGGLE_BUTTON_WIDTH = 12;
    public static final int TOGGLE_BUTTON_HEIGHT = 12;

    public AssemblyInvCPU(final AssemblyInvManager assemblyInvManager, final WidgetController widgetController) {
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
        this.toggleFirst.setMouseDownTexture(TOGGLE_FIRST_BUTTON_TEX_X, TOGGLE_BUTTON_HOVERED_TEX_Y);
        this.toggleFirst.setClickedTexture(TOGGLE_FIRST_BUTTON_TEX_X, TOGGLE_BUTTON_CLICKED_TEX_Y);
        this.toggleSecond.setTextureLocation(WIDGET_TEX_LOCATION);
        this.toggleSecond.setWidth(TOGGLE_BUTTON_WIDTH).setHeight(TOGGLE_BUTTON_HEIGHT);
        this.toggleSecond.setTexture(TOGGLE_SECOND_BUTTON_TEX_X, TOGGLE_BUTTON_TEX_Y);
        this.toggleSecond.setHoveredTexture(TOGGLE_SECOND_BUTTON_TEX_X, TOGGLE_BUTTON_HOVERED_TEX_Y);
        this.toggleSecond.setMouseDownTexture(TOGGLE_SECOND_BUTTON_TEX_X, TOGGLE_BUTTON_HOVERED_TEX_Y);
        this.toggleSecond.setClickedTexture(TOGGLE_SECOND_BUTTON_TEX_X, TOGGLE_BUTTON_CLICKED_TEX_Y);

        AssemblySlotManager slotManager = assemblyInvManager.slotManager;

        SlotDisabled ext_0_0 = new SlotDisabled();
        SlotCPU cpu_0_0 = new SlotCPU(0, slotManager);
        SlotRAM ram_0_0 = new SlotRAM(4, slotManager);
        SlotRAM ram_0_1 = new SlotRAM(5, slotManager);
        SlotRAM ram_0_2 = new SlotRAM(6, slotManager);
        SlotRAM ram_0_3 = new SlotRAM(7, slotManager);
        SlotCPUHeatRadiator cpu_heat_radiator_0_0 = new SlotCPUHeatRadiator(21, slotManager);
        SlotRAMHeatRadiator ram_heat_radiator_0_0 = new SlotRAMHeatRadiator(25, slotManager);
        SlotRAMHeatRadiator ram_heat_radiator_0_1 = new SlotRAMHeatRadiator(26, slotManager);
        SlotRAMHeatRadiator ram_heat_radiator_0_2 = new SlotRAMHeatRadiator(27, slotManager);
        SlotRAMHeatRadiator ram_heat_radiator_0_3 = new SlotRAMHeatRadiator(28, slotManager);

        SlotDisabled ext_1_0 = new SlotDisabled();
        SlotCPU cpu_1_0 = new SlotCPU(1, slotManager);
        SlotRAM ram_1_0 = new SlotRAM(8, slotManager);
        SlotRAM ram_1_1 = new SlotRAM(9, slotManager);
        SlotRAM ram_1_2 = new SlotRAM(10, slotManager);
        SlotRAM ram_1_3 = new SlotRAM(11, slotManager);
        SlotCPUHeatRadiator cpu_heat_radiator_1_0 = new SlotCPUHeatRadiator(22, slotManager);
        SlotRAMHeatRadiator ram_heat_radiator_1_0 = new SlotRAMHeatRadiator(29, slotManager);
        SlotRAMHeatRadiator ram_heat_radiator_1_1 = new SlotRAMHeatRadiator(30, slotManager);
        SlotRAMHeatRadiator ram_heat_radiator_1_2 = new SlotRAMHeatRadiator(31, slotManager);
        SlotRAMHeatRadiator ram_heat_radiator_1_3 = new SlotRAMHeatRadiator(32, slotManager);

        SlotCPUExtension ext_2_0 = new SlotCPUExtension(AssemblyInvCPUConst.CPU_EXTENSION_SLOT_ID, slotManager);
        SlotCPU cpu_2_0 = new SlotCPU(2, slotManager);
        SlotRAM ram_2_0 = new SlotRAM(12, slotManager);
        SlotRAM ram_2_1 = new SlotRAM(13, slotManager);
        SlotRAM ram_2_2 = new SlotRAM(14, slotManager);
        SlotRAM ram_2_3 = new SlotRAM(15, slotManager);
        SlotCPUHeatRadiator cpu_heat_radiator_2_0 = new SlotCPUHeatRadiator(23, slotManager);
        SlotRAMHeatRadiator ram_heat_radiator_2_0 = new SlotRAMHeatRadiator(33, slotManager);
        SlotRAMHeatRadiator ram_heat_radiator_2_1 = new SlotRAMHeatRadiator(34, slotManager);
        SlotRAMHeatRadiator ram_heat_radiator_2_2 = new SlotRAMHeatRadiator(35, slotManager);
        SlotRAMHeatRadiator ram_heat_radiator_2_3 = new SlotRAMHeatRadiator(36, slotManager);

        SlotDisabled ext_3_0 = new SlotDisabled();
        SlotCPU cpu_3_0 = new SlotCPU(3, slotManager);
        SlotRAM ram_3_0 = new SlotRAM(16, slotManager);
        SlotRAM ram_3_1 = new SlotRAM(17, slotManager);
        SlotRAM ram_3_2 = new SlotRAM(18, slotManager);
        SlotRAM ram_3_3 = new SlotRAM(19, slotManager);
        SlotCPUHeatRadiator cpu_heat_radiator_3_0 = new SlotCPUHeatRadiator(24, slotManager);
        SlotRAMHeatRadiator ram_heat_radiator_3_0 = new SlotRAMHeatRadiator(37, slotManager);
        SlotRAMHeatRadiator ram_heat_radiator_3_1 = new SlotRAMHeatRadiator(38, slotManager);
        SlotRAMHeatRadiator ram_heat_radiator_3_2 = new SlotRAMHeatRadiator(39, slotManager);
        SlotRAMHeatRadiator ram_heat_radiator_3_3 = new SlotRAMHeatRadiator(40, slotManager);

        slotColumn.setMarginLeft(4).setMarginUp(7);
        secondSlotColumn.setMarginLeft(4).setMarginUp(7);

        slotColumn.addWidgets(new Row().addWidgets(ext_0_0, cpu_0_0, ram_0_0, ram_0_1, ram_0_2, ram_0_3));
        slotColumn.addWidgets(new Row().addWidgets(ext_1_0, cpu_1_0, ram_1_0, ram_1_1, ram_1_2, ram_1_3));
        slotColumn.addWidgets(new Row().addWidgets(ext_2_0, cpu_2_0, ram_2_0, ram_2_1, ram_2_2, ram_2_3));
        slotColumn.addWidgets(new Row().addWidgets(ext_3_0, cpu_3_0, ram_3_0, ram_3_1, ram_3_2, ram_3_3));
        secondSlotColumn.addWidgets(new Row().addWidgets(cpu_heat_radiator_0_0, ram_heat_radiator_0_0, ram_heat_radiator_0_1, ram_heat_radiator_0_2, ram_heat_radiator_0_3));
        secondSlotColumn.addWidgets(new Row().addWidgets(cpu_heat_radiator_1_0, ram_heat_radiator_1_0, ram_heat_radiator_1_1, ram_heat_radiator_1_2, ram_heat_radiator_1_3));
        secondSlotColumn.addWidgets(new Row().addWidgets(cpu_heat_radiator_2_0, ram_heat_radiator_2_0, ram_heat_radiator_2_1, ram_heat_radiator_2_2, ram_heat_radiator_2_3));
        secondSlotColumn.addWidgets(new Row().addWidgets(cpu_heat_radiator_3_0, ram_heat_radiator_3_0, ram_heat_radiator_3_1, ram_heat_radiator_3_2, ram_heat_radiator_3_3));

        Column cpuOverlayCol = new Column();
        cpuOverlayCol.addWidgets(
                new OverlayRAM(ram_0_0).setMarginDown(1),
                new OverlayRAM(ram_0_1).setMarginDown(4),

                new OverlayCPU(cpu_0_0).setMarginLeft(13).setMarginDown(4),

                new OverlayRAM(ram_0_2),
                new OverlayRAM(ram_0_3).setMarginDown(1),
                new OverlayRAM(ram_1_0),
                new OverlayRAM(ram_1_1).setMarginDown(4),

                new OverlayCPU(cpu_1_0).setMarginLeft(13).setMarginDown(4),

                new OverlayRAM(ram_1_2).setMarginDown(1),
                new OverlayRAM(ram_1_3)
        ).setAbsX(203).setAbsY(28);
        widgetController.addWidgetContainer(cpuOverlayCol);
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
