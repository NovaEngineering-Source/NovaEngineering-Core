package github.kasuminova.novaeng.common.block;

import github.kasuminova.novaeng.NovaEngineeringCore;
import github.kasuminova.novaeng.common.CommonProxy;
import github.kasuminova.novaeng.common.machine.GeocentricDrill;
import github.kasuminova.novaeng.common.tile.machine.GeocentricDrillController;
import hellfirepvp.modularmachinery.common.block.BlockController;
import hellfirepvp.modularmachinery.common.machine.DynamicMachine;
import hellfirepvp.modularmachinery.common.machine.MachineRegistry;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.Random;
import java.util.UUID;

public class BlockGeocentricDrillController extends BlockController {

    public static final BlockGeocentricDrillController INSTANCE = new BlockGeocentricDrillController();

    private BlockGeocentricDrillController() {
        setRegistryName(new ResourceLocation(NovaEngineeringCore.MOD_ID, "geocentric_drill_controller"));
        setTranslationKey(NovaEngineeringCore.MOD_ID + '.' + "geocentric_drill_controller");
    }

    public DynamicMachine getParentMachine() {
        return MachineRegistry.getRegistry().getMachine(GeocentricDrill.REGISTRY_NAME);
    }

    @Nonnull
    @Override
    @SideOnly(Side.CLIENT)
    public String getLocalizedName() {
        return I18n.format("tile.novaeng_core.geocentric_drill_controller.name");
    }

    @Override
    public void breakBlock(World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
        Random rand = worldIn.rand;
        TileEntity te = worldIn.getTileEntity(pos);
        if (te instanceof GeocentricDrillController ctrl) {
            UUID ownerUUID = ctrl.getOwner();
            Item dropped = getItemDropped(state, rand, damageDropped(state));
            ItemStack stackCtrl = new ItemStack(dropped, 1);
            if (ownerUUID != null) {
                NBTTagCompound tag = new NBTTagCompound();
                tag.setString("owner", ownerUUID.toString());
                stackCtrl.setTagCompound(tag);
            }
            spawnAsEntity(worldIn, pos, stackCtrl);
        }

        // TODO MM warn.
        super.breakBlock(worldIn, pos, state);
    }

    @Override
    public boolean onBlockActivated(final World worldIn, @Nonnull final BlockPos pos, @Nonnull final IBlockState state, @Nonnull final EntityPlayer playerIn, @Nonnull final EnumHand hand, @Nonnull final EnumFacing facing, final float hitX, final float hitY, final float hitZ) {
        if (!worldIn.isRemote) {
            TileEntity te = worldIn.getTileEntity(pos);
            if (te instanceof GeocentricDrillController) {
                playerIn.openGui(NovaEngineeringCore.MOD_ID, CommonProxy.GuiType.GEOCENTRIC_DRILL_CONTROLLER.ordinal(), worldIn, pos.getX(), pos.getY(), pos.getZ());
            }
        }
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new GeocentricDrillController(state);
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new GeocentricDrillController(getStateFromMeta(meta));
    }

}
