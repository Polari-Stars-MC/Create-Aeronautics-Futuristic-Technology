package org.polaris2023.caft.block.multi;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import org.polaris2023.caft.content.energy.FutureEnergyCoreStructureRecipe;
import org.polaris2023.caft.registry.ModBlocks;
import org.polaris2023.caft.registry.ModRecipes;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class FutureEnergyCoreMultiblock {
    public static final int MAX_INTEGRITY = FutureEnergyCoreStructureRecipe.MAX_TOTAL_BLOCKS;

    private FutureEnergyCoreMultiblock() {
    }

    public static ValidationResult validate(Level level, BlockPos controllerPos) {
        if (!level.getBlockState(controllerPos).is(ModBlocks.FUTURE_ENERGY_CORE.get())) {
            return new ValidationResult(0, MAX_INTEGRITY, false, Map.of());
        }

        List<RecipeHolder<FutureEnergyCoreStructureRecipe>> recipes = level.getRecipeManager()
                .getAllRecipesFor(ModRecipes.FUTURE_ENERGY_CORE_STRUCTURE_TYPE.get());
        ValidationResult bestResult = new ValidationResult(1, MAX_INTEGRITY, false, new LinkedHashMap<>());

        for (RecipeHolder<FutureEnergyCoreStructureRecipe> holder : recipes) {
            FutureEnergyCoreStructureRecipe.ValidationResult recipeResult = holder.value().validate(level, controllerPos);
            ValidationResult candidate = new ValidationResult(
                    recipeResult.integrity(),
                    recipeResult.requiredIntegrity(),
                    recipeResult.complete(),
                    recipeResult.missingBlocks()
            );
            if (isBetter(candidate, bestResult)) {
                bestResult = candidate;
            }
        }

        return bestResult;
    }

    private static boolean isBetter(ValidationResult candidate, ValidationResult current) {
        if (candidate.complete() != current.complete()) {
            return candidate.complete();
        }

        double candidateRatio = candidate.requiredIntegrity() <= 0 ? 0.0D : (double) candidate.integrity() / candidate.requiredIntegrity();
        double currentRatio = current.requiredIntegrity() <= 0 ? 0.0D : (double) current.integrity() / current.requiredIntegrity();
        if (Double.compare(candidateRatio, currentRatio) != 0) {
            return candidateRatio > currentRatio;
        }

        if (candidate.integrity() != current.integrity()) {
            return candidate.integrity() > current.integrity();
        }

        return candidate.requiredIntegrity() < current.requiredIntegrity();
    }

    public static boolean isTopOutputFace(Direction direction) {
        return direction == Direction.UP;
    }

    public static boolean isBottomInputFace(Direction direction) {
        return direction == Direction.DOWN;
    }

    public record ValidationResult(int integrity, int requiredIntegrity, boolean complete, Map<BlockPos, FutureEnergyCoreStructureRecipe.StructureRequirement> missingBlocks) {
    }
}
