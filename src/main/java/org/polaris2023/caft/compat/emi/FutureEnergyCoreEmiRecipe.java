package org.polaris2023.caft.compat.emi;

import dev.emi.emi.api.recipe.BasicEmiRecipe;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
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

public class FutureEnergyCoreEmiRecipe extends BasicEmiRecipe {
    private static final int WIDTH = 176;
    private static final int HEIGHT = 152;
    private static final int PREVIEW_X = 8;
    private static final int PREVIEW_Y = 8;
    private static final int PREVIEW_SIZE = 116;

    private final RecipeHolder<FutureEnergyCoreStructureRecipe> recipeHolder;

    public FutureEnergyCoreEmiRecipe(RecipeHolder<FutureEnergyCoreStructureRecipe> recipeHolder) {
        super(FutureEnergyCoreEmiPlugin.CATEGORY, recipeHolder.id(), WIDTH, HEIGHT);
        this.recipeHolder = recipeHolder;

        for (FutureEnergyCoreStructureRecipe.StructureRequirement requirement : getUniqueRequirements(recipeHolder.value())) {
            this.inputs.add(asEmiIngredient(requirement));
        }
        this.catalysts.add(EmiStack.of(ModBlocks.FUTURE_ENERGY_CORE.get()));
        this.outputs.add(EmiStack.of(ModBlocks.FUTURE_ENERGY_CORE.get()));
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        widgets.add(new FutureEnergyCoreEmiStructureWidget(PREVIEW_X, PREVIEW_Y, PREVIEW_SIZE, PREVIEW_SIZE, this.recipeHolder.value()));
        widgets.addText(Component.translatable("emi.caft.future_energy_core.preview"), PREVIEW_X + 4, PREVIEW_Y + 4, 0xD7E3F4, false);
        widgets.addText(Component.translatable("emi.caft.future_energy_core.controls.left"), 8, 128, 0xFFFFFF, false);
//        widgets.addText(Component.translatable("emi.caft.future_energy_core.controls.right"), 8, 140, 0xFFFFFF, false);
//        widgets.addText(Component.translatable("emi.caft.future_energy_core.zoom"), 8, 152, 0xFFFFFF, false);
        widgets.addText(Component.literal(this.recipeHolder.id().toString()), 8, 140, 0xB0B7C3, false);
        widgets.addSlot(EmiStack.of(ModBlocks.FUTURE_ENERGY_CORE.get()), 136, 8)
                .catalyst(true)
                .recipeContext(this);

        int y = 34;
        for (FutureEnergyCoreStructureRecipe.StructureRequirement requirement : getUniqueRequirements(this.recipeHolder.value())) {
            widgets.addSlot(asEmiIngredient(requirement), 136, y);
            y += 20;
        }
    }

    private static List<FutureEnergyCoreStructureRecipe.StructureRequirement> getUniqueRequirements(FutureEnergyCoreStructureRecipe recipe) {
        Set<FutureEnergyCoreStructureRecipe.StructureRequirement> uniqueRequirements = new LinkedHashSet<>();
        for (FutureEnergyCoreStructureRecipe.DisplayCell cell : recipe.getDisplayCells()) {
            uniqueRequirements.add(cell.requirement());
        }
        return List.copyOf(uniqueRequirements);
    }

    private static EmiIngredient asEmiIngredient(FutureEnergyCoreStructureRecipe.StructureRequirement requirement) {
        if (requirement.block().isPresent()) {
            return EmiStack.of(requirement.block().get());
        }

        List<EmiIngredient> ingredients = new ArrayList<>();
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level != null && requirement.tag().isPresent()) {
            Optional<? extends HolderSet.Named<Block>> holders = minecraft.level.registryAccess()
                    .lookupOrThrow(Registries.BLOCK)
                    .get(requirement.tag().orElseThrow());
            holders.ifPresent(named -> named.forEach(holder -> {
                ItemStack stack = holder.value().asItem().getDefaultInstance();
                if (!stack.isEmpty()) {
                    ingredients.add(EmiStack.of(stack));
                }
            }));
        }

        if (ingredients.isEmpty()) {
            return EmiStack.of(Blocks.BARRIER);
        }
        return EmiIngredient.of(ingredients);
    }
}
