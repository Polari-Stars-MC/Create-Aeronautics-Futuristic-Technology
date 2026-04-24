package org.polaris2023.caft.compat.jei;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.Nullable;
import org.polaris2023.caft.compat.FutureCoreDisplayHooks;
import org.polaris2023.caft.content.energy.FutureEnergyCoreStructureRecipe;
import org.polaris2023.caft.registry.ModBlocks;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FutureEnergyCoreJeiCategory implements IRecipeCategory<RecipeHolder<FutureEnergyCoreStructureRecipe>> {
    private static final int WIDTH = 176;
    private static final int HEIGHT = 152;
    private static final int PREVIEW_X = 8;
    private static final int PREVIEW_Y = 8;
    private static final int PREVIEW_SIZE = 116;

    private final IDrawable icon;

    public FutureEnergyCoreJeiCategory(IGuiHelper guiHelper) {
        this.icon = guiHelper.createDrawableItemLike(ModBlocks.FUTURE_ENERGY_CORE.get());
    }

    @Override
    public mezz.jei.api.recipe.RecipeType<RecipeHolder<FutureEnergyCoreStructureRecipe>> getRecipeType() {
        return FutureEnergyCoreJeiPlugin.RECIPE_TYPE.get();
    }

    @Override
    public Component getTitle() {
        return Component.translatable("jei.caft.future_energy_core.title");
    }

    @Override
    public int getWidth() {
        return WIDTH;
    }

    @Override
    public int getHeight() {
        return HEIGHT;
    }

    @Override
    public @Nullable IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<FutureEnergyCoreStructureRecipe> recipeHolder, IFocusGroup focuses) {
        FutureEnergyCoreStructureRecipe recipe = recipeHolder.value();

        builder.addSlot(RecipeIngredientRole.CATALYST, 136, 8)
                .addItemStack(new ItemStack(ModBlocks.FUTURE_ENERGY_CORE.get()))
                .setStandardSlotBackground();

        List<FutureEnergyCoreStructureRecipe.StructureRequirement> requirements = new ArrayList<>();
        for (FutureEnergyCoreStructureRecipe.DisplayCell cell : recipe.getDisplayCells()) {
            if (!requirements.contains(cell.requirement())) {
                requirements.add(cell.requirement());
            }
        }

        int y = 34;
        for (FutureEnergyCoreStructureRecipe.StructureRequirement requirement : requirements) {
            builder.addSlot(RecipeIngredientRole.INPUT, 136, y)
                    .addItemStacks(FutureEnergyCoreJeiStructureWidget.getPreviewStacks(requirement))
                    .setStandardSlotBackground();
            y += 20;
        }
    }

    @Override
    public void createRecipeExtras(IRecipeExtrasBuilder builder, RecipeHolder<FutureEnergyCoreStructureRecipe> recipeHolder, IFocusGroup focuses) {
        FutureEnergyCoreJeiStructureWidget widget = new FutureEnergyCoreJeiStructureWidget(
                PREVIEW_X,
                PREVIEW_Y,
                PREVIEW_SIZE,
                PREVIEW_SIZE,
                recipeHolder.value()
        );
        builder.addWidget(widget);
        builder.addGuiEventListener(widget);
        builder.addText(Component.translatable("jei.caft.future_energy_core.controls.left"), 160, 28)
                .setPosition(8, 128);
        builder.addText(Component.literal(recipeHolder.id().toString()), 160, 20)
                .setPosition(8, 140);
    }

    @Override
    public void draw(RecipeHolder<FutureEnergyCoreStructureRecipe> recipeHolder, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        guiGraphics.renderOutline(PREVIEW_X, PREVIEW_Y, PREVIEW_SIZE, PREVIEW_SIZE, 0xFF5A6A7A);
        guiGraphics.drawString(
                Objects.requireNonNull(guiGraphics.guiWidth() >= 0 ? net.minecraft.client.Minecraft.getInstance().font : null),
                Component.translatable("jei.caft.future_energy_core.preview"),
                PREVIEW_X + 4,
                PREVIEW_Y + 4,
                0xD7E3F4,
                false
        );
    }
}
