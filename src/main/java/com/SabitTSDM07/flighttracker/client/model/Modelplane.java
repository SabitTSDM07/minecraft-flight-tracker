package com.SabitTSDM07.flighttracker.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.model.geom.PartPose;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

public class Modelplane<T extends Entity> extends EntityModel<T> {
	public static final ModelLayerLocation LAYER_LOCATION =
			new ModelLayerLocation(new ResourceLocation("flighttracker", "plane"), "main");

	private final ModelPart Fuselage;
	private final ModelPart Engine_Left;
	private final ModelPart Tail;

	public Modelplane(ModelPart root) {
		this.Fuselage = root.getChild("Fuselage");
		this.Engine_Left = root.getChild("Engine_Left");
		this.Tail = root.getChild("Tail");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition Fuselage = partdefinition.addOrReplaceChild("Fuselage",
				CubeListBuilder.create()
						.texOffs(0, 0).addBox(-4.0F, -4.0F, -8.0F, 8.0F, 8.0F, 16.0F)
						.texOffs(0, 24).addBox(-4.0F, -4.0F, 24.0F, 8.0F, 8.0F, 16.0F)
						.texOffs(0, 48).addBox(-4.0F, -4.0F, 8.0F, 8.0F, 8.0F, 16.0F),
				PartPose.offset(0.0F, 24.0F, -16.0F));

		Fuselage.addOrReplaceChild("Wing_Right",
				CubeListBuilder.create()
						.texOffs(64, 0).addBox(-30.0F, -1.0F, 10.0F, 11.0F, 2.0F, 10.0F)
						.texOffs(64, 12).addBox(-19.0F, -1.0F, 10.0F, 8.0F, 2.0F, 10.0F),
				PartPose.offset(7.0F, 0.0F, 0.0F));

		Fuselage.addOrReplaceChild("Engine_Right",
				CubeListBuilder.create()
						.texOffs(96, 0).addBox(-19.0F, 2.0F, -1.0F, 5.0F, 5.0F, 5.0F)
						.texOffs(96, 10).addBox(-19.0F, 2.0F, 4.0F, 5.0F, 3.0F, 5.0F),
				PartPose.offset(8.0F, -1.0F, 6.0F));

		Fuselage.addOrReplaceChild("Wing_Left",
				CubeListBuilder.create()
						.texOffs(64, 24).addBox(-27.0F, -1.0F, 10.0F, 8.0F, 2.0F, 10.0F)
						.texOffs(64, 36).addBox(-19.0F, -1.0F, 10.0F, 11.0F, 2.0F, 10.0F),
				PartPose.offset(31.0F, 0.0F, 0.0F));

		partdefinition.addOrReplaceChild("Engine_Left",
				CubeListBuilder.create()
						.texOffs(96, 20).addBox(-1.0F, 2.0F, -1.0F, 5.0F, 5.0F, 5.0F)
						.texOffs(96, 30).addBox(-1.0F, 2.0F, 4.0F, 5.0F, 3.0F, 5.0F),
				PartPose.offset(8.0F, 23.0F, -10.0F));

		partdefinition.addOrReplaceChild("Tail",
				CubeListBuilder.create()
						.texOffs(32, 0).addBox(-12.0F, -4.0F, 2.0F, 8.0F, 2.0F, 6.0F)
						.texOffs(32, 8).addBox(-17.0F, -16.0F, 2.0F, 2.0F, 12.0F, 8.0F)
						.texOffs(32, 28).addBox(-28.0F, -4.0F, 2.0F, 8.0F, 2.0F, 6.0F),
				PartPose.offset(16.0F, 24.0F, 14.0F));

		return LayerDefinition.create(meshdefinition, 128, 64);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount,
						  float ageInTicks, float netHeadYaw, float headPitch) {
		// No animations yet
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer,
							   int packedLight, int packedOverlay,
							   float red, float green, float blue, float alpha) {
		Fuselage.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		Engine_Left.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		Tail.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}
