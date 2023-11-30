package github.kasuminova.novaeng.common.hypernet.proc.server;

import github.kasuminova.novaeng.common.util.ServerModuleInv;

public interface ServerInvProvider {

    ServerModuleInv getInvByName(final String invName);

}
