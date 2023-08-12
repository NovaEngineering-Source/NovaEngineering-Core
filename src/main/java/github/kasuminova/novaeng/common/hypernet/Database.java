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
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraftforge.common.util.Constants;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenMethod;

import java.util.*;
import java.util.stream.IntStream;

@ZenRegister
@ZenClass("novaeng.hypernet.Database")
public class Database extends NetNode {
    private static final Map<TileMultiblockMachineController, Database> CACHED_DATABASE = new WeakHashMap<>();

    private final Set<String> storedResearchCognition = new HashSet<>();
    private final Object2DoubleOpenHashMap<String> researchingCognition = new Object2DoubleOpenHashMap<>();
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
        if (customData.hasKey("storedResearchCognition")) {
            NBTTagList tagList = customData.getTagList("storedResearchCognition", Constants.NBT.TAG_STRING);
            IntStream.range(0, tagList.tagCount())
                    .mapToObj(tagList::getStringTagAt)
                    .filter(researchName -> RegistryHyperNet.getResearchCognitionData(researchName) != null)
                    .forEach(storedResearchCognition::add);
        }

        researchingCognition.clear();
        if (customData.hasKey("researchingCognition")) {
            NBTTagList researching = customData.getTagList("researchingCognition", Constants.NBT.TAG_COMPOUND);
            IntStream.range(0, researching.tagCount())
                    .mapToObj(researching::getCompoundTagAt)
                    .filter(compound -> RegistryHyperNet.getResearchCognitionData(compound.getString("researchName")) != null)
                    .forEach(compound -> researchingCognition.put(compound.getString("researchName"), compound.getDouble("progress")));
        }
    }

    @Override
    public void writeNBT() {
        super.writeNBT();
        NBTTagCompound tag = owner.getCustomDataTag();

        NBTTagList stored = new NBTTagList();
        for (final String researchName : storedResearchCognition) {
            NBTTagString tagStr = new NBTTagString(researchName);
            stored.appendTag(tagStr);
        }

        NBTTagList researching = new NBTTagList();
        researchingCognition.forEach((name, progress) -> {
            NBTTagCompound compound = new NBTTagCompound();
            compound.setString("researchName", name);
            compound.setDouble("progress", progress);
            researching.appendTag(compound);
        });

        tag.setTag("storedResearchCognition", stored);
        tag.setTag("researchingCognition", researching);
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

    @ZenMethod
    public double getResearchingCognitionProgress(String researchName) {
        return researchingCognition.getOrDefault(researchName, 0D);
    }

    public Object2DoubleOpenHashMap<String> getAllResearchingCognition() {
        return researchingCognition;
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
