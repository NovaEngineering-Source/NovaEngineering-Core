package github.kasuminova.novaeng.common.container.appeng;

import appeng.api.networking.crafting.ICraftingJob;
import appeng.api.storage.ITerminalHost;
import appeng.container.AEBaseContainer;
import net.minecraft.entity.player.InventoryPlayer;

import java.util.concurrent.Future;

public class ContainerCraftingTree extends AEBaseContainer {

    private Future<ICraftingJob> job = null;

    public ContainerCraftingTree(final InventoryPlayer ip, final ITerminalHost te) {
        super(ip, te);
    }

    public void setJob(final Future<ICraftingJob> job) {
        this.job = job;
    }

    public Future<ICraftingJob> getJob() {
        return job;
    }

}
