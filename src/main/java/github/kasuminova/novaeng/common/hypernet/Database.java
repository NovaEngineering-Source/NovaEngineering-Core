package github.kasuminova.novaeng.common.hypernet;

import crafttweaker.annotations.ZenRegister;
import github.kasuminova.mmce.common.event.recipe.FactoryRecipeTickEvent;
import github.kasuminova.novaeng.common.hypernet.research.ResearchCognitionData;
import github.kasuminova.novaeng.common.registry.RegistryHyperNet;
import hellfirepvp.modularmachinery.common.lib.RequirementTypesMM;
import hellfirepvp.modularmachinery.common.machine.IOType;
import hellfirepvp.modularmachinery.common.machine.factory.FactoryRecipeThread;
import hellfirepvp.modularmachinery.common.modifier.RecipeModifier;
import hellfirepvp.modularmachinery.common.tiles.TileFactoryController;
import hellfirepvp.modularmachinery.common.tiles.base.TileMultiblockMachineController;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraftforge.common.util.Constants;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenMethod;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.IntStream;

@ZenRegister
@ZenClass("novaeng.hypernet.Database")
public class Database extends NetNode {
    private final Set<ResearchCognitionData> storedResearchCognition = new HashSet<>();
    private final Object2DoubleOpenHashMap<ResearchCognitionData> researchingCognition = new Object2DoubleOpenHashMap<>();
    private final DatabaseType type;

