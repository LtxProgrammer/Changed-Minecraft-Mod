package net.ltxprogrammer.changed.client.renderer.model;
// Made with Blockbench 4.1.5
// Exported for Minecraft version 1.17 with Mojang mappings
// Paste this class into your mod and generate all required imports

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.animate.AnimatorPresets;
import net.ltxprogrammer.changed.client.renderer.animate.HumanoidAnimator;
import net.ltxprogrammer.changed.entity.beast.CustomLatexEntity;
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
public class CustomLatexModel extends AdvancedHumanoidModel<CustomLatexEntity> implements AdvancedHumanoidModelInterface<CustomLatexEntity, CustomLatexModel> {
    // This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(Changed.modResource("custom_latex"), "main");
    private final ModelPart RightLeg;
    private final ModelPart LeftLeg;
    private final ModelPart RightArm;
    private final ModelPart LeftArm;
    private final ModelPart Head;
    private final ModelPart Torso;
    private final ModelPart Tail;

    private final ModelPart Abdomen;
    private final ModelPart LowerAbdomen;
    private final ModelPart LeglessTail;

    private final ModelPart ShortHair;
    private final ModelPart LongHair;

    private final ModelPart WolfEars;
    private final ModelPart CatEars;
    private final ModelPart DragonEars;
    private final ModelPart SharkEars;

    private final ModelPart GenericTorso;
    private final ModelPart ChiseledTorso;
    private final ModelPart FemaleTorso;
    private final ModelPart HeavyTorso;

    private final ModelPart WolfTail;
    private final ModelPart CatTail;
    private final ModelPart SharkTail;
    private final ModelPart DragonTail;

    private final ModelPart SharkRightArm;
    private final ModelPart SharkLeftArm;
    private final ModelPart WyvernRightArm;
    private final ModelPart WyvernLeftArm;

    private final HumanoidAnimator<CustomLatexEntity, CustomLatexModel> animator;
    private final HumanoidAnimator<CustomLatexEntity, CustomLatexModel> leglessAnimator;

    public CustomLatexModel(ModelPart root) {
        super(root);
        this.RightLeg = root.getChild("RightLeg");
        this.LeftLeg = root.getChild("LeftLeg");
        this.Head = root.getChild("Head");
        this.Torso = root.getChild("Torso");
        this.Tail = Torso.getChild("Tail");
        this.RightArm = root.getChild("RightArm");
        this.LeftArm = root.getChild("LeftArm");

        this.Abdomen = root.getChild("Abdomen");
        this.LowerAbdomen = Abdomen.getChild("LowerAbdomen");
        this.LeglessTail = LowerAbdomen.getChild("Tail2");

        this.GenericTorso = Torso.getChild("Generic");
        this.ChiseledTorso = Torso.getChild("Chiseled");
        this.FemaleTorso = Torso.getChild("Female");
        this.HeavyTorso = Torso.getChild("Heavy");

        final var ears = Head.getChild("Ears");
        this.WolfEars = ears.getChild("WolfEars");
        this.CatEars = ears.getChild("CatEars");
        this.DragonEars = ears.getChild("DragonEars");
        this.SharkEars = ears.getChild("SharkEars");

        final var hair = Head.getChild("Hair");
        this.ShortHair = hair.getChild("Short");
        this.LongHair = hair.getChild("Long");

        this.WolfTail = Tail.getChild("WolfTail");
        this.CatTail = Tail.getChild("CatTail");
        this.SharkTail = Tail.getChild("SharkTail");
        this.DragonTail = Tail.getChild("DragonTail");

        this.SharkRightArm = RightArm.getChild("RightArmShark");
        this.SharkLeftArm = LeftArm.getChild("LeftArmShark");
        this.WyvernRightArm = RightArm.getChild("RightArmWyvern");
        this.WyvernLeftArm = LeftArm.getChild("LeftArmWyvern");

        var leftLowerLeg = LeftLeg.getChild("LeftLowerLeg");
        var leftFoot = leftLowerLeg.getChild("LeftFoot");
        var rightLowerLeg = RightLeg.getChild("RightLowerLeg");
        var rightFoot = rightLowerLeg.getChild("RightFoot");

        animator = HumanoidAnimator.of(this).hipOffset(-1.5f);
        leglessAnimator = HumanoidAnimator.of(this).hipOffset(-1.5f).torsoLength(9.0f).legLength(9.5f);

        {
            var tailPrimary = WolfTail.getChild("TailPrimary");
            var tailSecondary = tailPrimary.getChild("TailSecondary");
            var tailTertiary = tailSecondary.getChild("TailTertiary");

            animator.addPreset(AnimatorPresets.wolfLike(
                    Head, WolfEars.getChild("LeftEar"), WolfEars.getChild("RightEar"),
                    Torso, LeftArm, RightArm,
                    WolfTail, List.of(tailPrimary, tailSecondary, tailTertiary),
                    LeftLeg, leftLowerLeg, leftFoot, leftFoot.getChild("LeftPad"), RightLeg, rightLowerLeg, rightFoot, rightFoot.getChild("RightPad")));
        }

        {
            var tailPrimary = CatTail.getChild("TailPrimary2");
            var tailSecondary = tailPrimary.getChild("TailSecondary2");
            var tailTertiary = tailSecondary.getChild("TailTertiary2");
            var tailQuaternary = tailTertiary.getChild("TailQuaternary");

            animator.addPreset(AnimatorPresets.catEars(CatEars.getChild("LeftEar2"), CatEars.getChild("RightEar2")))
                    .addPreset(AnimatorPresets.catTail(CatTail, List.of(tailPrimary, tailSecondary, tailTertiary, tailQuaternary)));
        }

        {
            var tailPrimary = SharkTail.getChild("TailPrimary3");
            var tailSecondary = tailPrimary.getChild("TailSecondary3");
            var tailTertiary = tailSecondary.getChild("TailTertiary3");

            animator.addPreset(AnimatorPresets.sharkTail(DragonTail, List.of(tailPrimary, tailSecondary, tailTertiary)));
        }

        {
            var tailPrimary = DragonTail.getChild("TailPrimary4");
            var tailSecondary = tailPrimary.getChild("TailSecondary4");
            var tailTertiary = tailSecondary.getChild("TailTertiary4");
            var tailQuaternary = tailTertiary.getChild("TailQuaternary2");

            animator.addPreset(AnimatorPresets.dragonTail(DragonTail, List.of(tailPrimary, tailSecondary, tailTertiary, tailQuaternary)));
        }

        {
            var tailPrimary = LeglessTail.getChild("TailPrimary5");
            var tailSecondary = tailPrimary.getChild("TailSecondary5");
            var tailTertiary = tailSecondary.getChild("TailTertiary5");
            var tailQuaternary = tailTertiary.getChild("TailQuaternary3");
            var tailQuintary = tailQuaternary.getChild("TailQuintary");

            leglessAnimator.addPreset(AnimatorPresets.leglessShark(Head, Torso, LeftArm, RightArm, Abdomen, LowerAbdomen, LeglessTail, List.of(tailPrimary, tailSecondary, tailTertiary, tailQuaternary, tailQuintary)));
        }
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition RightLeg = partdefinition.addOrReplaceChild("RightLeg", CubeListBuilder.create(), PartPose.offset(-2.5F, 10.5F, 0.0F));

        PartDefinition RightThigh_r1 = RightLeg.addOrReplaceChild("RightThigh_r1", CubeListBuilder.create().texOffs(48, 0).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 7.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.2182F, 0.0F, 0.0F));

