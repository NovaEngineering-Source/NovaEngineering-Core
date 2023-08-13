package github.kasuminova.novaeng.client.gui;

import github.kasuminova.novaeng.NovaEngineeringCore;
import github.kasuminova.novaeng.client.gui.widget.GuiScrollbarThin;
import github.kasuminova.novaeng.common.container.ContainerHyperNetTerminal;
import github.kasuminova.novaeng.common.crafttweaker.util.NovaEngUtils;
import github.kasuminova.novaeng.common.hypernet.ComputationCenterCache;
import github.kasuminova.novaeng.common.hypernet.HyperNetHelper;
import github.kasuminova.novaeng.common.hypernet.HyperNetTerminal;
import github.kasuminova.novaeng.common.hypernet.research.ResearchCognitionData;
import github.kasuminova.novaeng.common.network.PktTerminalGuiData;
import github.kasuminova.novaeng.common.registry.RegistryHyperNet;
import github.kasuminova.novaeng.common.tile.TileHyperNetTerminal;
import hellfirepvp.modularmachinery.client.ClientScheduler;
import hellfirepvp.modularmachinery.client.gui.GuiContainerBase;
import hellfirepvp.modularmachinery.common.crafting.helper.CraftingStatus;
import hellfirepvp.modularmachinery.common.util.IOInventory;
import hellfirepvp.modularmachinery.common.util.MiscUtils;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButtonImage;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.animation.Animation;
import org.lwjgl.input.Mouse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class GuiHyperNetTerminal extends GuiContainerBase<ContainerHyperNetTerminal> {
    private static final ResourceLocation TEXTURES_TERMINAL = new ResourceLocation(
            NovaEngineeringCore.MOD_ID, "textures/gui/guiterminal.png");
    private static final ResourceLocation TEXTURES_TERMINAL_ELEMENTS = new ResourceLocation(
            NovaEngineeringCore.MOD_ID, "textures/gui/guiterminalelement.png");

    private static final int TERMINAL_ELEMENT_WIDTH  = 92;
    private static final int TERMINAL_ELEMENT_HEIGHT = 22;

    private static final float FONT_SCALE = 0.72F;
    private static final int DATA_SCROLL_BAR_LEFT = 100;
    private static final int DATA_SCROLL_BAR_TOP = 30;
    private static final int DATA_SCROLL_BAR_HEIGHT = 176;
    private static final int MAX_PAGE_ELEMENTS = 8;

    private static final int SCREEN_SCROLL_BAR_LEFT = 336;
    private static final int SCREEN_SCROLL_BAR_TOP = 8;
    private static final int SCREEN_SCROLL_BAR_HEIGHT = 118;

    private static final int SCREEN_TEXT_MAX_LINES = 11;
    private static final int SCREEN_TEXT_MAX_WIDTH = 217;

    protected final TileHyperNetTerminal terminal;

    protected GuiTextField searchTextField = null;
    protected GuiScrollbarThin dataScrollbar = null;
    protected GuiScrollbarThin screenScrollbar = null;

    protected GuiButtonImage startResearch = null;

    protected final Set<ResearchCognitionData> unlockedData = new HashSet<>();
    protected final List<ResearchCognitionData> lockedData = new ArrayList<>();
    protected final Object2DoubleOpenHashMap<ResearchCognitionData> researchingData = new Object2DoubleOpenHashMap<>();

    protected final List<ResearchDataContext> renderingData = new ArrayList<>();
    protected ResearchDataContext inScreen = null;

    public GuiHyperNetTerminal(TileHyperNetTerminal terminal, EntityPlayer opening) {
        super(new ContainerHyperNetTerminal(terminal, opening));
        this.terminal = terminal;
        this.xSize = 350;
        this.ySize = 214;
    }

    private static void updateUnlockedData(Set<ResearchCognitionData> unlockedData) {
        unlockedData.clear();
        unlockedData.addAll(PktTerminalGuiData.getUnlockedData());
    }

    private static void updateResearchingData(Object2DoubleOpenHashMap<ResearchCognitionData> researchingData) {
        researchingData.clear();
        researchingData.putAll(PktTerminalGuiData.getResearchingData());
    }

    private void updateLockedData(List<ResearchCognitionData> lockedData) {
        lockedData.clear();
        RegistryHyperNet.getAllResearchCognitionData()
                .stream()
                .filter(data -> !unlockedData.contains(data) && !researchingData.containsKey(data))
                .forEach(lockedData::add);
    }

    public static boolean isMouseOver(final int startX,
                                      final int startY,
                                      final int endX,
                                      final int endY,
                                      final int mouseX,
                                      final int mouseY)
    {
        return mouseX >= startX && mouseX <= endX && mouseY >= startY && mouseY <= endY;
    }

    @Override
    public void initGui() {
        super.initGui();

        this.searchTextField = new GuiTextField(
                0, fontRenderer, 25, 18, 78, 11
        );
        this.searchTextField.setMaxStringLength(18);
        this.searchTextField.setEnableBackgroundDrawing(false);

        this.startResearch = new GuiButtonImage(0,
                316, 107, 16, 16,
                60, 22,
                0, TEXTURES_TERMINAL_ELEMENTS
        );

        this.dataScrollbar = new GuiScrollbarThin();
        this.screenScrollbar = new GuiScrollbarThin();

        updateUnlockedData(unlockedData);
        updateResearchingData(researchingData);
        updateLockedData(lockedData);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        drawTitle();
        updateSearchTextField();

        GlStateManager.pushMatrix();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.scale(FONT_SCALE, FONT_SCALE, FONT_SCALE);

        drawNetworkAndControllerStatus();
        drawCardStatus();

        GlStateManager.scale(1F, 1F, 1F);
        GlStateManager.popMatrix();

        updateAndDrawScrollbar();

        updateRenderingData();
        renderDataList(mouseX, mouseY);

        drawScreen(mouseX, mouseY);
    }

    protected void drawScreen(int mouseX, int mouseY) {
        GlStateManager.pushMatrix();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.scale(1F, 1F, 1F);

        if (inScreen == null) {
            fontRenderer.drawStringWithShadow(I18n.format("gui.terminal_controller.screen.none"), 115, 10, 0xFFFFFF);
            screenScrollbar.setRange(0, 0, 1);
        } else {
            drawDataInfo();
        }

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.popMatrix();

        startResearch.drawButton(mc, mouseX, mouseY, Animation.getPartialTickTime());
    }

    protected void drawDataInfo() {
        ResearchCognitionData data = inScreen.getData();

        fontRenderer.drawStringWithShadow(data.getTranslatedName(), 115, 10, 0xFFFFFF);

        GlStateManager.scale(FONT_SCALE, FONT_SCALE, FONT_SCALE);

        float descDrawOffsetX = 115 / FONT_SCALE;
        float descDrawOffsetY = 24 / FONT_SCALE;

        List<String> lines;
        if (inScreen.isLocked()) {
            lines = data.getDescriptions()
                    .stream()
                    .flatMap(desc -> fontRenderer.listFormattedStringToWidth(desc, (int) (SCREEN_TEXT_MAX_WIDTH / FONT_SCALE)).stream())
                    .collect(Collectors.toList());
        } else {
            lines = data.getUnlockedDescriptions()
                    .stream()
                    .flatMap(desc -> fontRenderer.listFormattedStringToWidth(desc, (int) (SCREEN_TEXT_MAX_WIDTH / FONT_SCALE)).stream())
                    .collect(Collectors.toList());
        }

        int currentIndex = 0;
        for (int i = screenScrollbar.getCurrentScroll(); i < lines.size(); i++) {
            String str = lines.get(i);
            fontRenderer.drawStringWithShadow(str, descDrawOffsetX, descDrawOffsetY, 0xFFFFFF);
            descDrawOffsetY += 10;

            currentIndex++;
            if (currentIndex >= SCREEN_TEXT_MAX_LINES) {
                break;
            }
        }

        float infoDrawOffsetY = 108 / FONT_SCALE;

        String dependencies;
        if (data.getDependencies().isEmpty()) {
            dependencies = I18n.format("gui.terminal_controller.screen.info.dependencies.none");
        } else {
            dependencies = data.getDependencies()
                   .stream()
                   .map(ResearchCognitionData::getTranslatedName)
                   .collect(Collectors.joining(", "));
        }
        fontRenderer.drawStringWithShadow(
                I18n.format("gui.terminal_controller.screen.info.dependencies") + dependencies,
                descDrawOffsetX, infoDrawOffsetY, 0xFFFFFF
        );
        fontRenderer.drawStringWithShadow(
                I18n.format("gui.terminal_controller.screen.info.tech_level") + data.getTechLevel(),
                descDrawOffsetX, infoDrawOffsetY + 12, 0xFFFFFF
        );
        fontRenderer.drawStringWithShadow(
                I18n.format("gui.terminal_controller.screen.info.required_points") +
                        MiscUtils.formatNumber((long) data.getRequiredPoints()),
                descDrawOffsetX + 90, infoDrawOffsetY + 12, 0xFFFFFF
        );
        fontRenderer.drawStringWithShadow(
                I18n.format("gui.terminal_controller.screen.info.min_computation_point_per_tick") +
                        NovaEngUtils.formatFLOPS(data.getMinComputationPointPerTick()),
                descDrawOffsetX + 180, infoDrawOffsetY + 12, 0xFFFFFF
        );

        screenScrollbar.setRange(0, Math.max(0, lines.size() - SCREEN_TEXT_MAX_LINES), 1);

        GlStateManager.scale(1F, 1F, 1F);
    }

    protected void drawTitle() {
        GlStateManager.pushMatrix();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        FontRenderer fr = this.fontRenderer;
        fr.drawString(I18n.format("tile.novaeng_core.hypernet_terminal_controller.name"), 8, 5, 0x404040);

        GlStateManager.popMatrix();
    }

    protected void drawNetworkAndControllerStatus() {
        final float statusRenderX = 113 / FONT_SCALE;
        float statusRenderY = 133 / FONT_SCALE;

        if (!terminal.isStructureFormed()) {
            fontRenderer.drawStringWithShadow(I18n.format("gui.terminal_controller.status") +
                    I18n.format("gui.terminal_controller.status.missing_structure"),
                    statusRenderX, statusRenderY, 0xFFFFFF);
            return;
        }

        CraftingStatus status = terminal.getControllerStatus();
        if (!status.isCrafting()) {
            fontRenderer.drawStringWithShadow(I18n.format("gui.terminal_controller.status") +
                    I18n.format(status.getUnlocMessage()),
                    statusRenderX, statusRenderY, 0xFFFFFF);
            return;
        }

        HyperNetTerminal nodeProxy = terminal.getNodeProxy();
        if (!nodeProxy.isConnected()) {
            fontRenderer.drawStringWithShadow(I18n.format("gui.terminal_controller.status") +
                            I18n.format("gui.terminal_controller.status.offline"),
                    statusRenderX, statusRenderY, 0xFFFFFF);
            return;
        }

        fontRenderer.drawStringWithShadow(I18n.format("gui.terminal_controller.status") +
                        I18n.format("gui.terminal_controller.status.online"),
                statusRenderX, statusRenderY, 0xFFFFFF);
        statusRenderY += 10;

        fontRenderer.drawStringWithShadow(
                I18n.format("gui.terminal_controller.status.network.computation_point_consumption"),
                statusRenderX, statusRenderY, 0xFFFFFF);
        statusRenderY += 10;

        fontRenderer.drawStringWithShadow(
                I18n.format("gui.terminal_controller.status.network.computation_point_consumption.info",
                        NovaEngUtils.formatFLOPS(ComputationCenterCache.getComputationPointConsumption()),
                                NovaEngUtils.formatFLOPS(ComputationCenterCache.getComputationPointGeneration())),
                statusRenderX, statusRenderY, 0xFFFFFF);
        statusRenderY += 10;

        fontRenderer.drawStringWithShadow(
                I18n.format("gui.terminal_controller.status.network.total_connected") +
                ComputationCenterCache.getTotalConnected(),
                statusRenderX, statusRenderY, 0xFFFFFF);
    }

    protected void drawCardStatus() {
        final float statusRenderX = 113 / FONT_SCALE;
        float statusRenderY = 195 / FONT_SCALE;

        IOInventory cardInventory = terminal.getCardInventory();
        ItemStack stack = cardInventory.getStackInSlot(0);

        if (stack == ItemStack.EMPTY) {
            fontRenderer.drawStringWithShadow(I18n.format("gui.terminal_controller.connect_card.empty"),
                    statusRenderX, statusRenderY, 0xFFFFFF);
        } else if (RegistryHyperNet.getHyperNetConnectCard() == stack.getItem() && HyperNetHelper.readConnectCardInfo(terminal, stack) != null) {
            fontRenderer.drawStringWithShadow(I18n.format("gui.terminal_controller.connect_card.validate"),
                    statusRenderX, statusRenderY, 0xFFFFFF);
        } else {
            fontRenderer.drawStringWithShadow(I18n.format("gui.terminal_controller.connect_card.invalidate"),
                    statusRenderX, statusRenderY, 0xFFFFFF);
        }
    }

    protected void updateSearchTextField() {
        searchTextField.drawTextBox();
    }

    protected void updateRenderingData() {
        renderingData.clear();

        final int currentScroll = dataScrollbar.getCurrentScroll();

        researchingData.entrySet().stream().skip(currentScroll).limit(MAX_PAGE_ELEMENTS).forEach(entry ->
                renderingData.add(new ResearchDataContext(entry.getKey(), true, entry.getValue())));

        if (renderingData.size() >= MAX_PAGE_ELEMENTS) {
            return;
        }

        unlockedData.stream().limit(MAX_PAGE_ELEMENTS - renderingData.size()).forEach(data ->
                renderingData.add(new ResearchDataContext(data, false, -1D)));

        if (renderingData.size() >= MAX_PAGE_ELEMENTS) {
            return;
        }

        lockedData.stream().limit(MAX_PAGE_ELEMENTS - renderingData.size()).forEach(data ->
                renderingData.add(new ResearchDataContext(data, true, -1D)));
    }

    protected void renderDataList(int mouseX, int mouseY) {
        int offsetY = 30;
        for (final ResearchDataContext dataContext : renderingData) {
            GlStateManager.pushMatrix();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

            drawResearchData(dataContext, offsetY, mouseX, mouseY);
            offsetY += TERMINAL_ELEMENT_HEIGHT;

            GlStateManager.popMatrix();
        }
    }

    protected void drawResearchData(final ResearchDataContext dataContext, final int offsetY, int mouseX, int mouseY) {
        ResearchCognitionData data = dataContext.getData();

        ItemStack previewStack = data.getPreviewStack();
        renderItemStackToGUI(mc, mc.getRenderItem(), 11, offsetY + 3, previewStack);

        if (dataContext.isLocked()) {
            GlStateManager.color(1.0F, 0.6F, 0.6F, 1.0F);
        }
        if (isMouseOver(8, offsetY,
                8 + TERMINAL_ELEMENT_WIDTH - 1, offsetY + TERMINAL_ELEMENT_HEIGHT - 1,
                mouseX - guiLeft, mouseY - guiTop))
        {
            GlStateManager.color(0.7F, 0.9F, 1.0F, 1.0F);
        }
        if (dataContext.equals(inScreen)) {
            GlStateManager.color(0.4F, 0.6F, 1.0F, 1.0F);
        }

        this.mc.getTextureManager().bindTexture(TEXTURES_TERMINAL_ELEMENTS);
        drawTexturedModalRect(8, offsetY, 0, 0, TERMINAL_ELEMENT_WIDTH, TERMINAL_ELEMENT_HEIGHT);

        Double researchProgress = dataContext.getProgress();
        if (researchProgress >= 0) {
            GlStateManager.color(0.6F, 1.0F, 0.6F, 1.0F);
            double progress = data.getRequiredPoints() / researchProgress;
            drawTexturedModalRect(8, offsetY, 0, 0, (int) (TERMINAL_ELEMENT_WIDTH * progress), TERMINAL_ELEMENT_HEIGHT);
        }

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.scale(FONT_SCALE, FONT_SCALE, FONT_SCALE);

        float textRenderOffsetX = 29 / FONT_SCALE;
        float textRenderOffsetY = (34 + (offsetY - 30)) / FONT_SCALE;
        fontRenderer.drawString(data.getTranslatedName(), (int) textRenderOffsetX, (int) textRenderOffsetY, 0x404040);

        if (dataContext.isLocked()) {
            if (researchProgress >= 0) {
                fontRenderer.drawString(
                        I18n.format("gui.terminal_controller.data.locked") +
                        I18n.format("gui.terminal_controller.data.progress",
                                NovaEngUtils.formatPercent(data.getRequiredPoints(), researchProgress)),
                        (int) textRenderOffsetX, (int) textRenderOffsetY + 12, 0x404040);
            } else {
                fontRenderer.drawString(I18n.format("gui.terminal_controller.data.locked"),
                        (int) textRenderOffsetX, (int) textRenderOffsetY + 12, 0x404040);
            }
        } else {
            fontRenderer.drawString(I18n.format("gui.terminal_controller.data.unlocked"),
                    (int) textRenderOffsetX, (int) textRenderOffsetY + 12, 0x404040);
        }

        GlStateManager.scale(1F, 1F, 1F);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }

    public static void renderItemStackToGUI(final Minecraft mc,
                                            final RenderItem ri,
                                            final int x,
                                            final int y,
                                            final ItemStack stack) {
        ri.renderItemAndEffectIntoGUI(stack, x, y);
        ri.renderItemOverlays(mc.fontRenderer, stack, x, y);
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        searchTextField.updateCursorCounter();

        if (ClientScheduler.getClientTick() % 20 == 0) {
            updateUnlockedData(unlockedData);
            updateResearchingData(researchingData);
            updateLockedData(lockedData);
        }
    }

    @Override
    protected void mouseClicked(final int mouseX, final int mouseY, final int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        if (mouseButton != 0) {
            return;
        }

        int x = mouseX - guiLeft;
        int y = mouseY - guiTop;

        if (searchTextField.mouseClicked(x, y, mouseButton)) {
            return;
        }

        dataScrollbar.click(x, y);
        screenScrollbar.click(x, y);

        int offsetY = 30;
        for (final ResearchDataContext data : renderingData) {
            if (isMouseOver(
                    8, 30,
                    8 + TERMINAL_ELEMENT_WIDTH, offsetY + TERMINAL_ELEMENT_HEIGHT,
                    mouseX - guiLeft, mouseY - guiTop))
            {
                inScreen = data;
                break;
            }

            offsetY += TERMINAL_ELEMENT_HEIGHT;
        }
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);

        int x = mouseX - guiLeft;
        int y = mouseY - guiTop;

        dataScrollbar.click(x, y);
        screenScrollbar.click(x, y);
    }

    @Override
    protected void keyTyped(final char c, final int i) throws IOException {
        if (!searchTextField.isFocused()) {
            super.keyTyped(c, i);
        }

        searchTextField.textboxKeyTyped(c, i);
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();

        final int i = Mouse.getEventDWheel();
        if (i != 0) {
            int mouseX = Mouse.getEventX() * this.width / this.mc.displayWidth;
            int mouseY = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;

            if (isMouseOver(7, 29, 106, 206, mouseX - guiLeft, mouseY - guiTop)) {
                dataScrollbar.wheel(i);
            }
            if (isMouseOver(111, 7, 342, 126, mouseX - guiLeft, mouseY - guiTop)) {
                screenScrollbar.wheel(i);
            }
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(final float partialTicks, final int mouseX, final int mouseY) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(TEXTURES_TERMINAL);
        final int x = (this.width - this.xSize) / 2;
        final int y = (this.height - this.ySize) / 2;
        Gui.drawModalRectWithCustomSizedTexture(x, y, 0, 0, this.xSize, this.ySize, this.xSize, this.ySize);
    }

    protected void updateAndDrawScrollbar() {
        dataScrollbar.setLeft(DATA_SCROLL_BAR_LEFT)
                .setTop(DATA_SCROLL_BAR_TOP)
                .setHeight(DATA_SCROLL_BAR_HEIGHT)
                .setRange(0, Math.max(0, unlockedData.size() + lockedData.size() - MAX_PAGE_ELEMENTS), 1);
        screenScrollbar.setLeft(SCREEN_SCROLL_BAR_LEFT)
                .setTop(SCREEN_SCROLL_BAR_TOP)
                .setHeight(SCREEN_SCROLL_BAR_HEIGHT);
        dataScrollbar.draw(this, mc);
        screenScrollbar.draw(this, mc);
    }

    @Override
    protected void setWidthHeight() {
    }

    public static class ResearchDataContext {
        public final ResearchCognitionData data;
        public final boolean locked;
        public final Double progress;

        public ResearchDataContext(final ResearchCognitionData data,
                                   final boolean locked,
                                   final Double progress)
        {
            this.data = data;
            this.locked = locked;
            this.progress = progress;
        }

        public ResearchCognitionData getData() {
            return data;
        }

        public boolean isLocked() {
            return locked;
        }

        public Double getProgress() {
            return progress;
        }

        @Override
        public int hashCode() {
            return data.hashCode();
        }

        @Override
        public boolean equals(final Object obj) {
            if (!(obj instanceof ResearchDataContext)) {
                return false;
            }
            final ResearchDataContext other = (ResearchDataContext) obj;
            return data.equals(other.data);
        }
    }
}
