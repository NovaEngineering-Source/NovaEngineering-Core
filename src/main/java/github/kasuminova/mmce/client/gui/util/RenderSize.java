package github.kasuminova.mmce.client.gui.util;

import com.github.bsideup.jabel.Desugar;

@Desugar
public record RenderSize(int width, int height) {

    public boolean isLimited() {
        return width != -1 && height != -1;
    }

    public boolean isWidthLimited() {
        return width != -1;
    }

    public boolean isHeightLimited() {
        return height != -1;
    }

    public RenderSize smaller(final RenderSize another) {
        return new RenderSize(Math.min(width, another.width), Math.min(height, another.height));
    }

}