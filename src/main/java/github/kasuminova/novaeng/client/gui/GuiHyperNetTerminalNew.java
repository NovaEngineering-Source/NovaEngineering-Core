package github.kasuminova.novaeng.client.gui;

import github.kasuminova.mmce.client.gui.GuiContainerDynamic;
import github.kasuminova.novaeng.common.container.ContainerHyperNetTerminal;
import github.kasuminova.novaeng.common.tile.TileHyperNetTerminal;
import net.minecraft.entity.player.EntityPlayer;

public class GuiHyperNetTerminalNew extends GuiContainerDynamic<ContainerHyperNetTerminal> {

    protected final TileHyperNetTerminal terminal;

    public GuiHyperNetTerminalNew(TileHyperNetTerminal terminal, EntityPlayer opening) {
        super(new ContainerHyperNetTerminal(terminal, opening));
        this.terminal = terminal;
        this.xSize = 350;
        this.ySize = 250;
    }


}
