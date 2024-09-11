package github.kasuminova.novaeng.client.gui;

import appeng.api.storage.ITerminalHost;
import github.kasuminova.mmce.client.gui.AEBaseGuiContainerDynamic;
import github.kasuminova.mmce.client.gui.util.TextureProperties;
import github.kasuminova.mmce.client.gui.widget.Button4State;
import github.kasuminova.mmce.client.gui.widget.Button5State;
import github.kasuminova.mmce.client.gui.widget.MultiLineLabel;
import github.kasuminova.mmce.client.gui.widget.base.WidgetController;
import github.kasuminova.mmce.client.gui.widget.base.WidgetGui;
import github.kasuminova.novaeng.NovaEngineeringCore;
import github.kasuminova.novaeng.client.gui.widget.craftingtree.Background;
import github.kasuminova.novaeng.client.gui.widget.craftingtree.CraftingTree;
import github.kasuminova.novaeng.client.gui.widget.craftingtree.event.CraftingTreeDataUpdateEvent;
import github.kasuminova.novaeng.common.container.appeng.ContainerCraftingTree;
import github.kasuminova.novaeng.common.integration.ae2.data.LiteCraftTreeNode;
import github.kasuminova.novaeng.common.network.appeng.PktSwitchCraftingTree;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

import java.util.Collections;

/**
 * TODO: Dark mode switch.
 */
public class GuiCraftingTree extends AEBaseGuiContainerDynamic {

    private static final TextureProperties BUTTON_BACK = TextureProperties.of(
            new ResourceLocation(NovaEngineeringCore.MOD_ID, "textures/gui/guicraftingtree_dark.png"),
            232, 100, 24, 20
    );
    private static final TextureProperties BUTTON_BACK_HOVERED = TextureProperties.of(
            new ResourceLocation(NovaEngineeringCore.MOD_ID, "textures/gui/guicraftingtree_dark.png"),
            208, 100, 24, 20
    );
    private static final TextureProperties BUTTON_BACK_MOUSEDOWN = TextureProperties.of(
            new ResourceLocation(NovaEngineeringCore.MOD_ID, "textures/gui/guicraftingtree_dark.png"),
            184, 100, 24, 20
    );

    private static final TextureProperties BUTTON_MISSING_ONLY = TextureProperties.of(
            new ResourceLocation(NovaEngineeringCore.MOD_ID, "textures/gui/guicraftingtree_dark.png"),
            232, 60, 24, 20
    );
    private static final TextureProperties BUTTON_MISSING_ONLY_HOVERED = TextureProperties.of(
            new ResourceLocation(NovaEngineeringCore.MOD_ID, "textures/gui/guicraftingtree_dark.png"),
            208, 60, 24, 20
    );
    private static final TextureProperties BUTTON_MISSING_ONLY_MOUSEDOWN = TextureProperties.of(
            new ResourceLocation(NovaEngineeringCore.MOD_ID, "textures/gui/guicraftingtree_dark.png"),
            184, 60, 24, 20
    );
    private static final TextureProperties BUTTON_MISSING_ONLY_CLICKED = TextureProperties.of(
            new ResourceLocation(NovaEngineeringCore.MOD_ID, "textures/gui/guicraftingtree_dark.png"),
            160, 60, 24, 20
    );

    private static final TextureProperties BUTTON_SCREENSHOT = TextureProperties.of(
            new ResourceLocation(NovaEngineeringCore.MOD_ID, "textures/gui/guicraftingtree_dark.png"),
            232, 0, 24, 20
    );
    private static final TextureProperties BUTTON_SCREENSHOT_HOVERED = TextureProperties.of(
            new ResourceLocation(NovaEngineeringCore.MOD_ID, "textures/gui/guicraftingtree_dark.png"),
            208, 0, 24, 20
    );
    private static final TextureProperties BUTTON_SCREENSHOT_MOUSEDOWN = TextureProperties.of(
            new ResourceLocation(NovaEngineeringCore.MOD_ID, "textures/gui/guicraftingtree_dark.png"),
            184, 0, 24, 20
    );

    private final CraftingTree tree = new CraftingTree();

    private final Button4State screenshot = new Button4State();
    private final Button5State missingOnly = new Button5State();
    private final Button4State back = new Button4State();

    private Background background;

