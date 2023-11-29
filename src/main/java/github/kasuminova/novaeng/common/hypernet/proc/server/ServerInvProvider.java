package github.kasuminova.novaeng.common.hypernet.proc.server;

import hellfirepvp.modularmachinery.common.util.IOInventory;

public interface ServerInvProvider {

    IOInventory getInvByName(final String name);

}
