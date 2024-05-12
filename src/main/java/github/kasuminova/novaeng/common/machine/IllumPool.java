package github.kasuminova.novaeng.common.machine;

import github.kasuminova.mmce.common.event.machine.MachineStructureUpdateEvent;
import github.kasuminova.mmce.common.event.recipe.FactoryRecipeFinishEvent;
import github.kasuminova.mmce.common.event.recipe.FactoryRecipeTickEvent;
import github.kasuminova.mmce.common.event.recipe.RecipeCheckEvent;
import github.kasuminova.novaeng.common.crafttweaker.util.NovaEngUtils;
import github.kasuminova.novaeng.common.util.RandomUtils;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.item.crystal.CrystalProperties;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.tile.network.TileCollectorCrystal;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.modularmachinery.ModularMachinery;
import hellfirepvp.modularmachinery.common.crafting.ActiveMachineRecipe;
import hellfirepvp.modularmachinery.common.machine.DynamicMachine;
import hellfirepvp.modularmachinery.common.machine.factory.FactoryRecipeThread;
import hellfirepvp.modularmachinery.common.modifier.MultiBlockModifierReplacement;
import hellfirepvp.modularmachinery.common.modifier.RecipeModifier;
import hellfirepvp.modularmachinery.common.tiles.TileFactoryController;
import hellfirepvp.modularmachinery.common.tiles.base.TileMultiblockMachineController;
import hellfirepvp.modularmachinery.common.util.BlockArray;
import hellfirepvp.modularmachinery.common.util.IBlockStateDescriptor;
import hellfirepvp.modularmachinery.common.util.MiscUtils;
import ink.ikx.mmce.common.utils.StackUtils;
import mcjty.theoneprobe.api.*;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.botania.common.Botania;
import vazkii.botania.common.block.ModBlocks;

