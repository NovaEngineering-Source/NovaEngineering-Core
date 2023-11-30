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
import github.kasuminova.novaeng.common.container.slot.AssemblySlotManager;
import github.kasuminova.novaeng.common.hypernet.proc.server.assembly.AssemblyInvCalculateCardConst;
import net.minecraft.util.ResourceLocation;

public class AssemblyInvExtension extends AssemblyInv {

    public static final int CLOSED_WIDTH = 27;
    public static final int CLOSED_HEIGHT = 26;

    public static final int OPENED_WIDTH = 103;
    public static final int OPENED_HEIGHT = 86;

    public static final int BUTTON_TEX_X = 55;

    public AssemblyInvExtension(final AssemblyInvManager assemblyInvManager, final WidgetController widgetController) {
        super(assemblyInvManager, widgetController);
        this.width = CLOSED_WIDTH;
        this.height = CLOSED_HEIGHT;

        this.openedBgTexLocation = new ResourceLocation(NovaEngineeringCore.MOD_ID, "textures/gui/msa_extension.png");
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

        SlotExtensionCardExtension ext_0_0 = new SlotExtensionCardExtension(0, AssemblyInvCalculateCardConst.EXTENSION_SLOT_0_ID, slotManager);
        SlotExtensionCard ext_c_0_0 = new SlotExtensionCard(0, 0, slotManager).dependsOn(ext_0_0);
        SlotExtensionCard ext_c_0_1 = new SlotExtensionCard(1, 1, slotManager).dependsOn(ext_0_0);
        SlotExtensionCard ext_c_0_2 = new SlotExtensionCard(2, 2, slotManager).dependsOn(ext_0_0);
        SlotExtensionCard ext_c_0_3 = new SlotExtensionCard(3, 3, slotManager).dependsOn(ext_0_0);

        SlotExtensionCardExtension ext_1_0 = new SlotExtensionCardExtension(1, AssemblyInvCalculateCardConst.EXTENSION_SLOT_1_ID, slotManager);
        SlotExtensionCard ext_c_1_0 = new SlotExtensionCard(4, 4, slotManager).dependsOn(ext_1_0);
        SlotExtensionCard ext_c_1_1 = new SlotExtensionCard(5, 5, slotManager).dependsOn(ext_1_0);
        SlotExtensionCard ext_c_1_2 = new SlotExtensionCard(6, 6, slotManager).dependsOn(ext_1_0);
        SlotExtensionCard ext_c_1_3 = new SlotExtensionCard(7, 7, slotManager).dependsOn(ext_1_0);

        SlotExtensionCardExtension ext_2_0 = new SlotExtensionCardExtension(2, AssemblyInvCalculateCardConst.EXTENSION_SLOT_2_ID, slotManager);
        SlotExtensionCard ext_c_2_0 = new SlotExtensionCard(8, 8, slotManager).dependsOn(ext_2_0);
        SlotExtensionCard ext_c_2_1 = new SlotExtensionCard(9, 9, slotManager).dependsOn(ext_2_0);
        SlotExtensionCard ext_c_2_2 = new SlotExtensionCard(10, 10, slotManager).dependsOn(ext_2_0);
        SlotExtensionCard ext_c_2_3 = new SlotExtensionCard(11, 11, slotManager).dependsOn(ext_2_0);

        SlotExtensionCardExtension ext_3_0 = new SlotExtensionCardExtension(3, AssemblyInvCalculateCardConst.EXTENSION_SLOT_3_ID, slotManager);
        SlotExtensionCard ext_c_3_0 = new SlotExtensionCard(12, 12, slotManager).dependsOn(ext_3_0);
        SlotExtensionCard ext_c_3_1 = new SlotExtensionCard(13, 13, slotManager).dependsOn(ext_3_0);
        SlotExtensionCard ext_c_3_2 = new SlotExtensionCard(14, 14, slotManager).dependsOn(ext_3_0);
        SlotExtensionCard ext_c_3_3 = new SlotExtensionCard(15, 15, slotManager).dependsOn(ext_3_0);

        slotColum.addWidgets(new Row().addWidgets(ext_0_0, ext_c_0_0, ext_c_0_1, ext_c_0_2, ext_c_0_3).setMarginLeft(7).setMarginUp(7));
        slotColum.addWidgets(new Row().addWidgets(ext_1_0, ext_c_1_0, ext_c_1_1, ext_c_1_2, ext_c_1_3).setMarginLeft(7));
        slotColum.addWidgets(new Row().addWidgets(ext_2_0, ext_c_2_0, ext_c_2_1, ext_c_2_2, ext_c_2_3).setMarginLeft(7));
        slotColum.addWidgets(new Row().addWidgets(ext_3_0, ext_c_3_0, ext_c_3_1, ext_c_3_2, ext_c_3_3).setMarginLeft(7));

        Column slotExtensionCardOverlayCol = new Column();
        slotExtensionCardOverlayCol.setAbsX(260).setAbsY(21).addWidgets(
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
        );
        widgetController.addWidgetContainer(slotExtensionCardOverlayCol);
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
