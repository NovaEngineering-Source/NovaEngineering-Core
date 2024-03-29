package github.kasuminova.novaeng.client.gui.widget.estorage;

import github.kasuminova.mmce.client.gui.util.MousePos;
import github.kasuminova.mmce.client.gui.util.RenderPos;
import github.kasuminova.mmce.client.gui.util.RenderSize;
import github.kasuminova.mmce.client.gui.widget.MultiLineLabel;
import github.kasuminova.mmce.client.gui.widget.base.WidgetGui;
import github.kasuminova.mmce.client.gui.widget.container.Column;
import github.kasuminova.novaeng.NovaEngineeringCore;
import github.kasuminova.novaeng.common.block.estorage.prop.DriveStorageLevel;
import github.kasuminova.novaeng.common.block.estorage.prop.DriveStorageType;
import github.kasuminova.novaeng.common.container.data.EStorageCellData;
import github.kasuminova.novaeng.common.crafttweaker.util.NovaEngUtils;
import github.kasuminova.novaeng.common.item.estorage.EStorageCellFluid;
import github.kasuminova.novaeng.common.item.estorage.EStorageCellItem;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.Collections;

public class EStorageCellInfo extends Column {

    public static final ResourceLocation BG_TEX_RES = new ResourceLocation(NovaEngineeringCore.MOD_ID, "textures/gui/estorage_controller_elements.png");

    public static final int CELL_BACKGROUND_TEXTURE_WIDTH = 67;
    public static final int CELL_BACKGROUND_TEXTURE_HEIGHT = 26;
    
    public static final int L4_CELL_BACKGROUND_TEXTURE_X = 1;
    public static final int L4_CELL_BACKGROUND_TEXTURE_Y = 1;
    
    public static final int L6_CELL_BACKGROUND_TEXTURE_X = 1;
    public static final int L6_CELL_BACKGROUND_TEXTURE_Y = 28;
    
    public static final int L9_CELL_BACKGROUND_TEXTURE_X = 1;
    public static final int L9_CELL_BACKGROUND_TEXTURE_Y = 55;
    
    public static final int CELL_TYPE_BACKGROUND_WIDTH = 8;
    public static final int CELL_TYPE_BACKGROUND_HEIGHT = 26;
    
    public static final int FLUID_CELL_TYPE_BACKGROUND_TEXTURE_X = 10;
    public static final int FLUID_CELL_TYPE_BACKGROUND_TEXTURE_Y = 82;

    public static final int ITEM_CELL_TYPE_BACKGROUND_TEXTURE_X = 19;
    public static final int ITEM_CELL_TYPE_BACKGROUND_TEXTURE_Y = 82;

    protected int cellBgTexX;
    protected int cellBgTexY;
    protected int cellTypeBgTexX;
    protected int cellTypeBgTexY;

    protected final EStorageCellData data;

    public EStorageCellInfo(final EStorageCellData data) {
        this.data = data;
        this.width = 67;
        this.height = 26;
        initBackground();
        initInfo();
        setMarginDown(2);
        setUseScissor(false);
    }

    @Override
    protected void preRenderInternal(final WidgetGui widgetGui, final RenderSize renderSize, final RenderPos renderPos, final MousePos mousePos) {
        GuiScreen gui = widgetGui.getGui();
        gui.mc.getTextureManager().bindTexture(BG_TEX_RES);
        gui.drawTexturedModalRect(
                renderPos.posX(), renderPos.posY(),
                cellBgTexX, cellBgTexY,
                CELL_BACKGROUND_TEXTURE_WIDTH, CELL_BACKGROUND_TEXTURE_HEIGHT
        );
        gui.drawTexturedModalRect(
                renderPos.posX(), renderPos.posY(),
                cellTypeBgTexX, cellTypeBgTexY,
                CELL_TYPE_BACKGROUND_WIDTH, CELL_TYPE_BACKGROUND_HEIGHT
        );
        super.preRenderInternal(widgetGui, renderSize, renderPos, mousePos);
    }

