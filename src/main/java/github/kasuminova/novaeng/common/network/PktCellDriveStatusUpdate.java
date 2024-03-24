package github.kasuminova.novaeng.common.network;

import github.kasuminova.novaeng.NovaEngineeringCore;
import github.kasuminova.novaeng.common.block.estorage.BlockEStorageCellDrive;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PktCellDriveStatusUpdate implements IMessage, IMessageHandler<PktCellDriveStatusUpdate, IMessage> {

    private BlockPos pos = null;
    private BlockEStorageCellDrive.Status status = null;

    public PktCellDriveStatusUpdate() {
    }

    public PktCellDriveStatusUpdate(final BlockPos pos, final BlockEStorageCellDrive.Status status) {
        this.pos = pos;
        this.status = status;
    }

    @Override
    public void fromBytes(final ByteBuf buf) {
        try {
            pos = BlockPos.fromLong(buf.readLong());
            status = BlockEStorageCellDrive.Status.values()[buf.readByte()];
        } catch (Exception e) {
            NovaEngineeringCore.log.error("PktCellDriveStatusUpdate read failed.", e);
        }
    }

    @Override
    public void toBytes(final ByteBuf buf) {
        buf.writeLong(pos.toLong());
        buf.writeByte(status.ordinal());
    }

    @Override
    public IMessage onMessage(final PktCellDriveStatusUpdate message, final MessageContext ctx) {
        if (!FMLCommonHandler.instance().getSide().isClient()) {
            return null;
        }
        processPacket(message);
        return null;
    }

    @SideOnly(Side.CLIENT)
    protected static void processPacket(final PktCellDriveStatusUpdate message) {
        BlockPos pos = message.pos;
        BlockEStorageCellDrive.Status status = message.status;
        if (pos == null || status == null) {
            return;
        }

        WorldClient world = Minecraft.getMinecraft().world;
        if (world == null) {
            return;
        }
        IBlockState state = world.getBlockState(pos);
        if (!(state.getBlock() instanceof BlockEStorageCellDrive)) {
            return;
        }
        world.setBlockState(pos, state.withProperty(BlockEStorageCellDrive.STATUS, status));
    }

}
