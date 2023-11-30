package github.kasuminova.novaeng.client.gui.widget.msa;

import github.kasuminova.mmce.client.gui.widget.base.WidgetController;
import github.kasuminova.mmce.client.gui.widget.container.Column;
import github.kasuminova.mmce.client.gui.widget.container.Row;
import github.kasuminova.novaeng.NovaEngineeringCore;
import github.kasuminova.novaeng.client.gui.GuiModularServerAssembler;
import github.kasuminova.novaeng.client.gui.widget.msa.overlay.OverlayCPU;
import github.kasuminova.novaeng.client.gui.widget.msa.overlay.OverlayRAM;
import github.kasuminova.novaeng.client.gui.widget.msa.slot.SlotCPU;
import github.kasuminova.novaeng.client.gui.widget.msa.slot.SlotCPUExtension;
import github.kasuminova.novaeng.client.gui.widget.msa.slot.SlotDisabled;
import github.kasuminova.novaeng.client.gui.widget.msa.slot.SlotRAM;
import github.kasuminova.novaeng.common.container.slot.AssemblySlotManager;
import github.kasuminova.novaeng.common.hypernet.proc.server.assembly.AssemblyInvCPUConst;
import net.minecraft.util.ResourceLocation;

public class AssemblyInvCPU extends AssemblyInv {

    public static final int CLOSED_WIDTH = 27;
    public static final int CLOSED_HEIGHT = 26;

    public static final int OPENED_WIDTH = 122;
    public static final int OPENED_HEIGHT = 86;

    public static final int BUTTON_TEX_X = 1;

    public AssemblyInvCPU(final AssemblyInvManager assemblyInvManager, final WidgetController widgetController) {
        super(assemblyInvManager, widgetController);
        this.width = CLOSED_WIDTH;
        this.height = CLOSED_HEIGHT;

        this.openedBgTexLocation = new ResourceLocation(NovaEngineeringCore.MOD_ID, "textures/gui/msa_cpu.png");
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
        this.open.setHoveredTextureXY(BUTTON_TEX_X, 219);

        AssemblySlotManager slotManager = assemblyInvManager.slotManager;

        SlotDisabled ext_0_0 = new SlotDisabled();
        SlotCPU cpu_0_0 = new SlotCPU(0, 0, slotManager);
        SlotRAM ram_0_0 = new SlotRAM(0, 4, slotManager);
        SlotRAM ram_0_1 = new SlotRAM(1, 5, slotManager);
        SlotRAM ram_0_2 = new SlotRAM(2, 6, slotManager);
        SlotRAM ram_0_3 = new SlotRAM(3, 7, slotManager);

        SlotDisabled ext_1_0 = new SlotDisabled();
        SlotCPU cpu_1_0 = new SlotCPU(1, 1, slotManager);
        SlotRAM ram_1_0 = new SlotRAM(4, 8, slotManager);
        SlotRAM ram_1_1 = new SlotRAM(5, 9, slotManager);
        SlotRAM ram_1_2 = new SlotRAM(6, 10, slotManager);
        SlotRAM ram_1_3 = new SlotRAM(7, 11, slotManager);

        SlotCPUExtension ext_2_0 = new SlotCPUExtension(AssemblyInvCPUConst.CPU_EXTENSION_SLOT_ID, slotManager);
        SlotCPU cpu_2_0 = new SlotCPU(2, 2, slotManager).dependsOn(ext_2_0);
        SlotRAM ram_2_0 = new SlotRAM(8, 12, slotManager).dependsOn(ext_2_0);
        SlotRAM ram_2_1 = new SlotRAM(9, 13, slotManager).dependsOn(ext_2_0);
        SlotRAM ram_2_2 = new SlotRAM(10, 14, slotManager).dependsOn(ext_2_0);
        SlotRAM ram_2_3 = new SlotRAM(11, 15, slotManager).dependsOn(ext_2_0);

        SlotDisabled ext_3_0 = new SlotDisabled();
        SlotCPU cpu_3_0 = new SlotCPU(3, 3, slotManager).dependsOn(ext_2_0);
        SlotRAM ram_3_0 = new SlotRAM(12, 16, slotManager).dependsOn(ext_2_0);
        SlotRAM ram_3_1 = new SlotRAM(13, 17, slotManager).dependsOn(ext_2_0);
        SlotRAM ram_3_2 = new SlotRAM(14, 18, slotManager).dependsOn(ext_2_0);
        SlotRAM ram_3_3 = new SlotRAM(15, 19, slotManager).dependsOn(ext_2_0);

        slotColum.addWidgets(new Row().addWidgets(ext_0_0, cpu_0_0, ram_0_0, ram_0_1, ram_0_2, ram_0_3).setMarginLeft(7).setMarginUp(7));
        slotColum.addWidgets(new Row().addWidgets(ext_1_0, cpu_1_0, ram_1_0, ram_1_1, ram_1_2, ram_1_3).setMarginLeft(7));
        slotColum.addWidgets(new Row().addWidgets(ext_2_0, cpu_2_0, ram_2_0, ram_2_1, ram_2_2, ram_2_3).setMarginLeft(7));
        slotColum.addWidgets(new Row().addWidgets(ext_3_0, cpu_3_0, ram_3_0, ram_3_1, ram_3_2, ram_3_3).setMarginLeft(7));

        Column cpuOverlayCol = new Column();
        cpuOverlayCol.setAbsX(203).setAbsY(28).addWidgets(
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
        );
        widgetController.addWidgetContainer(cpuOverlayCol);
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
