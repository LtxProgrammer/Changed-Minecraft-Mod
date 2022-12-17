package net.ltxprogrammer.changed.client.renderer.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.entity.beast.LatexWatermelonCat;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.world.entity.HumanoidArm;
import org.jetbrains.annotations.NotNull;

public class LatexWatermelonCatModel extends LatexHumanoidModel<LatexWatermelonCat> implements LatexHumanoidModelInterface {
        // This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
        public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(Changed.modResource("latex_watermelon_cat"), "main");
        private final ModelPart RightLeg;
        private final ModelPart LeftLeg;
        private final ModelPart RightArm;
        private final ModelPart LeftArm;
        private final ModelPart Head;
        private final ModelPart Torso;
        private final ModelPart Tail;
        private final LatexHumanoidModelController controller;

        public LatexWatermelonCatModel(ModelPart root) {
            super(root);
            this.RightLeg = root.getChild("RightLeg");
            this.LeftLeg = root.getChild("LeftLeg");
            this.Head = root.getChild("Head");
            this.Torso = root.getChild("Torso");
            this.Tail = Torso.getChild("Tail");
            this.RightArm = root.getChild("RightArm");
            this.LeftArm = root.getChild("LeftArm");
            controller = LatexHumanoidModelController.Builder.of(this, Head, Torso, Tail, RightArm, LeftArm, RightLeg, LeftLeg).build();
        }

