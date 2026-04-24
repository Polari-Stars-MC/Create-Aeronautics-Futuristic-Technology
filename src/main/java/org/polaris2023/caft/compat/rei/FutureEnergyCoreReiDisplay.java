package org.polaris2023.caft.compat.rei;

import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.client.Minecraft;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.polaris2023.caft.content.energy.FutureEnergyCoreStructureRecipe;
import org.polaris2023.caft.registry.ModBlocks;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class FutureEnergyCoreReiDisplay extends BasicDisplay {
    public static final CategoryIdentifier<FutureEnergyCoreReiDisplay> CATEGORY =
            CategoryIdentifier.of(org.polaris2023.caft.compat.FutureCoreDisplayHooks.REI_CATEGORY_ID);

    private final RecipeHolder<FutureEnergyCoreStructureRecipe> recipeHolder;

    public FutureEnergyCoreReiDisplay(RecipeHolder<FutureEnergyCoreStructureRecipe> recipeHolder) {
        super(buildInputs(recipeHolder.value()), List.of(EntryIngredient.of(EntryStacks.of(ModBlocks.FUTURE_ENERGY_CORE.get()))), Optional.of(recipeHolder.id()));
        this.recipeHolder = recipeHolder;
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return CATEGORY;
    }

    public RecipeHolder<FutureEnergyCoreStructureRecipe> getRecipeHolder() {
        return this.recipeHolder;
    }

    public FutureEnergyCoreStructureRecipe getRecipe() {
        return this.recipeHolder.value();
    }

    private static List<EntryIngredient> buildInputs(FutureEnergyCoreStructureRecipe recipe) {
        List<EntryIngredient> inputs = new ArrayList<>();
        for (FutureEnergyCoreStructureRecipe.StructureRequirement requirement : getUniqueRequirements(recipe)) {
            inputs.add(asIngredient(requirement));
        }
        return inputs;
    }

    private static List<FutureEnergyCoreStructureRecipe.StructureRequirement> getUniqueRequirements(FutureEnergyCoreStructureRecipe recipe) {
        Set<FutureEnergyCoreStructureRecipe.StructureRequirement> uniqueRequirements = new LinkedHashSet<>();
        for (FutureEnergyCoreStructureRecipe.DisplayCell cell : recipe.getDisplayCells()) {
            uniqueRequirements.add(cell.requirement());
        }
        return List.copyOf(uniqueRequirements);
    }

    private static EntryIngredient asIngredient(FutureEnergyCoreStructureRecipe.StructureRequirement requirement) {
        if (requirement.block().isPresent()) {
            return EntryIngredient.of(EntryStacks.of(requirement.block().get()));
        }

        List<me.shedaniel.rei.api.common.entry.EntryStack<?>> stacks = new ArrayList<>();
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level != null && requirement.tag().isPresent()) {
            Optional<? extends HolderSet.Named<Block>> holders = minecraft.level.registryAccess()
                    .lookupOrThrow(Registries.BLOCK)
                    .get(requirement.tag().orElseThrow());
            holders.ifPresent(named -> named.forEach(holder -> {
                ItemStack stack = holder.value().asItem().getDefaultInstance();
                if (!stack.isEmpty()) {
                    stacks.add(EntryStacks.of(stack));
                }
            }));
        }

        if (stacks.isEmpty()) {
            return EntryIngredient.of(EntryStacks.of(Blocks.BARRIER));
        }
        return EntryIngredient.of(stacks);
    }
}
