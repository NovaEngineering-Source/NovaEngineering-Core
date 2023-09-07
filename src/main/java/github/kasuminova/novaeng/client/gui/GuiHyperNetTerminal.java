package github.kasuminova.novaeng.client.gui;

import github.kasuminova.novaeng.NovaEngineeringCore;
import github.kasuminova.novaeng.client.gui.misc.TechLevelText;
import github.kasuminova.novaeng.client.gui.widget.GuiScrollbarThin;
import github.kasuminova.novaeng.common.container.ContainerHyperNetTerminal;
import github.kasuminova.novaeng.common.crafttweaker.hypernet.HyperNetHelper;
import github.kasuminova.novaeng.common.crafttweaker.util.NovaEngUtils;
import github.kasuminova.novaeng.common.hypernet.ComputationCenterCache;
import github.kasuminova.novaeng.common.hypernet.Database;
import github.kasuminova.novaeng.common.hypernet.HyperNetTerminal;
import github.kasuminova.novaeng.common.hypernet.research.ResearchCognitionData;
import github.kasuminova.novaeng.common.hypernet.research.ResearchStationType;
import github.kasuminova.novaeng.common.network.PktResearchTaskProvide;
import github.kasuminova.novaeng.common.network.PktResearchTaskReset;
import github.kasuminova.novaeng.common.network.PktTerminalGuiData;
import github.kasuminova.novaeng.common.registry.RegistryHyperNet;
import github.kasuminova.novaeng.common.tile.TileHyperNetTerminal;
import github.kasuminova.novaeng.common.util.StringUtils;
import github.kasuminova.novaeng.common.util.TimeUtils;
import hellfirepvp.modularmachinery.client.ClientScheduler;
import hellfirepvp.modularmachinery.client.gui.GuiContainerBase;
import hellfirepvp.modularmachinery.common.crafting.helper.CraftingStatus;
import hellfirepvp.modularmachinery.common.util.IOInventory;
import hellfirepvp.modularmachinery.common.util.MiscUtils;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButtonImage;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.model.animation.Animation;
import org.lwjgl.input.Mouse;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class GuiHyperNetTerminal extends GuiContainerBase<ContainerHyperNetTerminal> {
    private static final ResourceLocation TEXTURES_TERMINAL = new ResourceLocation(
            NovaEngineeringCore.MOD_ID, "textures/gui/guiterminal.png");
    private static final ResourceLocation TEXTURES_TERMINAL_ELEMENTS = new ResourceLocation(
            NovaEngineeringCore.MOD_ID, "textures/gui/guiterminalelement.png");

    private static final int TERMINAL_ELEMENT_WIDTH = 92;
    private static final int TERMINAL_ELEMENT_HEIGHT = 22;

    private static final float FONT_SCALE = 0.72F;
    private static final int DATA_SCROLL_BAR_LEFT = 100;
    private static final int DATA_SCROLL_BAR_TOP = 44;
    private static final int DATA_SCROLL_BAR_HEIGHT = 198;
    private static final int MAX_PAGE_ELEMENTS = 9;

    private static final int SCREEN_SCROLL_BAR_LEFT = 336;
    private static final int SCREEN_SCROLL_BAR_TOP = 44;
    private static final int SCREEN_SCROLL_BAR_HEIGHT = 118;

    private static final int SCREEN_TEXT_MAX_LINES = 11;
    private static final int SCREEN_TEXT_MAX_WIDTH = 217;

    private static String searchTextCache = "";
    private static ResearchDataContext currentCache = null;

    protected final TileHyperNetTerminal terminal;
    protected final Set<ResearchCognitionData> unlockedData = new HashSet<>();
    protected final List<ResearchCognitionData> lockedData = new ArrayList<>();
    protected final List<ResearchCognitionData> unavailableData = new ArrayList<>();
    protected final Object2DoubleOpenHashMap<ResearchCognitionData> researchingData = new Object2DoubleOpenHashMap<>();
    protected final List<ResearchDataContext> renderingData = new ArrayList<>();

    protected boolean darkMode = true;

    protected GuiTextField searchTextField = null;
    protected GuiScrollbarThin dataScrollbar = null;
    protected GuiScrollbarThin screenScrollbar = null;
    protected GuiButtonImage startResearch = null;

    protected ResearchDataContext current = null;

    public GuiHyperNetTerminal(TileHyperNetTerminal terminal, EntityPlayer opening) {
        super(new ContainerHyperNetTerminal(terminal, opening));
        this.terminal = terminal;
        this.xSize = 350;
        this.ySize = 250;
    }

    private static void updateUnlockedData(Set<ResearchCognitionData> unlockedData) {
        unlockedData.clear();
        unlockedData.addAll(PktTerminalGuiData.getUnlockedData());
    }

    private static void updateResearchingData(Object2DoubleOpenHashMap<ResearchCognitionData> researchingData) {
        researchingData.clear();
        researchingData.putAll(PktTerminalGuiData.getResearchingData());
    }

    public static boolean isMouseOver(final int startX, final int startY,
                                      final int endX, final int endY,
                                      final int mouseX, final int mouseY)
    {
        return mouseX >= startX && mouseX <= endX && mouseY >= startY && mouseY <= endY;
    }

    public static boolean hasDatabaseSpace(final List<Database.Status> databases) {
        return databases.stream().anyMatch(Database.Status::hasDatabaseSpace);
    }

    public static void renderItemStackToGUI(final Minecraft mc,
                                            final RenderItem ri,
                                            final int x,
                                            final int y,
                                            final ItemStack stack) {
        RenderHelper.enableGUIStandardItemLighting();
        ri.renderItemAndEffectIntoGUI(stack, x, y);
        ri.renderItemOverlays(mc.fontRenderer, stack, x, y);
        RenderHelper.disableStandardItemLighting();
    }

    protected static void drawTechLevelTitleText(final ResearchCognitionData data, final FontRenderer fr) {
        TechLevelText techLevelText = new TechLevelText(data.getTechLevel());
        String levelText = I18n.format("gui.terminal_controller.screen.info.tech_level") + techLevelText.getLevelText();
        String subLevelText = techLevelText.getSubLevelText();
        float levelTextWidth = fr.getStringWidth(levelText);
        float subLevelTextWidth = fr.getStringWidth(subLevelText);
        fr.drawStringWithShadow(levelText,
                330F - (levelTextWidth + (subLevelTextWidth * FONT_SCALE)), 46, 0xFFFFFF);

        GlStateManager.scale(FONT_SCALE, FONT_SCALE, FONT_SCALE);
        fr.drawStringWithShadow(subLevelText,
                (331F - subLevelTextWidth) / FONT_SCALE, 48 / FONT_SCALE, 0xFFFFFF);
    }

    @Override
    public void initGui() {
        super.initGui();

        this.searchTextField = new GuiTextField(
                0, fontRenderer, 25, 32, 78, 11
        );
        this.searchTextField.setMaxStringLength(18);
        this.searchTextField.setEnableBackgroundDrawing(false);
        this.searchTextField.setText(searchTextCache);

        this.startResearch = new GuiButtonImage(0,
                316, 143, 16, 16,
                60, 22,
                0, TEXTURES_TERMINAL_ELEMENTS
        );

        this.dataScrollbar = new GuiScrollbarThin();
        this.screenScrollbar = new GuiScrollbarThin();
        this.current = currentCache;

        updateUnlockedData(unlockedData);
        updateResearchingData(researchingData);
        updateFilteredData();
        updateRenderingData();
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        int x = mouseX - guiLeft;
        int y = mouseY - guiTop;

        try {
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

            renderDataList(x, y);

            drawScreen(mouseX, mouseY);
        } catch (Exception e) {
            NovaEngineeringCore.log.warn(e);
        }
    }

    protected void drawScreen(int mouseX, int mouseY) {
        GlStateManager.pushMatrix();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.scale(1F, 1F, 1F);

        if (current == null) {
            fontRenderer.drawStringWithShadow(I18n.format("gui.terminal_controller.screen.none"), 115, 46, 0xFFFFFF);
            screenScrollbar.setRange(0, 0, 1);
        } else {
            drawDataInfo();
        }

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.popMatrix();

//        startResearch.drawButton(mc, mouseX, mouseY, Animation.getPartialTickTime());
        if (current != null) {
            startResearch.drawButton(mc, mouseX, mouseY, Animation.getPartialTickTime());
            drawButtonOverlayAndHoveringText(mouseX, mouseY);
        }
    }

    protected void drawButtonOverlayAndHoveringText(final int mouseX, final int mouseY) {
        List<String> hoveredTip = new ArrayList<>();
        List<String> warnTip = new ArrayList<>();
        List<String> errorTip = new ArrayList<>();

        ResearchCognitionData data = current.getData();
        ResearchStationType stationType = PktTerminalGuiData.getResearchStationType();
        float consumption = ComputationCenterCache.getComputationPointConsumption();
        float generation = ComputationCenterCache.getComputationPointGeneration();

        if (stationType == null || stationType.getMaxTechLevel() < data.getTechLevel()) {
            errorTip.add(I18n.format("gui.terminal_controller.screen.info.start.error.tech_level"));
        }
        if (generation < data.getMinComputationPointPerTick()) {
            errorTip.add(I18n.format("gui.terminal_controller.screen.info.start.error.computation"));
        } else if ((generation - consumption) < data.getMinComputationPointPerTick()) {
            warnTip.add(I18n.format("gui.terminal_controller.screen.info.start.warn.computation"));
        }
        List<ResearchCognitionData> missingDependencies = getMissingResearches(data);
        if (!missingDependencies.isEmpty()) {
            errorTip.add(I18n.format("gui.terminal_controller.screen.info.start.error.dependencies",
                    missingDependencies.stream()
                            .map(ResearchCognitionData::getTranslatedName)
                            .collect(Collectors.joining(TextFormatting.RESET + ", "))
            ));
        }

        List<Database.Status> databases = PktTerminalGuiData.getDatabases();
        if (!hasDatabaseSpace(databases)) {
            errorTip.add(I18n.format("gui.terminal_controller.screen.info.start.error.database_space"));
        }

        if (!current.isLocked()) {
            drawButtonOverlay(44, 22);
            hoveredTip.add(I18n.format("gui.terminal_controller.data.unlocked"));
        } else if (errorTip.isEmpty()) {
            hoveredTip.add(I18n.format("gui.terminal_controller.screen.info.start"));
            long tickRequired = (long) ((data.getRequiredPoints() - current.getProgress()) / data.getMinComputationPointPerTick());
//                long tickRequired = (long) (data.getRequiredPoints() / Math.max(0.1F, (generation - consumption)));
            hoveredTip.add(TimeUtils.formatResearchRequiredTime(tickRequired * 50));
            hoveredTip.addAll(warnTip);
            hoveredTip.add(I18n.format("gui.terminal_controller.screen.info.reset"));
        } else {
            drawButtonOverlay(28, 22);
            hoveredTip.add(I18n.format("gui.terminal_controller.screen.info.start.error"));
            hoveredTip.addAll(errorTip);
            hoveredTip.add(I18n.format("gui.terminal_controller.screen.info.reset"));
        }

        if (isMouseOver(316, 143, 316 + 16, 143 + 16, mouseX - guiLeft, mouseY - guiTop)) {
            drawHoveringText(hoveredTip.stream()
                            .flatMap(s -> fontRenderer.listFormattedStringToWidth(s, width / 4).stream())
                            .collect(Collectors.toList()),
                    316 + 16 - 4, 143 + 16);
        }
    }

    protected List<ResearchCognitionData> getMissingResearches(ResearchCognitionData data) {
        return data.getDependencies().stream()
                .filter(dependency -> !unlockedData.contains(dependency))
                .collect(Collectors.toList());
    }

    protected void drawButtonOverlay(int textureX, int textureY) {
        this.mc.getTextureManager().bindTexture(TEXTURES_TERMINAL_ELEMENTS);
        GlStateManager.disableDepth();
        drawTexturedModalRect(316, 143, textureX, textureY, 16, 16);
        GlStateManager.enableDepth();
    }

    protected void drawDataInfo() {
        ResearchCognitionData data = current.getData();

        fontRenderer.drawStringWithShadow(data.getTranslatedName(), 115, 46, 0xFFFFFF);

        drawTechLevelTitleText(data, fontRenderer);

        float descDrawOffsetX = 115 / FONT_SCALE;
        float descDrawOffsetY = 60 / FONT_SCALE;

        List<String> lines;
        if (current.isLocked()) {
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

        float infoDrawOffsetY = 144 / FONT_SCALE;

        String dependencies;
        if (data.getDependencies().isEmpty()) {
            dependencies = I18n.format("gui.terminal_controller.screen.info.dependencies.none");
        } else {
            dependencies = data.getDependencies()
                    .stream()
                    .map(ResearchCognitionData::getTranslatedName)
                    .collect(Collectors.joining(TextFormatting.RESET + ", "));
        }
        fontRenderer.drawStringWithShadow(
                I18n.format("gui.terminal_controller.screen.info.dependencies") + dependencies,
                descDrawOffsetX, infoDrawOffsetY, 0xFFFFFF
        );
        fontRenderer.drawStringWithShadow(
                I18n.format("gui.terminal_controller.screen.info.required_points") +
                        MiscUtils.formatDecimal((long) data.getRequiredPoints()),
                descDrawOffsetX, infoDrawOffsetY + 12, 0xFFFFFF
        );
        fontRenderer.drawStringWithShadow(
                I18n.format("gui.terminal_controller.screen.info.min_computation_point_per_tick") +
                        NovaEngUtils.formatFLOPS(data.getMinComputationPointPerTick()),
                descDrawOffsetX + 125, infoDrawOffsetY + 12, 0xFFFFFF
        );

        screenScrollbar.setRange(0, Math.max(0, lines.size() - SCREEN_TEXT_MAX_LINES), 1);

        GlStateManager.scale(1F, 1F, 1F);
    }

    protected void drawTitle() {
        GlStateManager.pushMatrix();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        EntityPlayerSP player = Minecraft.getMinecraft().player;
        FontRenderer fr = this.fontRenderer;
        if (player != null && player.getName().equals("nxc_in_china")) {
            fr.drawStringWithShadow(I18n.format("gui.terminal_controller.title.nxc"), 8, 9, 0xFFFFFF);
        } else {
            fr.drawStringWithShadow(I18n.format("tile.novaeng_core.hypernet_terminal_controller.name"), 8, 9, 0xFFFFFF);
        }

        GlStateManager.popMatrix();
    }

    protected void drawNetworkAndControllerStatus() {
        final float statusRenderX = 113 / FONT_SCALE;
        float statusRenderY = 170 / FONT_SCALE;

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
        statusRenderY += 15;

        fontRenderer.drawStringWithShadow(
                I18n.format("gui.terminal_controller.status.network.computation_point_consumption"),
                statusRenderX, statusRenderY, 0xFFFFFF);
        statusRenderY += 10;

        fontRenderer.drawStringWithShadow(
                I18n.format("gui.terminal_controller.status.network.computation_point.info",
                        NovaEngUtils.formatFLOPS(ComputationCenterCache.getComputationPointConsumption())),
                statusRenderX, statusRenderY, 0xFFFFFF);
        statusRenderY += 10;

        fontRenderer.drawStringWithShadow(
                I18n.format("gui.terminal_controller.status.network.computation_point_generation"),
                statusRenderX, statusRenderY, 0xFFFFFF);
        statusRenderY += 10;

        fontRenderer.drawStringWithShadow(
                I18n.format("gui.terminal_controller.status.network.computation_point.info",
                        NovaEngUtils.formatFLOPS(ComputationCenterCache.getComputationPointGeneration())),
                statusRenderX, statusRenderY, 0xFFFFFF);
        statusRenderY += 15;

        fontRenderer.drawStringWithShadow(
                I18n.format("gui.terminal_controller.status.network.total_connected") +
                        ComputationCenterCache.getTotalConnected(),
                statusRenderX, statusRenderY, 0xFFFFFF);
    }

    protected void drawCardStatus() {
        final float statusRenderX = 113 / FONT_SCALE;
        float statusRenderY = 231 / FONT_SCALE;

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

        if (searchTextField.isFocused() || !searchTextField.getText().isEmpty()) {
            return;
        }

        int textRenderX = searchTextField.x;
        int textRenderY = searchTextField.y;
        fontRenderer.drawStringWithShadow(
                I18n.format("gui.terminal_controller.search"),
                textRenderX, textRenderY, 0xFFFFFF
        );
    }

    protected void updateRenderingData() {
        renderingData.clear();

        Map<String, ResearchDataContext> tmp = new LinkedHashMap<>();

        researchingData.forEach((data, progress) -> {
            ResearchDataContext context = new ResearchDataContext(data, true, true, progress);
            tmp.put(data.getTranslatedName(), context);
            if (context.equals(current)) {
                setCurrent(context);
            }
        });
        unlockedData.forEach(data -> {
            ResearchDataContext context = new ResearchDataContext(data, false, true, -1D);
            tmp.put(data.getTranslatedName(), context);
            if (context.equals(current)) {
                setCurrent(context);
            }
        });
        lockedData.forEach(data -> {
            ResearchDataContext context = new ResearchDataContext(data, true, true, -1D);
            tmp.put(data.getTranslatedName(), context);
            if (context.equals(current)) {
                setCurrent(context);
            }
        });
        unavailableData.forEach(data -> {
            ResearchDataContext context = new ResearchDataContext(data, true, false, -1D);
            tmp.put(data.getTranslatedName(), context);
            if (context.equals(current)) {
                setCurrent(context);
            }
        });

        String searchFilter = searchTextField.getText();
        if (!searchFilter.isEmpty()) {
            List<String> filtered = StringUtils.sortWithMatchRate(tmp.keySet(), searchFilter);
            if (filtered.isEmpty()) {
                searchTextField.setTextColor(0xFF0000);
            } else {
                searchTextField.setTextColor(0xFFFFFF);
                for (final String s : filtered) {
                    renderingData.add(tmp.get(s));
                }
            }
        } else {
            searchTextField.setTextColor(0xFFFFFF);
            renderingData.addAll(tmp.values());
        }
    }

    protected void setCurrent(final ResearchDataContext context) {
        this.current = context;
        currentCache = context;
    }

    protected void renderDataList(int mouseX, int mouseY) {
        int offsetY = 44;

        final int currentScroll = dataScrollbar.getCurrentScroll();
        int currentIndex = 0;
        while (currentIndex + currentScroll < renderingData.size() && currentIndex < MAX_PAGE_ELEMENTS) {
            final ResearchDataContext dataContext = renderingData.get(currentIndex + currentScroll);
            GlStateManager.pushMatrix();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

            drawResearchData(dataContext, offsetY, mouseX, mouseY);
            offsetY += TERMINAL_ELEMENT_HEIGHT;

            GlStateManager.popMatrix();
            currentIndex++;
        }
    }

    protected void drawResearchData(final ResearchDataContext dataContext, final int offsetY, int mouseX, int mouseY) {
        if (darkMode) {
            GlStateManager.color(0.7F, 0.7F, 0.7F, 1.0F);
        }
        if (dataContext.isLocked()) {
            if (darkMode) {
                GlStateManager.color(1.0F, 0.85F, 0.45F, 1.0F);
            } else {
                GlStateManager.color(1.0F, 0.9F, 0.6F, 1.0F);
            }
        }
        if (!dataContext.isAvailable()) {
            GlStateManager.color(1.0F, 0.6F, 0.6F, 1.0F);
        }
        if (isMouseOver(8, offsetY,
                8 + TERMINAL_ELEMENT_WIDTH - 1, offsetY + TERMINAL_ELEMENT_HEIGHT - 1,
                mouseX, mouseY)) {
            GlStateManager.color(0.7F, 0.9F, 1.0F, 1.0F);
        }
        if (dataContext.equals(current)) {
            GlStateManager.color(0.4F, 0.6F, 1.0F, 1.0F);
        }

        this.mc.getTextureManager().bindTexture(TEXTURES_TERMINAL_ELEMENTS);
        drawTexturedModalRect(8, offsetY, 0, 0, TERMINAL_ELEMENT_WIDTH, TERMINAL_ELEMENT_HEIGHT);

        ResearchCognitionData data = dataContext.getData();
        double researchProgress = dataContext.getProgress();
        if (researchProgress > 0) {
            GlStateManager.color(0.6F, 1.0F, 0.6F, 1.0F);
            double progress = researchProgress / data.getRequiredPoints();
            drawTexturedModalRect(8, offsetY, 0, 0, (int) (TERMINAL_ELEMENT_WIDTH * progress), TERMINAL_ELEMENT_HEIGHT);
        }
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        ItemStack previewStack = data.getPreviewStack();
        renderItemStackToGUI(mc, mc.getRenderItem(), 11, offsetY + 3, previewStack);

        GlStateManager.scale(FONT_SCALE, FONT_SCALE, FONT_SCALE);
        float textRenderOffsetX = 29 / FONT_SCALE;
        float textRenderOffsetY = (48 + (offsetY - 44)) / FONT_SCALE;
        fontRenderer.drawString(data.getTranslatedName(), (int) textRenderOffsetX, (int) textRenderOffsetY, 0x404040);

        if (dataContext.isLocked()) {
            if (researchProgress >= 0) {
                if (researchProgress == 0) {
                    fontRenderer.drawString(
                            I18n.format("gui.terminal_controller.data.progress", "0.00%"),
                            (int) textRenderOffsetX, (int) textRenderOffsetY + 12, 0x404040);
                } else {
                    fontRenderer.drawString(
                            I18n.format("gui.terminal_controller.data.progress",
                                    NovaEngUtils.formatPercent(researchProgress, data.getRequiredPoints())),
                            (int) textRenderOffsetX, (int) textRenderOffsetY + 12, 0x404040);
                }
            } else {
                if (dataContext.isAvailable()) {
                    fontRenderer.drawString(I18n.format("gui.terminal_controller.data.locked"),
                            (int) textRenderOffsetX, (int) textRenderOffsetY + 12, 0x404040);
                } else {
                    fontRenderer.drawString(I18n.format("gui.terminal_controller.data.unavailable"),
                            (int) textRenderOffsetX, (int) textRenderOffsetY + 12, 0x404040);
                }
            }
        } else {
            fontRenderer.drawString(I18n.format("gui.terminal_controller.data.unlocked"),
                    (int) textRenderOffsetX, (int) textRenderOffsetY + 12, 0x404040);
        }

        GlStateManager.scale(1F, 1F, 1F);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        searchTextField.updateCursorCounter();

        if (ClientScheduler.getClientTick() % 20 == 0) {
            updateResearchingData(researchingData);
            updateUnlockedData(unlockedData);
            updateFilteredData();
            updateRenderingData();
        }
    }

    @Override
    protected void mouseClicked(final int mouseX, final int mouseY, final int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        int x = mouseX - guiLeft;
        int y = mouseY - guiTop;

        if (mouseButton == 0) {
            if (searchTextField.mouseClicked(x, y, mouseButton)) {
                return;
            }
        } else if (mouseButton == 1) {
            if (isMouseOver(searchTextField.x, searchTextField.y,
                    searchTextField.x + searchTextField.width,
                    searchTextField.y + searchTextField.height,
                    x, y))
            {
                searchTextField.setText("");
                searchTextCache = "";
                updateRenderingData();
            }
        }

        if (startResearch.mousePressed(mc, x, y)) {
            if (mouseButton == 0 && current != null && current.isLocked() && canStartResearch(current.getData())) {
                NovaEngineeringCore.NET_CHANNEL.sendToServer(new PktResearchTaskProvide(current.getData()));
                startResearch.playPressSound(mc.getSoundHandler());
                return;
            }
            if (mouseButton == 1) {
                NovaEngineeringCore.NET_CHANNEL.sendToServer(new PktResearchTaskReset());
                startResearch.playPressSound(mc.getSoundHandler());
                return;
            }
        }

        if (mouseButton == 0 && (screenScrollbar.click(x, y) || dataScrollbar.click(x, y))) {
            return;
        }

        int offsetY = 44;

        int index = dataScrollbar.getCurrentScroll();
        int currentIndex = 0;
        while (index < renderingData.size() && currentIndex < MAX_PAGE_ELEMENTS) {
            final ResearchDataContext data = renderingData.get(index);

            if (isMouseOver(
                    8, offsetY,
                    8 + TERMINAL_ELEMENT_WIDTH - 1, offsetY + TERMINAL_ELEMENT_HEIGHT - 1,
                    x, y))
            {
                if (mouseButton == 0) {
                    setCurrent(data);
                } else if (mouseButton == 1) {
                    setCurrent(null);
                }
                break;
            }

            offsetY += TERMINAL_ELEMENT_HEIGHT;
            currentIndex++;
            index++;
        }
    }

    private void updateFilteredData() {
        lockedData.clear();
        unavailableData.clear();
        RegistryHyperNet.getAllResearchCognitionData().stream()
                .filter(data -> !researchingData.containsKey(data))
                .filter(data -> !unlockedData.contains(data))
                .forEach(data -> {
                    List<ResearchCognitionData> dependencies = data.getDependencies();
                    if (dependencies.isEmpty() || unlockedData.containsAll(dependencies)) {
                        lockedData.add(data);
                    } else if (!data.isHideByDefault()) {
                        unavailableData.add(data);
                    }
                });
    }

    private boolean canStartResearch(ResearchCognitionData data) {
        ResearchStationType stationType = PktTerminalGuiData.getResearchStationType();

        if (stationType == null || stationType.getMaxTechLevel() < data.getTechLevel()) {
            return false;
        }

        float generation = ComputationCenterCache.getComputationPointGeneration();
        if (generation < data.getMinComputationPointPerTick()) {
            return false;
        }

//        float consumption = ComputationCenterCache.getComputationPointConsumption();
//        if ((generation - consumption) < data.getMinComputationPointPerTick()) {
//            return false;
//        }

        if (!unlockedData.containsAll(data.getDependencies())) {
            return false;
        }

        List<Database.Status> databases = PktTerminalGuiData.getDatabases();
        return hasDatabaseSpace(databases);
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

        if (searchTextField.textboxKeyTyped(c, i)) {
            searchTextCache = searchTextField.getText();
            updateRenderingData();
        }
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();

        final int i = Mouse.getEventDWheel();
        if (i != 0) {
            int mouseX = Mouse.getEventX() * this.width / this.mc.displayWidth;
            int mouseY = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;

            if (isMouseOver(7, 43, 106, 242, mouseX - guiLeft, mouseY - guiTop)) {
                dataScrollbar.wheel(i);
            }
            if (isMouseOver(111, 43, 342, 162, mouseX - guiLeft, mouseY - guiTop)) {
                screenScrollbar.wheel(i);
            }
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(final float partialTicks, final int mouseX, final int mouseY) {
        if (darkMode) {
            GlStateManager.color(0.4F, 0.4F, 0.4F, 1.0F);
        } else {
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        }

        this.mc.getTextureManager().bindTexture(TEXTURES_TERMINAL);
        final int x = (this.width - this.xSize) / 2;
        final int y = (this.height - this.ySize) / 2;
        Gui.drawModalRectWithCustomSizedTexture(x, y, 0, 0, this.xSize, this.ySize, this.xSize, this.ySize);

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }

    protected void updateAndDrawScrollbar() {
        if (darkMode) {
            GlStateManager.color(0.4F, 0.4F, 0.4F, 1.0F);
        } else {
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        }

        dataScrollbar.setLeft(DATA_SCROLL_BAR_LEFT)
                .setTop(DATA_SCROLL_BAR_TOP)
                .setHeight(DATA_SCROLL_BAR_HEIGHT)
                .setRange(0, Math.max(0, renderingData.size() - MAX_PAGE_ELEMENTS), 1);
        screenScrollbar.setLeft(SCREEN_SCROLL_BAR_LEFT)
                .setTop(SCREEN_SCROLL_BAR_TOP)
                .setHeight(SCREEN_SCROLL_BAR_HEIGHT);
        dataScrollbar.draw(this, mc);
        screenScrollbar.draw(this, mc);

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }

    @Override
    protected void setWidthHeight() {
    }

    public static class ResearchDataContext {
        private final ResearchCognitionData data;
        private final boolean locked;
        private final boolean available;
        private final double progress;

        public ResearchDataContext(final ResearchCognitionData data,
                                   final boolean locked,
                                   final boolean available,
                                   final Double progress)
        {
            this.data = data;
            this.locked = locked;
            this.available = available;
            this.progress = progress;
        }

        public ResearchCognitionData getData() {
            return data;
        }

        public boolean isLocked() {
            return locked;
        }

        public boolean isAvailable() {
            return available;
        }

        public double getProgress() {
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
