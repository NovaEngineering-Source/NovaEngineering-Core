package github.kasuminova.novaeng.client;


import github.kasuminova.mmce.client.renderer.MachineControllerRenderer;
import github.kasuminova.novaeng.client.gui.*;
import github.kasuminova.novaeng.client.handler.BlockAngelRendererHandler;
import github.kasuminova.novaeng.client.handler.ClientEventHandler;
import github.kasuminova.novaeng.client.handler.HyperNetClientEventHandler;
import github.kasuminova.novaeng.client.hitokoto.HitokotoAPI;
import github.kasuminova.novaeng.client.util.TitleUtils;
import github.kasuminova.novaeng.common.CommonProxy;
import github.kasuminova.novaeng.common.command.CommandPacketProfiler;
import github.kasuminova.novaeng.common.command.ExportResearchDataToJson;
import github.kasuminova.novaeng.common.registry.RegistryBlocks;
import github.kasuminova.novaeng.common.registry.RegistryItems;
import github.kasuminova.novaeng.common.tile.TileHyperNetTerminal;
import github.kasuminova.novaeng.common.tile.TileModularServerAssembler;
import github.kasuminova.novaeng.common.tile.efabricator.EFabricatorController;
import github.kasuminova.novaeng.common.tile.efabricator.EFabricatorPatternBus;
import github.kasuminova.novaeng.common.tile.estorage.EStorageController;
import github.kasuminova.novaeng.common.tile.machine.SingularityCore;
import hellfirepvp.modularmachinery.common.base.Mods;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;

@SuppressWarnings("MethodMayBeStatic")
@Mod.EventBusSubscriber(Side.CLIENT)
public class ClientProxy extends CommonProxy {

    static {
        CompletableFuture.runAsync(HitokotoAPI::getRandomHitokoto);
    }

    public ClientProxy() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public void construction() {
        super.construction();

        TitleUtils.setRandomTitle("*Construction*");
    }

    @Override
    public void preInit() {
        super.preInit();
        MinecraftForge.EVENT_BUS.register(HyperNetClientEventHandler.INSTANCE);
        MinecraftForge.EVENT_BUS.register(ClientEventHandler.INSTANCE);
        MinecraftForge.EVENT_BUS.register(BlockAngelRendererHandler.INSTANCE);

        if (Mods.GECKOLIB.isPresent()) {
            ClientRegistry.bindTileEntitySpecialRenderer(SingularityCore.class, MachineControllerRenderer.INSTANCE);
        }

        TitleUtils.setRandomTitle("*PreInit*");
    }

    @Override
    public void init() {
        super.init();

        TitleUtils.setRandomTitle("*Init*");
    }

    @Override
    public void postInit() {
        super.postInit();

        ClientCommandHandler.instance.registerCommand(ExportResearchDataToJson.INSTANCE);
        ClientCommandHandler.instance.registerCommand(CommandPacketProfiler.INSTANCE);

        TitleUtils.setRandomTitle("*PostInit*");
    }

    @Override
    public void loadComplete() {
        super.loadComplete();

        TitleUtils.setRandomTitle();
    }

    @SubscribeEvent
    public void onModelRegister(ModelRegistryEvent event) {
        RegistryBlocks.registerBlockModels();
        RegistryItems.registerItemModels();
    }

    @Nullable
    @Override
    public Object getClientGuiElement(final int ID, final EntityPlayer player, final World world, final int x, final int y, final int z) {
        GuiType type = GuiType.values()[MathHelper.clamp(ID, 0, GuiType.values().length - 1)];
        Class<? extends TileEntity> required = type.requiredTileEntity;
        TileEntity present = null;
        if (required != null) {
            TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
            if (te != null && required.isAssignableFrom(te.getClass())) {
                present = te;
            } else {
                return null;
            }
        }

        return switch (type) {
            case HYPERNET_TERMINAL -> new GuiHyperNetTerminal((TileHyperNetTerminal) present, player);
            case MODULAR_SERVER_ASSEMBLER ->
                    new GuiModularServerAssembler((TileModularServerAssembler) present, player);
            case ESTORAGE_CONTROLLER -> new GuiEStorageController((EStorageController) present, player);
            case SINGULARITY_CORE -> new GuiSingularityCore((SingularityCore) present, player);
            case EFABRICATOR_CONTROLLER -> new GuiEFabricatorController((EFabricatorController) present, player);
            case EFABRICATOR_PATTERN_BUS -> new GuiEFabricatorPatternBus((EFabricatorPatternBus) present, player);
        };
    }

}
