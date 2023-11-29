package github.kasuminova.mmce.client.gui.util;

import com.github.bsideup.jabel.Desugar;

@Desugar
public record MousePos(int mouseX, int mouseY) {

    public MousePos relativeTo(RenderPos renderPos) {
        return new MousePos(mouseX - renderPos.posX(), mouseY - renderPos.posY());
    }

    public MousePos add(RenderPos renderPos) {
        return new MousePos(mouseX + renderPos.posX(), mouseY + renderPos.posY());
    }

}