    protected void initBackground() {
        switch (data.level()) {
            case A -> {
                cellBgTexX = L4_CELL_BACKGROUND_TEXTURE_X;
                cellBgTexY = L4_CELL_BACKGROUND_TEXTURE_Y;
            }
            case B -> {
                cellBgTexX = L6_CELL_BACKGROUND_TEXTURE_X;
                cellBgTexY = L6_CELL_BACKGROUND_TEXTURE_Y;
            }
            case C -> {
                cellBgTexX = L9_CELL_BACKGROUND_TEXTURE_X;
                cellBgTexY = L9_CELL_BACKGROUND_TEXTURE_Y;
            }
        }
        switch (data.type()) {
            case ITEM -> {
                cellTypeBgTexX = ITEM_CELL_TYPE_BACKGROUND_TEXTURE_X;
                cellTypeBgTexY = ITEM_CELL_TYPE_BACKGROUND_TEXTURE_Y;
            }
            case FLUID -> {
                cellTypeBgTexX = FLUID_CELL_TYPE_BACKGROUND_TEXTURE_X;
                cellTypeBgTexY = FLUID_CELL_TYPE_BACKGROUND_TEXTURE_Y;
            }
        }
    }

    protected void initInfo() {
        DriveStorageType type = data.type();
        DriveStorageLevel level = data.level();
        int usedTypes = data.usedTypes();
        long usedBytes = data.usedBytes();
        int maxTypes = getMaxTypes(data);
        long maxBytes = getMaxBytes(data);

        String typeName = I18n.format("gui.estorage_controller.cell_info." + switch (type) {
            case EMPTY -> "unknown";
            case ITEM -> "item";
            case FLUID -> "fluid";
        });
        String levelName = switch (level) {
            case EMPTY -> "unknown";
            case A -> "L4";
            case B -> "L6";
            case C -> "L9";
        };

        getWidgets().clear();

        // Type
        addWidget(new MultiLineLabel(Collections.singletonList(
                I18n.format("gui.estorage_controller.cell_info.tip.0", typeName, levelName))
        ).setAutoRecalculateSize(false).setScale(.6F).setWidth(width - (10)).setHeight(7).setMargin(10, 0, 2, 0));
        // StoredTypes / MaxTypes
        addWidget(new MultiLineLabel(Collections.singletonList(
                I18n.format("gui.estorage_controller.cell_info.tip.1", usedTypes, maxTypes))
        ).setAutoRecalculateSize(false).setScale(.6F).setWidth(width - (10)).setHeight(7).setMargin(10, 0, 0, 0));
        // UsedBytes / MaxBytes
        addWidget(new MultiLineLabel(Collections.singletonList(
                I18n.format("gui.estorage_controller.cell_info.tip.2",
                        NovaEngUtils.formatNumber(usedBytes, 1),
                        NovaEngUtils.formatNumber(maxBytes, 1)
                ))
        ).setAutoRecalculateSize(false).setScale(.6F).setWidth(width - (10)).setHeight(7).setMargin(10, 0, 0, 0));
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    public static int getMaxTypes(final EStorageCellData data) {
        return switch (data.type()) {
            case EMPTY -> 0;
            case ITEM -> 27;
            case FLUID -> 3;
        };
    }

    public static long getMaxBytes(final EStorageCellData data) {
        DriveStorageType type = data.type();
        DriveStorageLevel level = data.level();
        return switch (type) {
            case EMPTY -> 0;
            case ITEM -> switch (level) {
                case EMPTY -> 0;
                case A -> EStorageCellItem.LEVEL_A.getBytes(ItemStack.EMPTY);
                case B -> EStorageCellItem.LEVEL_B.getBytes(ItemStack.EMPTY);
                case C -> EStorageCellItem.LEVEL_C.getBytes(ItemStack.EMPTY);
            };
            case FLUID -> switch (level) {
                case EMPTY -> 0;
                case A -> EStorageCellFluid.LEVEL_A.getBytes(ItemStack.EMPTY);
                case B -> EStorageCellFluid.LEVEL_B.getBytes(ItemStack.EMPTY);
                case C -> EStorageCellFluid.LEVEL_C.getBytes(ItemStack.EMPTY);
            };
        };
    }

}
