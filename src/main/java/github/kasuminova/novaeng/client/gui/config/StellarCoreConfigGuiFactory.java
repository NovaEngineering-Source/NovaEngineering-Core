package github.kasuminova.novaeng.client.gui.config;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.IModGuiFactory;

import java.util.Collections;
import java.util.Set;

public class StellarCoreConfigGuiFactory implements IModGuiFactory {

    @Override
    public void initialize(final Minecraft minecraftInstance) {
        
    }

    @Override
    public boolean hasConfigGui() {
        return false;
    }

    @Override
    public GuiScreen createConfigGui(final GuiScreen parentScreen) {
        return null;
    }

    @Override
    public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {
        return Collections.emptySet();
    }

}
