package net.ltxprogrammer.changed.client.renderer.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.entity.beast.LatexSquidDog;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.HumanoidArm;

public class LatexSquidDogModel extends LatexHumanoidModel<LatexSquidDog> implements LatexHumanoidModelInterface {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(Changed.modResource("latex_squid_dog"), "main");
    public final ModelPart Head;
    public final ModelPart Torso;
    public final ModelPart LeftArm;
    public final ModelPart RightArm;
    public final ModelPart LeftArm2;
    public final ModelPart RightArm2;
    public final ModelPart LeftLeg;
    public final ModelPart Rightleg;
    public final LatexHumanoidModelController controller;

    public LatexSquidDogModel(ModelPart root) {
        super(root);
        this.Head = root.getChild("head");
        this.Torso = root.getChild("body");
        this.LeftArm = root.getChild("rightArm");
        this.RightArm = root.getChild("leftArm");
        this.LeftArm2 = root.getChild("rightArm2");
        this.RightArm2 = root.getChild("leftArm2");
        this.LeftLeg = root.getChild("rightLeg");
        this.Rightleg = root.getChild("leftLeg");
        this.controller = LatexHumanoidModelController.Builder.of(this, Head, Torso, Torso.getChild("Tail"), RightArm, LeftArm, Rightleg, LeftLeg).arms2(RightArm2, LeftArm2).hipOffset(-5.0F).build();
    }

