package github.kasuminova.novaeng.common.integration.theoneprobe;

import github.kasuminova.novaeng.NovaEngineeringCore;
import github.kasuminova.novaeng.common.machine.MachineSpecial;
import github.kasuminova.novaeng.common.registry.RegistryMachineSpecial;
import hellfirepvp.modularmachinery.common.machine.DynamicMachine;
import hellfirepvp.modularmachinery.common.tiles.base.TileMultiblockMachineController;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.IProbeInfoProvider;
import mcjty.theoneprobe.api.ProbeMode;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class SpecialMachineInfoProvider implements IProbeInfoProvider {
    public static final SpecialMachineInfoProvider INSTANCE = new SpecialMachineInfoProvider();
    public static final String ID = NovaEngineeringCore.MOD_ID + ':' + "special_machine_info_provider";

    private SpecialMachineInfoProvider() {
    }

    @Override
    public String getID() {
        return ID;
    }

    @Override
    public void addProbeInfo(final ProbeMode probeMode,
                             final IProbeInfo probeInfo,
                             final EntityPlayer player,
                             final World world,
                             final IBlockState blockState,
                             final IProbeHitData data)
    {
        if (!blockState.getBlock().hasTileEntity(blockState)) {
            return;
        }

        TileEntity te = world.getTileEntity(data.getPos());
        if (!(te instanceof final TileMultiblockMachineController ctrl)) {
            return;
        }

        DynamicMachine foundMachine = ctrl.getFoundMachine();
        if (foundMachine == null) {
            return;
        }

        MachineSpecial specialMachine = RegistryMachineSpecial.getSpecialMachine(foundMachine.getRegistryName());
        if (specialMachine != null) {
            specialMachine.onTOPInfo(probeMode, probeInfo, player, data, ctrl);
        }
    }
}
