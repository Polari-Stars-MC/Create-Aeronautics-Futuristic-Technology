package org.polaris2023.caft.compat.rei;

import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.polaris2023.caft.registry.ModBlocks;

import java.util.ArrayList;
import java.util.List;

import static me.shedaniel.rei.api.common.util.EntryStacks.of;

public class FutureEnergyCoreReiCategory implements DisplayCategory<FutureEnergyCoreReiDisplay> {
    private static final int WIDTH = 176;
    private static final int HEIGHT = 152;
    private static final int PREVIEW_X = 8;
    private static final int PREVIEW_Y = 8;
    private static final int PREVIEW_SIZE = 116;

    @Override
    public CategoryIdentifier<? extends FutureEnergyCoreReiDisplay> getCategoryIdentifier() {
        return FutureEnergyCoreReiDisplay.CATEGORY;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("rei.caft.future_energy_core.title");
    }

    @Override
    public Renderer getIcon() {
        return of(ModBlocks.FUTURE_ENERGY_CORE.get());
    }

    @Override
    public List<Widget> setupDisplay(FutureEnergyCoreReiDisplay display, Rectangle bounds) {
        List<Widget> widgets = new ArrayList<>();
        widgets.add(Widgets.createRecipeBase(bounds));

        Rectangle previewBounds = new Rectangle(bounds.x + PREVIEW_X, bounds.y + PREVIEW_Y, PREVIEW_SIZE, PREVIEW_SIZE);
        widgets.add(Widgets.createDrawableWidget((guiGraphics, mouseX, mouseY, delta) -> {
            guiGraphics.renderOutline(previewBounds.x, previewBounds.y, previewBounds.width, previewBounds.height, 0xFF5A6A7A);
            guiGraphics.drawString(
                    Minecraft.getInstance().font,
                    Component.translatable("rei.caft.future_energy_core.preview"),
                    previewBounds.x + 4,
                    previewBounds.y + 4,
                    0xD7E3F4,
                    false
            );
            guiGraphics.drawString(
                    Minecraft.getInstance().font,
                    Component.translatable("rei.caft.future_energy_core.controls.left"),
                    bounds.x + 8,
                    bounds.y + 128,
                    0xFFFFFF,
                    false
            );
            guiGraphics.drawString(
                    Minecraft.getInstance().font,
                    Component.literal(display.getRecipeHolder().id().toString()),
                    bounds.x + 8,
                    bounds.y + 140,
                    0xB0B7C3,
                    false
            );
        }));
        widgets.add(new FutureEnergyCoreReiStructureWidget(previewBounds, display.getRecipe()));

        widgets.add(Widgets.createSlot(new Point(bounds.x + 136, bounds.y + 8))
                .entry(of(ModBlocks.FUTURE_ENERGY_CORE.get()))
                .markOutput());

        int y = bounds.y + 34;
        for (var ingredient : display.getInputEntries()) {
            widgets.add(Widgets.createSlot(new Point(bounds.x + 136, y))
                    .entries(ingredient)
                    .markInput());
            y += 20;
        }
        return widgets;
    }

    @Override
    public int getDisplayHeight() {
        return HEIGHT;
    }

    @Override
    public int getDisplayWidth(FutureEnergyCoreReiDisplay display) {
        return WIDTH;
    }
}
