package org.polaris2023.caft.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;
import org.polaris2023.caft.blockentity.FutureEnergyCoreBlockEntity;

public class FutureEnergyCoreRenderer implements BlockEntityRenderer<FutureEnergyCoreBlockEntity> {
    public FutureEnergyCoreRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(FutureEnergyCoreBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        if (!blockEntity.isActive()) {
            return;
        }

        float integrityRatio = blockEntity.getIntegrity() / 16.0F;
        float pulse = 0.55F + 0.45F * Mth.sin((blockEntity.getAnimationTick() + partialTick) * 0.12F);
        float alpha = Mth.clamp(0.25F + integrityRatio * 0.35F + pulse * 0.15F, 0.15F, 0.95F);
        float size = 0.18F + integrityRatio * 0.12F;

        poseStack.pushPose();
        poseStack.translate(0.5F, 0.5F, 0.5F);
        VertexConsumer consumer = buffer.getBuffer(RenderType.lightning());
        Matrix4f matrix = poseStack.last().pose();
        addCube(consumer, matrix, size, 0.18F, 0.85F, 1.0F, alpha);
        poseStack.popPose();
    }

    private static void addCube(VertexConsumer consumer, Matrix4f matrix, float halfSize, float red, float green, float blue, float alpha) {
        float min = -halfSize;
        float max = halfSize;

        addQuad(consumer, matrix, min, min, max, max, min, max, max, max, max, min, max, max, red, green, blue, alpha);
        addQuad(consumer, matrix, min, min, min, min, max, min, max, max, min, max, min, min, red, green, blue, alpha);
        addQuad(consumer, matrix, min, max, min, min, max, max, max, max, max, max, max, min, red, green, blue, alpha);
        addQuad(consumer, matrix, min, min, min, max, min, min, max, min, max, min, min, max, red, green, blue, alpha);
        addQuad(consumer, matrix, max, min, min, max, max, min, max, max, max, max, min, max, red, green, blue, alpha);
        addQuad(consumer, matrix, min, min, min, min, min, max, min, max, max, min, max, min, red, green, blue, alpha);
    }

    private static void addQuad(VertexConsumer consumer, Matrix4f matrix,
                                float x1, float y1, float z1,
                                float x2, float y2, float z2,
                                float x3, float y3, float z3,
                                float x4, float y4, float z4,
                                float red, float green, float blue, float alpha) {
        consumer.addVertex(matrix, x1, y1, z1).setColor(red, green, blue, alpha).setUv(0.0F, 0.0F).setOverlay(OverlayTexture.NO_OVERLAY).setLight(0x00F000F0).setNormal(0.0F, 1.0F, 0.0F);
        consumer.addVertex(matrix, x2, y2, z2).setColor(red, green, blue, alpha).setUv(0.0F, 1.0F).setOverlay(OverlayTexture.NO_OVERLAY).setLight(0x00F000F0).setNormal(0.0F, 1.0F, 0.0F);
        consumer.addVertex(matrix, x3, y3, z3).setColor(red, green, blue, alpha).setUv(1.0F, 1.0F).setOverlay(OverlayTexture.NO_OVERLAY).setLight(0x00F000F0).setNormal(0.0F, 1.0F, 0.0F);
        consumer.addVertex(matrix, x4, y4, z4).setColor(red, green, blue, alpha).setUv(1.0F, 0.0F).setOverlay(OverlayTexture.NO_OVERLAY).setLight(0x00F000F0).setNormal(0.0F, 1.0F, 0.0F);
    }
}
