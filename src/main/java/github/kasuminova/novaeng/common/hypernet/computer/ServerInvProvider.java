package github.kasuminova.novaeng.common.hypernet.computer;

import github.kasuminova.novaeng.common.util.TileItemHandler;

public interface ServerInvProvider {

    TileItemHandler getInvByName(final String invName);

}
