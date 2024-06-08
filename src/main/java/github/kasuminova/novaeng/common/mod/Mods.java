package github.kasuminova.novaeng.common.mod;

import net.minecraftforge.fml.common.Loader;

public enum Mods {

    AE2(  "appliedenergistics2"),
    AE2EL("appliedenergistics2") {
        @Override
        public boolean loaded() {
            if (initialized) {
                return loaded;
            }
            initialized = true;
            if (!super.loaded()) {
                return loaded = false;
            }
            try {
                Class.forName("appeng.core.AE2ELCore");
                return loaded = true;
            } catch (Exception e) {
                return loaded = false;
            }
        }
    },
    IC2(   "ic2"),
    MEK(   "mekanism"),
    MEKCEU("mekanism") {
        @Override
        public boolean loaded() {
            if (!MEK.loaded()) {
                return false;
            }
            if (initialized) {
                return loaded;
            }

            try {
                Class.forName("mekanism.common.config.MEKCEConfig");
                initialized = true;
                return loaded = true;
            } catch (Throwable e) {
                return loaded = false;
            }
        }
    },
    UNIDICT("unidict"),
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
