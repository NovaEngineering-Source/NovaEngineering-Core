package github.kasuminova.novaeng.common.hypernet.server.frame;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nonnull;

public interface FrameModule {

    void readNBT(@Nonnull NBTTagCompound nbt);

    @Nonnull
    NBTTagCompound writeNBT();

    boolean isUpdateRequired(ItemStack stack);

}