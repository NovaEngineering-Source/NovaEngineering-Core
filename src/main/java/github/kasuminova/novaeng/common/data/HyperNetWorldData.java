package github.kasuminova.novaeng.common.data;

import github.kasuminova.novaeng.common.hypernet.HyperNet;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.storage.WorldSavedData;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class HyperNetWorldData extends WorldSavedData {

    public static final HyperNetWorldData INSTANCE = new HyperNetWorldData();

    private final Map<UUID, HyperNet> networks = new ConcurrentHashMap<>();

    private HyperNetWorldData() {
        super("hypernet_data");
    }

    @Override
    public void readFromNBT(final NBTTagCompound nbt) {
        networks.clear();
        nbt.getKeySet().forEach(uuidStr -> {
            UUID networkOwner = UUID.fromString(uuidStr);
            networks.put(networkOwner, new HyperNet(nbt.getCompoundTag(uuidStr), networkOwner));
        });
    }

    @Nonnull
    @Override
    public NBTTagCompound writeToNBT(@Nonnull final NBTTagCompound compound) {
        networks.forEach((uuid, hyperNet) -> compound.setTag(uuid.toString(), hyperNet.writeToNBT()));
        return compound;
    }

}
