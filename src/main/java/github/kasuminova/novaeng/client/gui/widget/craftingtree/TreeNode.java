package github.kasuminova.novaeng.client.gui.widget.craftingtree;

import appeng.api.storage.data.IAEItemStack;
import appeng.client.render.StackSizeRenderer;
import appeng.util.ReadableNumberConverter;
import github.kasuminova.mmce.client.gui.util.MousePos;
import github.kasuminova.mmce.client.gui.util.RenderPos;
import github.kasuminova.mmce.client.gui.util.RenderSize;
import github.kasuminova.mmce.client.gui.util.TextureProperties;
import github.kasuminova.mmce.client.gui.widget.base.DynamicWidget;
import github.kasuminova.mmce.client.gui.widget.base.WidgetGui;
import github.kasuminova.mmce.client.gui.widget.event.GuiEvent;
import github.kasuminova.novaeng.NovaEngineeringCore;
import github.kasuminova.novaeng.client.gui.widget.craftingtree.event.TreeNodeSelectEvent;
import github.kasuminova.novaeng.common.integration.ae2.data.LiteCraftTreeNode;
import github.kasuminova.novaeng.common.integration.ae2.data.LiteCraftTreeProc;
import hellfirepvp.modularmachinery.client.ClientProxy;
import hellfirepvp.modularmachinery.common.integration.ModIntegrationJEI;
import hellfirepvp.modularmachinery.common.integration.ingredient.IngredientItemStackRenderer;
import hellfirepvp.modularmachinery.common.util.MiscUtils;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.config.KeyBindings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.config.GuiUtils;

import java.util.Collections;
import java.util.List;

public class TreeNode extends DynamicWidget {

    public static final int LINE_WIDTH = 1;
    public static final int LINE_HEIGHT = 2;

    public static final int PARENT_LINE_HEIGHT = 2;
    public static final int LINE_TOTAL_HEIGHT = 6;

    public static final int WIDTH = 20;
    public static final int HEIGHT = 20;
    public static final int TOTAL_HEIGHT = HEIGHT + LINE_TOTAL_HEIGHT;

    public static final int ROOT_MARGIN_TOP = 4;
    public static final int MARGIN_LEFT = 6;
    public static final int LINE_RENDER_OFFSET = (WIDTH - (LINE_WIDTH * 2)) / 2;

    public static final int LINE_COLOR = 0xFFF2F2F2;
    public static final int LINE_SHADOW_COLOR = 0xFF4D4D67;

    public static final int MISSING_LINE_COLOR = 0xFFEE6363;
    public static final int MISSING_LINE_SHADOW_COLOR = 0xFF8B3A3A;

    public static final int ITEM_RENDER_OFFSET = 2;

    private static final TextureProperties BACKGROUND = TextureProperties.of(
            new ResourceLocation(NovaEngineeringCore.MOD_ID, "textures/gui/guicraftingtree_dark.png"),
            0, 216, 20, 20
    );

    private static final TextureProperties BACKGROUND_MISSING = TextureProperties.of(
            new ResourceLocation(NovaEngineeringCore.MOD_ID, "textures/gui/guicraftingtree_dark.png"),
            0, 236, 20, 20
    );

    private static final TextureProperties MISSING = TextureProperties.of(
            new ResourceLocation(NovaEngineeringCore.MOD_ID, "textures/gui/guicraftingtree_dark.png"),
            40, 216, 20, 20
    );

    private static final TextureProperties SELECTED = TextureProperties.of(
            new ResourceLocation(NovaEngineeringCore.MOD_ID, "textures/gui/guicraftingtree_dark.png"),
            0, 196, 20, 20
    );

    private static final StackSizeRenderer STACK_SIZE_RENDERER = new StackSizeRenderer();

    protected final CraftingTree tree;

    protected boolean root = false;
    protected int linkedSubNodes = 0;

    protected LiteCraftTreeNode node = null;
    protected TreeRow parentRow = null;
    protected TreeNode prev = null;
    protected TreeNode next = null;

    protected boolean mouseOver = false;
    protected boolean selected = false;

    public TreeNode(final CraftingTree tree) {
        this.tree = tree;
        setHeight(TOTAL_HEIGHT);
        setMargin(MARGIN_LEFT, 0, 0, 0);
    }

    @Override
    public void update(final WidgetGui gui) {
        super.update(gui);
        mouseOver = false;
    }