        public static LayerDefinition createBodyLayer() {
            MeshDefinition meshdefinition = new MeshDefinition();
            PartDefinition partdefinition = meshdefinition.getRoot();

            PartDefinition RightLeg = partdefinition.addOrReplaceChild("RightLeg", CubeListBuilder.create().texOffs(49, 55).addBox(-2.25F, 12.0F, -2.75F, 4.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.5F, 10.0F, 0.0F));

            PartDefinition Toe_r1 = RightLeg.addOrReplaceChild("Toe_r1", CubeListBuilder.create().texOffs(0, 42).addBox(-2.75F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                    .texOffs(4, 42).addBox(-1.25F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                    .texOffs(42, 42).addBox(-4.25F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.0F, 13.0F, -3.25F, -0.6109F, 0.0F, 0.0F));

            PartDefinition Toe_r2 = RightLeg.addOrReplaceChild("Toe_r2", CubeListBuilder.create().texOffs(12, 42).addBox(-1.25F, -3.0F, -1.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                    .texOffs(16, 42).addBox(-2.75F, -3.0F, -1.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                    .texOffs(42, 30).addBox(-4.25F, -3.0F, -1.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.0F, 16.0F, -1.75F, 0.0F, 0.0F, 0.0F));

            PartDefinition RightLowerLeg_r1 = RightLeg.addOrReplaceChild("RightLowerLeg_r1", CubeListBuilder.create().texOffs(26, 49).addBox(-2.25F, -7.5F, -1.0F, 4.0F, 6.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 14.25F, -1.25F, -0.5236F, 0.0F, 0.0F));

            PartDefinition RightMidLeg_r1 = RightLeg.addOrReplaceChild("RightMidLeg_r1", CubeListBuilder.create().texOffs(44, 12).addBox(-2.25F, 0.0F, -2.0F, 4.0F, 7.0F, 4.0F, new CubeDeformation(-0.025F)), PartPose.offsetAndRotation(0.0F, 4.25F, -1.5F, 0.5672F, 0.0F, 0.0F));

            PartDefinition RightUpperLeg_r1 = RightLeg.addOrReplaceChild("RightUpperLeg_r1", CubeListBuilder.create().texOffs(0, 47).addBox(-2.25F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.2F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.2182F, 0.0F, 0.0F));

            PartDefinition LeftLeg = partdefinition.addOrReplaceChild("LeftLeg", CubeListBuilder.create().texOffs(54, 23).addBox(-1.75F, 12.0F, -2.75F, 4.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(2.5F, 10.0F, 0.0F));

            PartDefinition Toe_r3 = LeftLeg.addOrReplaceChild("Toe_r3", CubeListBuilder.create().texOffs(16, 38).addBox(-2.25F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                    .texOffs(0, 40).addBox(-0.75F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                    .texOffs(41, 28).addBox(-3.75F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.0F, 13.0F, -3.25F, -0.6109F, 0.0F, 0.0F));

            PartDefinition Toe_r4 = LeftLeg.addOrReplaceChild("Toe_r4", CubeListBuilder.create().texOffs(4, 40).addBox(-0.75F, -3.0F, -1.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                    .texOffs(12, 40).addBox(-2.25F, -3.0F, -1.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                    .texOffs(16, 40).addBox(-3.75F, -3.0F, -1.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.0F, 16.0F, -1.75F, 0.0F, 0.0F, 0.0F));

            PartDefinition LeftLowerLeg_r1 = LeftLeg.addOrReplaceChild("LeftLowerLeg_r1", CubeListBuilder.create().texOffs(48, 0).addBox(-1.75F, -7.5F, -1.0F, 4.0F, 6.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 14.25F, -1.25F, -0.5236F, 0.0F, 0.0F));

            PartDefinition LeftMidLeg_r1 = LeftLeg.addOrReplaceChild("LeftMidLeg_r1", CubeListBuilder.create().texOffs(42, 28).addBox(-1.75F, 0.0F, -2.0F, 4.0F, 7.0F, 4.0F, new CubeDeformation(-0.025F)), PartPose.offsetAndRotation(0.0F, 4.25F, -1.5F, 0.5672F, 0.0F, 0.0F));

            PartDefinition LeftUpperLeg_r1 = LeftLeg.addOrReplaceChild("LeftUpperLeg_r1", CubeListBuilder.create().texOffs(46, 45).addBox(-1.75F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.2F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.2182F, 0.0F, 0.0F));

            PartDefinition Torso = partdefinition.addOrReplaceChild("Torso", CubeListBuilder.create().texOffs(0, 29).addBox(-4.0F, -25.0F, -2.0F, 8.0F, 5.0F, 4.0F, new CubeDeformation(0.0F))
                    .texOffs(0, 16).addBox(-4.0F, -21.0F, -2.0F, 8.0F, 8.0F, 4.0F, new CubeDeformation(-0.3F))
                    .texOffs(20, 29).addBox(-3.75F, -23.75F, -2.5F, 3.0F, 3.0F, 1.0F, new CubeDeformation(0.4F))
                    .texOffs(24, 4).addBox(0.75F, -23.75F, -2.5F, 3.0F, 3.0F, 1.0F, new CubeDeformation(0.4F))
                    .texOffs(36, 29).addBox(-1.0F, -23.75F, -2.5F, 2.0F, 3.0F, 1.0F, new CubeDeformation(0.2F)), PartPose.offset(0.0F, 24.0F, 0.0F));

            PartDefinition Tail = Torso.addOrReplaceChild("Tail", CubeListBuilder.create(), PartPose.offset(0.0F, -14.0F, 0.0F));

            PartDefinition Base_r1 = Tail.addOrReplaceChild("Base_r1", CubeListBuilder.create().texOffs(0, 57).addBox(-1.5F, 0.1914F, -1.4483F, 3.0F, 6.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 2.0F, 6.0F, 1.4835F, 0.0F, 0.0F));

            PartDefinition Base_r2 = Tail.addOrReplaceChild("Base_r2", CubeListBuilder.create().texOffs(37, 55).addBox(-1.5F, 0.0F, -1.0F, 3.0F, 7.0F, 3.0F, new CubeDeformation(0.001F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 1.1781F, 0.0F, 0.0F));

            PartDefinition Head = partdefinition.addOrReplaceChild("Head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -34.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F))
                    .texOffs(32, 19).addBox(-1.5F, -27.0F, -5.0F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                    .texOffs(24, 0).addBox(-2.0F, -29.0F, -6.0F, 4.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 25.0F, 0.0F));

            PartDefinition Snout_r1 = Head.addOrReplaceChild("Snout_r1", CubeListBuilder.create().texOffs(24, 45).addBox(-1.0F, -29.625F, -0.95F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.5F, 0.0F, 0.0F, 0.1745F, 0.0F, 0.0F));

            PartDefinition Hair = Head.addOrReplaceChild("Hair", CubeListBuilder.create().texOffs(16, 20).addBox(-2.0F, -35.0F, -4.0F, 4.0F, 1.0F, 8.0F, new CubeDeformation(0.0F))
                    .texOffs(42, 39).addBox(-4.0F, -26.75F, 2.0F, 8.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
                    .texOffs(12, 39).addBox(-4.0F, -34.75F, -4.0F, 2.0F, 1.0F, 8.0F, new CubeDeformation(0.0F))
                    .texOffs(0, 38).addBox(2.0F, -34.75F, -4.0F, 2.0F, 1.0F, 8.0F, new CubeDeformation(0.0F))
                    .texOffs(32, 37).addBox(-5.0F, -34.0F, -4.0F, 1.0F, 4.0F, 8.0F, new CubeDeformation(0.0F))
                    .texOffs(32, 16).addBox(4.0F, -34.0F, -4.0F, 1.0F, 4.0F, 8.0F, new CubeDeformation(0.0F))
                    .texOffs(59, 61).addBox(3.5F, -32.0F, -4.5F, 1.0F, 7.0F, 2.0F, new CubeDeformation(0.0F))
                    .texOffs(58, 38).addBox(3.75F, -30.0F, 0.0F, 1.0F, 4.0F, 4.0F, new CubeDeformation(0.0F))
                    .texOffs(58, 29).addBox(-4.75F, -30.0F, 0.0F, 1.0F, 4.0F, 4.0F, new CubeDeformation(0.0F))
                    .texOffs(16, 48).addBox(-2.0F, -35.0F, 4.0F, 4.0F, 12.0F, 1.0F, new CubeDeformation(0.0F))
                    .texOffs(60, 9).addBox(-4.0F, -34.75F, 3.75F, 2.0F, 11.0F, 1.0F, new CubeDeformation(0.0F))
                    .texOffs(26, 58).addBox(2.0F, -34.75F, 3.75F, 2.0F, 11.0F, 1.0F, new CubeDeformation(0.0F))
                    .texOffs(0, 0).addBox(-4.5F, -32.0F, -5.0F, 2.0F, 7.0F, 1.0F, new CubeDeformation(0.0F))
                    .texOffs(20, 16).addBox(-4.5F, -34.0F, -5.0F, 9.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

            PartDefinition RightEar = Head.addOrReplaceChild("RightEar", CubeListBuilder.create(), PartPose.offsetAndRotation(-3.3F, -33.0F, 0.0F, -0.6981F, 0.0F, -0.3927F));

            PartDefinition cube_r1 = RightEar.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(64, 0).addBox(-0.9F, -3.0F, -0.6F, 2.0F, 6.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.0F, -1.0F, 0.2034F, 0.0581F, 0.0598F));

            PartDefinition LeftEar = Head.addOrReplaceChild("LeftEar", CubeListBuilder.create(), PartPose.offsetAndRotation(3.3F, -33.0F, 0.0F, -0.6981F, 0.0F, 0.3927F));

            PartDefinition base_r3 = LeftEar.addOrReplaceChild("base_r3", CubeListBuilder.create().texOffs(64, 0).mirror().addBox(-1.3129F, -2.9943F, -0.7396F, 2.0F, 6.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.2129F, -1.0057F, -0.8604F, 0.2034F, -0.0581F, -0.0598F));

            PartDefinition RightArm = partdefinition.addOrReplaceChild("RightArm", CubeListBuilder.create().texOffs(32, 0).addBox(-8.0F, -26.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
                    .texOffs(4, 38).addBox(-5.0F, -14.25F, -2.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.025F))
                    .texOffs(0, 38).addBox(-8.0F, -14.25F, 1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.025F))
                    .texOffs(35, 22).addBox(-8.0F, -14.25F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.025F))
                    .texOffs(32, 21).addBox(-8.0F, -14.25F, -2.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.025F)), PartPose.offset(0.0F, 25.0F, 0.0F));

            PartDefinition LeftArm = partdefinition.addOrReplaceChild("LeftArm", CubeListBuilder.create().texOffs(24, 29).addBox(4.0F, -26.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
                    .texOffs(0, 30).addBox(4.0F, -14.25F, -2.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.025F))
                    .texOffs(0, 28).addBox(7.0F, -14.25F, 1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.025F))
                    .texOffs(0, 18).addBox(7.0F, -14.25F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.025F))
                    .texOffs(0, 16).addBox(7.0F, -14.25F, -2.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.025F)), PartPose.offset(0.0F, 25.0F, 0.0F));

            return LayerDefinition.create(process(meshdefinition), 80, 80);
        }

        @Override
        public void prepareMobModel(LatexWatermelonCat p_102861_, float p_102862_, float p_102863_, float p_102864_) {
            this.prepareMobModel(controller, p_102861_, p_102862_, p_102863_, p_102864_);
        }

        public void setupHand() {
            controller.setupHand();
        }

        @Override
        public void setupAnim(@NotNull LatexWatermelonCat entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
            controller.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        }

        public ModelPart getArm(HumanoidArm p_102852_) {
            return p_102852_ == HumanoidArm.LEFT ? this.LeftArm : this.RightArm;
        }

        public ModelPart getHead() {
            return this.Head;
        }

        @Override
        public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
            RightLeg.render(poseStack, buffer, packedLight, packedOverlay);
            LeftLeg.render(poseStack, buffer, packedLight, packedOverlay);
            Head.render(poseStack, buffer, packedLight, packedOverlay);
            Torso.render(poseStack, buffer, packedLight, packedOverlay);
            RightArm.render(poseStack, buffer, packedLight, packedOverlay);
            LeftArm.render(poseStack, buffer, packedLight, packedOverlay);
        }

        @Override
        public LatexHumanoidModelController getController() {
            return controller;
        }
}
