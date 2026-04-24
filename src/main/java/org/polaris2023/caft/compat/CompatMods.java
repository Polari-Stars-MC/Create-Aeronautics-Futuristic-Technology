package org.polaris2023.caft.compat;

import net.neoforged.fml.ModList;
import org.polaris2023.caft.compat.ponder.ModPonderSetup;

import java.util.Locale;

public enum CompatMods {
    PONDER(ModPonderSetup::registerPonderScenes);
    private final String name;
    private Runnable run;

    CompatMods(String name, Runnable run) {
        this.name = name;
        this.run = run;
    }
    CompatMods(Runnable run) {
        this.name = name().toLowerCase(Locale.ROOT);
        this.run = run;

    }

    public boolean isLoader() {
        return ModList.get().isLoaded(name);
    }

    public void run() {
        run.run();
        run = null;// gc
    }
}
