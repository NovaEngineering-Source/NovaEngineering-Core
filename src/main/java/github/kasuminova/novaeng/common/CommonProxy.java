package github.kasuminova.novaeng.common;

import github.kasuminova.novaeng.NovaEngineeringCore;
import github.kasuminova.novaeng.common.adapter.RecipeAdapterExtended;
import github.kasuminova.novaeng.common.container.ContainerHyperNetTerminal;
import github.kasuminova.novaeng.common.container.ContainerModularServerAssembler;
import github.kasuminova.novaeng.common.handler.HyperNetEventHandler;
import github.kasuminova.novaeng.common.handler.HyperNetMachineEventHandler;
import github.kasuminova.novaeng.common.hypernet.HyperNetTerminal;
import github.kasuminova.novaeng.common.hypernet.machine.AssemblyLine;
import github.kasuminova.novaeng.common.hypernet.recipe.HyperNetRecipeManager;
import github.kasuminova.novaeng.common.integration.IntegrationCRT;
import github.kasuminova.novaeng.common.integration.fluxnetworks.IntegrationsFluxNetworks;
import github.kasuminova.novaeng.common.integration.theoneprobe.IntegrationTOP;
import github.kasuminova.novaeng.common.registry.RegistryBlocks;
import github.kasuminova.novaeng.common.registry.RegistryHyperNet;
import github.kasuminova.novaeng.common.registry.RegistryItems;
import github.kasuminova.novaeng.common.tile.TileHyperNetTerminal;
import github.kasuminova.novaeng.common.tile.TileModularServerAssembler;
import hellfirepvp.modularmachinery.ModularMachinery;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import javax.annotation.Nullable;

@SuppressWarnings("MethodMayBeStatic")
public class CommonProxy implements IGuiHandler {

    public CommonProxy() {
        MinecraftForge.EVENT_BUS.register(new RegistryBlocks());
        MinecraftForge.EVENT_BUS.register(new RegistryItems());
    }

    public void construction() {

    }

    public void preInit() {
        NetworkRegistry.INSTANCE.registerGuiHandler(NovaEngineeringCore.MOD_ID, this);

        MinecraftForge.EVENT_BUS.register(IntegrationCRT.INSTANCE);
        MinecraftForge.EVENT_BUS.register(HyperNetEventHandler.INSTANCE);

        ModularMachinery.EVENT_BUS.register(HyperNetMachineEventHandler.INSTANCE);

        if (Loader.isModLoaded("fluxnetworks")) {
            IntegrationsFluxNetworks.preInit();
        }
    }

    public void init() {
        RegistryHyperNet.registerHyperNetNode(
                new ResourceLocation(ModularMachinery.MODID, "hypernet_terminal"),
                HyperNetTerminal.class
        );

        IntegrationTOP.registerProvider();
        RecipeAdapterExtended.registerAdapter();
        HyperNetRecipeManager.registerRecipes();

        AssemblyLine.registerNetNode();
    }

    public void postInit() {

    }

    public void loadComplete() {

    }

    @Nullable
    @Override
    public Object getServerGuiElement(final int ID, final EntityPlayer player, final World world, final int x, final int y, final int z) {
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
            case HYPERNET_TERMINAL -> new ContainerHyperNetTerminal((TileHyperNetTerminal) present, player);
            case MODULAR_SERVER_ASSEMBLER -> new ContainerModularServerAssembler((TileModularServerAssembler) present, player);
        };
    }

    @Nullable
    @Override
    public Object getClientGuiElement(final int ID, final EntityPlayer player, final World world, final int x, final int y, final int z) {
        return null;
    }

    public enum GuiType {

        HYPERNET_TERMINAL(TileHyperNetTerminal.class),
        MODULAR_SERVER_ASSEMBLER(TileModularServerAssembler.class),
        ;

        public final Class<? extends TileEntity> requiredTileEntity;

        GuiType(@Nullable Class<? extends TileEntity> requiredTileEntity) {
            this.requiredTileEntity = requiredTileEntity;
        }
    }
}
