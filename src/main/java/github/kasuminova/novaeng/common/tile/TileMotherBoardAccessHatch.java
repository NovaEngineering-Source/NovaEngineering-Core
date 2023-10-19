package github.kasuminova.novaeng.common.tile;

import hellfirepvp.modularmachinery.common.tiles.base.TileColorableMachineComponent;
import hellfirepvp.modularmachinery.common.util.IOInventory;

import java.util.Arrays;

public class TileMotherBoardAccessHatch extends TileColorableMachineComponent {
    public static final int MAX_COLUMNS = 4;

    private IOInventory inventory;

    public TileMotherBoardAccessHatch() {
    }

    public TileMotherBoardAccessHatch(int maxSlots) {
        int[] slots = new int[maxSlots];
        Arrays.setAll(slots, i -> i);
        inventory = new IOInventory(this, slots, slots);
    }

    public int getFacingIndex(int currentIndex, Facing facing) {
        int currentRow = currentIndex / MAX_COLUMNS;
        int currentColumn = currentIndex % MAX_COLUMNS;
        int maxRows = inventory.getSlots() / MAX_COLUMNS;

        return switch (facing) {
            case LEFT -> currentColumn <= 0 ? -1 : (currentRow * MAX_COLUMNS) + (currentColumn - 1);
            case RIGHT -> {
                int index = (currentRow * MAX_COLUMNS) + (currentColumn + 1);
                if (currentColumn == MAX_COLUMNS - 1 || index >= inventory.getSlots()) yield -1;
                yield index;
            }
            case UP -> currentRow <= 0 ? -1 : ((currentRow - 1) * MAX_COLUMNS) + currentColumn;
            case DOWN -> currentRow >= maxRows - 1 ? -1 : ((currentRow + 1) * MAX_COLUMNS) + currentColumn;
        };
    }

    public enum Facing {
        LEFT, RIGHT, UP, DOWN
    }
}
