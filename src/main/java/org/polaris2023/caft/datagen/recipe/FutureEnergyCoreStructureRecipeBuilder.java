package org.polaris2023.caft.datagen.recipe;

import net.minecraft.advancements.Criterion;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;
import org.polaris2023.caft.content.energy.FutureEnergyCoreStructureRecipe;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class FutureEnergyCoreStructureRecipeBuilder {
    private final int width;
    private final int height;
    private final int depth;
    private final String controller;
    private final Map<String, FutureEnergyCoreStructureRecipe.SymbolDefinition> key = new LinkedHashMap<>();
    private final List<List<String>> layers = new ArrayList<>();

    private FutureEnergyCoreStructureRecipeBuilder(int width, int height, int depth, char controller) {
        this.width = width;
        this.height = height;
        this.depth = depth;
        this.controller = String.valueOf(controller);
    }

    public static FutureEnergyCoreStructureRecipeBuilder structure(int width, int height, int depth, char controller) {
        return new FutureEnergyCoreStructureRecipeBuilder(width, height, depth, controller);
    }

    public FutureEnergyCoreStructureRecipeBuilder key(char symbol, Block block) {
        ResourceLocation blockId = ResourceLocation.fromNamespaceAndPath(block.builtInRegistryHolder().key().location().getNamespace(),
                block.builtInRegistryHolder().key().location().getPath());
        this.key.put(String.valueOf(symbol), new FutureEnergyCoreStructureRecipe.SymbolDefinition(blockId));
        return this;
    }

    public FutureEnergyCoreStructureRecipeBuilder key(char symbol, TagKey<Block> tag) {
        this.key.put(String.valueOf(symbol), new FutureEnergyCoreStructureRecipe.SymbolDefinition(java.util.Optional.empty(), java.util.Optional.of(tag.location())));
        return this;
    }

    public FutureEnergyCoreStructureRecipeBuilder layer(String... rows) {
        this.layers.add(List.of(rows));
        return this;
    }

    public FutureEnergyCoreStructureRecipe build() {
        return new FutureEnergyCoreStructureRecipe(this.width, this.height, this.depth, this.controller, this.key, this.layers);
    }
}
