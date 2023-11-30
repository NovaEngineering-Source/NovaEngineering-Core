package github.kasuminova.novaeng.client.gui.widget.msa;

import github.kasuminova.mmce.client.gui.widget.base.WidgetController;
import github.kasuminova.mmce.client.gui.widget.container.Row;
import github.kasuminova.novaeng.NovaEngineeringCore;
import github.kasuminova.novaeng.client.gui.GuiModularServerAssembler;
import github.kasuminova.novaeng.client.gui.widget.msa.slot.SlotCapacitor;
import github.kasuminova.novaeng.client.gui.widget.msa.slot.SlotPSU;
import github.kasuminova.novaeng.common.container.slot.AssemblySlotManager;
import net.minecraft.util.ResourceLocation;

public class AssemblyInvPower extends AssemblyInv {

    public static final int CLOSED_WIDTH = 27;
    public static final int CLOSED_HEIGHT = 26;

    public static final int OPENED_WIDTH = 86;
    public static final int OPENED_HEIGHT = 50;

    public static final int BUTTON_TEX_X = 73;

    public AssemblyInvPower(final AssemblyInvManager assemblyInvManager, final WidgetController widgetController) {
        super(assemblyInvManager, widgetController);
        this.width = CLOSED_WIDTH;
        this.height = CLOSED_HEIGHT;

        this.openedBgTexLocation = new ResourceLocation(NovaEngineeringCore.MOD_ID, "textures/gui/msa_power.png");
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

        SlotPSU slot_0_0 = new SlotPSU(0, 0, slotManager);
        SlotPSU slot_0_1 = new SlotPSU(1, 1, slotManager);
        SlotPSU slot_0_2 = new SlotPSU(2, 2, slotManager);
        SlotPSU slot_0_3 = new SlotPSU(3, 3, slotManager);

        SlotCapacitor slot_1_0 = new SlotCapacitor(0, 4, slotManager);
        SlotCapacitor slot_1_1 = new SlotCapacitor(1, 5, slotManager);
        SlotCapacitor slot_1_2 = new SlotCapacitor(2, 6, slotManager);
        SlotCapacitor slot_1_3 = new SlotCapacitor(3, 7, slotManager);

        slotColum.addWidgets(new Row().addWidgets(slot_0_0, slot_0_1, slot_0_2, slot_0_3).setMarginLeft(7).setMarginUp(7));
        slotColum.addWidgets(new Row().addWidgets(slot_1_0, slot_1_1, slot_1_2, slot_1_3).setMarginLeft(7));
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
