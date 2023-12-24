package github.kasuminova.novaeng.common.hypernet.server;

import github.kasuminova.novaeng.common.util.ServerModuleInv;

public interface ServerInvProvider {

    ServerModuleInv getInvByName(final String invName);

}
