package org.polaris2023.caft.compat.jei;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.math.Axis;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.inputs.IJeiGuiEventListener;
import mezz.jei.api.gui.widgets.IRecipeWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenPosition;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.polaris2023.caft.content.energy.FutureEnergyCoreStructureRecipe;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FutureEnergyCoreJeiStructureWidget implements IRecipeWidget, IJeiGuiEventListener {
    private static final float PREVIEW_CENTER_X = 0.44F;
    private static final float PREVIEW_CENTER_Y = 0.64F;
    private final int x;
    private final int y;
    private final int width;
    private final int height;
    private final FutureEnergyCoreStructureRecipe recipe;
    private float rotationX = -28.0F;
    private float rotationY = 35.0F;
    private float scale = 18.0F;
    private boolean dragging;
    private boolean rightDragging;
    private float panX;
    private float panY;

    public FutureEnergyCoreJeiStructureWidget(int x, int y, int width, int height, FutureEnergyCoreStructureRecipe recipe) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.recipe = recipe;
    }

    @Override
    public ScreenPosition getPosition() {
        return new ScreenPosition(this.x, this.y);
    }

    @Override
    public ScreenRectangle getArea() {
        return new ScreenRectangle(this.x, this.y, this.width, this.height);
    }

    @Override
    public void drawWidget(GuiGraphics guiGraphics, double mouseX, double mouseY) {
        Minecraft minecraft = Minecraft.getInstance();
        List<FutureEnergyCoreStructureRecipe.DisplayCell> displayCells = this.recipe.getDisplayCells();
        if (displayCells.isEmpty()) {
            return;
        }

        float[] bounds = getBounds(displayCells);
        float centerX = (bounds[0] + bounds[1]) * 0.5F;
        float centerY = (bounds[2] + bounds[3]) * 0.5F;
        float centerZ = (bounds[4] + bounds[5]) * 0.5F;

        guiGraphics.flush();
        RenderSystem.enableDepthTest();
        Lighting.setupFor3DItems();
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(this.width * PREVIEW_CENTER_X + this.panX, this.height * PREVIEW_CENTER_Y + this.panY, 150.0F);
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
    public void getTooltip(ITooltipBuilder tooltip, double mouseX, double mouseY) {
        if (mouseX >= 0 && mouseX < this.width && mouseY >= 0 && mouseY < this.height) {
            tooltip.add(Component.translatable("jei.caft.future_energy_core.controls.left"));
            tooltip.add(Component.translatable("jei.caft.future_energy_core.zoom"));
            tooltip.add(Component.translatable("jei.caft.future_energy_core.controls.right"));
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
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
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0) {
            this.dragging = false;
            return true;
        }
        if (button == 1) {
            this.rightDragging = false;
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
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
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        this.scale = Math.clamp(this.scale + (float) scrollY * 1.5F, 8.0F, 40.0F);
        return true;
    }

    public static List<ItemStack> getPreviewStacks(FutureEnergyCoreStructureRecipe.StructureRequirement requirement) {
        List<ItemStack> stacks = new ArrayList<>();
        if (requirement.block().isPresent()) {
            stacks.add(requirement.block().get().asItem().getDefaultInstance());
            return stacks;
        }

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level != null && requirement.tag().isPresent()) {
            Optional<? extends net.minecraft.core.HolderSet.Named<Block>> holders = minecraft.level.registryAccess()
                    .lookupOrThrow(Registries.BLOCK)
                    .get(requirement.tag().orElseThrow());
            holders.ifPresent(named -> named.forEach(holder -> {
                ItemStack stack = holder.value().asItem().getDefaultInstance();
                if (!stack.isEmpty()) {
                    stacks.add(stack);
                }
            }));
        }

        if (stacks.isEmpty()) {
            stacks.add(Blocks.BARRIER.asItem().getDefaultInstance());
        }
        return stacks;
    }

    private static ItemStack resolvePreviewStack(FutureEnergyCoreStructureRecipe.StructureRequirement requirement) {
        List<ItemStack> stacks = getPreviewStacks(requirement);
        return stacks.isEmpty() ? Blocks.BARRIER.asItem().getDefaultInstance() : stacks.getFirst();
    }

    private static BlockState resolvePreviewState(FutureEnergyCoreStructureRecipe.StructureRequirement requirement) {
        if (requirement.block().isPresent()) {
            return requirement.block().get().defaultBlockState();
        }

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level != null && requirement.tag().isPresent()) {
            Optional<? extends net.minecraft.core.HolderSet.Named<Block>> holders = minecraft.level.registryAccess()
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

    private static float[] getBounds(List<FutureEnergyCoreStructureRecipe.DisplayCell> cells) {
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
