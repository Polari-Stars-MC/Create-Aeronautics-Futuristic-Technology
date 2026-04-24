package org.polaris2023.caft.compat.rei;

import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.forge.REIPluginClient;
import org.polaris2023.caft.content.energy.FutureEnergyCoreStructureRecipe;
import org.polaris2023.caft.registry.ModBlocks;
import org.polaris2023.caft.registry.ModRecipes;

@REIPluginClient
public class FutureEnergyCoreReiClientPlugin implements REIClientPlugin {
    private static final FutureEnergyCoreReiCategory CATEGORY = new FutureEnergyCoreReiCategory();

    @Override
    public void registerCategories(CategoryRegistry registry) {
        registry.add(CATEGORY);
        registry.addWorkstations(FutureEnergyCoreReiDisplay.CATEGORY, EntryStacks.of(ModBlocks.FUTURE_ENERGY_CORE.get()));
    }

    @Override
    public void registerDisplays(DisplayRegistry registry) {
        registry.registerRecipeFiller(
                FutureEnergyCoreStructureRecipe.class,
                ModRecipes.FUTURE_ENERGY_CORE_STRUCTURE_TYPE.get(),
                FutureEnergyCoreReiDisplay::new
        );
    }
}