    public Database(final TileMultiblockMachineController owner) {
        super(owner);
        this.type = RegistryHyperNet.getDatabaseType(
                Objects.requireNonNull(owner.getFoundMachine()).getRegistryName().getPath()
        );

        researchingCognition.defaultReturnValue(-1D);
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

    @Override
    public boolean isWorking() {
        if (!(owner instanceof TileFactoryController)) {
            return false;
        }

        TileFactoryController factory = (TileFactoryController) owner;
        FactoryRecipeThread thread = factory.getCoreRecipeThreads().get(DatabaseType.DATABASE_WORKING_THREAD_NAME);

        return thread != null && thread.isWorking();
    }

    @Override
    public void readNBT(final NBTTagCompound customData) {
        super.readNBT(customData);

        storedResearchCognition.clear();
        if (customData.hasKey("storedResearchCognition")) {
            NBTTagList tagList = customData.getTagList("storedResearchCognition", Constants.NBT.TAG_STRING);
            int bound = tagList.tagCount();
            IntStream.range(0, bound)
                    .mapToObj(tagList::getStringTagAt)
                    .map(RegistryHyperNet::getResearchCognitionData)
                    .filter(Objects::nonNull)
                    .forEach(storedResearchCognition::add);
        }

        researchingCognition.clear();
        if (customData.hasKey("researchingCognition")) {
            NBTTagList researching = customData.getTagList("researchingCognition", Constants.NBT.TAG_COMPOUND);
            int bound = researching.tagCount();
            IntStream.range(0, bound).mapToObj(researching::getCompoundTagAt).forEach(compound -> {
                ResearchCognitionData data = RegistryHyperNet.getResearchCognitionData(compound.getString("researchName"));
                if (data != null) {
                    researchingCognition.put(data, compound.getDouble("progress"));
                }
            });
        }
    }

    @Override
    public void writeNBT() {
        super.writeNBT();
        NBTTagCompound tag = owner.getCustomDataTag();

        NBTTagList stored = new NBTTagList();
        for (final ResearchCognitionData data : storedResearchCognition) {
            NBTTagString tagStr = new NBTTagString(data.getResearchName());
            stored.appendTag(tagStr);
        }

        NBTTagList researching = new NBTTagList();
        researchingCognition.forEach((data, progress) -> {
            NBTTagCompound compound = new NBTTagCompound();
            compound.setString("researchName", data.getResearchName());
            compound.setDouble("progress", progress);
            researching.appendTag(compound);
        });

        tag.setTag("storedResearchCognition", stored);
        tag.setTag("researchingCognition", researching);
    }

    @ZenMethod
    public boolean hasResearchCognition(String researchName) {
        ResearchCognitionData data = RegistryHyperNet.getResearchCognitionData(researchName);
        return data != null && hasResearchCognition(data);
    }

    @ZenMethod
    public boolean hasResearchCognition(ResearchCognitionData research) {
        return isWorking() && research != null && storedResearchCognition.contains(research);
    }

    @ZenMethod
    public double getResearchingData(ResearchCognitionData data) {
        return isWorking() ? researchingCognition.getDouble(data) : -1;
    }

    @ZenMethod
    public boolean writeResearchingData(ResearchCognitionData data, double progress) {
        if (!hasDatabaseSpace(data)) {
            return false;
        }

        researchingCognition.put(data, progress);
        writeNBT();
        return true;
    }

    @ZenMethod
    public boolean hasDatabaseSpace() {
        int maxStoreSize = type.getMaxResearchDataStoreSize();
        int stored = storedResearchCognition.size();
        return maxStoreSize > stored;
    }

    @ZenMethod
    public boolean hasDatabaseSpace(@Nullable ResearchCognitionData toStore) {
        int maxStoreSize = type.getMaxResearchDataStoreSize();
        int stored = storedResearchCognition.size();
        int researching = researchingCognition.size();

        if (researchingCognition.containsKey(toStore)) {
            researching--;
        }

        return maxStoreSize > stored + researching;
    }

    @ZenMethod
    public boolean storeResearchCognitionData(ResearchCognitionData data) {
        if (!hasDatabaseSpace(data)) {
            return false;
        }
        storedResearchCognition.add(data);
        writeNBT();
        return true;
    }

    @ZenGetter("storedResearchCognition")
    public ResearchCognitionData[] getStoredResearchCognitionArr() {
        return storedResearchCognition.toArray(new ResearchCognitionData[0]);
    }

    @ZenMethod
    public double getResearchingCognitionProgress(String researchName) {
        return researchingCognition.getOrDefault(RegistryHyperNet.getResearchCognitionData(researchName), 0D);
    }

    public Object2DoubleOpenHashMap<ResearchCognitionData> getAllResearchingCognition() {
        return researchingCognition;
    }

    public Set<ResearchCognitionData> getStoredResearchCognition() {
        return storedResearchCognition;
    }

    @ZenGetter("type")
    public DatabaseType getType() {
        return type;
    }

    public Status createStatus() {
        return new Status(type, storedResearchCognition.size(), researchingCognition.size());
    }

    @SuppressWarnings("unused")
    public static class Status {
        private final DatabaseType type;
        private final int storedCognition;
        private final int researchingCognition;

        public Status(final DatabaseType type, final int storedCognition, final int researchingCognition) {
            this.type = type;
            this.storedCognition = storedCognition;
            this.researchingCognition = researchingCognition;
        }

        public static Status readFromNBT(final NBTTagCompound tag) {
            if (!tag.hasKey("t") || !tag.hasKey("s") || !tag.hasKey("r")) {
                return null;
            }

            DatabaseType type = RegistryHyperNet.getDatabaseType(tag.getString("t"));
            if (type == null) {
                return null;
            }

            return new Status(type, tag.getInteger("s"), tag.getInteger("r"));
        }

        public NBTTagCompound writeToNBT() {
            NBTTagCompound tag = new NBTTagCompound();
            tag.setString("t", type.getTypeName());
            tag.setInteger("s", storedCognition);
            tag.setInteger("r", researchingCognition);

            return tag;
        }

        public boolean hasDatabaseSpace() {
            return type.getMaxResearchDataStoreSize() > storedCognition + researchingCognition;
        }

        public DatabaseType getType() {
            return type;
        }

        public int getStoredCognition() {
            return storedCognition;
        }

        public int getResearchingCognition() {
            return researchingCognition;
        }
    }
}
