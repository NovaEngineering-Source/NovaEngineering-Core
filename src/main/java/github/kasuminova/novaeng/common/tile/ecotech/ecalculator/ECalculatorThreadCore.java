package github.kasuminova.novaeng.common.tile.ecotech.ecalculator;

import appeng.api.util.WorldCoord;
import appeng.me.cluster.implementations.CraftingCPUCluster;
import github.kasuminova.novaeng.common.ecalculator.ECPUCluster;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;

import java.util.List;

public class ECalculatorThreadCore extends ECalculatorPart {

    protected final ObjectArrayList<CraftingCPUCluster> cpus = new ObjectArrayList<>();

    protected int maxClusterCount = 0;
    protected int maxClusterCountHyperThread = 0;

    public ECalculatorThreadCore() {
    }

    public ECalculatorThreadCore(final int maxClusterCount, final int maxClusterCountHyperThread) {
        this.maxClusterCount = maxClusterCount;
        this.maxClusterCountHyperThread = maxClusterCountHyperThread;
    }

    public List<CraftingCPUCluster> getCpus() {
        return cpus;
    }

    public boolean addCPU(final CraftingCPUCluster cluster, final boolean hyperThread) {
        if (cpus.size() >= maxClusterCount) {
            if (!hyperThread || cpus.size() >= maxClusterCount + maxClusterCountHyperThread) {
                return false;
            }
        }

        ((ECPUCluster) (Object) cluster).novaeng_ec$setThreadCore(this);
        cpus.add(cluster);
        return true;
    }

    public boolean canAddCPU() {
        return cpus.size() < (maxClusterCount + maxClusterCountHyperThread);
    }

    @SuppressWarnings("DataFlowIssue")
    public void refreshCPUSource() {
        for (final CraftingCPUCluster cluster : cpus) {
            ECPUCluster eCluster = (ECPUCluster) (Object) cluster;
            // Refresh machine source.
            eCluster.novaeng_ec$setThreadCore(this);
        }
    }

    public void onBlockDestroyed() {
        cpus.clone().forEach(CraftingCPUCluster::destroy);
        cpus.clear();
    }

    public void onCPUDestroyed(final CraftingCPUCluster cluster) {
        cpus.remove(cluster);
        ECalculatorController controller = getController();
        if (controller != null) {
            controller.onClusterChanged();
        }
    }

    public long getUsedStorage() {
        if (cpus.isEmpty()) {
            return 0L;
        }
        return cpus.stream().mapToLong(CraftingCPUCluster::getAvailableStorage).sum();
    }

    @Override
    public void readCustomNBT(final NBTTagCompound compound) {
        super.readCustomNBT(compound);

        if (compound.hasKey("maxClusterCount")) {
            this.maxClusterCount = compound.getByte("maxClusterCount");
        }
        if (compound.hasKey("maxClusterCountHyperThread")) {
            this.maxClusterCountHyperThread = compound.getByte("maxClusterCountHyperThread");
        }

        cpus.clone().forEach(CraftingCPUCluster::destroy);
        cpus.clear();

        final NBTTagList clustersTag = compound.getTagList("clusters", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < clustersTag.tagCount(); i++) {
            NBTTagCompound clusterTag = clustersTag.getCompoundTagAt(i);

            WorldCoord coord = new WorldCoord(getPos());
            CraftingCPUCluster cluster = new CraftingCPUCluster(coord, coord);
            ECPUCluster eCluster = (ECPUCluster) (Object) cluster;

            cluster.readFromNBT(clusterTag);
            eCluster.novaeng_ec$setThreadCore(this);
            eCluster.novaeng_ec$setAvailableStorage(clusterTag.getLong("availableStorage"));
            cpus.add(cluster);
        }
    }

    @Override
    public void writeCustomNBT(final NBTTagCompound compound) {
        super.writeCustomNBT(compound);
        
        compound.setByte("maxClusterCount", (byte) maxClusterCount);
        compound.setByte("maxClusterCountHyperThread", (byte) maxClusterCountHyperThread);

        final NBTTagList clustersTag = new NBTTagList();
        cpus.forEach(cluster -> {
            NBTTagCompound clusterTag = new NBTTagCompound();
            cluster.writeToNBT(clusterTag);
            clusterTag.setLong("availableStorage", cluster.getAvailableStorage());
            clustersTag.appendTag(clusterTag);
        });
        compound.setTag("clusters", clustersTag);
    }

    @Override
    public void onDisassembled() {
        super.onDisassembled();
        markNoUpdateSync();
    }

    @Override
    public void onAssembled() {
        super.onAssembled();
        markNoUpdateSync();
    }

}