    @Override
    public void preRender(final WidgetGui gui, final RenderSize renderSize, final RenderPos renderPos, final MousePos mousePos) {
        mouseOver = isMouseOver(mousePos.mouseX(), mousePos.mouseY());
        if (renderPos.posX() + WIDTH < 0 && renderPos.posY() + TOTAL_HEIGHT < 0) {
            return;
        }

        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        if (LiteCraftTreeNode.isMissing(node)) {
            BACKGROUND_MISSING.render(renderPos.add(new RenderPos(0, LINE_HEIGHT)), gui);
        } else {
            BACKGROUND.render(renderPos.add(new RenderPos(0, LINE_HEIGHT)), gui);
        }
        if (selected) {
            SELECTED.render(renderPos.add(new RenderPos(0, LINE_HEIGHT)), gui);
        }
    }

    @Override
    public void render(final WidgetGui gui, final RenderSize renderSize, final RenderPos renderPos, final MousePos mousePos) {
        if (renderPos.posX() + WIDTH < 0 && renderPos.posY() + TOTAL_HEIGHT < 0) {
            return;
        }
        renderItem(renderPos.add(new RenderPos(0, LINE_HEIGHT)).add(new RenderPos(ITEM_RENDER_OFFSET, ITEM_RENDER_OFFSET)));
        if (LiteCraftTreeNode.isMissing(node) && node.missing() > 0) {
            GlStateManager.disableDepth();
            MISSING.render(renderPos.add(new RenderPos(0, LINE_HEIGHT)), gui);
            GlStateManager.enableDepth();
        }
        GlStateManager.disableLighting();
    }

    @Override
    public void postRender(final WidgetGui gui, final RenderSize renderSize, final RenderPos renderPos, final MousePos mousePos) {
        renderParentNodeLink(renderPos);
        renderSubNodeLinkLine(renderPos.add(new RenderPos(0, HEIGHT + LINE_HEIGHT)));
        CraftingTree.DEBUG_RENDERED_NODES.incrementAndGet();
    }

    @Override
    public List<String> getHoverTooltips(final WidgetGui widgetGui, final MousePos mousePos) {
        IAEItemStack output = node.output();
        if (output == null) {
            return Collections.emptyList();
        }

        ItemStack definition = output.getDefinition();
        GuiScreen g = widgetGui.getGui();
        GuiUtils.preItemToolTip(definition);

        List<String> toolTip = g.getItemToolTip(definition);
        if (LiteCraftTreeNode.isMissing(node)) {
            toolTip.add(node.missing() > 0
                    ? I18n.format("gui.crafting_tree.missing", MiscUtils.formatDecimal((node.missing())))
                    : I18n.format("gui.crafting_tree.sub_node_missing"));
        }
        if (output.getStackSize() >= 10000) {
            toolTip.add(MiscUtils.formatDecimal(output.getStackSize()));
        }

        return toolTip;
    }

    protected void renderItem(final RenderPos renderPos) {
        if (node.output() == null) {
            return;
        }
        IngredientItemStackRenderer.DEFAULT_INSTANCE.render(Minecraft.getMinecraft(), renderPos.posX(), renderPos.posY(), node.output().getCachedItemStack(1));
        STACK_SIZE_RENDERER.renderStackSize(Minecraft.getMinecraft().fontRenderer, node.output(), renderPos.posX(), renderPos.posY());
    }

    protected void renderParentNodeLink(final RenderPos renderPos) {
        if (root) {
            return;
        }

        int lineColor = LiteCraftTreeNode.isMissing(node) ? MISSING_LINE_COLOR : LINE_COLOR;
        int lineShadowColor = LiteCraftTreeNode.isMissing(node) ? MISSING_LINE_SHADOW_COLOR : LINE_SHADOW_COLOR;

        // Vertical Line
        renderLine(renderPos.add(new RenderPos(LINE_RENDER_OFFSET, -1)), LINE_WIDTH, LINE_HEIGHT + 1, lineColor);
        // Shadow
        renderLine(renderPos.add(new RenderPos(LINE_RENDER_OFFSET + 1, 0)), LINE_WIDTH, LINE_HEIGHT, lineShadowColor);
    }

    protected void renderSubNodeLinkLine(final RenderPos renderPos) {
        if (node.inputs().isEmpty()) {
            return;
        }

        int lineColor = LiteCraftTreeNode.isMissing(node) ? MISSING_LINE_COLOR : LINE_COLOR;
        int lineShadowColor = LiteCraftTreeNode.isMissing(node) ? MISSING_LINE_SHADOW_COLOR : LINE_SHADOW_COLOR;

        if (linkedSubNodes > 0) {
            int totalWidth = (linkedSubNodes * (WIDTH + MARGIN_LEFT)) + 1;
            // Horizontal Line
            renderLine(renderPos.add(new RenderPos(LINE_RENDER_OFFSET, LINE_HEIGHT)), totalWidth, 1, lineColor);
            // Shadow
            renderLine(renderPos.add(new RenderPos(LINE_RENDER_OFFSET + 1, LINE_HEIGHT + 1)), totalWidth, 1, lineShadowColor);

            // Vertical Line
            renderLine(renderPos.add(new RenderPos(LINE_RENDER_OFFSET, 0)), LINE_WIDTH, LINE_HEIGHT, lineColor);
            // Shadow
            renderLine(renderPos.add(new RenderPos(LINE_RENDER_OFFSET + 1, 0)), LINE_WIDTH, LINE_HEIGHT, lineShadowColor);
        } else {
            // Vertical Line
            renderLine(renderPos.add(new RenderPos(LINE_RENDER_OFFSET, 0)), LINE_WIDTH, LINE_HEIGHT + 1, lineColor);
            // Shadow
            renderLine(renderPos.add(new RenderPos(LINE_RENDER_OFFSET + 1, 0)), LINE_WIDTH, LINE_HEIGHT + 2, lineShadowColor);
        }
    }

