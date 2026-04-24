package org.polaris2023.caft.compat.jei;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.client.Minecraft;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import org.jetbrains.annotations.Nullable;
import org.polaris2023.caft.CreateAeronauticsFuturisticTechnology;
import org.polaris2023.caft.compat.FutureCoreDisplayHooks;
import org.polaris2023.caft.content.energy.FutureEnergyCoreStructureRecipe;
import org.polaris2023.caft.registry.ModBlocks;
import org.polaris2023.caft.registry.ModRecipes;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

@JeiPlugin
public class FutureEnergyCoreJeiPlugin implements IModPlugin {
    public static final Supplier<RecipeType<RecipeHolder<FutureEnergyCoreStructureRecipe>>> RECIPE_TYPE =
            RecipeType.createFromDeferredVanilla(ModRecipes.FUTURE_ENERGY_CORE_STRUCTURE_TYPE);
    private static final ResourceLocation STRUCTURE_RECIPE_TYPE_ID = CreateAeronauticsFuturisticTechnology.path("future_energy_core_structure");
    private static final Map<ResourceLocation, RecipeHolder<FutureEnergyCoreStructureRecipe>> REGISTERED_RECIPES = new LinkedHashMap<>();
    @Nullable
    private static IJeiRuntime jeiRuntime;

    @Override
    public ResourceLocation getPluginUid() {
        return FutureCoreDisplayHooks.JEI_CATEGORY_ID;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new FutureEnergyCoreJeiCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        List<RecipeHolder<FutureEnergyCoreStructureRecipe>> recipes = collectRecipes(getCurrentRecipeManager(), getCurrentResourceManager());
        if (!recipes.isEmpty()) {
            registration.addRecipes(RECIPE_TYPE.get(), recipes);
            REGISTERED_RECIPES.clear();
            recipes.forEach(recipe -> REGISTERED_RECIPES.put(recipe.id(), recipe));
        }
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(ModBlocks.FUTURE_ENERGY_CORE.get(), RECIPE_TYPE.get());
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
        FutureEnergyCoreJeiPlugin.jeiRuntime = jeiRuntime;
        syncRuntimeRecipes(getCurrentRecipeManager(), getCurrentResourceManager());
    }

    @Override
    public void onRuntimeUnavailable() {
        jeiRuntime = null;
        REGISTERED_RECIPES.clear();
    }

    public static void syncRuntimeRecipes(@Nullable RecipeManager recipeManager) {
        syncRuntimeRecipes(recipeManager, getCurrentResourceManager());
    }

    public static void syncRuntimeRecipes(@Nullable RecipeManager recipeManager, @Nullable ResourceManager resourceManager) {
        if (jeiRuntime == null) {
            return;
        }

        List<RecipeHolder<FutureEnergyCoreStructureRecipe>> recipes = collectRecipes(recipeManager, resourceManager);

        if (recipes.isEmpty()) {
            return;
        }

        List<RecipeHolder<FutureEnergyCoreStructureRecipe>> removedRecipes = REGISTERED_RECIPES.entrySet().stream()
                .filter(entry -> recipes.stream().noneMatch(recipe -> recipe.id().equals(entry.getKey())))
                .map(Map.Entry::getValue)
                .toList();
        if (!removedRecipes.isEmpty()) {
            jeiRuntime.getRecipeManager().hideRecipes(RECIPE_TYPE.get(), removedRecipes);
            removedRecipes.forEach(recipe -> REGISTERED_RECIPES.remove(recipe.id()));
        }

        List<RecipeHolder<FutureEnergyCoreStructureRecipe>> addedRecipes = recipes.stream()
                .filter(recipe -> !REGISTERED_RECIPES.containsKey(recipe.id()))
                .toList();
        if (!addedRecipes.isEmpty()) {
            jeiRuntime.getRecipeManager().addRecipes(RECIPE_TYPE.get(), addedRecipes);
            addedRecipes.forEach(recipe -> REGISTERED_RECIPES.put(recipe.id(), recipe));
        }
    }

    @Nullable
    private static RecipeManager getCurrentRecipeManager() {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level != null) {
            return minecraft.level.getRecipeManager();
        }
        if (minecraft.getConnection() != null) {
            return minecraft.getConnection().getRecipeManager();
        }
        return null;
    }

    @Nullable
    private static ResourceManager getCurrentResourceManager() {
        Minecraft minecraft = Minecraft.getInstance();
        return minecraft.getResourceManager();
    }

    private static List<RecipeHolder<FutureEnergyCoreStructureRecipe>> collectRecipes(@Nullable RecipeManager recipeManager, @Nullable ResourceManager resourceManager) {
        List<RecipeHolder<FutureEnergyCoreStructureRecipe>> recipes = recipeManager != null
                ? recipeManager.getAllRecipesFor(ModRecipes.FUTURE_ENERGY_CORE_STRUCTURE_TYPE.get())
                : List.of();
        if (recipes.isEmpty()) {
            recipes = collectRecipesFromResources(resourceManager);
        }
        if (!recipes.isEmpty()) {
        }
        return recipes;
    }

    private static List<RecipeHolder<FutureEnergyCoreStructureRecipe>> collectRecipesFromResources(@Nullable ResourceManager resourceManager) {
        if (resourceManager == null) {
            return List.of();
        }

        Map<ResourceLocation, RecipeHolder<FutureEnergyCoreStructureRecipe>> recipes = new LinkedHashMap<>();
        Map<ResourceLocation, Resource> resources = resourceManager.listResources("recipes", path -> path.getPath().endsWith(".json"));
        for (Map.Entry<ResourceLocation, Resource> entry : resources.entrySet()) {
            RecipeHolder<FutureEnergyCoreStructureRecipe> recipeHolder = readStructureRecipe(entry.getKey(), entry.getValue());
            if (recipeHolder != null) {
                recipes.put(recipeHolder.id(), recipeHolder);
            }
        }
        return List.copyOf(recipes.values());
    }

    @Nullable
    private static RecipeHolder<FutureEnergyCoreStructureRecipe> readStructureRecipe(ResourceLocation fileId, Resource resource) {
        try (BufferedReader reader = resource.openAsReader()) {
            JsonElement element = JsonParser.parseReader(reader);
            if (!element.isJsonObject()) {
                return null;
            }

            JsonObject json = element.getAsJsonObject();
            if (!json.has("type")) {
                return null;
            }

            ResourceLocation typeId = ResourceLocation.parse(json.get("type").getAsString());
            if (!Objects.equals(typeId, STRUCTURE_RECIPE_TYPE_ID)) {
                return null;
            }

            FutureEnergyCoreStructureRecipe recipe = FutureEnergyCoreStructureRecipe.CODEC.codec()
                    .parse(JsonOps.INSTANCE, json)
                    .getOrThrow(error -> new IllegalStateException("Failed to parse Future Energy Core structure recipe " + fileId + ": " + error));
            return new RecipeHolder<>(toRecipeId(fileId), recipe);
        } catch (RuntimeException | IOException exception) {
            return null;
        }
    }

    private static ResourceLocation toRecipeId(ResourceLocation fileId) {
        String path = fileId.getPath();
        String relativePath = path.substring("recipes/".length(), path.length() - ".json".length());
        return ResourceLocation.fromNamespaceAndPath(fileId.getNamespace(), relativePath);
    }
}
