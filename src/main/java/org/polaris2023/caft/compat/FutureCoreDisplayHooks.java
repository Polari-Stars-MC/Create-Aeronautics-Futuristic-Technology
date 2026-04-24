package org.polaris2023.caft.compat;

import net.minecraft.resources.ResourceLocation;
import org.polaris2023.caft.CreateAeronauticsFuturisticTechnology;

public final class FutureCoreDisplayHooks {
    public static final ResourceLocation EMI_CATEGORY_ID = CreateAeronauticsFuturisticTechnology.path("future_energy_core");
    public static final ResourceLocation JEI_CATEGORY_ID = CreateAeronauticsFuturisticTechnology.path("future_energy_core");

    private FutureCoreDisplayHooks() {
    }

    public static String getIntegrationNotes() {
        return "Expose controller recipes, structure preview, AF capacity, rotational input, top output, and heat penalty data through JEI or EMI plugins.";
    }
}
