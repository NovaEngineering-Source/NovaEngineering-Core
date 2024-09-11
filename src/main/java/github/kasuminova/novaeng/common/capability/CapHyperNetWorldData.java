package github.kasuminova.novaeng.common.capability;

import github.kasuminova.novaeng.NovaEngineeringCore;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CapHyperNetWorldData implements ICapabilitySerializable<NBTTagCompound> {

    public static final ResourceLocation NAME = new ResourceLocation(NovaEngineeringCore.MOD_ID, "hypernet_data");

    @CapabilityInject(CapHyperNetWorldData.class)
    @SuppressWarnings("NonConstantFieldWithUpperCaseName")
    public static Capability<CapHyperNetWorldData> HYPERNET_DATA_CAP = null;

    public static void register() {
        CapabilityManager.INSTANCE.register(CapHyperNetWorldData.class, new Capability.IStorage<>() {
            @Nullable
            @Override
            public NBTBase writeNBT(final Capability<CapHyperNetWorldData> capability, final CapHyperNetWorldData instance, final EnumFacing side) {
                throw new UnsupportedOperationException("Deprecated");
            }

            @Override
            public void readNBT(final Capability<CapHyperNetWorldData> capability, final CapHyperNetWorldData instance, final EnumFacing side, final NBTBase nbt) {
                throw new UnsupportedOperationException("Deprecated");
            }
        }, CapHyperNetWorldData::new);
    }

    @Override
    public boolean hasCapability(@Nonnull final Capability<?> capability, @Nullable final EnumFacing facing) {
        return capability == HYPERNET_DATA_CAP;
    }

    @Nullable
    @Override
    public <T> T getCapability(@Nonnull final Capability<T> capability, @Nullable final EnumFacing facing) {
        return capability == HYPERNET_DATA_CAP ? HYPERNET_DATA_CAP.cast(this) : null;
    }

    @Override
    public NBTTagCompound serializeNBT() {
        return null;
    }

    @Override
    public void deserializeNBT(final NBTTagCompound nbt) {

    }

}
