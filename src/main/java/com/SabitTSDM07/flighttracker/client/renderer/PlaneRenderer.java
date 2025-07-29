package com.SabitTSDM07.flighttracker.client.renderer;

import com.SabitTSDM07.flighttracker.client.model.Modelplane;
import com.SabitTSDM07.flighttracker.entity.PlaneEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import com.mojang.math.Axis;

public class PlaneRenderer extends EntityRenderer<PlaneEntity> {

    private final Modelplane<PlaneEntity> model;

    public PlaneRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = new Modelplane<>(context.bakeLayer(Modelplane.LAYER_LOCATION));
        this.shadowRadius = 0.5f;
    }

    @Override
    public void render(PlaneEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();

        // Move model to entity position
        poseStack.translate(0.0F, 1.5F, 0.0F); // Move up to compensate for model center
        poseStack.mulPose(Axis.XP.rotationDegrees(180f)); // Flip model upright

        // Apply yaw (horizontal rotation based on heading)
        poseStack.mulPose(Axis.YP.rotationDegrees(-entity.getYRot()));

        // Render the model
        VertexConsumer vertexConsumer = buffer.getBuffer(this.model.renderType(getTextureLocation(entity)));
        this.model.renderToBuffer(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1);

        poseStack.popPose();
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(PlaneEntity entity) {
        return new ResourceLocation("flighttracker", "textures/entity/plane.png");
    }
}