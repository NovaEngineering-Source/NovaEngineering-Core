package github.kasuminova.novaeng.common.hypernet.server;

import github.kasuminova.novaeng.common.util.TileItemHandler;

public interface ServerInvProvider {

    TileItemHandler getInvByName(final String invName);

}