        PartDefinition RightLowerLeg = RightLeg.addOrReplaceChild("RightLowerLeg", CubeListBuilder.create(), PartPose.offset(0.0F, 6.375F, -3.45F));

        PartDefinition RightCalf_r1 = RightLowerLeg.addOrReplaceChild("RightCalf_r1", CubeListBuilder.create().texOffs(48, 40).addBox(-1.99F, -0.125F, -2.9F, 4.0F, 6.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, -2.125F, 1.95F, 0.8727F, 0.0F, 0.0F));

        PartDefinition RightFoot = RightLowerLeg.addOrReplaceChild("RightFoot", CubeListBuilder.create(), PartPose.offset(0.0F, 0.8F, 7.175F));

        PartDefinition RightArch_r1 = RightFoot.addOrReplaceChild("RightArch_r1", CubeListBuilder.create().texOffs(56, 11).addBox(-2.0F, -8.45F, -0.725F, 4.0F, 6.0F, 3.0F, new CubeDeformation(0.005F)), PartPose.offsetAndRotation(0.0F, 7.075F, -4.975F, -0.3491F, 0.0F, 0.0F));

        PartDefinition RightPad = RightFoot.addOrReplaceChild("RightPad", CubeListBuilder.create().texOffs(52, 32).addBox(-2.0F, 0.0F, -2.5F, 4.0F, 2.0F, 5.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 4.325F, -4.425F));

        PartDefinition LeftLeg = partdefinition.addOrReplaceChild("LeftLeg", CubeListBuilder.create(), PartPose.offset(2.5F, 10.5F, 0.0F));

