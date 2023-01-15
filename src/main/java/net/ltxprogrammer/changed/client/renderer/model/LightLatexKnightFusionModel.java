package net.ltxprogrammer.changed.client.renderer.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.entity.beast.LightLatexKnightFusion;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.world.entity.HumanoidArm;
import org.jetbrains.annotations.NotNull;

public class LightLatexKnightFusionModel extends LatexHumanoidModel<LightLatexKnightFusion> implements LatexHumanoidModelInterface {
    // This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(Changed.modResource("light_latex_knight_fusion"), "main");
    private final ModelPart RightLeg;
    private final ModelPart LeftLeg;
    private final ModelPart RightArm;
    private final ModelPart LeftArm;
    private final ModelPart Head;
    private final ModelPart Torso;
    private final ModelPart Tail;
    private final LatexHumanoidModelController controller;

    public LightLatexKnightFusionModel(ModelPart root) {
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

        PartDefinition RightLeg = partdefinition.addOrReplaceChild("RightLeg", CubeListBuilder.create().texOffs(52, 7).addBox(-2.0F, 12.0F, -2.75F, 4.0F, 2.0F, 4.0F, new CubeDeformation(0.05F)), PartPose.offset(-2.5F, 10.0F, 0.0F));

        PartDefinition Toe_r1 = RightLeg.addOrReplaceChild("Toe_r1", CubeListBuilder.create().texOffs(45, 0).addBox(-2.5F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0489F))
                .texOffs(46, 31).addBox(-1.0F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0489F))
                .texOffs(26, 50).addBox(-4.0F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0489F)), PartPose.offsetAndRotation(2.0F, 12.95F, -3.225F, -0.6109F, 0.0F, 0.0F));

        PartDefinition Toe_r2 = RightLeg.addOrReplaceChild("Toe_r2", CubeListBuilder.create().texOffs(48, 1).addBox(-1.0F, -3.0F, -1.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.049F))
                .texOffs(11, 48).addBox(-2.5F, -3.0F, -1.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.049F))
                .texOffs(23, 49).addBox(-4.0F, -3.0F, -1.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.049F)), PartPose.offsetAndRotation(2.0F, 16.0F, -1.75F, 0.0F, 0.0F, 0.0F));

        PartDefinition RightLowerLeg_r1 = RightLeg.addOrReplaceChild("RightLowerLeg_r1", CubeListBuilder.create().texOffs(0, 48).addBox(-2.0F, -7.5F, -1.0F, 4.0F, 6.0F, 3.0F, new CubeDeformation(0.11F)), PartPose.offsetAndRotation(0.0F, 14.15F, -1.25F, -0.5236F, 0.0F, 0.0F));

        PartDefinition RightMidLeg_r1 = RightLeg.addOrReplaceChild("RightMidLeg_r1", CubeListBuilder.create().texOffs(16, 38).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 7.0F, 4.0F, new CubeDeformation(0.1F)), PartPose.offsetAndRotation(0.0F, 4.05F, -1.5F, 0.5672F, 0.0F, 0.0F));

        PartDefinition RightUpperLeg_r1 = RightLeg.addOrReplaceChild("RightUpperLeg_r1", CubeListBuilder.create().texOffs(40, 21).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.185F)), PartPose.offsetAndRotation(0.0F, -0.2F, 0.0F, -0.2182F, 0.0F, 0.0F));

        PartDefinition LeftLeg = partdefinition.addOrReplaceChild("LeftLeg", CubeListBuilder.create().texOffs(26, 50).addBox(-2.0F, 12.0F, -2.75F, 4.0F, 2.0F, 4.0F, new CubeDeformation(0.05F)), PartPose.offset(2.5F, 10.0F, 0.0F));

        PartDefinition Toe_r3 = LeftLeg.addOrReplaceChild("Toe_r3", CubeListBuilder.create().texOffs(38, 50).addBox(-2.5F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0489F))
                .texOffs(52, 6).addBox(-1.0F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0489F))
                .texOffs(52, 23).addBox(-4.0F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0489F)), PartPose.offsetAndRotation(2.0F, 12.95F, -3.225F, -0.6109F, 0.0F, 0.0F));

        PartDefinition Toe_r4 = LeftLeg.addOrReplaceChild("Toe_r4", CubeListBuilder.create().texOffs(52, 8).addBox(-1.0F, -3.0F, -1.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.049F))
                .texOffs(52, 13).addBox(-2.5F, -3.0F, -1.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.049F))
                .texOffs(52, 21).addBox(-4.0F, -3.0F, -1.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.049F)), PartPose.offsetAndRotation(2.0F, 16.0F, -1.75F, 0.0F, 0.0F, 0.0F));

        PartDefinition LeftLowerLeg_r1 = LeftLeg.addOrReplaceChild("LeftLowerLeg_r1", CubeListBuilder.create().texOffs(47, 47).addBox(-2.0F, -7.5F, -1.0F, 4.0F, 6.0F, 3.0F, new CubeDeformation(0.11F)), PartPose.offsetAndRotation(0.0F, 14.15F, -1.25F, -0.5236F, 0.0F, 0.0F));

        PartDefinition LeftMidLeg_r1 = LeftLeg.addOrReplaceChild("LeftMidLeg_r1", CubeListBuilder.create().texOffs(34, 34).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 7.0F, 4.0F, new CubeDeformation(0.1F)), PartPose.offsetAndRotation(0.0F, 4.05F, -1.5F, 0.5672F, 0.0F, 0.0F));

        PartDefinition LeftUpperLeg_r1 = LeftLeg.addOrReplaceChild("LeftUpperLeg_r1", CubeListBuilder.create().texOffs(40, 11).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.185F)), PartPose.offsetAndRotation(0.0F, -0.2F, 0.0F, -0.2182F, 0.0F, 0.0F));

        PartDefinition Head = partdefinition.addOrReplaceChild("Head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -34.7F, -4.0F, 8.0F, 8.0F, 8.0F, CubeDeformation.NONE)
                .texOffs(61, 50).addBox(-2.0F, -29.7F, -6.0F, 4.0F, 2.0F, 2.0F, CubeDeformation.NONE)
                .texOffs(0, 6).addBox(-1.5F, -27.7F, -5.0F, 3.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(30, 62).addBox(-4.6F, -31.6F, -1.0F, 1.0F, 3.0F, 3.0F, CubeDeformation.NONE)
                .texOffs(0, 0).addBox(3.6F, -31.6F, -1.0F, 1.0F, 3.0F, 3.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 25.0F, 0.0F));

        PartDefinition RightEar = Head.addOrReplaceChild("RightEar", CubeListBuilder.create().texOffs(47, 60).addBox(-0.2606F, -2.2578F, -2.0F, 2.0F, 3.0F, 3.0F, CubeDeformation.NONE)
                .texOffs(0, 65).addBox(1.3394F, -0.8578F, -1.3F, 1.0F, 2.0F, 2.0F, CubeDeformation.NONE)
                .texOffs(36, 15).addBox(-1.2606F, -2.3578F, -2.0F, 1.0F, 3.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(33, 32).addBox(-1.2606F, -2.3578F, 0.0F, 1.0F, 3.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(12, 32).addBox(-1.2606F, -3.2578F, -1.0F, 2.0F, 1.0F, 2.0F, CubeDeformation.NONE)
                .texOffs(40, 23).addBox(-1.2606F, -4.2578F, -1.0F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-3.0F, -34.2F, 0.0F, 0.0F, 0.0F, -0.3491F));

        PartDefinition Rightear2 = Head.addOrReplaceChild("Rightear2", CubeListBuilder.create().texOffs(50, 37).addBox(1.7086F, -1.094F, -1.4F, 5.0F, 1.0F, 3.0F, CubeDeformation.NONE)
                .texOffs(16, 35).addBox(0.7086F, -1.094F, -0.4F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(38, 31).addBox(3.3086F, -1.694F, -0.5F, 3.0F, 1.0F, 2.0F, CubeDeformation.NONE)
                .texOffs(32, 11).addBox(2.7086F, -0.094F, -1.4F, 3.0F, 1.0F, 3.0F, CubeDeformation.NONE)
                .texOffs(32, 8).addBox(1.7086F, -0.094F, 0.6F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-8.8F, -31.8F, 0.0F, 0.0F, 0.0F, 0.1309F));

        PartDefinition LeftEar = Head.addOrReplaceChild("LeftEar", CubeListBuilder.create().texOffs(28, 38).addBox(-8.1606F, -0.3928F, -1.3F, 1.0F, 2.0F, 2.0F, CubeDeformation.NONE)
                .texOffs(37, 59).addBox(-7.4337F, -1.8598F, -2.0F, 2.0F, 3.0F, 3.0F, CubeDeformation.NONE)
                .texOffs(0, 32).addBox(-5.4337F, -1.8598F, -2.0F, 1.0F, 3.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(0, 16).addBox(-5.4337F, -1.8598F, 0.0F, 1.0F, 3.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(20, 16).addBox(-6.4337F, -2.8598F, -1.0F, 2.0F, 1.0F, 2.0F, CubeDeformation.NONE)
                .texOffs(40, 21).addBox(-5.4337F, -3.8598F, -1.0F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(8.5F, -32.7F, 0.0F, 0.0F, 0.0F, 0.3403F));

        PartDefinition Leftear2 = Head.addOrReplaceChild("Leftear2", CubeListBuilder.create().texOffs(46, 56).addBox(-6.6086F, -0.894F, -1.4F, 5.0F, 1.0F, 3.0F, CubeDeformation.NONE)
                .texOffs(16, 40).addBox(-1.6086F, -0.894F, -0.4F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(64, 8).addBox(-6.2086F, -1.494F, -0.5F, 3.0F, 1.0F, 2.0F, CubeDeformation.NONE)
                .texOffs(28, 56).addBox(-5.6086F, 0.106F, -1.4F, 3.0F, 1.0F, 3.0F, CubeDeformation.NONE)
                .texOffs(16, 38).addBox(-2.6086F, 0.106F, 0.6F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(8.8F, -32.0F, 0.0F, 0.0F, 0.0F, -0.1309F));

        PartDefinition fur4 = Head.addOrReplaceChild("fur4", CubeListBuilder.create().texOffs(16, 32).addBox(-5.6F, 6.9F, -1.6F, 6.0F, 1.0F, 5.0F, CubeDeformation.NONE), PartPose.offset(2.5F, -34.0F, 0.0F));

        PartDefinition Fur5 = Head.addOrReplaceChild("Fur5", CubeListBuilder.create().texOffs(21, 56).addBox(-0.5F, -1.5F, -1.9F, 1.0F, 3.0F, 5.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-4.2F, -28.4F, 0.3F, 0.0F, 0.0F, -0.1309F));

        PartDefinition Fur6 = Head.addOrReplaceChild("Fur6", CubeListBuilder.create().texOffs(37, 51).addBox(-0.5F, -1.5F, -2.5F, 1.0F, 3.0F, 5.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(4.2F, -28.4F, 0.8F, 0.0F, 0.0F, 0.1309F));

        PartDefinition Torso = partdefinition.addOrReplaceChild("Torso", CubeListBuilder.create().texOffs(0, 16).addBox(-4.0F, -25.7F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.225F)), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition fur = Torso.addOrReplaceChild("fur", CubeListBuilder.create().texOffs(32, 6).addBox(-4.0F, -25.9F, -1.925F, 8.0F, 1.0F, 4.0F, new CubeDeformation(0.5F))
                .texOffs(24, 6).addBox(-2.0F, -25.2F, 0.775F, 4.0F, 1.0F, 1.0F, new CubeDeformation(0.4F))
                .texOffs(24, 0).addBox(-4.5F, -25.9F, -2.6F, 9.0F, 3.0F, 3.0F, new CubeDeformation(0.1F))
                .texOffs(56, 13).addBox(-3.0F, -25.925F, -2.75F, 6.0F, 6.0F, 1.0F, new CubeDeformation(0.2F))
                .texOffs(63, 41).addBox(-2.0F, -25.725F, -3.35F, 4.0F, 3.0F, 1.0F, new CubeDeformation(0.1F))
                .texOffs(64, 4).addBox(-2.0F, -21.8F, -2.6F, 4.0F, 3.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(12, 63).addBox(-4.0F, -23.925F, -2.425F, 4.0F, 3.0F, 1.0F, new CubeDeformation(0.1F))
                .texOffs(63, 34).addBox(0.0F, -23.925F, -2.425F, 4.0F, 3.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(48, 42).addBox(-3.0F, -26.7F, -1.0F, 6.0F, 1.0F, 3.0F, CubeDeformation.NONE)
                .texOffs(58, 29).addBox(-2.6F, -25.5F, 1.0F, 5.0F, 3.0F, 2.0F, CubeDeformation.NONE)
                .texOffs(32, 45).addBox(-3.6F, -26.4F, 0.9F, 7.0F, 3.0F, 2.0F, new CubeDeformation(-0.1F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition Tail = Torso.addOrReplaceChild("Tail", CubeListBuilder.create(), PartPose.offset(0.0F, -14.0F, 0.0F));

        PartDefinition Base_r1 = Tail.addOrReplaceChild("Base_r1", CubeListBuilder.create().texOffs(56, 20).addBox(-1.5F, 1.2F, -1.6F, 3.0F, 6.0F, 3.0F, new CubeDeformation(0.3F)), PartPose.offsetAndRotation(0.0F, 1.3F, 6.0F, 1.4835F, 0.0F, 0.0F));

        PartDefinition Base_r2 = Tail.addOrReplaceChild("Base_r2", CubeListBuilder.create().texOffs(14, 49).addBox(-1.5F, 0.4F, -0.6F, 3.0F, 7.0F, 3.0F, new CubeDeformation(0.51F)), PartPose.offsetAndRotation(0.0F, -0.7F, 0.0F, 1.1781F, 0.0F, 0.0F));

        PartDefinition RightArm = partdefinition.addOrReplaceChild("RightArm", CubeListBuilder.create().texOffs(24, 16).addBox(-8.225F, -26.7F, -2.0F, 4.0F, 12.0F, 4.0F, CubeDeformation.NONE)
                .texOffs(56, 29).addBox(-5.225F, -14.95F, -2.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.001F))
                .texOffs(55, 20).addBox(-8.225F, -14.95F, 1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.001F))
                .texOffs(38, 52).addBox(-8.225F, -14.95F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.001F))
                .texOffs(26, 52).addBox(-8.225F, -14.95F, -2.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.001F)), PartPose.offset(0.0F, 25.0F, 0.0F));

        PartDefinition fur3 = RightArm.addOrReplaceChild("fur3", CubeListBuilder.create().texOffs(0, 57).addBox(-1.925F, -27.0F, -1.65F, 2.0F, 4.0F, 4.0F, new CubeDeformation(-0.2F))
                .texOffs(19, 64).addBox(-1.725F, -23.7F, -1.05F, 1.0F, 2.0F, 3.0F, new CubeDeformation(-0.1F))
                .texOffs(12, 59).addBox(-1.125F, -25.6F, 1.3F, 3.0F, 2.0F, 1.0F, new CubeDeformation(-0.1F))
                .texOffs(46, 31).addBox(-0.825F, -26.9F, -2.0F, 4.0F, 2.0F, 4.0F, new CubeDeformation(0.3F)), PartPose.offset(-7.0F, 0.0F, 0.0F));

        PartDefinition cube_r1 = fur3.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(58, 46).addBox(-1.5F, -0.5F, -1.5F, 3.0F, 1.0F, 3.0F, new CubeDeformation(0.2F)), PartPose.offsetAndRotation(1.275F, -26.8F, 0.3F, 0.0F, 0.0F, -0.0873F));

        PartDefinition LeftArm = partdefinition.addOrReplaceChild("LeftArm", CubeListBuilder.create().texOffs(0, 32).addBox(4.2F, -26.7F, -2.0F, 4.0F, 12.0F, 4.0F, CubeDeformation.NONE)
                .texOffs(8, 59).addBox(4.2F, -14.95F, -2.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.001F))
                .texOffs(0, 59).addBox(7.2F, -14.95F, 1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.001F))
                .texOffs(8, 57).addBox(7.2F, -14.95F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.001F))
                .texOffs(0, 57).addBox(7.2F, -14.95F, -2.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.001F)), PartPose.offset(0.0F, 25.0F, 0.0F));

        PartDefinition fur2 = LeftArm.addOrReplaceChild("fur2", CubeListBuilder.create().texOffs(58, 56).addBox(3.0F, -3.0F, -1.65F, 2.0F, 4.0F, 4.0F, new CubeDeformation(-0.2F))
                .texOffs(54, 64).addBox(3.6F, 0.2F, -1.05F, 1.0F, 2.0F, 3.0F, new CubeDeformation(-0.1F))
                .texOffs(59, 64).addBox(1.1F, -1.6F, 1.3F, 3.0F, 2.0F, 1.0F, new CubeDeformation(-0.1F))
                .texOffs(48, 0).addBox(-0.2F, -2.9F, -2.0F, 4.0F, 2.0F, 4.0F, new CubeDeformation(0.3F)), PartPose.offset(4.0F, -24.0F, 0.0F));

        PartDefinition cube_r2 = fur2.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(60, 0).addBox(-1.5F, -0.5F, -1.5F, 3.0F, 1.0F, 3.0F, new CubeDeformation(0.2F)), PartPose.offsetAndRotation(1.7F, -2.8F, 0.3F, 0.0F, 0.0F, 0.0873F));

        return LayerDefinition.create(process(meshdefinition), 128, 128);
    }

    @Override
    public void prepareMobModel(LightLatexKnightFusion p_102861_, float p_102862_, float p_102863_, float p_102864_) {
        this.prepareMobModel(controller, p_102861_, p_102862_, p_102863_, p_102864_);
    }

    public void setupHand() {
        controller.setupHand();
    }

    @Override
    public void setupAnim(@NotNull LightLatexKnightFusion entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
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