    protected static void renderLine(final RenderPos pos, final int width, final int height, final int color) {
        GuiContainer.drawRect(pos.posX(), pos.posY(), pos.posX() + width, pos.posY() + height, color);
    }

    @Override
    public boolean onGuiEvent(final GuiEvent event) {
        if (event instanceof TreeNodeSelectEvent selectEvent) {
            if (selected && selectEvent.getSelectedNode() != this) {
                selected = false;
            }
        }
        return super.onGuiEvent(event);
    }

    @Override
    public boolean onMouseClick(final MousePos mousePos, final RenderPos renderPos, final int mouseButton) {
        if (!selected) {
            select();
            return true;
        }
        return false;
    }

    @Override
    public boolean onKeyTyped(final char typedChar, final int keyCode) {
        if (!mouseOver || node == null) {
            return false;
        }

        int showRecipeKeyCode = KeyBindings.showRecipe.getKeyCode();
        int showUsesKeyCode = KeyBindings.showUses.getKeyCode();
        int bookmarkKeyCode = KeyBindings.bookmark.getKeyCode();

        if (showRecipeKeyCode > 0 && showRecipeKeyCode <= 255 && showRecipeKeyCode == keyCode) {
            return showStackFocus(IFocus.Mode.OUTPUT);
        }
        if (showUsesKeyCode > 0 && showUsesKeyCode <= 255 && showUsesKeyCode == keyCode) {
            return showStackFocus(IFocus.Mode.INPUT);
        }
        if (bookmarkKeyCode > 0 && bookmarkKeyCode <= 255 && bookmarkKeyCode == keyCode) {
            ModIntegrationJEI.addItemStackToBookmarkList(node.output().getCachedItemStack(1));
            return true;
        }

        return false;
    }

    protected boolean showStackFocus(final IFocus.Mode output) {
        ClientProxy.clientScheduler.addRunnable(() -> {
            IFocus<ItemStack> focus = ModIntegrationJEI.recipeRegistry.createFocus(output, node.output().getCachedItemStack(1));
            ModIntegrationJEI.jeiRuntime.getRecipesGui().show(focus);
        }, 0);
        return true;
    }

    protected int getTotalSubNodes() {
        int total = 0;
        for (final LiteCraftTreeProc input : node.inputs()) {
            total += input.inputs().size();
        }
        return total;
    }

    public TreeNode select() {
        selected = true;
        tree.onGuiEvent(new TreeNodeSelectEvent(this));
        return this;
    }

    public TreeNode setNode(final LiteCraftTreeNode node) {
        this.node = node;
        return this;
    }

    public TreeRow getParentRow() {
        return parentRow;
    }

    public TreeNode setParentRow(final TreeRow parentRow) {
        this.parentRow = parentRow;
        return this;
    }

    public TreeNode getPrev() {
        return prev;
    }

    public TreeNode getNext() {
        return next;
    }

    public TreeNode setPrev(final TreeNode prev) {
        this.prev = prev;
        return this;
    }

    public TreeNode setNext(final TreeNode next) {
        this.next = next;
        return this;
    }

    public TreeNode setLinkedSubNodes(final int linkedSubNodes) {
        this.linkedSubNodes = linkedSubNodes;
        return this;
    }

    public TreeNode setRoot(final boolean root) {
        this.root = root;
        return this;
    }

    @Override
    public boolean isMouseOver(final MousePos mousePos) {
        return isMouseOver(0, PARENT_LINE_HEIGHT, mousePos.mouseX(), mousePos.mouseY(), WIDTH, HEIGHT);
    }

    public boolean isMouseOver(int startX, int startY, int mouseX, int mouseY, int width, int height) {
        if (isInvisible()) {
            return false;
        }

        int endX = startX + width;
        int endY = startY + height;
        return mouseX >= startX && mouseX < endX && mouseY >= startY && mouseY < endY;
    }

}
