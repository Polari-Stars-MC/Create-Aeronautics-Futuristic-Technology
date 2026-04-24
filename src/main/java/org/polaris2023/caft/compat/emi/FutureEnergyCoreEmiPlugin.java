package org.polaris2023.caft.compat.emi;

import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.Widget;
import dev.emi.emi.screen.RecipeScreen;
import dev.emi.emi.screen.WidgetGroup;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.polaris2023.caft.compat.CompatMods;
import org.polaris2023.caft.compat.FutureCoreDisplayHooks;
import org.polaris2023.caft.content.energy.FutureEnergyCoreStructureRecipe;
import org.polaris2023.caft.registry.ModBlocks;
import org.polaris2023.caft.registry.ModRecipes;

import java.lang.reflect.Field;
import java.util.List;

@EmiEntrypoint
public final class FutureEnergyCoreEmiPlugin implements EmiPlugin {
    public static final EmiRecipeCategory CATEGORY =
            new EmiRecipeCategory(FutureCoreDisplayHooks.EMI_CATEGORY_ID, EmiStack.of(ModBlocks.FUTURE_ENERGY_CORE.get()));

    @Override
    public void register(EmiRegistry registry) {
        registry.addCategory(CATEGORY);
        registry.addWorkstation(CATEGORY, EmiStack.of(ModBlocks.FUTURE_ENERGY_CORE.get()));

        List<RecipeHolder<FutureEnergyCoreStructureRecipe>> recipes = registry.getRecipeManager()
                .getAllRecipesFor(ModRecipes.FUTURE_ENERGY_CORE_STRUCTURE_TYPE.get());
        for (RecipeHolder<FutureEnergyCoreStructureRecipe> recipeHolder : recipes) {
            registry.addRecipe(new FutureEnergyCoreEmiRecipe(recipeHolder));
        }
    }

    public static boolean dispatch(Screen screen, InteractiveDispatch dispatch) {
        if (!(screen instanceof RecipeScreen recipeScreen)) {
            return false;
        }

        List<WidgetGroup> currentPage = getCurrentPage(recipeScreen);
        if (currentPage.isEmpty()) {
            return false;
        }

        for (WidgetGroup group : currentPage) {
            for (Widget widget : group.widgets) {
                if (widget instanceof InteractiveEmiWidget interactiveWidget && dispatch.call(group, widget, interactiveWidget)) {
                    return true;
                }
            }
        }
        return false;
    }

    @FunctionalInterface
    public interface InteractiveDispatch {
        boolean call(WidgetGroup group, Widget widget, InteractiveEmiWidget interactiveWidget);
    }

    private static final Field CURRENT_PAGE_FIELD = findCurrentPageField();

    @SuppressWarnings("unchecked")
    private static List<WidgetGroup> getCurrentPage(RecipeScreen recipeScreen) {
        if (CURRENT_PAGE_FIELD == null) {
            return List.of();
        }
        try {
            Object value = CURRENT_PAGE_FIELD.get(recipeScreen);
            if (value instanceof List<?> list) {
                return (List<WidgetGroup>) list;
            }
        } catch (IllegalAccessException ignored) {
        }
        return List.of();
    }

    private static Field findCurrentPageField() {
        try {
            Field field = RecipeScreen.class.getDeclaredField("currentPage");
            field.setAccessible(true);
            return field;
        } catch (NoSuchFieldException exception) {
            return null;
        }
    }
}
