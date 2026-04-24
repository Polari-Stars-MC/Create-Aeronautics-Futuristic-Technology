package org.polaris2023.caft.datagen.provider;

import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import org.polaris2023.caft.content.energy.FutureEnergyCoreStructureRecipe;
import org.polaris2023.caft.datagen.recipe.FutureEnergyCoreStructureRecipeBuilder;
import org.polaris2023.caft.registry.ModBlocks;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

import static org.polaris2023.caft.CreateAeronauticsFuturisticTechnology.MODID;

public final class FutureEnergyCoreStructureRecipeProvider implements DataProvider {
    private final PackOutput.PathProvider pathProvider;

    public FutureEnergyCoreStructureRecipeProvider(PackOutput output) {
        this.pathProvider = output.createPathProvider(PackOutput.Target.DATA_PACK, "recipe");
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        FutureEnergyCoreStructureRecipe recipe = FutureEnergyCoreStructureRecipeBuilder.structure(3, 3, 3, 'C')
                .key('C', ModBlocks.FUTURE_ENERGY_CORE.get())
                .key('E', ModBlocks.ENERGY_CONDUIT.get())
                .key('H', ModBlocks.HEAT_SINK.get())
                .layer(
                        " H ",
                        "H H",
                        " H "
                )
                .layer(
                        "HEH",
                        "ECE",
                        "HEH"
                )
                .layer(
                        " H ",
                        "H H",
                        " H "
                )
                .build();

        Path target = this.pathProvider.json(ResourceLocation.fromNamespaceAndPath(MODID, "future_energy_core"));
        JsonElement json = FutureEnergyCoreStructureRecipe.CODEC.codec()
                .encodeStart(JsonOps.INSTANCE, recipe)
                .getOrThrow(message -> new IllegalStateException("Failed to encode future energy core structure recipe: " + message));
        json.getAsJsonObject().addProperty("type", MODID + ":future_energy_core_structure");
        return DataProvider.saveStable(cache, json, target);
    }

    @Override
    public String getName() {
        return "Future Energy Core Structure Recipes";
    }
}
