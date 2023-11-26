package github.kasuminova.mmce.client.gui.util;

public class RenderOffset {
    private int offsetX;
    private int offsetY;

    public RenderOffset(final int offsetX, final int offsetY) {
        this.offsetX = offsetX;
        this.offsetY = offsetY;
    }

    public RenderOffset add(RenderOffset another) {
        return new RenderOffset(offsetX + another.offsetX, offsetY + another.offsetY);
    }

    public int addX(int add) {
        return offsetX += add;
    }

    public int addY(int add) {
        return offsetY += add;
    }

    public int reduceX(int reduce) {
        return offsetX -= reduce;
    }

    public int reduceY(int reduce) {
        return offsetY -= reduce;
    }

    public int getOffsetX() {
        return offsetX;
    }

    public void setOffsetX(final int offsetX) {
        this.offsetX = offsetX;
    }

    public int getOffsetY() {
        return offsetY;
    }

    public void setOffsetY(final int offsetY) {
        this.offsetY = offsetY;
    }
}
