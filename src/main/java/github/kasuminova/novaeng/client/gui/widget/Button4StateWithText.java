package github.kasuminova.novaeng.client.gui.widget;

import github.kasuminova.mmce.client.gui.util.MousePos;
import github.kasuminova.mmce.client.gui.util.RenderPos;
import github.kasuminova.mmce.client.gui.util.RenderSize;
import github.kasuminova.mmce.client.gui.widget.Button4State;
import github.kasuminova.mmce.client.gui.widget.base.WidgetGui;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;

public class Button4StateWithText extends Button4State {
    
    private int textColor = 0xFFFFFF;
    private String content = "";

    @Override
    public void render(final WidgetGui gui, final RenderSize renderSize, final RenderPos renderPos, final MousePos mousePos) {
        if (!isVisible()) {
            return;
        }
        super.render(gui, renderSize, renderPos, mousePos);
        FontRenderer fr = Minecraft.getMinecraft().fontRenderer;
        int stringWidth = fr.getStringWidth(content);
        fr.drawStringWithShadow(content,
                renderPos.posX() + (float) renderSize.width() / 2 - (float) stringWidth / 2,
                renderPos.posY() + (float) renderSize.height() / 2 - (float) fr.FONT_HEIGHT / 2,
                textColor
        );
    }

    public String getContent() {
        return content;
    }

    public Button4StateWithText setContent(final String content) {
        this.content = content;
        return this;
    }

    public int getTextColor() {
        return textColor;
    }

    public Button4StateWithText setTextColor(final int textColor) {
        this.textColor = textColor;
        return this;
    }

}
