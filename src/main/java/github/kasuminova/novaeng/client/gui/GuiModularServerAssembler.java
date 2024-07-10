package github.kasuminova.novaeng.client.gui;

import github.kasuminova.mmce.client.gui.GuiContainerDynamic;
import github.kasuminova.mmce.client.gui.util.MousePos;
import github.kasuminova.mmce.client.gui.widget.base.WidgetController;
import github.kasuminova.mmce.client.gui.widget.base.WidgetGui;
import github.kasuminova.novaeng.NovaEngineeringCore;
import github.kasuminova.novaeng.client.gui.widget.msa.*;
import github.kasuminova.novaeng.client.gui.widget.msa.event.AssemblerInvUpdateEvent;
import github.kasuminova.novaeng.common.container.ContainerModularServerAssembler;
import github.kasuminova.novaeng.common.tile.TileModularServerAssembler;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

public class GuiModularServerAssembler extends GuiContainerDynamic<ContainerModularServerAssembler> {
    public static final ResourceLocation TEXTURES_BACKGROUND = new ResourceLocation(
            NovaEngineeringCore.MOD_ID, "textures/gui/modular_server_assembler.png");

    public static final ResourceLocation TEXTURES_ELEMENTS = new ResourceLocation(
            NovaEngineeringCore.MOD_ID, "textures/gui/msa_elements.png");

    public static final int MAIN_GUI_WIDTH = 329;
    public static final int MAIN_GUI_HEIGHT = 206;

    protected final TileModularServerAssembler assembler;
    protected final AssemblyInvManager assemblyInvManager = new AssemblyInvManager(container.getSlotManager());

    public GuiModularServerAssembler(final TileModularServerAssembler assembler, final EntityPlayer opening) {
        super(new ContainerModularServerAssembler(assembler, opening));
        this.assembler = assembler;
        this.xSize = MAIN_GUI_WIDTH + 138;
        this.ySize = 206;
        this.widgetController = new WidgetController(WidgetGui.of(this));

        this.assemblyInvManager.addInv(new AssemblyInvCPU(assemblyInvManager, widgetController));
        this.assemblyInvManager.addInv(new AssemblyInvCalculateCard(assemblyInvManager, widgetController));
        this.assemblyInvManager.addInv(new AssemblyInvExtension(assemblyInvManager, widgetController));
        this.assemblyInvManager.addInv(new AssemblyInvPower(assemblyInvManager, widgetController));
        this.assemblyInvManager.setAbsX(MAIN_GUI_WIDTH);
        this.widgetController.addWidgetContainer(assemblyInvManager);

        ServerInfoColumn serverInfoColumn = new ServerInfoColumn(assembler.getServer());
        serverInfoColumn.setAbsX(7).setAbsY(7).setWidth(121).setHeight(192);

//        for (int i = 0; i < 10; i++) {
//            List<String> list = new ArrayList<>();
//            for (int i1 = 0; i1 < 25; i1++) {
//                StringJoiner joiner = new StringJoiner("", "测试_test_", "");
//                for (int i2 = 0; i2 < i1; i2++) {
//                    String s = String.valueOf(i2);
//                    joiner.add(s);
//                }
//                list.add(joiner.toString());
//            }
//
//            MultiLineLabel multiLineLabel = new MultiLineLabel(list);
//            multiLineLabel.setWidth(111);
//            multiLineLabel.setScale(0.75F);
//            tipColumn.addWidget(multiLineLabel);
//            tipColumn.addWidget(new HorizontalSeparator().setWidth(109).setHeight(i + 1).setMarginLeft(2));
//        }

        this.widgetController.addWidgetContainer(serverInfoColumn);

//        Column column = new Column();
//        column.addWidget(new DragBar(new DataReference<>(0D), new DataReference<>(0D), new DataReference<>(5D)));
//        column.setAbsY(-20);
//        this.widgetController.addWidgetContainer(column);
    }

    public void onServerInventoryUpdate() {
        this.widgetController.postGuiEvent(new AssemblerInvUpdateEvent(widgetController.getGui(), assembler.getServer()));
    }

    public TileModularServerAssembler getAssembler() {
        return assembler;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(final float partialTicks, final int mouseX, final int mouseY) {
        GlStateManager.color(1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(TEXTURES_BACKGROUND);
        final int x = (this.width - this.xSize) / 2;
        final int y = (this.height - this.ySize) / 2;
        Gui.drawModalRectWithCustomSizedTexture(x, y, 0, 0, MAIN_GUI_WIDTH, MAIN_GUI_HEIGHT, 512, 512);
        super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(final int mouseX, final int mouseY) {
        widgetController.postRender(new MousePos(mouseX, mouseY), false);
    }

}