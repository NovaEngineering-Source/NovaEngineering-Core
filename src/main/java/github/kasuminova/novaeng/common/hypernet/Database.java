package github.kasuminova.novaeng.common.hypernet;

import crafttweaker.annotations.ZenRegister;
import github.kasuminova.mmce.common.event.recipe.FactoryRecipeTickEvent;
import github.kasuminova.mmce.common.helper.IMachineController;
import github.kasuminova.novaeng.common.hypernet.research.ResearchCognitionData;
import github.kasuminova.novaeng.common.registry.RegistryHyperNet;
import hellfirepvp.modularmachinery.common.lib.RequirementTypesMM;
import hellfirepvp.modularmachinery.common.machine.IOType;
import hellfirepvp.modularmachinery.common.machine.factory.FactoryRecipeThread;
import hellfirepvp.modularmachinery.common.modifier.RecipeModifier;
import hellfirepvp.modularmachinery.common.tiles.base.TileMultiblockMachineController;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraftforge.common.util.Constants;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenMethod;

import java.util.*;

@ZenRegister
@ZenClass("novaeng.hypernet.Database")
public class Database extends NetNode {
    private static final Map<TileMultiblockMachineController, Database> CACHED_DATABASE = new WeakHashMap<>();

    private final Set<String> storedResearchCognition = new HashSet<>();
    private final DatabaseType type;

    public Database(final TileMultiblockMachineController owner, final NBTTagCompound customData) {
        super(owner);
        this.type = RegistryHyperNet.getDatabaseType(
                Objects.requireNonNull(owner.getFoundMachine()).getRegistryName().getPath()
        );

        readNBT(customData);
    }

    @ZenMethod
    public void onWorkingTick(final FactoryRecipeTickEvent event) {
        FactoryRecipeThread thread = event.getRecipeThread();
        float energyUsage = Math.max(1, 1 + storedResearchCognition.size() * 0.1F);

        thread.addModifier("energy", new RecipeModifier(
                RequirementTypesMM.REQUIREMENT_ENERGY,
                IOType.INPUT, energyUsage, RecipeModifier.OPERATION_MULTIPLY,
                false));
    }

    @ZenMethod
    public static Database from(final IMachineController machine) {
        TileMultiblockMachineController ctrl = machine.getController();
        return CACHED_DATABASE.computeIfAbsent(ctrl, v ->
                new Database(ctrl, ctrl.getCustomDataTag()));
    }

    @Override
    public void readNBT(final NBTTagCompound customData) {
        super.readNBT(customData);

        storedResearchCognition.clear();
        if (!customData.hasKey("storedResearchCognition")) {
            return;
        }

        NBTTagList tagList = customData.getTagList("storedResearchCognition", Constants.NBT.TAG_STRING);
        for (int i = 0; i < tagList.tagCount(); i++) {
            String researchName = tagList.getStringTagAt(i);
            if (RegistryHyperNet.getResearchCognitionData(researchName) != null) {
                storedResearchCognition.add(researchName);
            }
        }
    }

    @Override
    public void writeNBT() {
        super.writeNBT();
        NBTTagCompound tag = owner.getCustomDataTag();

        NBTTagList tagList = new NBTTagList();
        for (final String researchName : storedResearchCognition) {
            NBTTagString tagStr = new NBTTagString(researchName);
            tagList.appendTag(tagStr);
        }

        tag.setTag("storedResearchCognition", tagList);
    }

    @ZenMethod
    public boolean hasResearchCognition(String researchName) {
        return owner.isWorking() && storedResearchCognition.contains(researchName);
    }

    @ZenMethod
    public void storeResearchCognitionData(ResearchCognitionData data) {
        storedResearchCognition.add(data.getResearchName());
        writeNBT();
    }

    @ZenGetter("storedResearchCognition")
    public String[] getStoredResearchCognitionArr() {
        return storedResearchCognition.toArray(new String[0]);
    }

    public Set<String> getStoredResearchCognition() {
        return storedResearchCognition;
    }

    @ZenGetter("type")
    public DatabaseType getType() {
        return type;
    }

    public static void clearCache() {
        CACHED_DATABASE.clear();
    }
}
