package github.kasuminova.novaeng.common.machine;

import hellfirepvp.modularmachinery.ModularMachinery;
import hellfirepvp.modularmachinery.common.integration.crafttweaker.MachineModifier;
import hellfirepvp.modularmachinery.common.integration.crafttweaker.model.GeoMachineModel;
import hellfirepvp.modularmachinery.common.machine.DynamicMachine;
import net.minecraft.util.ResourceLocation;

public class SingularityCore implements MachineSpecial {
    public static final ResourceLocation REGISTRY_NAME = new ResourceLocation(ModularMachinery.MODID, "singularity_core");
    public static final SingularityCore SINGULARITY_CORE = new SingularityCore();

    @Override
    public void init(final DynamicMachine machine) {
        GeoMachineModel.registerGeoMachineModel("singularity_core",
                "modularmachinery:geo/singularity_core.geo.json",
                "modularmachinery:textures/singularity_core.png",
                "modularmachinery:animations/singularity_core.animation.json"
        );
        MachineModifier.setMachineGeoModel("singularity_core", "singularity_core");
    }

    @Override
    public ResourceLocation getRegistryName() {
        return REGISTRY_NAME;
    }

}
