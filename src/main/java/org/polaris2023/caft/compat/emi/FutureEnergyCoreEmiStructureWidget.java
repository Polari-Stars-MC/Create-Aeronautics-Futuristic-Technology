package org.polaris2023.caft.compat.emi;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.math.Axis;
import dev.emi.emi.api.widget.Bounds;
import dev.emi.emi.api.widget.Widget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.polaris2023.caft.content.energy.FutureEnergyCoreStructureRecipe;

import java.util.List;
import java.util.Optional;

public class FutureEnergyCoreEmiStructureWidget extends Widget implements InteractiveEmiWidget {
    private static final float PREVIEW_CENTER_X = 0.44F;
    private static final float PREVIEW_CENTER_Y = 0.64F;

    private final Bounds bounds;
    private final FutureEnergyCoreStructureRecipe recipe;
    private float rotationX = -28.0F;
    private float rotationY = 35.0F;
    private float scale = 18.0F;
    private boolean dragging;
    private boolean rightDragging;
    private float panX;
    private float panY;

    public FutureEnergyCoreEmiStructureWidget(int x, int y, int width, int height, FutureEnergyCoreStructureRecipe recipe) {
        this.bounds = new Bounds(x, y, width, height);
        this.recipe = recipe;
    }

    @Override
    public Bounds getBounds() {
        return this.bounds;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        guiGraphics.renderOutline(this.bounds.x(), this.bounds.y(), this.bounds.width(), this.bounds.height(), 0xFF5A6A7A);

        Minecraft minecraft = Minecraft.getInstance();
        List<FutureEnergyCoreStructureRecipe.DisplayCell> displayCells = this.recipe.getDisplayCells();
        if (displayCells.isEmpty()) {
            return;
        }

        float[] previewBounds = getPreviewBounds(displayCells);
        float centerX = (previewBounds[0] + previewBounds[1]) * 0.5F;
        float centerY = (previewBounds[2] + previewBounds[3]) * 0.5F;
        float centerZ = (previewBounds[4] + previewBounds[5]) * 0.5F;

        guiGraphics.flush();
        RenderSystem.enableDepthTest();
        Lighting.setupFor3DItems();
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(
                this.bounds.x() + this.bounds.width() * PREVIEW_CENTER_X + this.panX,
                this.bounds.y() + this.bounds.height() * PREVIEW_CENTER_Y + this.panY,
                150.0F
        );
        guiGraphics.pose().mulPose(Axis.XP.rotationDegrees(this.rotationX));
        guiGraphics.pose().mulPose(Axis.YP.rotationDegrees(this.rotationY));
        guiGraphics.pose().scale(this.scale, -this.scale, this.scale);
        guiGraphics.pose().translate(-centerX, -centerY, -centerZ);

        for (FutureEnergyCoreStructureRecipe.DisplayCell cell : displayCells) {
            guiGraphics.pose().pushPose();
            BlockPos offset = cell.offset();
            guiGraphics.pose().translate(offset.getX() + 0.5F, offset.getY() + 0.5F, offset.getZ() + 0.5F);
            minecraft.getBlockRenderer().renderSingleBlock(
                    resolvePreviewState(cell.requirement()),
                    guiGraphics.pose(),
                    guiGraphics.bufferSource(),
                    LightTexture.FULL_BRIGHT,
                    OverlayTexture.NO_OVERLAY
            );
            guiGraphics.pose().popPose();
        }

        guiGraphics.pose().popPose();
        guiGraphics.flush();
        Lighting.setupForFlatItems();
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int button) {
        if (!this.bounds.contains(mouseX, mouseY)) {
            return false;
        }
        if (button == 0) {
            this.dragging = true;
            return true;
        }
        if (button == 1) {
            this.rightDragging = true;
            return true;
        }
        return false;
    }

    @Override
    public boolean caft$mouseReleased(int mouseX, int mouseY, int button) {
        if (button == 0 && this.dragging) {
            this.dragging = false;
            return true;
        }
        if (button == 1 && this.rightDragging) {
            this.rightDragging = false;
            return true;
        }
        return false;
    }

    @Override
    public boolean caft$mouseDragged(int mouseX, int mouseY, int button, double dragX, double dragY) {
        if (button == 0 && this.dragging) {
            this.rotationY += (float) dragX * 1.2F;
            this.rotationX = Math.clamp(this.rotationX + (float) dragY * 1.2F, -85.0F, 85.0F);
            return true;
        }
        if (button == 1 && this.rightDragging) {
            this.panX += (float) dragX;
            this.panY += (float) dragY;
            return true;
        }
        return false;
    }

    @Override
    public boolean caft$mouseScrolled(int mouseX, int mouseY, double scrollY) {
        if (!this.bounds.contains(mouseX, mouseY)) {
            return false;
        }
        this.scale = Math.clamp(this.scale + (float) scrollY * 1.5F, 8.0F, 40.0F);
        return true;
    }

    @Override
    public boolean caft$isInteracting() {
        return this.dragging || this.rightDragging;
    }

    private static BlockState resolvePreviewState(FutureEnergyCoreStructureRecipe.StructureRequirement requirement) {
        if (requirement.block().isPresent()) {
            return requirement.block().get().defaultBlockState();
        }

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level != null && requirement.tag().isPresent()) {
            Optional<? extends HolderSet.Named<Block>> holders = minecraft.level.registryAccess()
                    .lookupOrThrow(Registries.BLOCK)
                    .get(requirement.tag().orElseThrow());
            Optional<Block> block = holders.flatMap(named -> named.stream()
                    .map(holder -> holder.value())
                    .findFirst());
            if (block.isPresent()) {
                return block.get().defaultBlockState();
            }
        }
        return Blocks.BARRIER.defaultBlockState();
    }

    private static float[] getPreviewBounds(List<FutureEnergyCoreStructureRecipe.DisplayCell> cells) {
        float minX = Float.POSITIVE_INFINITY;
        float maxX = Float.NEGATIVE_INFINITY;
        float minY = Float.POSITIVE_INFINITY;
        float maxY = Float.NEGATIVE_INFINITY;
        float minZ = Float.POSITIVE_INFINITY;
        float maxZ = Float.NEGATIVE_INFINITY;

        for (FutureEnergyCoreStructureRecipe.DisplayCell cell : cells) {
            BlockPos offset = cell.offset();
            minX = Math.min(minX, offset.getX());
            maxX = Math.max(maxX, offset.getX() + 1);
            minY = Math.min(minY, offset.getY());
            maxY = Math.max(maxY, offset.getY() + 1);
            minZ = Math.min(minZ, offset.getZ());
            maxZ = Math.max(maxZ, offset.getZ() + 1);
        }

        return new float[]{minX, maxX, minY, maxY, minZ, maxZ};
    }
}
