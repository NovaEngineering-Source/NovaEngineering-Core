package github.kasuminova.novaeng.common.tile.machine;

import github.kasuminova.mmce.common.util.Sides;
import github.kasuminova.novaeng.client.gui.GuiGeocentricDrill;
import github.kasuminova.novaeng.common.machine.GeocentricDrill;
import hellfirepvp.modularmachinery.common.crafting.ActiveMachineRecipe;
import hellfirepvp.modularmachinery.common.crafting.MachineRecipe;
import hellfirepvp.modularmachinery.common.crafting.helper.RecipeCraftingContext;
import hellfirepvp.modularmachinery.common.tiles.TileMachineController;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

import static github.kasuminova.novaeng.common.machine.GeocentricDrill.*;

public class GeocentricDrillController extends TileMachineController {

    protected final Set<String> accelerateOres = new ObjectLinkedOpenHashSet<>();

    protected boolean accelerateOresChanged = false;

    protected float depth = 0;
    protected int targetDepth = MIN_DEPTH;

    public GeocentricDrillController() {
    }

    public GeocentricDrillController(final IBlockState state) {
        super(state);
    }

    public boolean diveOrAscend() {
        if (depth < targetDepth) {
            depth += 0.1F;
            return true;
        } else if (depth > targetDepth) {
            depth = Math.max(depth - 1, targetDepth);
            return false;
        }
        return false;
    }

    public void setTargetDepth(final int targetDepth) {
        this.targetDepth = MathHelper.clamp(targetDepth, MIN_DEPTH, MAX_DEPTH);
        markNoUpdateSync();
    }

    public float getDepth() {
        return depth;
    }

    public int getTargetDepth() {
        return targetDepth;
    }

    @Nullable
    @Override
    public RecipeCraftingContext.CraftingCheckResult checkPreStartResult(final RecipeCraftingContext context) {
        context.getActiveRecipe().setMaxParallelism(MathHelper.clamp((int) (depth / PARALLELISM_PER_DEPTH), 1, MAX_PARALLELISM));
        return super.checkPreStartResult(context);
    }

    public Set<String> getAccelerateOres() {
        return accelerateOres;
    }

    public synchronized void addAccelerateOre(final String accelerateOre) {
        if (GeocentricDrill.GEOCENTRIC_DRILL.getRawOres().containsKey(accelerateOre)) {
            accelerateOres.add(accelerateOre);
            accelerateOresChanged = true;
            markNoUpdateSync();
        }
    }

    public synchronized void removeAccelerateOre(final String accelerateOre) {
        if (accelerateOres.remove(accelerateOre)) {
            accelerateOresChanged = true;
            markNoUpdateSync();
        }
    }

    @Override
    public synchronized RecipeCraftingContext createContext(final ActiveMachineRecipe activeRecipe) {
        MachineRecipe newRecipe = GeocentricDrill.GEOCENTRIC_DRILL.rebuildRecipe(activeRecipe.getRecipe(), accelerateOres);
        ActiveMachineRecipe modifiedRecipe = new ActiveMachineRecipe(newRecipe, activeRecipe.getMaxParallelism());
        modifiedRecipe.setTick(activeRecipe.getTick());
        modifiedRecipe.setTotalTick(activeRecipe.getTotalTick());
        modifiedRecipe.setParallelism(activeRecipe.getParallelism());
        modifiedRecipe.setMaxParallelism(activeRecipe.getMaxParallelism());
        return super.createContext(modifiedRecipe);
    }

    @Override
    public synchronized RecipeCraftingContext.CraftingCheckResult onRestartCheck(final RecipeCraftingContext context) {
        if (accelerateOresChanged) {
            accelerateOresChanged = false;
            RecipeCraftingContext.CraftingCheckResult failure = new RecipeCraftingContext.CraftingCheckResult();
            failure.addError("重新加载配方！");
            return failure;
        }
        return super.onRestartCheck(context);
    }

    @Override
    public void writeCustomNBT(final NBTTagCompound compound) {
        super.writeCustomNBT(compound);
        NBTTagList accelerateOresList = new NBTTagList();
        for (String ore : accelerateOres) {
            accelerateOresList.appendTag(new NBTTagString(ore));
        }
        compound.setTag("accelerateOres", accelerateOresList);
        compound.setFloat("depth", depth);
        compound.setInteger("targetDepth", targetDepth);
    }

    @Override
    public void readCustomNBT(final NBTTagCompound compound) {
        super.readCustomNBT(compound);
        accelerateOres.clear();
        NBTTagList accelerateOresList = compound.getTagList("accelerateOres", Constants.NBT.TAG_STRING);
        for (int i = 0; i < accelerateOresList.tagCount(); i++) {
            accelerateOres.add(accelerateOresList.getStringTagAt(i));
        }
        if (compound.hasKey("depth")) {
            depth = compound.getFloat("depth");
        }
        if (compound.hasKey("targetDepth")) {
            targetDepth = compound.getInteger("targetDepth");
        }
        if (Sides.isRunningOnClient()) {
            notifyClientGUIUpdate();
        }
    }

    @SideOnly(Side.CLIENT)
    protected void notifyClientGUIUpdate() {
        if (Minecraft.getMinecraft().currentScreen instanceof GuiGeocentricDrill geocentricDrill) {
            geocentricDrill.updateData();
        }
    }

}
