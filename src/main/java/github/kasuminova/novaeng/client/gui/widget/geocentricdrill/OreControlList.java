package github.kasuminova.novaeng.client.gui.widget.geocentricdrill;

import github.kasuminova.mmce.client.gui.util.TextureProperties;
import github.kasuminova.mmce.client.gui.widget.base.WidgetGui;
import github.kasuminova.mmce.client.gui.widget.container.Row;
import github.kasuminova.mmce.client.gui.widget.impl.preview.IngredientList;
import github.kasuminova.mmce.client.gui.widget.slot.SlotVirtual;
import github.kasuminova.novaeng.client.gui.GuiGeocentricDrill;
import github.kasuminova.novaeng.common.machine.GeocentricDrill;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.minecraft.item.ItemStack;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class OreControlList extends IngredientList {

    public OreControlList() {
        super();
        setMaxStackPerRow(8);
    }

    @Override
    public void initWidget(final WidgetGui gui) {
        super.initWidget(gui);
        scrollbar.setMargin(5, 1, 1, 1);
        scrollbar.setWidthHeight(9, 52);
        scrollbar.getScroll()
                .setMouseDownTexture(198, 0)
                .setHoveredTexture(187, 0)
                .setTexture(176, 0)
                .setUnavailableTexture(209, 0)
                .setTextureLocation(GuiGeocentricDrill.GUI_TEXTURE)
                .setWidthHeight(9, 18);
    }

    public OreControlList setStackList(final Map<String, ItemStack> oreList, final Set<String> accelerateOres) {
        getWidgets().clear();

        Map<String, ItemStack> accelerateOreList = oreList.entrySet().stream()
                .filter(entry -> accelerateOres.contains(entry.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> b, Object2ObjectLinkedOpenHashMap::new));
        Map<String, ItemStack> defaultOreList = oreList.entrySet().stream()
                .filter(entry -> !accelerateOres.contains(entry.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> b, Object2ObjectLinkedOpenHashMap::new));

        Map<String, ItemStack> merged = new Object2ObjectLinkedOpenHashMap<>();
        merged.putAll(accelerateOreList);
        merged.putAll(defaultOreList);

        float chance = 1F / (merged.size() + (accelerateOres.size() * GeocentricDrill.ACCELERATE_MULTIPLIER));
        Row row = new Row();
        row = addSlots(merged, row, merged.size(), 
                entry -> SlotOreControl.of(entry.getKey(), entry.getValue(), chance, accelerateOres.contains(entry.getKey()))
        );

        addWidget(row.setUseScissor(false));
        return this;
    }

    protected Row addSlots(final Map<String, ItemStack> stackList,
                           Row row, final int totalSize,
                           final Function<Map.Entry<String, ItemStack>, SlotVirtual> slotSupplier)
    {
        int stackPerRow = 0;
        int count = 0;
        for (final Map.Entry<String, ItemStack> entry : stackList.entrySet()) {
            row.addWidget(initSlot(slotSupplier.apply(entry)));
            stackPerRow++;
            if (stackPerRow >= maxStackPerRow && count + 1 < totalSize) {
                addWidget(row.setUseScissor(false));
                row = new Row();
                stackPerRow = 0;
            }
            count++;
        }
        return row;
    }

    protected static SlotVirtual initSlot(final SlotVirtual slot) {
        return slot.setSlotTex(TextureProperties.of(GuiGeocentricDrill.GUI_TEXTURE, 7, 130));
    }

}
