package org.polaris2023.caft.registry;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.polaris2023.caft.CreateAeronauticsFuturisticTechnology;
import org.polaris2023.caft.content.energy.FutureEnergyCoreStructureRecipe;

public final class ModRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS =
            DeferredRegister.create(BuiltInRegistries.RECIPE_SERIALIZER, CreateAeronauticsFuturisticTechnology.MODID);
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES =
            DeferredRegister.create(Registries.RECIPE_TYPE, CreateAeronauticsFuturisticTechnology.MODID);

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<FutureEnergyCoreStructureRecipe>> FUTURE_ENERGY_CORE_STRUCTURE_SERIALIZER =
            RECIPE_SERIALIZERS.register("future_energy_core_structure", FutureEnergyCoreStructureRecipe.Serializer::new);

    public static final DeferredHolder<RecipeType<?>, RecipeType<FutureEnergyCoreStructureRecipe>> FUTURE_ENERGY_CORE_STRUCTURE_TYPE =
            RECIPE_TYPES.register("future_energy_core_structure",
                    () -> RecipeType.simple(CreateAeronauticsFuturisticTechnology.path("future_energy_core_structure")));

    private ModRecipes() {
    }

    public static void register(IEventBus modEventBus) {
        RECIPE_SERIALIZERS.register(modEventBus);
        RECIPE_TYPES.register(modEventBus);
    }
}
