package github.kasuminova.novaeng.client.renderer;

import github.kasuminova.novaeng.common.block.estorage.prop.EnergyCellStatus;
import github.kasuminova.novaeng.common.tile.estorage.EStorageEnergyCell;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

public class EStorageEnergyCellItemRenderer implements ItemMeshDefinition {
    
    protected final ResourceLocation modelLocation;

    public EStorageEnergyCellItemRenderer(String level) {
        this.modelLocation = new ResourceLocation("novaeng_core", "estorage_energy_cell_" + level);
    }

    @Nonnull
    @Override
    public ModelResourceLocation getModelLocation(@Nonnull final ItemStack stack) {
        double fillFactor = getFillFactor(stack);
        EnergyCellStatus status = EStorageEnergyCell.getStatusFromFillFactor(fillFactor);
        return new ModelResourceLocation(modelLocation, "facing=north," + EnergyCellStatus.STATUS.getName() + "=" + status.getName());
    }

    private static double getFillFactor(ItemStack stack) {
        NBTTagCompound tag = stack.getTagCompound();
        if (tag == null || tag.isEmpty()) {
            return 0;
        }

        double maxEnergyStore = tag.getDouble("maxEnergyStore");
        if (maxEnergyStore <= 0) {
            return 0;
        }
        double energyStored = tag.getDouble("energyStored");
        if (energyStored <= 0) {
            return 0;
        }
        return energyStored / maxEnergyStore;
    }

}