import java.awt.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class IllumPool implements MachineSpecial {
    public static final ResourceLocation REGISTRY_NAME = new ResourceLocation(ModularMachinery.MODID, "illum_pool");
    public static final IllumPool ILLUM_POOL = new IllumPool();

    public static final int MAX_MANA_STORE = 10_000_000;
    public static final int MAX_ILLUM_STORE = 10_000;

    /**
     *   x x x
     * x x x x x
     * x x x x x
     * x x x x x
     *   x x x
     */
    public static final List<BlockPos> CATALYST_POS_PRESET = Arrays.asList(
            withXZ(1, 1), withXZ(0, 1), withXZ(-1, 1),
            withXZ(2, 2), withXZ(1, 2), withXZ(0, 2), withXZ(-1, 2), withXZ(-2, 2),
            withXZ(2, 3), withXZ(1, 3), withXZ(0, 3), withXZ(-1, 3), withXZ(-2, 3),
            withXZ(2, 4), withXZ(1, 4), withXZ(0, 4), withXZ(-1, 4), withXZ(-2, 4),
            withXZ(1, 5), withXZ(0, 5), withXZ(-1, 5)
    );
    /**
     * c x x x x x c
     * x x x x x x x
     * x x x x x x x
     * x x x C x x x
     * x x x x x x x
     * x x x x x x x
     * c x x x x x c
     */
    public static final List<BlockPos> CRYSTAL_POS_PRESET = Arrays.asList(
            new BlockPos(3, 2, 6), new BlockPos(-3, 2, 6),
            new BlockPos(3, 2, 0), new BlockPos(-3, 2, 0)
    );
    public static final BlockPos ASTRAL_CRYSTAL_POS = new BlockPos(0, 5, 3);

    public static final String NORMAL_CATALYST = "normal";
    public static final String ALCHEMY_CATALYST = "alchemyCatalyst";
    public static final String CONJURATION_CATALYST = "conjurationCatalyst";
    public static final String DIMENSION_CATALYST = "dimensionCatalyst";
    public static final String STARLIGHT_CATALYST = "starlightCatalyst";

    protected IllumPool() {
    }

    @Override
    public void init(final DynamicMachine machine) {
        FactoryRecipeThread infusionThread = FactoryRecipeThread.createCoreThread("辉光转化术式");
        machine.addCoreThread(infusionThread);
        FactoryRecipeThread manaInputThread = FactoryRecipeThread.createCoreThread("魔力注入术式");
        machine.addCoreThread(manaInputThread);

        Block blockLiquidStarLight = BlocksAS.blockLiquidStarlight;
        Block blockBifrostPerm = ModBlocks.bifrostPerm;
        Block blockAlchemyCatalyst = ModBlocks.alchemyCatalyst;
        Block blockConjurationCatalyst = ModBlocks.conjurationCatalyst;
        Block blockDimensionCatalyst = com.meteor.extrabotany.common.block.ModBlocks.dimensioncatalyst;

        // 普通模式
        machine.getMultiBlockModifiers().add(new MultiBlockModifierReplacement(NORMAL_CATALYST,
                buildModifierReplacementBlockArray(blockBifrostPerm, CATALYST_POS_PRESET.stream().map(pos -> pos.add(0, 1, 0)).collect(Collectors.toList())),
                Collections.emptyList(),
                Collections.singletonList("魔力池上方布满彩虹桥方块可使其激活§a普通模式§f，催化剂模式必须基于此模式。"),
                StackUtils.getStackFromBlockState(blockBifrostPerm.getDefaultState())));
        // 星光模式
        machine.getMultiBlockModifiers().add(new MultiBlockModifierReplacement(STARLIGHT_CATALYST,
                buildModifierReplacementBlockArray(blockLiquidStarLight, CATALYST_POS_PRESET.stream().map(pos -> pos.add(0, 1, 0)).collect(Collectors.toList())),
                Collections.emptyList(),
                Collections.singletonList("魔力池上方倒满星能液可使其激活§b星光模式§f，与催化剂模式冲突。"),
                StackUtils.getStackFromBlockState(blockLiquidStarLight.getDefaultState())));
        // 炼金模式
        machine.getMultiBlockModifiers().add(new MultiBlockModifierReplacement(ALCHEMY_CATALYST,
                buildModifierReplacementBlockArray(blockAlchemyCatalyst, CATALYST_POS_PRESET),
                Collections.emptyList(),
                Collections.singletonList("将彩虹桥方块下方的§c所有方块§f替换为§e炼金催化器§f方块可使其激活§e炼金模式§f。"),
                StackUtils.getStackFromBlockState(blockAlchemyCatalyst.getDefaultState())));
        // 炼造模式
        machine.getMultiBlockModifiers().add(new MultiBlockModifierReplacement(CONJURATION_CATALYST,
                buildModifierReplacementBlockArray(blockConjurationCatalyst, CATALYST_POS_PRESET),
                Collections.emptyList(),
                Collections.singletonList("将彩虹桥方块下方的§c所有方块§f替换为§d炼造催化器§f方块可使其激活§d炼造模式§f。"),
                StackUtils.getStackFromBlockState(blockConjurationCatalyst.getDefaultState())));
        // 次元模式
        machine.getMultiBlockModifiers().add(new MultiBlockModifierReplacement(DIMENSION_CATALYST,
                buildModifierReplacementBlockArray(blockDimensionCatalyst, CATALYST_POS_PRESET),
                Collections.emptyList(),
                Collections.singletonList("将彩虹桥方块下方的§c所有方块§f替换为§5次元催化器§f方块可使其激活§5次元模式§f。"),
                StackUtils.getStackFromBlockState(blockDimensionCatalyst.getDefaultState())));

        machine.addMachineEventHandler(MachineStructureUpdateEvent.class, event -> {
            TileMultiblockMachineController controller = event.getController();
            NBTTagCompound tag = controller.getCustomDataTag();

            String catalyst = null;
            Map<String, List<RecipeModifier>> foundModifiers = controller.getFoundModifiers();
            if (foundModifiers.containsKey(ALCHEMY_CATALYST) && foundModifiers.containsKey(NORMAL_CATALYST)) {
                catalyst = ALCHEMY_CATALYST;
            } else if (foundModifiers.containsKey(CONJURATION_CATALYST) && foundModifiers.containsKey(NORMAL_CATALYST)) {
                catalyst = CONJURATION_CATALYST;
            } else if (foundModifiers.containsKey(DIMENSION_CATALYST) && foundModifiers.containsKey(NORMAL_CATALYST)) {
                catalyst = DIMENSION_CATALYST;
            } else if (foundModifiers.containsKey(STARLIGHT_CATALYST) && !foundModifiers.containsKey(NORMAL_CATALYST)) {
                catalyst = STARLIGHT_CATALYST;
            } else if (foundModifiers.containsKey(NORMAL_CATALYST) && !foundModifiers.containsKey(STARLIGHT_CATALYST)) {
                catalyst = NORMAL_CATALYST;
            }
            if (catalyst != null) {
                tag.setString("currentMode", catalyst);
            } else {
                tag.removeTag("currentMode");
            }

            World world = controller.getWorld();
            EnumFacing facing = controller.getControllerRotation();
            BlockPos astralCrystalPos = MiscUtils.rotateYCCWNorthUntil(ASTRAL_CRYSTAL_POS, facing);
            TileEntity te = world.getTileEntity(astralCrystalPos.add(controller.getPos()));
            if (te instanceof TileCollectorCrystal collectorCrystal) {
                CrystalProperties crystalProperties = collectorCrystal.getCrystalProperties();
                int size = crystalProperties.getSize(); // 大小 0 - 900
                int purity = crystalProperties.getPurity(); // 纯度 0 - 100
                int collectiveCapability = crystalProperties.getCollectiveCapability(); // 抛光 0 - 100
                int illumStored = getIllumStored(tag);

                // 最大并行数
                float parallelism = 256;
                // 大小 500 到达极限，最低值 40%。
                parallelism *= calculateCrystalSizeRatio(size);
                // 纯度 90 到达极限，最低值 20%。
                parallelism *= calculateCrystalPurityRatio(purity);
                // 抛光 40 到达极限，最低值 60%。
                parallelism *= calculateCrystalCollectiveCapabilityRatio(collectiveCapability);

                // 魔力消耗系数
                float manaConsumeRatio = 2;
                // 100 纯度极限，至高降低 60%
                manaConsumeRatio -= calculateCrystalPurityEfficiency(purity);
                // 100 抛光极限，至高降低 90%
                manaConsumeRatio -= calculateCrystalCollectiveCapabilityEfficiency(collectiveCapability);
                // 辉光魔力提供 x0.75 效果
                manaConsumeRatio *= illumStored > 0 ? .75F : 1F;

                tag.setInteger("size", size);
                tag.setInteger("purity", purity);
                tag.setInteger("collectiveCapability", collectiveCapability);
                tag.setInteger("poolExtraParallelism", Math.round(parallelism));
                tag.setFloat("manaConsumeRatio", manaConsumeRatio);
            }
        });
    }

    public static void onRecipeCheck(final RecipeCheckEvent event, final int manaRequired) {
        TileMultiblockMachineController controller = event.getController();
        NBTTagCompound tag = controller.getCustomDataTag();
        int manaStored = getManaStored(tag);
        float manaConsumeRatio = getManaConsumeRatio(tag);

        int required = Math.max(Math.round(manaRequired * manaConsumeRatio), 1);
        if (manaStored < required) {
            event.setFailed("魔力存储不足或缺少物品输入！");
            return;
        }

        int maxParallelism = manaStored / required;
        int extraParallelism = controller.getCustomDataTag().getInteger("poolExtraParallelism");
        event.getActiveRecipe().setMaxParallelism(Math.max(1, Math.min(extraParallelism, maxParallelism)));
    }

    public static void onRecipeTick(final FactoryRecipeTickEvent event, final int manaRequired) {
        ActiveMachineRecipe activeRecipe = event.getActiveRecipe();
        int parallelism = activeRecipe.getParallelism();
        int tickTime = activeRecipe.getTotalTick();

        TileMultiblockMachineController controller = event.getController();
        NBTTagCompound tag = controller.getCustomDataTag();
        int manaStored = getManaStored(tag);
        float manaConsumeRatio = getManaConsumeRatio(tag);
        int required = Math.round(((float) manaRequired / tickTime) * parallelism * manaConsumeRatio);

        if (manaStored < required) {
            event.setFailed(false, "魔力存储不足！");
        } else {
            tag.setInteger("manaStored", manaStored - required);
        }
    }

    public static void onRecipeFinished(final FactoryRecipeFinishEvent event) {
        TileMultiblockMachineController controller = event.getController();
        NBTTagCompound tag = controller.getCustomDataTag();
        int illumStored = getIllumStored(tag);
        if (illumStored > 0) {
            tag.setInteger("illumStored", illumStored - 1);
        }
    }

    public static void onAddManaRecipeCheck(final RecipeCheckEvent event, final int manaToAdd) {
        TileMultiblockMachineController controller = event.getController();
        NBTTagCompound tag = controller.getCustomDataTag();
        int manaStored = getManaStored(tag);
        if (manaStored + manaToAdd > MAX_MANA_STORE) {
            event.setFailed("魔力存储已抵达极限！");
            return;
        }

        int maxParallelism = (MAX_MANA_STORE - manaStored) / manaToAdd;
        event.getActiveRecipe().setMaxParallelism(Math.max(1, maxParallelism));
    }

    public static void onAddManaRecipeTick(final FactoryRecipeTickEvent event, final int manaToAdd) {
        ActiveMachineRecipe activeRecipe = event.getActiveRecipe();
        int parallelism = activeRecipe.getParallelism();
        int tickTime = activeRecipe.getTotalTick();
        int requireToAdd = (manaToAdd / tickTime) * parallelism;

        TileMultiblockMachineController controller = event.getController();
        NBTTagCompound tag = controller.getCustomDataTag();
        int manaStored = getManaStored(tag);
        if (manaStored + requireToAdd > MAX_MANA_STORE) {
            event.setFailed(false, "魔力存储已抵达极限！");
        } else {
            tag.setInteger("manaStored", manaStored + requireToAdd);
        }
    }

    public static void onAddIllumRecipeCheck(final RecipeCheckEvent event, final int illumToAdd) {
        TileMultiblockMachineController controller = event.getController();
        NBTTagCompound tag = controller.getCustomDataTag();
        int illumStored = getIllumStored(tag);
        if (illumStored + illumToAdd > MAX_ILLUM_STORE) {
            event.setFailed("辉光魔力存储已抵达极限！");
            return;
        }

        int maxParallelism = (MAX_ILLUM_STORE - illumStored) / illumToAdd;
        event.getActiveRecipe().setMaxParallelism(Math.max(1, maxParallelism));
    }

    public static void onAddIllumRecipeTick(final FactoryRecipeTickEvent event, final int illumToAdd) {
        ActiveMachineRecipe activeRecipe = event.getActiveRecipe();
        int parallelism = activeRecipe.getParallelism();
        int tickTime = activeRecipe.getTotalTick();
        int requireToAdd = (illumToAdd / tickTime) * parallelism;

        TileMultiblockMachineController controller = event.getController();
        NBTTagCompound tag = controller.getCustomDataTag();
        int illumStored = getIllumStored(tag);
        if (illumStored + requireToAdd > MAX_ILLUM_STORE) {
            event.setFailed(false, "辉光魔力存储已抵达极限！");
        } else {
            tag.setInteger("illumStored", illumStored + requireToAdd);
        }
    }

    public static void checkCatalyst(final RecipeCheckEvent event, final String requiredCatalyst) {

    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onClientTick(final TileMultiblockMachineController controller) {
        BlockPos pos = controller.getPos();
        World world = controller.getWorld();
        EnumFacing facing = controller.getControllerRotation();
        NBTTagCompound tag = controller.getCustomDataTag();
        BlockPos center = pos.add(0, 1, 0).offset(facing.getOpposite(), 3);

        int manaStored = getManaStored(tag);
        int wispFXCount = RandomUtils.nextInt(Math.round(Math.max(((float) manaStored / MAX_MANA_STORE) * 11, 2F)));
        for (int i = 0; i < wispFXCount; i++) {
            float size = RandomUtils.nextFloat() / 3;
            wispFX(controller, center, size, 2);
        }

        if (!controller.isWorking()) {
            return;
        }

        int sparkleFXCount = 3;
        TileFactoryController factory = (TileFactoryController) controller;
        FactoryRecipeThread recipeThread = factory.getCoreRecipeThreads().get("辉光转化术式");
        if (recipeThread != null) {
            sparkleFXCount += Math.min(recipeThread.getActiveRecipe().getParallelism() / 40, 4);
        }
        for (int i = 0; i < sparkleFXCount; i++) {
            float size = .8F + (0.8F * RandomUtils.nextFloat());
            sparkleFX(controller, center, size, 20);
        }

        if (RandomUtils.nextBool()) {
            int illumStored = getIllumStored(tag);
            Color color = (illumStored > 0 && RandomUtils.nextBool()) ? new Color(0xFFD700) : new Color(0x98F5FF);
            Vector3 astralCrystalPos = new Vector3(MiscUtils.rotateYCCWNorthUntil(ASTRAL_CRYSTAL_POS, facing));
            Vector3 crystalPos = new Vector3(MiscUtils.rotateYCCWNorthUntil(CRYSTAL_POS_PRESET.get(RandomUtils.nextInt(CRYSTAL_POS_PRESET.size())), facing));
            AstralSorcery.proxy.fireLightning(world,
                    astralCrystalPos.add(pos.getX() + .5, pos.getY() + .5, pos.getZ() + .5),
                    crystalPos.add(pos.getX() + .5, pos.getY() + .5, pos.getZ() + .5),
                    color
            );
        }
    }

    @Override
    public void onTOPInfo(final ProbeMode probeMode,
                          final IProbeInfo probeInfo,
                          final EntityPlayer player,
                          final IProbeHitData data,
                          final TileMultiblockMachineController controller)
    {
        NBTTagCompound tag = controller.getCustomDataTag();

        IProbeInfo box = probeInfo;

        int manaStored = getManaStored(tag);
        float manaPercent = (float) manaStored / MAX_MANA_STORE;
        // Example: 724K / 10M (7.24%)
        String manaPercentStr = NovaEngUtils.formatNumber(manaStored)
                + " / "
                + NovaEngUtils.formatNumber(MAX_MANA_STORE)
                + " (" + NovaEngUtils.formatFloat(manaPercent * 100, 2) + "%)";
        MachineSpecial.newBox(box).horizontal(probeInfo.defaultLayoutStyle().alignment(ElementAlignment.ALIGN_CENTER))
                .text(TextFormatting.AQUA + "魔力存储:  ")
                .progress(Math.round(manaPercent * 100), 100, probeInfo.defaultProgressStyle()
                        .prefix(manaPercentStr)
                        .filledColor(0xCC63B8FF)
                        .alternateFilledColor(0xCC00BFFF)
                        .borderColor(0xCC4F94CD)
                        .backgroundColor(0xFF000000)
                        .numberFormat(NumberFormat.NONE)
                        .width(120)
                );

        int illumStored = getIllumStored(tag);
        float illumPercent = (float) illumStored / MAX_ILLUM_STORE;
        // Example: 724K / 10M (7.24%)
        String illumPercentStr = NovaEngUtils.formatNumber(illumStored)
                + " / "
                + NovaEngUtils.formatNumber(MAX_ILLUM_STORE)
                + " (" + NovaEngUtils.formatFloat(illumPercent * 100, 2) + "%)";
        MachineSpecial.newBox(box).horizontal(probeInfo.defaultLayoutStyle().alignment(ElementAlignment.ALIGN_CENTER))
                .text(TextFormatting.YELLOW + "辉光魔力存储:  ")
                .progress(Math.round(illumPercent * 100), 100, probeInfo.defaultProgressStyle()
                        .prefix(illumPercentStr)
                        .filledColor(0xCCFFFF00)
                        .alternateFilledColor(0xCCFFD700)
                        .borderColor(0xCCEEC900)
                        .backgroundColor(0xFF000000)
                        .numberFormat(NumberFormat.NONE)
                        .width(80)
                );

        box = MachineSpecial.newBox(probeInfo);
        IProbeInfo left = MachineSpecial.newVertical(box);
        IProbeInfo mid = MachineSpecial.newVertical(box);
        IProbeInfo right = MachineSpecial.newVertical(box);

        left.text(TextFormatting.YELLOW + "辉光转换术式：");
        switch (tag.getString("currentMode")) {
            case ALCHEMY_CATALYST -> mid.text("§e炼金");
            case CONJURATION_CATALYST -> mid.text("§d炼造");
            case DIMENSION_CATALYST -> mid.text("§5次元");
            case STARLIGHT_CATALYST -> mid.text("§b星光");
            case NORMAL_CATALYST -> mid.text("§a普通");
            default -> mid.text("§c未知");
        }
        right.text("");

        int crystalSize = tag.getInteger("size");
        int crystalPurity = tag.getInteger("purity");
        int crystalCollectiveCapability = tag.getInteger("collectiveCapability");
        int parallelism = tag.getInteger("poolExtraParallelism");
        float manaConsumeRatio = getManaConsumeRatio(tag);

        int maxParallelism = 256;
        float crystalSizeRatio = calculateCrystalSizeRatio(crystalSize);
        float crystalPurityRatio = calculateCrystalPurityRatio(crystalPurity);
        float crystalCollectiveCapabilityRatio = calculateCrystalCollectiveCapabilityRatio(crystalCollectiveCapability);

        left.text(TextFormatting.BLUE + "水晶石大小：");
        mid.text(TextFormatting.AQUA + String.valueOf(crystalSize));
        right.text(TextFormatting.DARK_GREEN + String.format(" (%s x %.1f%% = %s)",
                maxParallelism, crystalSizeRatio * 100, maxParallelism = Math.round(maxParallelism * crystalSizeRatio)));

        left.text(TextFormatting.BLUE + "水晶石纯度：");
        mid.text(TextFormatting.AQUA + String.valueOf(crystalPurity));
        right.text(TextFormatting.DARK_GREEN + String.format(" (%s x %.1f%% = %s)",
                maxParallelism, crystalPurityRatio * 100, maxParallelism = Math.round(maxParallelism * crystalPurityRatio)) + ' ' +
                TextFormatting.DARK_AQUA + String.format("(-%.1f%%)", calculateCrystalPurityEfficiency(crystalPurity) * 100));

        left.text(TextFormatting.BLUE + "水晶石抛光：");
        mid.text(TextFormatting.AQUA + String.valueOf(crystalCollectiveCapability));
        right.text(TextFormatting.DARK_GREEN + String.format(" (%s x %.1f%% = %s)",
                maxParallelism, crystalCollectiveCapabilityRatio * 100, Math.round(maxParallelism * crystalCollectiveCapabilityRatio)) + ' ' +
                TextFormatting.DARK_AQUA + String.format("(-%.1f%%)", calculateCrystalCollectiveCapabilityEfficiency(crystalCollectiveCapability) * 100));

        left.text(TextFormatting.BLUE + "最大并行数：");
        mid.text(TextFormatting.DARK_GREEN + String.valueOf(parallelism));
        right.text(TextFormatting.DARK_GREEN + String.format(" (%.1f%%)", (parallelism / 256F) * 100));

        left.text(TextFormatting.BLUE + "魔力消耗系数：");
        mid.text((TextFormatting.DARK_AQUA) + String.format("%.1f%%", manaConsumeRatio * 100F));
        if (illumStored > 0) {
            right.text(TextFormatting.DARK_AQUA + String.format(" (200%% - %.1f%%)", (2 - manaConsumeRatio / 0.75F) * 100F) + TextFormatting.YELLOW + " x 0.75");
        } else {
            right.text(TextFormatting.DARK_AQUA + String.format(" (200%% - %.1f%%)", (2 - manaConsumeRatio) * 100F));
        }
    }

    @Override
    public ResourceLocation getRegistryName() {
        return REGISTRY_NAME;
    }

    protected static int getIllumStored(final NBTTagCompound tag) {
        return tag.getInteger("illumStored");
    }

    protected static int getManaStored(final NBTTagCompound tag) {
        return tag.getInteger("manaStored");
    }

    protected static float getManaConsumeRatio(final NBTTagCompound tag) {
        float ratio = tag.getFloat("manaConsumeRatio");
        return ratio == 0 ? 2 : ratio;
    }

    protected static float calculateCrystalCollectiveCapabilityRatio(final float collectiveCapability) {
        return Math.max(Math.min(collectiveCapability / 35, 1F), .75F);
    }

    protected static float calculateCrystalPurityRatio(final float purity) {
        return Math.max(Math.min(purity / 80, 1F), .5F);
    }

    protected static float calculateCrystalSizeRatio(final float size) {
        return Math.max(Math.min(size / 700, 1F), .2F);
    }

    public static float calculateCrystalPurityEfficiency(final float purity) {
        return (purity / 100) * 0.6F;
    }

    protected static float calculateCrystalCollectiveCapabilityEfficiency(final float collectiveCapability) {
        return (collectiveCapability / 100) * 0.9F;
    }

    protected static BlockArray buildModifierReplacementBlockArray(final Block block, final List<BlockPos> posSet) {
        BlockArray blockArray = new BlockArray();
        IBlockStateDescriptor descriptor = new IBlockStateDescriptor(block);
        posSet.forEach(pos -> blockArray.addBlock(pos, new BlockArray.BlockInformation(Collections.singletonList(descriptor))));
        return blockArray;
    }

    protected static BlockPos withXZ(final int x, final int z) {
        return new BlockPos(x, 0, z);
    }

    @SuppressWarnings("StandardVariableNames")
    protected static void sparkleFX(final TileMultiblockMachineController controller, final BlockPos center, final float size, final int lifeTime) {
        double x = center.getX() + .5 + RandomUtils.nextFloat(4) - 2;
        double y = center.getY() + .5 + RandomUtils.nextFloat(1);
        double z = center.getZ() + .5 + RandomUtils.nextFloat(4) - 2;

        boolean starlightMode = controller.getCustomDataTag().getString("currentMode").equals(STARLIGHT_CATALYST);
        float r = starlightMode ? 0.9F : RandomUtils.nextFloat();
        float g = starlightMode ? 0.9F : RandomUtils.nextFloat();
        float b = starlightMode ? 1F : RandomUtils.nextFloat();

        sparkleFX(x, y, z, r, g, b, size, lifeTime);
    }

    @SuppressWarnings("StandardVariableNames")
    protected static void wispFX(final TileMultiblockMachineController controller, final BlockPos center, final float size, final float lifeTime) {
        double x = center.getX() + .5 + RandomUtils.nextFloat(4) - 2;
        double y = center.getY() + .5 + RandomUtils.nextFloat(1);
        double z = center.getZ() + .5 + RandomUtils.nextFloat(4) - 2;

        boolean starlightMode = controller.getCustomDataTag().getString("currentMode").equals(STARLIGHT_CATALYST);
        float r = starlightMode ? 0.9F : RandomUtils.nextFloat();
        float g = starlightMode ? 0.9F : RandomUtils.nextFloat();
        float b = starlightMode ? 1F : RandomUtils.nextFloat();

        float gravity = RandomUtils.nextFloat() / 15F;

        wispFX(x, y, z, r, g, b, size, gravity, lifeTime);
    }

    protected static void sparkleFX(double x, double y, double z, float r, float g, float b, float size, int lifetime) {
        Botania.proxy.sparkleFX(x, y, z, r, g, b, size, lifetime);
    }

    protected static void wispFX(double x, double y, double z, float r, float g, float b, float size, float gravity, float lifetime) {
        Botania.proxy.wispFX(x, y, z, r, g, b, size, -gravity, lifetime);
    }

}
