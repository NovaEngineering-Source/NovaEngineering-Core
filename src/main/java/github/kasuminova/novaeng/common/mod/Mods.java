package github.kasuminova.novaeng.common.mod;

import net.minecraftforge.fml.common.Loader;

public enum Mods {

    IC2("ic2"),
    ;

    protected final String modID;
    protected boolean loaded = false;
    protected boolean initialized = false;

    Mods(final String modID) {
        this.modID = modID;
    }

    public boolean loaded() {
        if (!initialized) {
            loaded = Loader.isModLoaded(modID);
            initialized = true;
        }
        return loaded;
    }

}
