package net.ltxprogrammer.changed.client.renderer.model;
// Made with Blockbench 4.1.5
// Exported for Minecraft version 1.17 with Mojang mappings
// Paste this class into your mod and generate all required imports

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.animate.AnimatorPresets;
import net.ltxprogrammer.changed.client.renderer.animate.LatexAnimator;
import net.ltxprogrammer.changed.entity.beast.LatexMantaRayMale;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public class LatexMantaRayMaleModel extends LatexHumanoidModel<LatexMantaRayMale> implements LatexHumanoidModelInterface<LatexMantaRayMale, LatexMantaRayMaleModel> {
    // This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(Changed.modResource("latex_manta_ray_male"), "main");
    private final ModelPart RightLeg;
    private final ModelPart LeftLeg;
    private final ModelPart RightArm;
    private final ModelPart LeftArm;
    private final ModelPart Head;
    private final ModelPart Torso;
    private final ModelPart Tail;
    private final LatexAnimator<LatexMantaRayMale, LatexMantaRayMaleModel> animator;

    public LatexMantaRayMaleModel(ModelPart root) {
        super(root);
        this.RightLeg = root.getChild("RightLeg");
        this.LeftLeg = root.getChild("LeftLeg");
        this.Head = root.getChild("Head");
        this.Torso = root.getChild("Torso");
        this.Tail = Torso.getChild("Tail");
        this.RightArm = root.getChild("RightArm");
        this.LeftArm = root.getChild("LeftArm");
        animator = LatexAnimator.of(this).addPreset(AnimatorPresets.sharkLike(Head, Torso, LeftArm, RightArm, Tail, List.of(), LeftLeg, RightLeg));
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition RightLeg = partdefinition.addOrReplaceChild("RightLeg", CubeListBuilder.create(), PartPose.offset(-2.75F, 10.0F, 0.0F));

        PartDefinition Toe_r1 = RightLeg.addOrReplaceChild("Toe_r1", CubeListBuilder.create().texOffs(0, 18).addBox(-4.0F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.001F))
                .texOffs(24, 5).addBox(-2.5F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.001F))
                .texOffs(27, 6).addBox(-1.0F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.001F)), PartPose.offsetAndRotation(2.0F, 13.0F, -2.25F, -0.6109F, 0.0F, 0.0F));

        PartDefinition Toe_r2 = RightLeg.addOrReplaceChild("Toe_r2", CubeListBuilder.create().texOffs(29, 0).addBox(-1.0F, -3.0F, -1.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.001F))
                .texOffs(0, 32).addBox(-2.5F, -3.0F, -1.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.001F))
                .texOffs(32, 1).addBox(-4.0F, -3.0F, -1.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.001F))
                .texOffs(52, 10).addBox(-4.0F, -4.0F, -1.0F, 4.0F, 2.0F, 3.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(2.0F, 16.0F, -0.75F, 0.0F, 0.0F, 0.0F));

        PartDefinition RightLowerLeg_r1 = RightLeg.addOrReplaceChild("RightLowerLeg_r1", CubeListBuilder.create().texOffs(16, 43).addBox(-2.0F, -7.5F, -1.0F, 4.0F, 6.0F, 3.0F, new CubeDeformation(0.005F)), PartPose.offsetAndRotation(0.0F, 14.25F, -1.25F, -0.5236F, 0.0F, 0.0F));

        PartDefinition RightMidLeg_r1 = RightLeg.addOrReplaceChild("RightMidLeg_r1", CubeListBuilder.create().texOffs(16, 32).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 7.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 4.25F, -1.5F, 0.5672F, 0.0F, 0.0F));

        PartDefinition RightUpperLeg_r1 = RightLeg.addOrReplaceChild("RightUpperLeg_r1", CubeListBuilder.create().texOffs(40, 12).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.2182F, 0.0F, 0.0F));

        PartDefinition LeftLeg = partdefinition.addOrReplaceChild("LeftLeg", CubeListBuilder.create(), PartPose.offset(2.75F, 10.0F, 0.0F));

        PartDefinition Toe_r3 = LeftLeg.addOrReplaceChild("Toe_r3", CubeListBuilder.create().texOffs(12, 32).addBox(-2.5F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.001F))
                .texOffs(16, 32).addBox(-1.0F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.001F))
                .texOffs(12, 34).addBox(-4.0F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.001F)), PartPose.offsetAndRotation(2.0F, 13.0F, -2.25F, -0.6109F, 0.0F, 0.0F));

        PartDefinition Toe_r4 = LeftLeg.addOrReplaceChild("Toe_r4", CubeListBuilder.create().texOffs(28, 32).addBox(-1.0F, -3.0F, -1.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.001F))
                .texOffs(32, 32).addBox(-2.5F, -3.0F, -1.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.001F))
                .texOffs(0, 34).addBox(-4.0F, -3.0F, -1.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.001F))
                .texOffs(18, 52).addBox(-4.0F, -4.0F, -1.0F, 4.0F, 2.0F, 3.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(2.0F, 16.0F, -0.75F, 0.0F, 0.0F, 0.0F));

        PartDefinition LeftLowerLeg_r1 = LeftLeg.addOrReplaceChild("LeftLowerLeg_r1", CubeListBuilder.create().texOffs(30, 43).addBox(-2.0F, -7.5F, -1.0F, 4.0F, 6.0F, 3.0F, new CubeDeformation(0.005F)), PartPose.offsetAndRotation(0.0F, 14.25F, -1.25F, -0.5236F, 0.0F, 0.0F));

        PartDefinition LeftMidLeg_r1 = LeftLeg.addOrReplaceChild("LeftMidLeg_r1", CubeListBuilder.create().texOffs(32, 32).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 7.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 4.25F, -1.5F, 0.5672F, 0.0F, 0.0F));

        PartDefinition LeftUpperLeg_r1 = LeftLeg.addOrReplaceChild("LeftUpperLeg_r1", CubeListBuilder.create().texOffs(40, 22).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.2182F, 0.0F, 0.0F));

        PartDefinition Head = partdefinition.addOrReplaceChild("Head", CubeListBuilder.create(), PartPose.offset(0.0F, -1.5F, 0.0F));

        PartDefinition Snout_r1 = Head.addOrReplaceChild("Snout_r1", CubeListBuilder.create().texOffs(14, 57).addBox(0.0F, 0.0F, 0.0F, 1.0F, 2.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-1.5F, -2.5F, -7.0F, 0.0F, -0.2182F, 0.0F));

        PartDefinition Snout_r2 = Head.addOrReplaceChild("Snout_r2", CubeListBuilder.create().texOffs(0, 58).addBox(-1.0F, 0.0F, 0.0F, 1.0F, 2.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(1.5F, -2.5F, -7.0F, 0.0F, 0.2182F, 0.0F));

        PartDefinition Snout_r3 = Head.addOrReplaceChild("Snout_r3", CubeListBuilder.create().texOffs(32, 12).addBox(-1.5F, -27.0F, -6.0F, 3.0F, 1.0F, 2.0F, CubeDeformation.NONE)
                .texOffs(56, 44).addBox(-1.5F, -29.0F, -7.0F, 3.0F, 2.0F, 3.0F, new CubeDeformation(0.001F)), PartPose.offsetAndRotation(0.0F, 26.5F, 0.0F, 0.0F, 0.0F, 0.0F));

        PartDefinition Head_r1 = Head.addOrReplaceChild("Head_r1", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.5F, 0.0F, 0.0F, 0.0F, 0.0F));

        PartDefinition Fins = Head.addOrReplaceChild("Fins", CubeListBuilder.create(), PartPose.offset(0.0F, -8.0F, 0.0F));

        PartDefinition leftear_r1 = Fins.addOrReplaceChild("leftear_r1", CubeListBuilder.create().texOffs(48, 32).addBox(-6.15F, -28.9F, -23.8F, 2.0F, 7.0F, 3.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 33.5F, 0.0F, -0.7418F, 0.2182F, 0.2618F));

        PartDefinition leftear_r2 = Fins.addOrReplaceChild("leftear_r2", CubeListBuilder.create().texOffs(29, 54).addBox(-5.0F, -12.25F, -29.05F, 2.0F, 6.0F, 3.0F, new CubeDeformation(0.025F)), PartPose.offsetAndRotation(0.0F, 33.5F, 0.0F, -1.2654F, 0.3927F, 0.2618F));

        PartDefinition rightear_r1 = Fins.addOrReplaceChild("rightear_r1", CubeListBuilder.create().texOffs(56, 15).addBox(3.0F, -12.25F, -29.05F, 2.0F, 6.0F, 3.0F, new CubeDeformation(0.025F)), PartPose.offsetAndRotation(0.0F, 33.5F, 0.0F, -1.2654F, -0.3927F, -0.2618F));

        PartDefinition rightear_r2 = Fins.addOrReplaceChild("rightear_r2", CubeListBuilder.create().texOffs(41, 51).addBox(4.15F, -28.9F, -23.8F, 2.0F, 7.0F, 3.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 33.5F, 0.0F, -0.7418F, -0.2182F, -0.2618F));

        PartDefinition Head_r2 = Fins.addOrReplaceChild("Head_r2", CubeListBuilder.create().texOffs(58, 0).addBox(-1.0F, 0.0F, 0.0F, 2.0F, 2.0F, 3.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 4.0F, 3.0F, 0.0873F, 0.0F, 0.0F));

        PartDefinition Head_r3 = Fins.addOrReplaceChild("Head_r3", CubeListBuilder.create().texOffs(20, 16).addBox(-1.0F, 0.0F, 0.0F, 2.0F, 2.0F, 2.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 1.0F, 3.0F, 0.2618F, 0.0F, 0.0F));

        PartDefinition Head_r4 = Fins.addOrReplaceChild("Head_r4", CubeListBuilder.create().texOffs(54, 38).addBox(-1.0F, 0.0F, 0.0F, 2.0F, 2.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.5F, -1.0F, 0.6545F, 0.0F, 0.0F));

        PartDefinition Head_r5 = Fins.addOrReplaceChild("Head_r5", CubeListBuilder.create().texOffs(58, 5).addBox(-1.0F, 0.0F, -1.0F, 1.0F, 2.0F, 3.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-4.0F, 6.5F, -3.5F, 1.0472F, 0.0F, -2.0071F));

        PartDefinition Head_r6 = Fins.addOrReplaceChild("Head_r6", CubeListBuilder.create().texOffs(59, 54).addBox(0.0F, 0.0F, -1.0F, 1.0F, 2.0F, 3.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(4.0F, 6.5F, -3.5F, 1.0472F, 0.0F, 2.0071F));

        PartDefinition Head_r7 = Fins.addOrReplaceChild("Head_r7", CubeListBuilder.create().texOffs(55, 28).addBox(-1.0F, 0.0F, 0.0F, 2.0F, 2.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.5F, -3.5F, 1.0472F, 0.0F, 0.0F));

        PartDefinition Torso = partdefinition.addOrReplaceChild("Torso", CubeListBuilder.create(), PartPose.offset(0.0F, -1.5F, 0.0F));

        PartDefinition LeftArm_r1 = Torso.addOrReplaceChild("LeftArm_r1", CubeListBuilder.create().texOffs(0, 48).addBox(-2.0F, 1.0F, 0.0F, 4.0F, 9.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(4.0F, 1.5F, 0.0F, 0.0F, 0.0F, 0.1745F));

        PartDefinition RightArm_r1 = Torso.addOrReplaceChild("RightArm_r1", CubeListBuilder.create().texOffs(48, 0).addBox(-2.0F, 1.0F, 0.0F, 4.0F, 9.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-4.0F, 1.5F, 0.0F, 0.0F, 0.0F, -0.1745F));

        PartDefinition Torso_r1 = Torso.addOrReplaceChild("Torso_r1", CubeListBuilder.create().texOffs(0, 16).addBox(-4.0F, -12.0F, -2.0F, 8.0F, 12.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 12.5F, 0.0F, 0.0F, 0.0F, 0.0F));

        PartDefinition Tail = Torso.addOrReplaceChild("Tail", CubeListBuilder.create(), PartPose.offset(0.0F, 11.5F, 0.0F));

        PartDefinition Base_r1 = Tail.addOrReplaceChild("Base_r1", CubeListBuilder.create().texOffs(0, 16).addBox(-0.5F, 5.3462F, -1.8296F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.05F))
                .texOffs(10, 49).addBox(-0.5F, -2.5538F, -1.8296F, 1.0F, 8.0F, 3.0F, new CubeDeformation(-0.05F)), PartPose.offsetAndRotation(0.0F, 5.25F, 17.0F, 1.1345F, 0.0F, 0.0F));

        PartDefinition Base_r2 = Tail.addOrReplaceChild("Base_r2", CubeListBuilder.create().texOffs(24, 0).addBox(-0.5F, -8.0668F, -2.1179F, 1.0F, 2.0F, 3.0F, CubeDeformation.NONE)
                .texOffs(24, 57).addBox(-0.5F, -6.0668F, 0.8821F, 1.0F, 7.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(51, 51).addBox(-0.5F, -6.0668F, -2.1179F, 1.0F, 8.0F, 3.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 4.25F, 17.0F, -0.8727F, 0.0F, 0.0F));

        PartDefinition Base_r3 = Tail.addOrReplaceChild("Base_r3", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, -0.3449F, -0.7203F, 2.0F, 5.0F, 2.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 5.0F, 13.0F, 1.4835F, 0.0F, 0.0F));

        PartDefinition Base_r4 = Tail.addOrReplaceChild("Base_r4", CubeListBuilder.create().texOffs(44, 43).addBox(-1.5F, -1.3563F, -0.6088F, 3.0F, 5.0F, 3.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 4.5F, 9.25F, 1.309F, 0.0F, 0.0F));

        PartDefinition Base_r5 = Tail.addOrReplaceChild("Base_r5", CubeListBuilder.create().texOffs(56, 49).addBox(-1.0F, -1.075F, -0.625F, 2.0F, 2.0F, 3.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 1.0F, 0.0F, 0.9599F, 0.0F, 0.0F));

        PartDefinition Base_r6 = Tail.addOrReplaceChild("Base_r6", CubeListBuilder.create().texOffs(32, 0).addBox(-2.0F, 0.75F, -0.8F, 4.0F, 8.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 1.0F, 0.0F, 1.1781F, 0.0F, 0.0F));

        PartDefinition RightArm = partdefinition.addOrReplaceChild("RightArm", CubeListBuilder.create(), PartPose.offset(-5.0F, 0.5F, 0.0F));

        PartDefinition Finger_r1 = RightArm.addOrReplaceChild("Finger_r1", CubeListBuilder.create().texOffs(16, 34).addBox(3.0F, 9.0F, 1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.025F))
                .texOffs(36, 15).addBox(6.0F, 9.0F, 1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.025F)), PartPose.offsetAndRotation(-6.0F, 1.25F, -3.0F, 0.0F, 0.0F, 0.0F));

        PartDefinition Finger_r2 = RightArm.addOrReplaceChild("Finger_r2", CubeListBuilder.create().texOffs(28, 34).addBox(3.0F, 9.0F, 1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.025F)), PartPose.offsetAndRotation(-6.0F, 1.25F, -1.5F, 0.0F, 0.0F, 0.0F));

        PartDefinition Finger_r3 = RightArm.addOrReplaceChild("Finger_r3", CubeListBuilder.create().texOffs(32, 34).addBox(3.0F, 9.0F, 1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.025F)), PartPose.offsetAndRotation(-6.0F, 1.25F, 0.0F, 0.0F, 0.0F, 0.0F));

        PartDefinition RightArm_r2 = RightArm.addOrReplaceChild("RightArm_r2", CubeListBuilder.create().texOffs(24, 16).addBox(-4.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(1.0F, 0.5F, 0.0F, 0.0F, 0.0F, 0.0F));

        PartDefinition LeftArm = partdefinition.addOrReplaceChild("LeftArm", CubeListBuilder.create(), PartPose.offset(5.0F, 0.5F, 0.0F));

        PartDefinition Finger_r4 = LeftArm.addOrReplaceChild("Finger_r4", CubeListBuilder.create().texOffs(36, 17).addBox(3.0F, 9.0F, 1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.025F))
                .texOffs(40, 12).addBox(6.0F, 9.0F, 1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.025F)), PartPose.offsetAndRotation(-4.0F, 1.25F, -3.0F, 0.0F, 0.0F, 0.0F));

        PartDefinition Finger_r5 = LeftArm.addOrReplaceChild("Finger_r5", CubeListBuilder.create().texOffs(40, 22).addBox(3.0F, 9.0F, 1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.025F)), PartPose.offsetAndRotation(-1.0F, 1.25F, -1.5F, 0.0F, 0.0F, 0.0F));

        PartDefinition Finger_r6 = LeftArm.addOrReplaceChild("Finger_r6", CubeListBuilder.create().texOffs(40, 24).addBox(3.0F, 9.0F, 1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.025F)), PartPose.offsetAndRotation(-1.0F, 1.25F, 0.0F, 0.0F, 0.0F, 0.0F));

        PartDefinition LeftArm_r2 = LeftArm.addOrReplaceChild("LeftArm_r2", CubeListBuilder.create().texOffs(0, 32).addBox(0.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-1.0F, 0.5F, 0.0F, 0.0F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    @Override
    public void prepareMobModel(LatexMantaRayMale p_102861_, float p_102862_, float p_102863_, float p_102864_) {
        this.prepareMobModel(animator, p_102861_, p_102862_, p_102863_, p_102864_);
    }

    public void setupHand() {
        animator.setupHand();
    }

    @Override
    public void setupAnim(@NotNull LatexMantaRayMale entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        animator.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
    }

    public ModelPart getArm(HumanoidArm p_102852_) {
        return p_102852_ == HumanoidArm.LEFT ? this.LeftArm : this.RightArm;
    }

    public ModelPart getHead() {
        return this.Head;
    }

    public ModelPart getTorso() {
        return Torso;
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
    public LatexAnimator<LatexMantaRayMale, LatexMantaRayMaleModel> getAnimator() {
        return animator;
    }

    public static class Remodel extends LatexHumanoidModel.LatexRemodel<LatexMantaRayMale, Remodel> {
        private final ModelPart RightLeg;
        private final ModelPart LeftLeg;
        private final ModelPart RightArm;
        private final ModelPart LeftArm;
        private final ModelPart Head;
        private final ModelPart Torso;
        private final ModelPart Tail;
        private final LatexAnimator<LatexMantaRayMale, Remodel> animator;

        public Remodel(ModelPart root) {
            super(root);
            this.RightLeg = root.getChild("RightLeg");
            this.LeftLeg = root.getChild("LeftLeg");
            this.Head = root.getChild("Head");
            this.Torso = root.getChild("Torso");
            this.Tail = Torso.getChild("Tail");
            this.RightArm = root.getChild("RightArm");
            this.LeftArm = root.getChild("LeftArm");
            animator = LatexAnimator.of(this).addPreset(AnimatorPresets.sharkLike(Head, Torso, LeftArm, RightArm, Tail, List.of(), LeftLeg, RightLeg)).hipOffset(0.0f);
        }

        public static LayerDefinition createBodyLayer() {
            MeshDefinition meshdefinition = new MeshDefinition();
            PartDefinition partdefinition = meshdefinition.getRoot();

            PartDefinition Head = partdefinition.addOrReplaceChild("Head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

            PartDefinition Snout_r1 = Head.addOrReplaceChild("Snout_r1", CubeListBuilder.create().texOffs(50, 57).addBox(0.0F, 0.0F, 0.0F, 1.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.5F, -3.01F, -7.0F, 0.0F, -0.2182F, 0.0F));

            PartDefinition Snout_r2 = Head.addOrReplaceChild("Snout_r2", CubeListBuilder.create().texOffs(58, 0).addBox(-1.0F, 0.0F, 0.0F, 1.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.5F, -3.01F, -7.0F, 0.0F, 0.2182F, 0.0F));

            PartDefinition Snout_r3 = Head.addOrReplaceChild("Snout_r3", CubeListBuilder.create().texOffs(24, 0).addBox(-1.5F, -27.0F, -6.0F, 3.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
                    .texOffs(56, 24).addBox(-1.5F, -29.0F, -7.0F, 3.0F, 2.0F, 3.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(0.0F, 26.0F, 0.0F, 0.0F, 0.0F, 0.0F));

            PartDefinition Fins = Head.addOrReplaceChild("Fins", CubeListBuilder.create(), PartPose.offset(0.0F, -9.5F, 0.0F));

            PartDefinition leftear_r1 = Fins.addOrReplaceChild("leftear_r1", CubeListBuilder.create().texOffs(24, 52).addBox(-6.15F, -28.9F, -23.8F, 2.0F, 7.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 34.5F, 0.0F, -0.7418F, 0.2182F, 0.2618F));

            PartDefinition leftear_r2 = Fins.addOrReplaceChild("leftear_r2", CubeListBuilder.create().texOffs(53, 29).addBox(-5.0F, -12.25F, -29.05F, 2.0F, 6.0F, 3.0F, new CubeDeformation(0.025F)), PartPose.offsetAndRotation(0.0F, 34.5F, 0.0F, -1.2654F, 0.3927F, 0.2618F));

            PartDefinition rightear_r1 = Fins.addOrReplaceChild("rightear_r1", CubeListBuilder.create().texOffs(54, 48).addBox(3.0F, -12.25F, -29.05F, 2.0F, 6.0F, 3.0F, new CubeDeformation(0.025F)), PartPose.offsetAndRotation(0.0F, 34.5F, 0.0F, -1.2654F, -0.3927F, -0.2618F));

            PartDefinition rightear_r2 = Fins.addOrReplaceChild("rightear_r2", CubeListBuilder.create().texOffs(34, 52).addBox(4.15F, -28.9F, -23.8F, 2.0F, 7.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 34.5F, 0.0F, -0.7418F, -0.2182F, -0.2618F));

            PartDefinition Head_r1 = Fins.addOrReplaceChild("Head_r1", CubeListBuilder.create().texOffs(17, 59).addBox(-1.0F, 0.0F, 0.0F, 2.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 5.0F, 3.0F, 0.0873F, 0.0F, 0.0F));

            PartDefinition Head_r2 = Fins.addOrReplaceChild("Head_r2", CubeListBuilder.create().texOffs(20, 16).addBox(-1.0F, 0.0F, 0.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 2.0F, 3.0F, 0.2618F, 0.0F, 0.0F));

            PartDefinition Head_r3 = Fins.addOrReplaceChild("Head_r3", CubeListBuilder.create().texOffs(52, 18).addBox(-1.0F, 0.0F, 0.0F, 2.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 1.5F, -1.0F, 0.6545F, 0.0F, 0.0F));

            PartDefinition Head_r4 = Fins.addOrReplaceChild("Head_r4", CubeListBuilder.create().texOffs(24, 3).addBox(-1.0F, 0.0F, -1.0F, 1.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.0F, 7.5F, -3.5F, 1.0472F, 0.0F, -2.0071F));

            PartDefinition Head_r5 = Fins.addOrReplaceChild("Head_r5", CubeListBuilder.create().texOffs(41, 59).addBox(0.0F, 0.0F, -1.0F, 1.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.0F, 7.5F, -3.5F, 1.0472F, 0.0F, 2.0071F));

            PartDefinition Head_r6 = Fins.addOrReplaceChild("Head_r6", CubeListBuilder.create().texOffs(56, 39).addBox(-1.0F, 0.0F, 0.0F, 2.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 1.5F, -3.5F, 1.0472F, 0.0F, 0.0F));

            PartDefinition Torso = partdefinition.addOrReplaceChild("Torso", CubeListBuilder.create().texOffs(0, 16).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

            PartDefinition RightArm_r1 = Torso.addOrReplaceChild("RightArm_r1", CubeListBuilder.create().texOffs(48, 0).addBox(-5.25F, 1.0F, 0.0F, 4.0F, 9.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.0F, 0.5F, 0.0F, 0.0F, 0.0F, -0.5672F));

            PartDefinition LeftArm_r1 = Torso.addOrReplaceChild("LeftArm_r1", CubeListBuilder.create().texOffs(44, 48).addBox(1.25F, 1.0F, 0.0F, 4.0F, 9.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.0F, 0.5F, 0.0F, 0.0F, 0.0F, 0.5672F));

            PartDefinition Tail = Torso.addOrReplaceChild("Tail", CubeListBuilder.create(), PartPose.offset(0.0F, 10.75F, -0.25F));

            PartDefinition Base_r1 = Tail.addOrReplaceChild("Base_r1", CubeListBuilder.create().texOffs(0, 16).addBox(-0.5F, 5.3462F, -1.8296F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.05F))
                    .texOffs(9, 54).addBox(-0.5F, -2.5538F, -1.8296F, 1.0F, 8.0F, 3.0F, new CubeDeformation(-0.05F)), PartPose.offsetAndRotation(0.0F, 5.25F, 17.0F, 1.1345F, 0.0F, 0.0F));

            PartDefinition Base_r2 = Tail.addOrReplaceChild("Base_r2", CubeListBuilder.create().texOffs(36, 12).addBox(-0.5F, -6.1668F, 0.8821F, 1.0F, 7.0F, 1.0F, new CubeDeformation(0.0F))
                    .texOffs(16, 44).addBox(-0.5F, -8.1668F, -2.1179F, 1.0F, 10.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 4.25F, 17.0F, -0.8727F, 0.0F, 0.0F));

            PartDefinition Base_r3 = Tail.addOrReplaceChild("Base_r3", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, -0.3449F, -0.7203F, 2.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 5.0F, 13.0F, 1.4835F, 0.0F, 0.0F));

            PartDefinition Base_r4 = Tail.addOrReplaceChild("Base_r4", CubeListBuilder.create().texOffs(0, 48).addBox(-1.5F, -1.3563F, -0.6088F, 3.0F, 5.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 4.5F, 9.25F, 1.309F, 0.0F, 0.0F));

            PartDefinition Base_r5 = Tail.addOrReplaceChild("Base_r5", CubeListBuilder.create().texOffs(52, 10).addBox(-2.0F, -1.075F, -0.625F, 4.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 1.0F, 0.0F, 0.9599F, 0.0F, 0.0F));

            PartDefinition Base_r6 = Tail.addOrReplaceChild("Base_r6", CubeListBuilder.create().texOffs(64, 2).addBox(-2.0F, 0.75F, -0.8F, 4.0F, 8.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 1.0F, 0.0F, 1.1781F, 0.0F, 0.0F));

            PartDefinition RightArm = partdefinition.addOrReplaceChild("RightArm", CubeListBuilder.create().texOffs(24, 16).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-5.0F, 2.0F, 0.0F));

            PartDefinition LeftArm = partdefinition.addOrReplaceChild("LeftArm", CubeListBuilder.create().texOffs(0, 32).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(5.0F, 2.0F, 0.0F));

            PartDefinition LeftLeg = partdefinition.addOrReplaceChild("LeftLeg", CubeListBuilder.create(), PartPose.offset(2.5F, 12.0F, 0.0F));

            PartDefinition leg_r1 = LeftLeg.addOrReplaceChild("leg_r1", CubeListBuilder.create().texOffs(40, 12).addBox(-2.0F, -6.7987F, -2.9677F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 5.275F, 4.9F, 1.2217F, 0.0F, 0.0F));

            PartDefinition thigh_r1 = LeftLeg.addOrReplaceChild("thigh_r1", CubeListBuilder.create().texOffs(28, 42).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.05F)), PartPose.offsetAndRotation(0.0F, -0.1F, 0.0F, -0.0873F, 0.0F, 0.0F));

            PartDefinition LeftLower = LeftLeg.addOrReplaceChild("LeftLower", CubeListBuilder.create().texOffs(44, 43).addBox(-2.0F, 6.975F, -4.675F, 4.0F, 1.0F, 4.0F, new CubeDeformation(0.025F)), PartPose.offset(0.0F, 4.0F, 4.5F));

            PartDefinition leg_r2 = LeftLower.addOrReplaceChild("leg_r2", CubeListBuilder.create().texOffs(16, 32).addBox(-2.0F, -0.225F, -3.0F, 4.0F, 8.0F, 4.0F, new CubeDeformation(0.025F)), PartPose.offsetAndRotation(0.0F, 0.15F, -1.0F, -0.0873F, 0.0F, 0.0F));

            PartDefinition LeftLowerBeans2 = LeftLower.addOrReplaceChild("LeftLowerBeans2", CubeListBuilder.create(), PartPose.offset(-2.0F, 20.0F, -3.2F));

            PartDefinition RightLeg = partdefinition.addOrReplaceChild("RightLeg", CubeListBuilder.create(), PartPose.offset(-2.5F, 12.0F, 0.0F));

            PartDefinition leg_r3 = RightLeg.addOrReplaceChild("leg_r3", CubeListBuilder.create().texOffs(32, 32).addBox(-2.0F, -6.7987F, -2.9677F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 5.275F, 4.9F, 1.2217F, 0.0F, 0.0F));

            PartDefinition thigh_r2 = RightLeg.addOrReplaceChild("thigh_r2", CubeListBuilder.create().texOffs(40, 22).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.05F)), PartPose.offsetAndRotation(0.0F, -0.1F, 0.0F, -0.0873F, 0.0F, 0.0F));

            PartDefinition RightLower = RightLeg.addOrReplaceChild("RightLower", CubeListBuilder.create().texOffs(44, 38).addBox(-2.0F, 6.975F, -4.675F, 4.0F, 1.0F, 4.0F, new CubeDeformation(0.025F)), PartPose.offset(0.0F, 4.0F, 4.5F));

            PartDefinition leg_r4 = RightLower.addOrReplaceChild("leg_r4", CubeListBuilder.create().texOffs(32, 0).addBox(-2.0F, -0.225F, -3.0F, 4.0F, 8.0F, 4.0F, new CubeDeformation(0.025F)), PartPose.offsetAndRotation(0.0F, 0.15F, -1.0F, -0.0873F, 0.0F, 0.0F));

            PartDefinition RightLowerBeans = RightLower.addOrReplaceChild("RightLowerBeans", CubeListBuilder.create(), PartPose.offset(-2.0F, 20.0F, -3.2F));

            return LayerDefinition.create(meshdefinition, 128, 128);
        }

        public ModelPart getArm(HumanoidArm p_102852_) {
            return p_102852_ == HumanoidArm.LEFT ? this.LeftArm : this.RightArm;
        }

        public ModelPart getHead() {
            return this.Head;
        }

        public ModelPart getTorso() {
            return Torso;
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
        public LatexAnimator<LatexMantaRayMale, Remodel> getAnimator() {
            return animator;
        }
    }
}