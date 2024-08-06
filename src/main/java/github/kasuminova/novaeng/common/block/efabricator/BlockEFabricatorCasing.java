package github.kasuminova.novaeng.common.block.efabricator;

import github.kasuminova.novaeng.NovaEngineeringCore;
import net.minecraft.util.ResourceLocation;

public class BlockEFabricatorCasing extends BlockEFabricator {

    public static final BlockEFabricatorCasing INSTANCE = new BlockEFabricatorCasing();

    protected BlockEFabricatorCasing() {
        this.setDefaultState(this.blockState.getBaseState());
        this.setRegistryName(new ResourceLocation(NovaEngineeringCore.MOD_ID, "efabricator_casing"));
        this.setTranslationKey(NovaEngineeringCore.MOD_ID + '.' + "efabricator_casing");
    }

}
