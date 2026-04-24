package org.polaris2023.caft.compat.ponder;

import net.createmod.ponder.foundation.PonderIndex;

public class ModPonderSetup {
    public static void registerPonderScenes() {
        PonderIndex.addPlugin(new FuturePonderPlugin());
    }
}
