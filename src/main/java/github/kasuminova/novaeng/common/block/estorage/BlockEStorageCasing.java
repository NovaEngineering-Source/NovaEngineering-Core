package github.kasuminova.novaeng.common.block.estorage;

import github.kasuminova.novaeng.NovaEngineeringCore;
import net.minecraft.util.ResourceLocation;

public class BlockEStorageCasing extends BlockEStorage {
    
    public static final BlockEStorageCasing INSTANCE = new BlockEStorageCasing();

    protected BlockEStorageCasing() {
        this.setDefaultState(this.blockState.getBaseState());
        this.setRegistryName(new ResourceLocation(NovaEngineeringCore.MOD_ID, "estorage_casing"));
        this.setTranslationKey(NovaEngineeringCore.MOD_ID + '.' + "estorage_casing");
    }

}