    public static LayerDefinition createBodyLayer() {

        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 19).addBox(-4.0F, -7.375F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F))
                .texOffs(33, 99).addBox(-1.5F, -0.5F, -5.0F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(22, 96).addBox(-2.0F, -2.5F, -6.0F, 4.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -5.0F, 0.0F));

        PartDefinition Snout_r1 = head.addOrReplaceChild("Snout_r1", CubeListBuilder.create().texOffs(28, 51).addBox(-1.0F, -29.625F, -0.95F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.5F, 26.5F, 0.0F, 0.1745F, 0.0F, 0.0F));

        PartDefinition Head_r1 = head.addOrReplaceChild("Head_r1", CubeListBuilder.create().texOffs(0, 19).addBox(0.5F, -1.75F, -1.625F, 1.0F, 4.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(14, 35).addBox(-0.5F, -0.75F, -1.625F, 1.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.5F, -8.375F, 0.5F, 0.0F, 1.5708F, 0.3927F));

        PartDefinition Head_r2 = head.addOrReplaceChild("Head_r2", CubeListBuilder.create().texOffs(38, 27).addBox(-0.5F, -0.75F, -1.625F, 1.0F, 3.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(0, 35).addBox(-1.5F, -1.75F, -1.625F, 1.0F, 4.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.5F, -8.375F, 0.5F, 0.0F, -1.5708F, -0.3927F));

        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(24, 19).addBox(-4.5F, 0.625F, -2.0F, 9.0F, 4.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(28, 38).addBox(-1.375F, -1.375F, -1.375F, 3.0F, 2.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(30, 47).addBox(1.0F, -1.0F, -3.0F, 4.0F, 4.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(0, 46).addBox(-5.0F, -1.0F, -3.0F, 4.0F, 4.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(38, 12).addBox(-4.5F, 10.625F, -2.0F, 9.0F, 2.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(36, 41).addBox(-4.0F, 4.625F, -2.0F, 8.0F, 2.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(53, 0).addBox(-2.5F, 12.625F, -2.0F, 5.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(50, 18).addBox(-3.5F, 6.625F, -2.0F, 7.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -5.0F, 0.0F));

        PartDefinition Tentapaws = body.addOrReplaceChild("Tentapaws", CubeListBuilder.create(), PartPose.offset(0.0F, 25.0F, 0.0F));

        PartDefinition Tentapaw1 = Tentapaws.addOrReplaceChild("Tentapaw1", CubeListBuilder.create(), PartPose.offset(1.3642F, -22.6544F, 2.3598F));

        PartDefinition Body_r1 = Tentapaw1.addOrReplaceChild("Body_r1", CubeListBuilder.create().texOffs(61, 47).addBox(0.8003F, -4.0208F, 19.792F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(42, 57).addBox(0.8003F, -3.0208F, 24.167F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(5, 57).addBox(0.8003F, -1.6458F, 24.167F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(0, 56).addBox(0.8003F, -0.0208F, 24.167F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(26, 49).addBox(0.8003F, 1.3542F, 24.167F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(44, 47).addBox(0.8003F, 2.3542F, 19.792F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(51, 90).addBox(0.8003F, -2.3958F, 19.792F, 1.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(10.7875F, 5.4329F, 5.2181F, -2.2119F, 0.7864F, 2.9331F));

        PartDefinition Body_r2 = Tentapaw1.addOrReplaceChild("Body_r2", CubeListBuilder.create().texOffs(57, 84).addBox(-2.347F, -1.375F, -4.6602F, 3.0F, 3.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(11.0F, -3.0F, 4.0F, -2.9234F, 1.5272F, 3.1416F));

        PartDefinition Body_r3 = Tentapaw1.addOrReplaceChild("Body_r3", CubeListBuilder.create().texOffs(91, 57).addBox(-1.0849F, -0.25F, 16.4149F, 3.0F, 6.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(31, 57).addBox(-1.0849F, -1.25F, 11.4149F, 3.0F, 8.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(11, 84).addBox(-1.3349F, 1.375F, 6.6649F, 3.0F, 3.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(11.0F, -3.0F, 4.0F, -2.2119F, 0.7864F, 2.9331F));

        PartDefinition Body_r4 = Tentapaw1.addOrReplaceChild("Body_r4", CubeListBuilder.create().texOffs(38, 30).addBox(-1.3349F, -1.625F, -0.3351F, 3.0F, 3.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(11.0F, -3.0F, 4.0F, -2.6483F, 0.7864F, 2.9331F));

        PartDefinition Body_r5 = Tentapaw1.addOrReplaceChild("Body_r5", CubeListBuilder.create().texOffs(68, 87).addBox(-2.875F, -0.75F, 1.0F, 3.0F, 3.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.9067F, -1.5622F, 1.8475F, 0.491F, 1.1418F, 0.3592F));

        PartDefinition Body_r6 = Tentapaw1.addOrReplaceChild("Body_r6", CubeListBuilder.create().texOffs(84, 87).addBox(-2.5F, -0.5F, 0.0F, 3.0F, 3.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.1358F, -0.8456F, -1.3598F, 0.2182F, 0.4363F, 0.0F));

        PartDefinition Tentapaw2 = Tentapaws.addOrReplaceChild("Tentapaw2", CubeListBuilder.create(), PartPose.offsetAndRotation(2.3642F, -18.6544F, 2.3598F, 0.0F, 0.0F, 0.6109F));

        PartDefinition Body_r7 = Tentapaw2.addOrReplaceChild("Body_r7", CubeListBuilder.create().texOffs(41, 82).addBox(-2.347F, -1.375F, -4.6602F, 3.0F, 3.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(9.9122F, -3.2783F, 4.0F, -2.9234F, 1.5272F, 3.1416F));

        PartDefinition Body_r8 = Tentapaw2.addOrReplaceChild("Body_r8", CubeListBuilder.create().texOffs(91, 35).addBox(-0.4081F, 1.8708F, 15.506F, 3.0F, 6.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(86, 74).addBox(1.8003F, 1.2292F, 10.917F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(84, 0).addBox(1.8003F, 2.2292F, 15.292F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(36, 81).addBox(1.8003F, 3.6042F, 15.292F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(52, 82).addBox(1.8003F, 5.2292F, 15.292F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(86, 12).addBox(1.8003F, 6.6042F, 15.292F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(79, 87).addBox(1.8003F, 7.6042F, 10.917F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(92, 45).addBox(1.8003F, 2.8542F, 10.917F, 1.0F, 4.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(15, 52).addBox(-0.4081F, 0.8708F, 10.506F, 3.0F, 8.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(83, 4).addBox(-0.6581F, 3.4958F, 5.756F, 3.0F, 3.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(9.6998F, -4.8455F, 5.2181F, -2.1217F, 0.9972F, -2.8432F));

        PartDefinition Body_r9 = Tentapaw2.addOrReplaceChild("Body_r9", CubeListBuilder.create().texOffs(14, 38).addBox(-0.6581F, 0.6812F, -0.2626F, 3.0F, 3.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(9.6998F, -4.8455F, 5.2181F, -2.5581F, 0.9972F, -2.8432F));

        PartDefinition Body_r10 = Tentapaw2.addOrReplaceChild("Body_r10", CubeListBuilder.create().texOffs(83, 27).addBox(-2.875F, -0.75F, 1.0F, 3.0F, 3.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.8189F, -1.8405F, 1.8475F, 0.491F, 1.1418F, 0.3592F));

        PartDefinition Body_r11 = Tentapaw2.addOrReplaceChild("Body_r11", CubeListBuilder.create().texOffs(83, 66).addBox(-2.5F, -0.5F, 0.0F, 3.0F, 3.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0481F, -1.1239F, -1.3598F, 0.2182F, 0.4363F, 0.0F));

        PartDefinition Tentapaw3 = Tentapaws.addOrReplaceChild("Tentapaw3", CubeListBuilder.create(), PartPose.offset(-2.6358F, -22.6544F, 3.3598F));

        PartDefinition Body_r12 = Tentapaw3.addOrReplaceChild("Body_r12", CubeListBuilder.create().texOffs(47, 26).addBox(-1.8003F, -1.6458F, 24.167F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(46, 18).addBox(-1.8003F, -0.0208F, 24.167F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(39, 4).addBox(-1.8003F, 1.3542F, 24.167F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(39, 0).addBox(-1.8003F, -4.0208F, 19.792F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(30, 12).addBox(-1.8003F, -3.0208F, 24.167F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(22, 0).addBox(-1.8003F, -2.3958F, 19.792F, 1.0F, 4.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(22, 12).addBox(-1.8003F, 2.3542F, 19.792F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-9.5159F, 5.4329F, 4.2181F, -2.2119F, -0.7864F, -2.9331F));

        PartDefinition Body_r13 = Tentapaw3.addOrReplaceChild("Body_r13", CubeListBuilder.create().texOffs(0, 81).addBox(-0.653F, -1.375F, -4.6602F, 3.0F, 3.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-9.7284F, -3.0F, 3.0F, -2.9234F, -1.5272F, -3.1416F));

        PartDefinition Body_r14 = Tentapaw3.addOrReplaceChild("Body_r14", CubeListBuilder.create().texOffs(39, 90).addBox(-1.9151F, -0.25F, 16.4149F, 3.0F, 6.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(50, 47).addBox(-1.9151F, -1.25F, 11.4149F, 3.0F, 8.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(25, 81).addBox(-1.6651F, 1.375F, 6.6649F, 3.0F, 3.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-9.7284F, -3.0F, 3.0F, -2.2119F, -0.7864F, -2.9331F));

        PartDefinition Body_r15 = Tentapaw3.addOrReplaceChild("Body_r15", CubeListBuilder.create().texOffs(0, 35).addBox(-1.6651F, -1.625F, -0.3351F, 3.0F, 3.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-9.7284F, -3.0F, 3.0F, -2.6483F, -0.7864F, -2.9331F));

        PartDefinition Body_r16 = Tentapaw3.addOrReplaceChild("Body_r16", CubeListBuilder.create().texOffs(81, 79).addBox(-0.125F, -0.75F, 1.0F, 3.0F, 3.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.635F, -1.5622F, 0.8475F, 0.491F, -1.1418F, -0.3592F));

        PartDefinition Body_r17 = Tentapaw3.addOrReplaceChild("Body_r17", CubeListBuilder.create().texOffs(82, 19).addBox(-0.5F, -0.5F, 0.0F, 3.0F, 3.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.1358F, -0.8456F, -2.3598F, 0.2182F, -0.4363F, 0.0F));

        PartDefinition Tentapaw4 = Tentapaws.addOrReplaceChild("Tentapaw4", CubeListBuilder.create(), PartPose.offsetAndRotation(-2.3642F, -18.6544F, 2.3598F, 0.0F, 0.0F, -0.6109F));

        PartDefinition Body_r18 = Tentapaw4.addOrReplaceChild("Body_r18", CubeListBuilder.create().texOffs(12, 92).addBox(-2.8003F, 2.8542F, 10.917F, 1.0F, 4.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(67, 0).addBox(-2.8003F, 7.6042F, 10.917F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(68, 33).addBox(-2.8003F, 6.6042F, 15.292F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(74, 54).addBox(-2.8003F, 5.2292F, 15.292F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(76, 41).addBox(-2.8003F, 3.6042F, 15.292F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(78, 19).addBox(-2.8003F, 2.2292F, 15.292F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(79, 66).addBox(-2.8003F, 1.2292F, 10.917F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(0, 89).addBox(-2.5919F, 1.8708F, 15.506F, 3.0F, 6.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(-2.5919F, 0.8708F, 10.506F, 3.0F, 8.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(75, 58).addBox(-2.3419F, 3.4958F, 5.756F, 3.0F, 3.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-9.6998F, -4.8455F, 5.2181F, -2.1217F, -0.9972F, 2.8432F));

        PartDefinition Body_r19 = Tentapaw4.addOrReplaceChild("Body_r19", CubeListBuilder.create().texOffs(72, 33).addBox(-0.653F, -1.375F, -4.6602F, 3.0F, 3.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-9.9122F, -3.2783F, 4.0F, -2.9234F, -1.5272F, -3.1416F));

        PartDefinition Body_r20 = Tentapaw4.addOrReplaceChild("Body_r20", CubeListBuilder.create().texOffs(24, 27).addBox(-2.3419F, 0.6812F, -0.2626F, 3.0F, 3.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-9.6998F, -4.8455F, 5.2181F, -2.5581F, -0.9972F, 2.8432F));

        PartDefinition Body_r21 = Tentapaw4.addOrReplaceChild("Body_r21", CubeListBuilder.create().texOffs(79, 49).addBox(-0.125F, -0.75F, 1.0F, 3.0F, 3.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.8189F, -1.8405F, 1.8475F, 0.491F, -1.1418F, -0.3592F));

        PartDefinition Body_r22 = Tentapaw4.addOrReplaceChild("Body_r22", CubeListBuilder.create().texOffs(80, 41).addBox(-0.5F, -0.5F, 0.0F, 3.0F, 3.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.0481F, -1.1239F, -1.3598F, 0.2182F, -0.4363F, 0.0F));

        PartDefinition Tail = body.addOrReplaceChild("Tail", CubeListBuilder.create(), PartPose.offset(0.0F, 12.0F, 0.0F));

        PartDefinition Tail_r1 = Tail.addOrReplaceChild("Tail_r1", CubeListBuilder.create().texOffs(39, 0).addBox(-1.25F, -19.459F, -1.031F, 3.0F, 1.0F, 8.0F, new CubeDeformation(0.0F))
                .texOffs(22, 0).addBox(-1.25F, -15.459F, 3.969F, 3.0F, 1.0F, 11.0F, new CubeDeformation(0.0F))
                .texOffs(22, 8).addBox(-1.25F, -18.459F, 15.969F, 3.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(-1.25F, -18.459F, -0.031F, 3.0F, 3.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 14.2802F, -8.3882F, -0.5672F, 0.0F, 0.0F));

        PartDefinition rightArm2 = partdefinition.addOrReplaceChild("rightArm2", CubeListBuilder.create(), PartPose.offset(4.0F, -2.0F, 0.125F));

        PartDefinition bone = rightArm2.addOrReplaceChild("bone", CubeListBuilder.create().texOffs(16, 73).addBox(-1.7276F, 0.0853F, -2.0F, 3.0F, 7.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.0F, 0.0F, 0.0F, 0.0F, -1.0908F));

        PartDefinition bone8 = bone.addOrReplaceChild("bone8", CubeListBuilder.create().texOffs(47, 60).addBox(-1.7109F, -0.4976F, -1.9985F, 4.0F, 7.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(39, 9).addBox(1.2891F, 6.5024F, -1.9985F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(38, 33).addBox(1.2891F, 6.5024F, -0.4985F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(38, 14).addBox(1.2891F, 6.5024F, 1.0015F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.1687F, 6.9791F, 0.0F, -0.5672F, 0.0F, 0.7418F));

        PartDefinition rightArm = partdefinition.addOrReplaceChild("rightArm", CubeListBuilder.create(), PartPose.offset(3.75F, 2.25F, 0.75F));

        PartDefinition bone13 = rightArm.addOrReplaceChild("bone13", CubeListBuilder.create().texOffs(58, 73).addBox(-1.6233F, -0.4507F, -2.0F, 3.0F, 7.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.0F, 0.0F, 0.0F, 0.0F, -0.6545F));

        PartDefinition bone2 = bone13.addOrReplaceChild("bone2", CubeListBuilder.create().texOffs(0, 61).addBox(-2.2102F, -0.9644F, -2.1567F, 4.0F, 7.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(42, 33).addBox(0.7898F, 6.0356F, -2.1567F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(14, 41).addBox(0.7898F, 6.0356F, -0.6567F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(18, 41).addBox(0.7898F, 6.0356F, 0.8433F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.2094F, 6.7095F, 0.0F, -0.5564F, -0.1166F, 0.5569F));

        PartDefinition leftArm2 = partdefinition.addOrReplaceChild("leftArm2", CubeListBuilder.create(), PartPose.offset(-4.0F, -1.0F, 0.125F));

        PartDefinition bone12 = leftArm2.addOrReplaceChild("bone12", CubeListBuilder.create().texOffs(68, 22).addBox(-2.1594F, -0.3764F, -2.0F, 3.0F, 7.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.0F, 0.0F, 0.0F, 0.0F, 1.0908F));

        PartDefinition bone11 = bone12.addOrReplaceChild("bone11", CubeListBuilder.create().texOffs(52, 26).addBox(-2.3812F, -0.5688F, -2.0439F, 4.0F, 7.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(12, 14).addBox(-2.3812F, 6.4312F, -2.0439F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(11, 3).addBox(-2.3812F, 6.4312F, -0.5439F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(0, 3).addBox(-2.3812F, 6.4312F, 0.9561F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.5933F, 6.5173F, 0.0F, -0.5672F, 0.0F, -0.7418F));

        PartDefinition leftArm = partdefinition.addOrReplaceChild("leftArm", CubeListBuilder.create(), PartPose.offset(-3.75F, 2.25F, 0.75F));

        PartDefinition bone14 = leftArm.addOrReplaceChild("bone14", CubeListBuilder.create().texOffs(30, 70).addBox(-1.3767F, -0.4507F, -2.0F, 3.0F, 7.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.0F, 0.0F, 0.0F, 0.0F, 0.6545F));

        PartDefinition bone10 = bone14.addOrReplaceChild("bone10", CubeListBuilder.create().texOffs(60, 5).addBox(-1.7898F, -0.9644F, -2.1567F, 4.0F, 7.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(19, 35).addBox(-1.7898F, 6.0356F, -2.1567F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(28, 2).addBox(-1.7898F, 6.0356F, -0.6567F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(28, 0).addBox(-1.7898F, 6.0356F, 0.8433F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.2094F, 6.7095F, 0.0F, -0.5564F, 0.1166F, -0.5569F));

        PartDefinition rightLeg = partdefinition.addOrReplaceChild("rightLeg", CubeListBuilder.create().texOffs(72, 12).addBox(-2.0F, -0.375F, -1.75F, 5.0F, 3.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(16, 65).addBox(-0.125F, 13.9965F, -3.8953F, 4.0F, 3.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(2.125F, 7.0F, 0.125F));

        PartDefinition bone5 = rightLeg.addOrReplaceChild("bone5", CubeListBuilder.create().texOffs(72, 0).addBox(-1.5F, -3.0357F, -2.6784F, 4.0F, 5.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(23, 89).addBox(-1.25F, 1.9643F, -2.6784F, 4.0F, 3.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 5.0F, 0.0F, -0.3927F, 0.0F, 0.0F));

        PartDefinition bone6 = rightLeg.addOrReplaceChild("bone6", CubeListBuilder.create(), PartPose.offsetAndRotation(-4.0F, 8.0F, -1.0F, 0.6545F, 0.0F, 0.0F));

        PartDefinition bone6_r1 = bone6.addOrReplaceChild("bone6_r1", CubeListBuilder.create().texOffs(63, 63).addBox(-1.3698F, -4.6945F, -1.3912F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(5.0F, 3.688F, -0.051F, 0.0F, 0.0F, -0.1745F));

        PartDefinition bone7 = rightLeg.addOrReplaceChild("bone7", CubeListBuilder.create().texOffs(72, 73).addBox(-0.125F, -1.1097F, 3.0027F, 4.0F, 8.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 8.0F, -1.0F, -0.48F, 0.0F, 0.0F));

        PartDefinition leftLeg = partdefinition.addOrReplaceChild("leftLeg", CubeListBuilder.create().texOffs(66, 47).addBox(-3.0F, -0.375F, -1.75F, 5.0F, 3.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(61, 55).addBox(-3.875F, 13.9965F, -3.8953F, 4.0F, 3.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.125F, 7.0F, 0.125F, 0.0F, 0.0F, 0.0F));

        PartDefinition bone3 = leftLeg.addOrReplaceChild("bone3", CubeListBuilder.create().texOffs(0, 72).addBox(-2.5F, -3.0357F, -2.6784F, 4.0F, 5.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(90, 12).addBox(-2.75F, 1.9643F, -2.6784F, 4.0F, 3.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 5.0F, 0.0F, -0.3927F, 0.0F, 0.0F));

        PartDefinition bone4 = leftLeg.addOrReplaceChild("bone4", CubeListBuilder.create(), PartPose.offsetAndRotation(4.0F, 8.0F, -1.0F, 0.6545F, 0.0F, 0.0F));

        PartDefinition bone7_r1 = bone4.addOrReplaceChild("bone7_r1", CubeListBuilder.create().texOffs(60, 37).addBox(-2.6302F, -4.6945F, -1.3912F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-5.0F, 3.688F, -0.051F, 0.0F, 0.0F, 0.1745F));

        PartDefinition bone9 = leftLeg.addOrReplaceChild("bone9", CubeListBuilder.create().texOffs(44, 71).addBox(-3.875F, -1.1097F, 3.0027F, 4.0F, 8.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 8.0F, -1.0F, -0.48F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 112, 112);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green,
                               float blue, float alpha) {
        Head.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        Torso.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        LeftArm.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        RightArm.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        LeftArm2.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        RightArm2.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        LeftLeg.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        Rightleg.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }

    @Override
    public void prepareMobModel(LatexSquidDog p_102861_, float p_102862_, float p_102863_, float p_102864_) {
        this.prepareMobModel(controller, p_102861_, p_102862_, p_102863_, p_102864_);
    }

    @Override
    public void setupAnim(LatexSquidDog entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float HeadPitch) {
        controller.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, HeadPitch);
    }

    @Override
    public ModelPart getArm(HumanoidArm humanoidArm) {
        return switch (humanoidArm) {
            case LEFT -> LeftArm;
            case RIGHT -> RightArm;
        };
    }

    @Override
    public void setupHand() {
        controller.setupHand();
    }

    @Override
    public LatexHumanoidModelController getController() {
        return controller;
    }

    @Override
    public void translateToHand(HumanoidArm p_102108_, PoseStack p_102109_) {
        this.getArm(p_102108_).translateAndRotate(p_102109_);
    }

    @Override
    public ModelPart getHead() {
        return Head;
    }
}
