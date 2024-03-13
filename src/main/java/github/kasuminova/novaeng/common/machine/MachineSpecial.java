package github.kasuminova.novaeng.common.machine;

import hellfirepvp.modularmachinery.common.machine.DynamicMachine;
import hellfirepvp.modularmachinery.common.tiles.base.TileMultiblockMachineController;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.ProbeMode;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface MachineSpecial {

    default void preInit(final DynamicMachine machine) {
    }

    default void init(final DynamicMachine machine) {
    }

    default void onSyncTick(final TileMultiblockMachineController controller) {
    }

    @SideOnly(Side.CLIENT)
    default void onClientTick(final TileMultiblockMachineController controller) {
    }

    default void onTOPInfo(final ProbeMode probeMode,
                           final IProbeInfo probeInfo,
                           final EntityPlayer player,
                           final IProbeHitData data,
                           final TileMultiblockMachineController controller)
    {
    }

    static IProbeInfo newBox(final IProbeInfo info) {
        return info.horizontal(info.defaultLayoutStyle().borderColor(0x801E90FF));
    }

    static IProbeInfo newVertical(final IProbeInfo probeInfo) {
        return probeInfo.vertical(probeInfo.defaultLayoutStyle().spacing(0));
    }

    ResourceLocation getRegistryName();

}