        PartDefinition LeftThigh_r1 = LeftLeg.addOrReplaceChild("LeftThigh_r1", CubeListBuilder.create().texOffs(32, 44).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 7.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.2182F, 0.0F, 0.0F));

        PartDefinition LeftLowerLeg = LeftLeg.addOrReplaceChild("LeftLowerLeg", CubeListBuilder.create(), PartPose.offset(0.0F, 6.375F, -3.45F));

        PartDefinition LeftCalf_r1 = LeftLowerLeg.addOrReplaceChild("LeftCalf_r1", CubeListBuilder.create().texOffs(48, 22).addBox(-2.01F, -0.125F, -2.9F, 4.0F, 6.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, -2.125F, 1.95F, 0.8727F, 0.0F, 0.0F));

        PartDefinition LeftFoot = LeftLowerLeg.addOrReplaceChild("LeftFoot", CubeListBuilder.create(), PartPose.offset(0.0F, 0.8F, 7.175F));

        PartDefinition LeftArch_r1 = LeftFoot.addOrReplaceChild("LeftArch_r1", CubeListBuilder.create().texOffs(13, 57).addBox(-2.0F, -8.45F, -0.725F, 4.0F, 6.0F, 3.0F, new CubeDeformation(0.005F)), PartPose.offsetAndRotation(0.0F, 7.075F, -4.975F, -0.3491F, 0.0F, 0.0F));

        PartDefinition LeftPad = LeftFoot.addOrReplaceChild("LeftPad", CubeListBuilder.create().texOffs(24, 0).addBox(-2.0F, 0.0F, -2.5F, 4.0F, 2.0F, 5.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 4.325F, -4.425F));

        {
            PartDefinition Abdomen = partdefinition.addOrReplaceChild("Abdomen", CubeListBuilder.create().texOffs(28, 32).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 4.0F, 4.0F, new CubeDeformation(0.2F)), PartPose.offset(0.0F, 8.0F, 0.0F));
            PartDefinition LowerAbdomen = Abdomen.addOrReplaceChild("LowerAbdomen", CubeListBuilder.create().texOffs(28, 28).addBox(-4.5F, -1.25F, -2.5F, 9.0F, 7.0F, 5.0F, new CubeDeformation(0.06F)), PartPose.offset(0.0F, 4.25F, 0.0F));
            PartDefinition Tail2 = LowerAbdomen.addOrReplaceChild("Tail2", CubeListBuilder.create().texOffs(28, 32).addBox(-4.0F, -0.75F, -2.0F, 8.0F, 6.0F, 4.0F, new CubeDeformation(0.25F)), PartPose.offset(0.0F, 5.5F, 0.0F));
            PartDefinition TailPrimary5 = Tail2.addOrReplaceChild("TailPrimary5", CubeListBuilder.create().texOffs(28, 32).addBox(-3.5F, -0.25F, -2.0F, 7.0F, 5.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 4.5F, 0.0F));
            PartDefinition TailSecondary5 = TailPrimary5.addOrReplaceChild("TailSecondary5", CubeListBuilder.create().texOffs(29, 32).addBox(-3.0F, -0.25F, -2.0F, 6.0F, 5.0F, 4.0F, new CubeDeformation(-0.25F)), PartPose.offset(0.0F, 4.25F, 0.0F));
            PartDefinition TailTertiary5 = TailSecondary5.addOrReplaceChild("TailTertiary5", CubeListBuilder.create().texOffs(29, 32).addBox(-2.0F, -0.25F, -1.5F, 4.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 4.5F, 0.0F));
            PartDefinition TailQuaternary3 = TailTertiary5.addOrReplaceChild("TailQuaternary3", CubeListBuilder.create().texOffs(31, 31).addBox(-1.5F, 0.0F, -1.5F, 3.0F, 4.0F, 3.0F, new CubeDeformation(-0.05F)), PartPose.offset(0.0F, 2.55F, 0.0F));
            PartDefinition TailQuintary = TailQuaternary3.addOrReplaceChild("TailQuintary", CubeListBuilder.create(), PartPose.offset(0.0F, 2.5F, -0.5F));
            PartDefinition Base_r1 = TailQuintary.addOrReplaceChild("Base_r1", CubeListBuilder.create().texOffs(20, 49).addBox(-0.5F, 9.4311F, 0.1673F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.05F))
                .texOffs(32, 30).addBox(-0.5F, 1.5311F, 0.1673F, 1.0F, 8.0F, 3.0F, new CubeDeformation(-0.05F)), PartPose.offsetAndRotation(0.0F, 0.5834F, -0.2961F, -0.3491F, 0.0F, 0.0F));
            PartDefinition Base_r2 = TailQuintary.addOrReplaceChild("Base_r2", CubeListBuilder.create().texOffs(5, 48).addBox(-0.5F, -10.3457F, 2.9744F, 1.0F, 7.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(7, 46).addBox(-0.5F, -12.3457F, -0.0256F, 1.0F, 10.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.5834F, -0.2961F, -2.3562F, 0.0F, 0.0F));
            PartDefinition Base_r3 = TailQuintary.addOrReplaceChild("Base_r3", CubeListBuilder.create().texOffs(6, 48).addBox(-1.0F, 0.1701F, -0.3405F, 2.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.5834F, -0.2961F, 0.0F, 0.0F, 0.0F));
        }

        PartDefinition Head = partdefinition.addOrReplaceChild("Head", CubeListBuilder.create().texOffs(0, 16).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, CubeDeformation.NONE)
                .texOffs(15, 32).addBox(-2.0F, -3.0F, -6.0F, 4.0F, 2.0F, 2.0F, CubeDeformation.NONE)
                .texOffs(24, 22).addBox(-1.5F, -1.0F, -5.0F, 3.0F, 1.0F, 1.0F, CubeDeformation.NONE), PartPose.offset(0.0F, -0.5F, 0.0F));

        PartDefinition Ears = Head.addOrReplaceChild("Ears", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition WolfEars = Ears.addOrReplaceChild("WolfEars", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition RightEar = WolfEars.addOrReplaceChild("RightEar", CubeListBuilder.create(), PartPose.offset(-3.0F, -7.5F, 0.0F));

        PartDefinition RightEarPivot = RightEar.addOrReplaceChild("RightEarPivot", CubeListBuilder.create().texOffs(0, 4).addBox(-1.9F, -1.2F, -1.0F, 3.0F, 3.0F, 1.0F, new CubeDeformation(0.05F))
                .texOffs(0, 16).addBox(-0.9F, -1.6F, -0.4F, 2.0F, 3.0F, 1.0F, new CubeDeformation(0.04F))
                .texOffs(32, 22).addBox(-0.9F, -2.3F, -1.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.05F))
                .texOffs(24, 0).addBox(0.1F, -3.1F, -1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.05F)), PartPose.offsetAndRotation(0.5F, -1.25F, 0.0F, -0.1309F, 0.5236F, -0.3491F));

        PartDefinition LeftEar = WolfEars.addOrReplaceChild("LeftEar", CubeListBuilder.create(), PartPose.offset(3.0F, -7.5F, 0.0F));

        PartDefinition LeftEarPivot = LeftEar.addOrReplaceChild("LeftEarPivot", CubeListBuilder.create().texOffs(0, 0).addBox(-1.1F, -1.2F, -1.0F, 3.0F, 3.0F, 1.0F, new CubeDeformation(0.05F))
                .texOffs(0, 20).addBox(-1.1F, -1.6F, -0.4F, 2.0F, 3.0F, 1.0F, new CubeDeformation(0.04F))
                .texOffs(32, 24).addBox(-1.1F, -2.3F, -1.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.05F))
                .texOffs(0, 32).addBox(-1.1F, -3.1F, -1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.05F)), PartPose.offsetAndRotation(-0.5F, -1.25F, 0.0F, -0.1309F, -0.5236F, 0.3491F));

        PartDefinition CatEars = Ears.addOrReplaceChild("CatEars", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition RightEar2 = CatEars.addOrReplaceChild("RightEar2", CubeListBuilder.create(), PartPose.offset(-2.5F, -5.0F, 0.0F));

        PartDefinition rightear_r1 = RightEar2.addOrReplaceChild("rightear_r1", CubeListBuilder.create().texOffs(29, 30).addBox(4.25F, -31.25F, -18.25F, 2.0F, 5.0F, 3.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(2.5F, 30.0F, 0.0F, -0.5236F, -0.1745F, -0.2618F));

        PartDefinition LeftEar2 = CatEars.addOrReplaceChild("LeftEar2", CubeListBuilder.create(), PartPose.offset(2.5F, -5.0F, 0.0F));

        PartDefinition leftear_r1 = LeftEar2.addOrReplaceChild("leftear_r1", CubeListBuilder.create().texOffs(30, 29).addBox(-6.25F, -31.25F, -18.25F, 2.0F, 5.0F, 3.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-2.5F, 30.0F, 0.0F, -0.5236F, 0.1745F, 0.2618F));

        PartDefinition DragonEars = Ears.addOrReplaceChild("DragonEars", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition RightEar3 = DragonEars.addOrReplaceChild("RightEar3", CubeListBuilder.create(), PartPose.offset(0.0F, 25.0F, 0.3F));

        PartDefinition rightear_r2 = RightEar3.addOrReplaceChild("rightear_r2", CubeListBuilder.create().texOffs(31, 32).addBox(4.0F, -24.8F, -22.0F, 2.0F, 6.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(30, 29).addBox(4.0F, -26.0F, -25.0F, 2.0F, 8.0F, 3.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.8727F, -0.1309F, -0.2618F));

        PartDefinition LeftEar3 = DragonEars.addOrReplaceChild("LeftEar3", CubeListBuilder.create(), PartPose.offset(0.0F, 25.0F, 0.3F));

        PartDefinition leftear_r2 = LeftEar3.addOrReplaceChild("leftear_r2", CubeListBuilder.create().texOffs(33, 31).mirror().addBox(-6.0F, -24.8F, -22.0F, 2.0F, 6.0F, 1.0F, CubeDeformation.NONE).mirror(false)
                .texOffs(30, 29).addBox(-6.0F, -26.0F, -25.0F, 2.0F, 8.0F, 3.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.8727F, 0.1309F, 0.2618F));

        PartDefinition SharkEars = Ears.addOrReplaceChild("SharkEars", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition HeadFin_r1 = SharkEars.addOrReplaceChild("HeadFin_r1", CubeListBuilder.create().texOffs(30, 33).addBox(-0.25F, -1.0F, 0.0F, 6.0F, 3.0F, 2.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(3.0F, -6.5F, -1.0F, 1.0263F, -0.733F, -0.9599F));

        PartDefinition HeadFin_r2 = SharkEars.addOrReplaceChild("HeadFin_r2", CubeListBuilder.create().texOffs(31, 31).addBox(-0.25F, -2.0F, 0.0F, 6.0F, 3.0F, 2.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-3.0F, -6.5F, -1.0F, -1.0263F, -0.733F, -2.1817F));

        PartDefinition Hair = Head.addOrReplaceChild("Hair", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition Short = Hair.addOrReplaceChild("Short", CubeListBuilder.create().texOffs(24, 8).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 6.0F, 8.0F, new CubeDeformation(0.3F))
                .texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.2F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition Long = Hair.addOrReplaceChild("Long", CubeListBuilder.create().texOffs(64, 31).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 11.0F, 8.0F, new CubeDeformation(0.2F))
                .texOffs(64, 50).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 10.0F, 8.0F, new CubeDeformation(0.35F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition Torso = partdefinition.addOrReplaceChild("Torso", CubeListBuilder.create(), PartPose.offset(0.0F, -0.5F, 0.0F));

        PartDefinition Generic = Torso.addOrReplaceChild("Generic", CubeListBuilder.create().texOffs(28, 28).addBox(-4.0F, -26.0F, -2.0F, 8.0F, 12.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 26.0F, 0.0F));

        PartDefinition Chiseled = Torso.addOrReplaceChild("Chiseled", CubeListBuilder.create().texOffs(28, 28).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 6.0F, 4.0F, CubeDeformation.NONE)
                .texOffs(28, 33).addBox(-4.0F, 5.5F, -2.0F, 8.0F, 4.0F, 4.0F, new CubeDeformation(-0.2F))
                .texOffs(28, 37).addBox(-4.0F, 9.0F, -2.0F, 8.0F, 3.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition Muscles = Chiseled.addOrReplaceChild("Muscles", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition cube_r1 = Muscles.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(30, 30).mirror().addBox(0.3F, -1.0F, 0.0F, 4.0F, 4.0F, 2.0F, new CubeDeformation(0.1F)).mirror(false), PartPose.offsetAndRotation(-0.15F, 1.25F, -2.25F, -0.0436F, -0.0087F, 0.0004F));

        PartDefinition cube_r2 = Muscles.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(30, 30).addBox(-4.0F, -1.0F, 0.0F, 4.0F, 4.0F, 2.0F, new CubeDeformation(0.1F)), PartPose.offsetAndRotation(-0.15F, 1.25F, -2.25F, -0.0436F, 0.0087F, -0.0004F));

        PartDefinition Abs = Muscles.addOrReplaceChild("Abs", CubeListBuilder.create(), PartPose.offset(0.0F, 4.3F, 0.2F));

        PartDefinition cube_r3 = Abs.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(32, 33).addBox(-2.25F, 1.0F, 0.2F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.175F)), PartPose.offsetAndRotation(-0.05F, 2.45F, -2.225F, 0.0F, 0.0087F, 0.0F));

        PartDefinition cube_r4 = Abs.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(33, 36).addBox(0.25F, 1.0F, 0.2F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.175F)), PartPose.offsetAndRotation(0.05F, 2.45F, -2.225F, 0.0F, -0.0087F, 0.0F));

        PartDefinition cube_r5 = Abs.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(30, 36).addBox(-3.25F, -0.5F, 0.2F, 3.0F, 1.0F, 2.0F, new CubeDeformation(0.2F)), PartPose.offsetAndRotation(-0.05F, 2.45F, -2.2F, 0.0F, 0.0087F, 0.0F));

        PartDefinition cube_r6 = Abs.addOrReplaceChild("cube_r6", CubeListBuilder.create().texOffs(30, 36).mirror().addBox(0.25F, -0.5F, 0.2F, 3.0F, 1.0F, 2.0F, new CubeDeformation(0.2F)).mirror(false), PartPose.offsetAndRotation(0.05F, 2.45F, -2.2F, 0.0F, -0.0087F, 0.0F));

        PartDefinition cube_r7 = Abs.addOrReplaceChild("cube_r7", CubeListBuilder.create().texOffs(30, 36).addBox(-3.25F, -2.0F, 0.2F, 3.0F, 1.0F, 2.0F, new CubeDeformation(0.225F)), PartPose.offsetAndRotation(-0.05F, 2.35F, -2.45F, 0.0F, 0.0087F, 0.0F));

        PartDefinition cube_r8 = Abs.addOrReplaceChild("cube_r8", CubeListBuilder.create().texOffs(30, 36).mirror().addBox(0.25F, -2.0F, 0.2F, 3.0F, 1.0F, 2.0F, new CubeDeformation(0.225F)).mirror(false), PartPose.offsetAndRotation(0.05F, 2.35F, -2.45F, 0.0F, -0.0087F, 0.0F));

        PartDefinition Female = Torso.addOrReplaceChild("Female", CubeListBuilder.create(), PartPose.offset(0.0F, -0.5F, 0.0F));

        PartDefinition TorsoLower_r1 = Female.addOrReplaceChild("TorsoLower_r1", CubeListBuilder.create().texOffs(0, 66).addBox(-4.0F, -8.0F, -2.0F, 8.0F, 6.0F, 4.0F, new CubeDeformation(-0.3F))
                .texOffs(0, 80).addBox(-4.0F, -12.0F, -2.0F, 8.0F, 5.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 12.5F, 0.0F, 0.0F, 0.0F, 0.0F));

        PartDefinition Waist_r1 = Female.addOrReplaceChild("Waist_r1", CubeListBuilder.create().texOffs(0, 89).addBox(-4.0F, -3.4F, -2.0F, 8.0F, 3.0F, 4.0F, new CubeDeformation(0.1F)), PartPose.offsetAndRotation(0.0F, 12.75F, 0.0F, 0.0F, 0.0F, 0.0F));

        PartDefinition Plantoids = Female.addOrReplaceChild("Plantoids", CubeListBuilder.create(), PartPose.offset(0.0F, 0.5F, -2.0F));

        PartDefinition RightPlantoid_r1 = Plantoids.addOrReplaceChild("RightPlantoid_r1", CubeListBuilder.create().texOffs(24, 84).addBox(-4.25F, -1.7F, -0.8F, 4.0F, 4.0F, 2.0F, new CubeDeformation(-0.03F))
                .texOffs(24, 90).addBox(0.25F, -1.7F, -0.8F, 4.0F, 4.0F, 2.0F, new CubeDeformation(-0.03F)), PartPose.offsetAndRotation(0.0F, 2.0F, 0.0F, -0.2793F, 0.0F, 0.0F));

        PartDefinition Center_r1 = Plantoids.addOrReplaceChild("Center_r1", CubeListBuilder.create().texOffs(20, 89).addBox(-0.5F, -1.3F, -0.1F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.2F)), PartPose.offsetAndRotation(0.0F, 2.0F, 0.0F, -0.192F, 0.0F, 0.0F));

        PartDefinition Heavy = Torso.addOrReplaceChild("Heavy", CubeListBuilder.create().texOffs(28, 28).addBox(-4.0F, -26.0F, -2.0F, 8.0F, 12.0F, 4.0F, CubeDeformation.NONE)
                .texOffs(28, 34).addBox(-4.0F, -20.0F, -2.15F, 8.0F, 5.0F, 4.0F, new CubeDeformation(0.25F)), PartPose.offset(0.0F, 26.0F, 0.0F));

        PartDefinition Tail = Torso.addOrReplaceChild("Tail", CubeListBuilder.create(), PartPose.offset(0.0F, 10.5F, 0.0F));

        PartDefinition WolfTail = Tail.addOrReplaceChild("WolfTail", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition TailPrimary = WolfTail.addOrReplaceChild("TailPrimary", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.1309F, 0.0F, 0.0F));

        PartDefinition Base_r1 = TailPrimary.addOrReplaceChild("Base_r1", CubeListBuilder.create().texOffs(48, 50).addBox(-2.0F, 0.75F, -1.5F, 4.0F, 5.0F, 4.0F, new CubeDeformation(-0.2F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 1.1781F, 0.0F, 0.0F));

        PartDefinition TailSecondary = TailPrimary.addOrReplaceChild("TailSecondary", CubeListBuilder.create(), PartPose.offset(0.0F, 1.25F, 4.5F));

        PartDefinition Base_r2 = TailSecondary.addOrReplaceChild("Base_r2", CubeListBuilder.create().texOffs(0, 32).addBox(-2.5F, -0.45F, -2.1F, 5.0F, 7.0F, 5.0F, new CubeDeformation(-0.1F)), PartPose.offsetAndRotation(0.0F, 0.5F, 0.0F, 1.4835F, 0.0F, 0.0F));

        PartDefinition TailTertiary = TailSecondary.addOrReplaceChild("TailTertiary", CubeListBuilder.create(), PartPose.offset(0.0F, 0.75F, 2.5F));

        PartDefinition Base_r3 = TailTertiary.addOrReplaceChild("Base_r3", CubeListBuilder.create().texOffs(28, 55).addBox(-2.0F, -1.2F, -1.95F, 4.0F, 4.0F, 4.0F, new CubeDeformation(-0.15F)), PartPose.offsetAndRotation(0.0F, 0.0F, 4.0F, 1.8326F, 0.0F, 0.0F));

        PartDefinition CatTail = Tail.addOrReplaceChild("CatTail", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition TailPrimary2 = CatTail.addOrReplaceChild("TailPrimary2", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.1309F, 0.0F, 0.0F));

        PartDefinition Base_r4 = TailPrimary2.addOrReplaceChild("Base_r4", CubeListBuilder.create().texOffs(31, 31).addBox(-2.0F, 0.75F, -1.5F, 4.0F, 5.0F, 4.0F, new CubeDeformation(-0.2F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 1.0908F, 0.0F, 0.0F));

        PartDefinition TailSecondary2 = TailPrimary2.addOrReplaceChild("TailSecondary2", CubeListBuilder.create(), PartPose.offset(0.0F, 1.25F, 4.5F));

        PartDefinition Base_r5 = TailSecondary2.addOrReplaceChild("Base_r5", CubeListBuilder.create().texOffs(29, 29).addBox(-2.5F, -0.45F, -2.5F, 5.0F, 6.0F, 5.0F, new CubeDeformation(-0.3F)), PartPose.offsetAndRotation(0.0F, 0.5F, 0.0F, 1.3526F, 0.0F, 0.0F));

        PartDefinition TailTertiary2 = TailSecondary2.addOrReplaceChild("TailTertiary2", CubeListBuilder.create(), PartPose.offset(0.0F, 0.75F, 4.5F));

        PartDefinition Base_r6 = TailTertiary2.addOrReplaceChild("Base_r6", CubeListBuilder.create().texOffs(28, 29).addBox(-2.5F, 4.55F, -3.3F, 5.0F, 8.0F, 5.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, -0.25F, -4.5F, 1.5272F, 0.0F, 0.0F));

        PartDefinition TailQuaternary = TailTertiary2.addOrReplaceChild("TailQuaternary", CubeListBuilder.create(), PartPose.offset(0.0F, 1.0F, 7.5F));

        PartDefinition Base_r7 = TailQuaternary.addOrReplaceChild("Base_r7", CubeListBuilder.create().texOffs(28, 30).addBox(-2.0F, 5.5F, -3.8F, 4.0F, 3.0F, 4.0F, new CubeDeformation(0.15F)), PartPose.offsetAndRotation(0.0F, -1.0F, -5.5F, 1.7017F, 0.0F, 0.0F));

        PartDefinition SharkTail = Tail.addOrReplaceChild("SharkTail", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition TailPrimary3 = SharkTail.addOrReplaceChild("TailPrimary3", CubeListBuilder.create(), PartPose.offset(0.0F, -0.5F, 0.75F));

        PartDefinition TailFin_r1 = TailPrimary3.addOrReplaceChild("TailFin_r1", CubeListBuilder.create().texOffs(35, 33).addBox(-4.0F, 4.0F, -0.75F, 1.0F, 4.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(35, 31).addBox(-4.0F, 0.0F, 0.25F, 1.0F, 9.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(3.5F, 1.75F, 1.0F, 1.789F, 0.0F, 0.0F));

        PartDefinition Base_r8 = TailPrimary3.addOrReplaceChild("Base_r8", CubeListBuilder.create().texOffs(31, 36).addBox(-2.0F, -1.075F, 0.375F, 4.0F, 2.0F, 2.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.875F, 0.85F, 1.9199F, 0.0F, 0.0F));

        PartDefinition Base_r9 = TailPrimary3.addOrReplaceChild("Base_r9", CubeListBuilder.create().texOffs(32, 28).addBox(-2.0F, 0.75F, -0.8F, 4.0F, 8.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 1.0F, -1.0F, 1.1781F, 0.0F, 0.0F));

        PartDefinition TailSecondary3 = TailPrimary3.addOrReplaceChild("TailSecondary3", CubeListBuilder.create(), PartPose.offset(0.0F, 3.25F, 7.25F));

        PartDefinition Base_r10 = TailSecondary3.addOrReplaceChild("Base_r10", CubeListBuilder.create().texOffs(34, 29).addBox(-1.5F, -1.3563F, -0.6088F, 3.0F, 5.0F, 3.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 1.25F, 1.0F, 1.309F, 0.0F, 0.0F));

        PartDefinition TailTertiary3 = TailSecondary3.addOrReplaceChild("TailTertiary3", CubeListBuilder.create(), PartPose.offset(0.0F, 1.5F, 4.5F));

        PartDefinition Base_r11 = TailTertiary3.addOrReplaceChild("Base_r11", CubeListBuilder.create().texOffs(34, 33).addBox(-0.5F, 5.3462F, -1.8296F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.05F))
                .texOffs(34, 31).addBox(-0.5F, -2.5538F, -1.8296F, 1.0F, 8.0F, 3.0F, new CubeDeformation(-0.05F)), PartPose.offsetAndRotation(0.0F, 0.5F, 4.25F, 1.1345F, 0.0F, 0.0F));

        PartDefinition Base_r12 = TailTertiary3.addOrReplaceChild("Base_r12", CubeListBuilder.create().texOffs(36, 34).addBox(-0.5F, -6.1668F, 0.8821F, 1.0F, 7.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(31, 28).addBox(-0.5F, -8.1668F, -2.1179F, 1.0F, 10.0F, 3.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, -0.5F, 4.25F, -0.8727F, 0.0F, 0.0F));

        PartDefinition Base_r13 = TailTertiary3.addOrReplaceChild("Base_r13", CubeListBuilder.create().texOffs(32, 32).addBox(-1.0F, -0.3449F, -0.7203F, 2.0F, 5.0F, 2.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.25F, 0.25F, 1.4835F, 0.0F, 0.0F));

        PartDefinition DragonTail = Tail.addOrReplaceChild("DragonTail", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition TailPrimary4 = DragonTail.addOrReplaceChild("TailPrimary4", CubeListBuilder.create(), PartPose.offset(0.0F, 0.5F, 0.0F));

        PartDefinition Base_r14 = TailPrimary4.addOrReplaceChild("Base_r14", CubeListBuilder.create().texOffs(28, 31).addBox(-2.0F, -2.9F, 0.4F, 4.0F, 4.0F, 4.0F, new CubeDeformation(-0.3F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.5236F, 0.0F, 0.0F));

        PartDefinition TailSecondary4 = TailPrimary4.addOrReplaceChild("TailSecondary4", CubeListBuilder.create(), PartPose.offset(0.0F, 1.0F, 3.5F));

        PartDefinition Base_r15 = TailSecondary4.addOrReplaceChild("Base_r15", CubeListBuilder.create().texOffs(28, 31).addBox(-1.5F, -1.4F, -2.7F, 3.0F, 3.0F, 6.0F, new CubeDeformation(-0.1F)), PartPose.offsetAndRotation(0.0F, 1.0F, 2.5F, -0.3927F, 0.0F, 0.0F));

        PartDefinition TailTertiary4 = TailSecondary4.addOrReplaceChild("TailTertiary4", CubeListBuilder.create(), PartPose.offset(0.0F, 2.5F, 5.0F));

        PartDefinition Base_r16 = TailTertiary4.addOrReplaceChild("Base_r16", CubeListBuilder.create().texOffs(29, 30).addBox(-1.5F, -13.225F, 6.6F, 3.0F, 3.0F, 6.0F, new CubeDeformation(-0.32F)), PartPose.offsetAndRotation(0.0F, 10.5F, -8.5F, -0.1309F, 0.0F, 0.0F));

        PartDefinition TailQuaternary2 = TailTertiary4.addOrReplaceChild("TailQuaternary2", CubeListBuilder.create(), PartPose.offset(0.0F, 0.5F, 4.5F));

        PartDefinition Base_r17 = TailQuaternary2.addOrReplaceChild("Base_r17", CubeListBuilder.create().texOffs(31, 31).addBox(-1.0F, -10.45F, 13.5F, 2.0F, 2.0F, 4.0F, new CubeDeformation(-0.05F)), PartPose.offsetAndRotation(0.0F, 10.0F, -13.0F, 0.0436F, 0.0F, 0.0F));

        PartDefinition RightArm = partdefinition.addOrReplaceChild("RightArm", CubeListBuilder.create().texOffs(16, 40).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(-5.0F, 1.5F, 0.0F));

        PartDefinition RightArmShark = RightArm.addOrReplaceChild("RightArmShark", CubeListBuilder.create(), PartPose.offset(-2.0789F, 2.8746F, 1.1151F));

        PartDefinition Spike_r1 = RightArmShark.addOrReplaceChild("Spike_r1", CubeListBuilder.create().texOffs(36, 33).addBox(-0.5F, -1.5F, -1.0F, 1.0F, 4.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(32, 31).addBox(-0.5F, -2.5F, -2.0F, 1.0F, 5.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.6425F, 0.8346F, 3.1091F));

        PartDefinition RightArmWyvern = RightArm.addOrReplaceChild("RightArmWyvern", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition Wing_r1 = RightArmWyvern.addOrReplaceChild("Wing_r1", CubeListBuilder.create().texOffs(4, 2).addBox(-6.0F, -9.1F, 17.0F, 0.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(6, 37).addBox(-6.0F, -10.1F, 14.0F, 0.0F, 1.0F, 4.0F, CubeDeformation.NONE)
                .texOffs(30, 36).addBox(-6.0F, -12.1F, 12.0F, 0.0F, 2.0F, 6.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(4.0F, 23.0F, 0.0F, 0.6545F, -0.2182F, 0.0F));

        PartDefinition Wing_r2 = RightArmWyvern.addOrReplaceChild("Wing_r2", CubeListBuilder.create().texOffs(28, 32).addBox(-6.5F, -12.0F, 13.0F, 1.0F, 1.0F, 6.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(4.0F, 23.0F, 0.0F, 0.6981F, -0.2182F, 0.0F));

        PartDefinition Wing_r3 = RightArmWyvern.addOrReplaceChild("Wing_r3", CubeListBuilder.create().texOffs(6, 32).addBox(-6.0F, -10.75F, 17.0F, 0.0F, 4.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(4.0F, 23.0F, 0.0F, 0.9599F, -0.2182F, 0.0F));

        PartDefinition Wing_r4 = RightArmWyvern.addOrReplaceChild("Wing_r4", CubeListBuilder.create().texOffs(0, 48).addBox(-6.5F, -13.89F, 7.68F, 1.0F, 1.0F, 7.0F, new CubeDeformation(0.05F)), PartPose.offsetAndRotation(4.0F, 23.0F, 0.0F, 0.3491F, -0.2182F, 0.0F));

        PartDefinition LeftArm = partdefinition.addOrReplaceChild("LeftArm", CubeListBuilder.create().texOffs(0, 44).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(5.0F, 1.5F, 0.0F));

        PartDefinition LeftArmShark = LeftArm.addOrReplaceChild("LeftArmShark", CubeListBuilder.create(), PartPose.offset(2.6568F, 2.0711F, 1.6568F));

        PartDefinition Spike_r2 = LeftArmShark.addOrReplaceChild("Spike_r2", CubeListBuilder.create().texOffs(8, 48).addBox(0.875F, -1.5F, -0.5F, 1.0F, 5.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(7, 48).addBox(-0.125F, -0.5F, -0.5F, 1.0F, 4.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.4796F, -0.6979F, 0.7102F));

        PartDefinition LeftArmWyvern = LeftArm.addOrReplaceChild("LeftArmWyvern", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition Wing_r5 = LeftArmWyvern.addOrReplaceChild("Wing_r5", CubeListBuilder.create().texOffs(3, 1).addBox(6.0F, -9.1F, 17.0F, 0.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(9, 35).addBox(6.0F, -10.1F, 14.0F, 0.0F, 1.0F, 4.0F, CubeDeformation.NONE)
                .texOffs(21, 45).addBox(6.0F, -12.1F, 12.0F, 0.0F, 2.0F, 6.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-4.0F, 23.0F, 0.0F, 0.6545F, 0.2182F, 0.0F));

        PartDefinition Wing_r6 = LeftArmWyvern.addOrReplaceChild("Wing_r6", CubeListBuilder.create().texOffs(28, 32).addBox(5.5F, -12.0F, 13.0F, 1.0F, 1.0F, 6.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-4.0F, 23.0F, 0.0F, 0.6981F, 0.2182F, 0.0F));

        PartDefinition Wing_r7 = LeftArmWyvern.addOrReplaceChild("Wing_r7", CubeListBuilder.create().texOffs(9, 19).addBox(6.0F, -10.75F, 17.0F, 0.0F, 4.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-4.0F, 23.0F, 0.0F, 0.9599F, 0.2182F, 0.0F));

        PartDefinition Wing_r8 = LeftArmWyvern.addOrReplaceChild("Wing_r8", CubeListBuilder.create().texOffs(28, 30).addBox(5.5F, -13.89F, 7.68F, 1.0F, 1.0F, 7.0F, new CubeDeformation(0.05F)), PartPose.offsetAndRotation(-4.0F, 23.0F, 0.0F, 0.3491F, 0.2182F, 0.0F));

        return LayerDefinition.create(meshdefinition, 96, 96);
    }

    protected void prepareTorso(CustomLatexEntity.TorsoType type) {
        GenericTorso.visible = false;
        ChiseledTorso.visible = false;
        FemaleTorso.visible = false;
        HeavyTorso.visible = false;

        switch (type) {
            case GENERIC -> GenericTorso.visible = true;
            case CHISELED -> ChiseledTorso.visible = true;
            case FEMALE -> FemaleTorso.visible = true;
            case HEAVY -> HeavyTorso.visible = true;
        }
    }

    protected void prepareHair(CustomLatexEntity.HairType type) {
        ShortHair.visible = false;
        LongHair.visible = false;

        switch (type) {
            case SHORT -> ShortHair.visible = true;
            case LONG -> LongHair.visible = true;
        }
    }

    protected void prepareEars(CustomLatexEntity.EarType type) {
        WolfEars.visible = false;
        CatEars.visible = false;
        DragonEars.visible = false;
        SharkEars.visible = false;

        switch (type) {
            case WOLF -> WolfEars.visible = true;
            case CAT -> CatEars.visible = true;
            case DRAGON -> DragonEars.visible = true;
            case SHARK -> SharkEars.visible = true;
        }
    }

    protected void prepareTail(CustomLatexEntity.TailType type) {
        WolfTail.visible = false;
        CatTail.visible = false;
        DragonTail.visible = false;
        SharkTail.visible = false;

        switch (type) {
            case WOLF -> WolfTail.visible = true;
            case CAT -> CatTail.visible = true;
            case DRAGON -> DragonTail.visible = true;
            case SHARK -> SharkTail.visible = true;
        }
    }

    protected void prepareLegs(CustomLatexEntity.LegType type) {
        RightLeg.visible = false;
        LeftLeg.visible = false;
        Abdomen.visible = false;

        switch (type) {
            case BIPEDAL -> {
                RightLeg.visible = true;
                LeftLeg.visible = true;
                Tail.visible = true;
            }
            case MERMAID -> {
                Abdomen.visible = true;
                Tail.visible = false;
            }
        }
    }

    protected void prepareArms(CustomLatexEntity.ArmType type) {
        WyvernRightArm.visible = false;
        WyvernLeftArm.visible = false;
        SharkRightArm.visible = false;
        SharkLeftArm.visible = false;

        switch (type) {
            case WYVERN -> {
                WyvernRightArm.visible = true;
                WyvernLeftArm.visible = true;
            }
            case SHARK -> {
                SharkRightArm.visible = true;
                SharkLeftArm.visible = true;
            }
        }
    }

    public void prepareVisibility(CustomLatexEntity entity) {
        this.prepareTorso(entity.getTorsoType());
        this.prepareHair(entity.getHairType());
        this.prepareEars(entity.getEarType());
        this.prepareTail(entity.getTailType());
        this.prepareLegs(entity.getLegType());
        this.prepareArms(entity.getArmType());
    }

    @Override
    public void prepareMobModel(CustomLatexEntity p_102861_, float p_102862_, float p_102863_, float p_102864_) {
        this.prepareMobModel(getAnimator(p_102861_), p_102861_, p_102862_, p_102863_, p_102864_);
        this.prepareVisibility(p_102861_);
    }

    public void setupHand(CustomLatexEntity entity) {
        getAnimator(entity).setupHand();
    }

    @Override
    public void setupAnim(@NotNull CustomLatexEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        getAnimator(entity).setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
    }

    public ModelPart getArm(HumanoidArm p_102852_) {
        return p_102852_ == HumanoidArm.LEFT ? this.LeftArm : this.RightArm;
    }

    public ModelPart getLeg(HumanoidArm p_102852_) {
        return p_102852_ == HumanoidArm.LEFT ? this.LeftLeg : this.RightLeg;
    }

    public ModelPart getHead() {
        return this.Head;
    }

    public ModelPart getTorso() {
        return Torso;
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        Abdomen.render(poseStack, buffer, packedLight, packedOverlay, red, green ,blue, alpha);
        RightLeg.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        LeftLeg.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        Head.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        Torso.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        RightArm.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        LeftArm.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }

    @Override
    public HumanoidAnimator<CustomLatexEntity, CustomLatexModel> getAnimator(CustomLatexEntity entity) {
        if (entity.getLegType() == CustomLatexEntity.LegType.MERMAID)
            return leglessAnimator;
        else
            return animator;
    }
}