    public GuiCraftingTree(final InventoryPlayer inventoryPlayer, final ITerminalHost te) {
        super(new ContainerCraftingTree(inventoryPlayer, te));
        this.background = Background.getLargest(this.width, this.height, true);
        this.xSize = background.getTexture().width();
        this.ySize = background.getTexture().height();
        this.guiLeft = (this.width - this.xSize) / 2;
        this.guiTop = (this.height - this.ySize) / 2;

        this.widgetController = new WidgetController(WidgetGui.of(this, this.xSize, this.ySize, guiLeft, guiTop));
        this.widgetController.addWidget(tree
                .setWidthHeight(background.getInternalWidth(), background.getInternalHeight())
                .setAbsXY(background.getInternalXOffset(), background.getInternalYOffset())
        );
        // Title
        this.widgetController.addWidget(new MultiLineLabel(Collections.singletonList(I18n.format("gui.crafting_tree.title")))
                .setAutoWrap(false)
                .setMargin(0)
                .setAbsXY(6, 9)
        );
        // Right Top
        this.widgetController.addWidget(screenshot
                .setMouseDownTexture(BUTTON_SCREENSHOT_MOUSEDOWN)
                .setHoveredTexture(BUTTON_SCREENSHOT_HOVERED)
                .setTexture(BUTTON_SCREENSHOT)
                .setTooltipFunction(input -> Collections.singletonList(I18n.format("gui.crafting_tree.screenshot")))
                .setOnClickedListener(btn -> {})
                .setWidthHeight(24, 20)
                .setAbsXY(xSize - 86, 3));
        this.widgetController.addWidget(missingOnly
                .setClickedTexture(BUTTON_MISSING_ONLY_CLICKED)
                .setMouseDownTexture(BUTTON_MISSING_ONLY_MOUSEDOWN)
                .setHoveredTexture(BUTTON_MISSING_ONLY_HOVERED)
                .setTexture(BUTTON_MISSING_ONLY)
                .setTooltipFunction(input -> missingOnly.isClicked() 
                        ? Collections.singletonList(I18n.format("gui.crafting_tree.default"))
                        : Collections.singletonList(I18n.format("gui.crafting_tree.missing_only"))
                )
                .setOnClickedListener(btn -> tree.setMissingOnly(missingOnly.isClicked()))
                .setWidthHeight(24, 20)
                .setAbsXY(xSize - 58, 3));
        this.widgetController.addWidget(back
                .setMouseDownTexture(BUTTON_BACK_MOUSEDOWN)
                .setHoveredTexture(BUTTON_BACK_HOVERED)
                .setTexture(BUTTON_BACK)
                .setTooltipFunction(input -> Collections.singletonList(I18n.format("gui.crafting_tree.back")))
                .setOnClickedListener(btn -> NovaEngineeringCore.NET_CHANNEL.sendToServer(new PktSwitchCraftingTree()))
                .setWidthHeight(24, 20)
                .setAbsXY(xSize - 30, 3));
    }

    @Override
    public void initGui() {
        this.background = Background.getLargest(this.width, this.height, true);
        this.xSize = background.getTexture().width();
        this.ySize = background.getTexture().height();
        this.guiLeft = (this.width - this.xSize) / 2;
        this.guiTop = (this.height - this.ySize) / 2;

        this.tree.setWidthHeight(background.getInternalWidth(), background.getInternalHeight())
                .setAbsXY(background.getInternalXOffset(), background.getInternalYOffset());
        this.screenshot.setAbsXY(xSize - 86, 3);
        this.missingOnly.setAbsXY(xSize - 58, 3);
        this.back.setAbsXY(xSize - 30, 3);

        super.initGui();
    }

    @Override
    public void drawBG(final int offsetX, final int offsetY, final int mouseX, final int mouseY) {
        GlStateManager.color(1.0F, 1.0F, 1.0F);

        assert background.getTexture().texRes() != null;
        this.mc.getTextureManager().bindTexture(background.getTexture().texRes());
        final int x = (this.width - this.xSize) / 2;
        final int y = (this.height - this.ySize) / 2;
        Gui.drawModalRectWithCustomSizedTexture(x, y, 0, 0, xSize, ySize, xSize, ySize);

        super.drawBG(offsetX, offsetY, mouseX, mouseY);
    }

    public void onDataUpdate(final LiteCraftTreeNode root) {
        this.widgetController.postGuiEvent(new CraftingTreeDataUpdateEvent(root));
    }

}
