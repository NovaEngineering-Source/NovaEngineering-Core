package github.kasuminova.novaeng.client.handler;

import github.kasuminova.novaeng.common.block.BlockAngel;
import github.kasuminova.novaeng.common.item.ItemBlockAngel;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

@SuppressWarnings("MethodMayBeStatic")
public class BlockAngelRendererHandler {
    public static final BlockAngelRendererHandler INSTANCE = new BlockAngelRendererHandler();

    private BlockAngelRendererHandler() {
    }

    @SubscribeEvent
    public void onRenderLast(final RenderWorldLastEvent ignored) {
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        WorldClient world = Minecraft.getMinecraft().world;
        ItemStack held = player.getHeldItemMainhand();
        if (held.isEmpty() && (held = player.getHeldItemOffhand()).isEmpty() || !(held.getItem() instanceof ItemBlockAngel)) {
            return;
        }
        if (world == null) {
            return;
        }

        BlockPos renderPos = new BlockPos(player.posX, player.posY + player.eyeHeight, player.posZ);
        if (!player.isSneaking()) {
            renderPos = renderPos.offset(player.getAdjustedHorizontalFacing(), 2);
        } else {
            renderPos = renderPos.add(0, -player.height, 0);
        }
        IBlockState block = world.getBlockState(renderPos);
        if (block.getBlock().isReplaceable(world, renderPos)) {
            renderAngelBlockToWorld(renderPos);
        }
    }

    protected static void renderAngelBlockToWorld(final BlockPos renderPos) {
        Entity view = getViewEntity();

        float partialTicks = Minecraft.getMinecraft().getRenderPartialTicks();
        double tx = view.lastTickPosX + ((view.posX - view.lastTickPosX) * partialTicks);
        double ty = view.lastTickPosY + ((view.posY - view.lastTickPosY) * partialTicks);
        double tz = view.lastTickPosZ + ((view.posZ - view.lastTickPosZ) * partialTicks);
        GlStateManager.translate(-tx, -ty, -tz);
        GlStateManager.color(1F, 1F, 1F, 0.5F);
        GlStateManager.disableDepth();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_ONE_MINUS_DST_COLOR, GL11.GL_DST_COLOR);

        BlockRendererDispatcher brd = Minecraft.getMinecraft().getBlockRendererDispatcher();
        Tessellator tes = Tessellator.getInstance();
        BufferBuilder vb = tes.getBuffer();
        VertexFormat blockFormat = DefaultVertexFormats.BLOCK;

        GlStateManager.pushMatrix();
        GlStateManager.translate(renderPos.getX(), renderPos.getY(), renderPos.getZ());
        GlStateManager.translate(0.125, 0.125, 0.125);
        GlStateManager.scale(0.75, 0.75, 0.75);

        vb.begin(GL11.GL_QUADS, blockFormat);
        brd.renderBlock(
                BlockAngel.INSTANCE.getDefaultState(),
                BlockPos.ORIGIN,
                Minecraft.getMinecraft().world,
                vb
        );
        tes.draw();

        GlStateManager.popMatrix();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.enableDepth();
        GlStateManager.color(1F, 1F, 1F, 1F);
    }

    protected static Entity getViewEntity() {
        Minecraft mc = Minecraft.getMinecraft();
        Entity rView = mc.getRenderViewEntity();
        return rView == null ? mc.player : rView;
    }

}
