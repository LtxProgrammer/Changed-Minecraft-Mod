package net.ltxprogrammer.changed.client.tfanimations;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.animations.Limb;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorModel;
import net.ltxprogrammer.changed.util.Cacheable;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;

import java.util.Arrays;
import java.util.EnumMap;

/**
 * Provides player limb shaped parts, that are pre-divided into their joints. So the animator has an easier time lerping them
 */
public class TransfurHelper {
    public static final ModelLayerLocation TRANSFUR_HELPER = new ModelLayerLocation(Changed.modResource("transfur_helper"), "main");

    protected final HelperModel DigitigradeLeftLeg;
    protected final HelperModel DigitigradeRightLeg;
    protected final HelperModel BasicLeftArm;
    protected final HelperModel BasicRightArm;
    protected final HelperModel TailedTorso;
    protected final HelperModel FeminineTorso;
    protected final HelperModel FeminineTorsoAlt;
    protected final HelperModel FeminineTorsoLegless;
    protected final HelperModel SnoutedHead;
    protected final HelperModel Legless;
    protected final HelperModel TaurTorso;
    protected final HelperModel PupTorso;

    protected final EnumMap<ArmorModel, ArmorHelper> ArmorMap;

    public static class ArmorHelper {
        protected final HelperModel DigitigradeLeftLeg;
        protected final HelperModel DigitigradeRightLeg;
        protected final HelperModel FeminineTorso;

        protected ArmorHelper(ModelPart root) {
            this.DigitigradeLeftLeg = HelperModel.fixed(root.getChild("DigitigradeLeftLeg"));
            this.DigitigradeRightLeg = HelperModel.fixed(root.getChild("DigitigradeRightLeg"));
            this.FeminineTorso = HelperModel.fixed(root.getChild("FeminineTorso"));
        }
    }

    protected static void copyRotations(ModelPart from, ModelPart to) {
        to.xRot = from.xRot;
        to.yRot = from.yRot;
        to.zRot = from.zRot;
    }

    protected TransfurHelper(ModelPart root) {
        this.DigitigradeLeftLeg = HelperModel.fixed(root.getChild("DigitigradeLeftLeg"));
        this.DigitigradeRightLeg = HelperModel.fixed(root.getChild("DigitigradeRightLeg"));
        this.BasicLeftArm = HelperModel.fixed(root.getChild("BasicLeftArm"));
        this.BasicRightArm = HelperModel.fixed(root.getChild("BasicRightArm"));
        this.TailedTorso = HelperModel.fixed(root.getChild("TailedTorso"));
        this.FeminineTorso = HelperModel.fixed(root.getChild("FeminineTorso"));
        this.FeminineTorsoAlt = HelperModel.fixed(root.getChild("FeminineTorsoAlt"));
        this.FeminineTorsoLegless = HelperModel.fixed(root.getChild("FeminineTorsoLegless"));
        this.SnoutedHead = HelperModel.fixed(root.getChild("SnoutedHead"));
        this.Legless = HelperModel.withPrepareAndTransition(root.getChild("Legless"), (beforePose, part, model) -> {
            return beforePose.translate(0.0f, 12.0f, 0.0f)
                    .averageRotation(model.leftLeg, model.rightLeg);
        }, (model, preMorph) -> {
            float avgX = (model.leftLeg.xRot + model.rightLeg.xRot) * 0.5f;
            float avgY = (model.leftLeg.yRot + model.rightLeg.yRot) * 0.5f;
            float avgZ = (model.leftLeg.zRot + model.rightLeg.zRot) * 0.5f;
            model.leftLeg.xRot = Mth.lerp(preMorph, model.leftLeg.xRot, avgX);
            model.leftLeg.yRot = Mth.lerp(preMorph, model.leftLeg.yRot, avgY);
            model.leftLeg.zRot = Mth.lerp(preMorph, model.leftLeg.zRot, avgZ);
            model.rightLeg.xRot = Mth.lerp(preMorph, model.rightLeg.xRot, avgX);
            model.rightLeg.yRot = Mth.lerp(preMorph, model.rightLeg.yRot, avgY);
            model.rightLeg.zRot = Mth.lerp(preMorph, model.rightLeg.zRot, avgZ);
        });
        this.TaurTorso = HelperModel.withPrepare(root.getChild("TaurTorso"), (beforePose, part, model) -> {
            copyRotations(Limb.RIGHT_LEG.getModelPart(model), part.getChild("RightLeg"));
            copyRotations(Limb.LEFT_LEG.getModelPart(model), part.getChild("LeftLeg"));
            copyRotations(Limb.RIGHT_LEG.getModelPart(model), part.getChild("RightLeg2"));
            copyRotations(Limb.LEFT_LEG.getModelPart(model), part.getChild("LeftLeg2"));
            return beforePose.translate(0.0f, 12.0f, 0.0f);
        });
        this.PupTorso = HelperModel.fixed(root.getChild("PupTorso"));

        ArmorMap = Util.make(new EnumMap<>(ArmorModel.class), map -> {
            Arrays.stream(ArmorModel.values()).forEach(armorModel -> map.put(armorModel, new ArmorHelper(root.getChild(armorModel.identifier))));
        });
    }

    private static EntityModelSet getModelSet() {
        return Minecraft.getInstance().getEntityRenderDispatcher().entityModels;
    }

    private static void createArmorLayer(PartDefinition root, ArmorModel armor) {
        // DIGI LEFT
        {
            PartDefinition DigitigradeLeftLeg = root.addOrReplaceChild("DigitigradeLeftLeg", CubeListBuilder.create(), PartPose.offset(2.0F, 12.0F, 0.0F));

            PartDefinition LeftThigh_r1 = DigitigradeLeftLeg.addOrReplaceChild("LeftThigh_r1", CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 5.0F, 4.0F, armor.dualDeformation), PartPose.offset(0.0F, 0.0F, 0.0F));

            PartDefinition LeftLowerLeg = DigitigradeLeftLeg.addOrReplaceChild("LeftLowerLeg", CubeListBuilder.create(), PartPose.offset(0.0F, 5.0F, -2.0F));

            PartDefinition LeftCalf_r1 = LeftLowerLeg.addOrReplaceChild("LeftCalf_r1", CubeListBuilder.create().texOffs(0, 21).addBox(-2.0F, 5.0F, -2.0F, 4.0F, 3.0F, 4.0F, armor.dualDeformation), PartPose.offset(0.0F, -5.0F, 2.0F));

            PartDefinition LeftFoot = LeftLowerLeg.addOrReplaceChild("LeftFoot", CubeListBuilder.create(), PartPose.offset(0.0F, 3.0F, 4.0F));

            PartDefinition LeftArch_r1 = LeftFoot.addOrReplaceChild("LeftArch_r1", CubeListBuilder.create().texOffs(0, 24).mirror().addBox(-2.0F, -0.5F, -4.0F, 4.0F, 3.0F, 4.0F, armor.dualDeformation).mirror(false), PartPose.offset(0.0F, 0.0F, 0.0F));

            PartDefinition LeftPad = LeftFoot.addOrReplaceChild("LeftPad", CubeListBuilder.create().texOffs(0, 26).addBox(-2.0F, 10.0F, -2.0F, 4.0F, 2.0F, 4.0F, armor.dualDeformation), PartPose.offset(0.0F, -8.0F, -2.0F));

        }

        // DIGI RIGHT
        {
            PartDefinition DigitigradeRightLeg = root.addOrReplaceChild("DigitigradeRightLeg", CubeListBuilder.create(), PartPose.offset(-2.0F, 12.0F, 0.0F));

            PartDefinition RightThigh_r1 = DigitigradeRightLeg.addOrReplaceChild("RightThigh_r1", CubeListBuilder.create().texOffs(0, 16).mirror().addBox(-2.0F, 0.0F, -2.0F, 4.0F, 5.0F, 4.0F, armor.dualDeformation).mirror(false), PartPose.offset(0.0F, 0.0F, 0.0F));

            PartDefinition RightLowerLeg = DigitigradeRightLeg.addOrReplaceChild("RightLowerLeg", CubeListBuilder.create(), PartPose.offset(0.0F, 5.0F, -2.0F));

            PartDefinition RightCalf_r1 = RightLowerLeg.addOrReplaceChild("RightCalf_r1", CubeListBuilder.create().texOffs(0, 21).mirror().addBox(-2.0F, 5.0F, -2.0F, 4.0F, 3.0F, 4.0F, armor.dualDeformation).mirror(false), PartPose.offset(0.0F, -5.0F, 2.0F));

            PartDefinition RightFoot = RightLowerLeg.addOrReplaceChild("RightFoot", CubeListBuilder.create(), PartPose.offset(0.0F, 3.0F, 4.0F));

            PartDefinition RightArch_r1 = RightFoot.addOrReplaceChild("RightArch_r1", CubeListBuilder.create().texOffs(0, 24).addBox(-2.0F, -0.5F, -4.0F, 4.0F, 3.0F, 4.0F, armor.dualDeformation), PartPose.offset(0.0F, 0.0F, 0.0F));

            PartDefinition RightPad = RightFoot.addOrReplaceChild("RightPad", CubeListBuilder.create().texOffs(0, 26).mirror().addBox(-2.0F, 10.0F, -2.0F, 4.0F, 2.0F, 4.0F, armor.dualDeformation).mirror(false), PartPose.offset(0.0F, -8.0F, -2.0F));
        }

        // FEMININE TORSO
        {
            PartDefinition Torso = root.addOrReplaceChild("FeminineTorso", CubeListBuilder.create().texOffs(16, 16).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, armor.dualDeformation), PartPose.offset(0.0F, 0.0F, 0.0F));

            PartDefinition Plantoids = Torso.addOrReplaceChild("Plantoids", CubeListBuilder.create(), PartPose.offset(0.0F, 1.8F, -1.0F));

            PartDefinition Plantoids_r1 = Plantoids.addOrReplaceChild("Plantoids", CubeListBuilder.create().texOffs(18, 19).mirror().addBox(-4.0F, -2.3F, -0.9F, 8.0F, 2.0F, 2.0F, armor.dualDeformation).mirror(false)
                    .texOffs(18, 22).mirror().addBox(-4.0F, -0.3F, -0.9F, 8.0F, 1.0F, 2.0F, armor.dualDeformation).mirror(false), PartPose.offset(0.0F, 2.5F, 0.0F));
        }
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        // DIGI LEFT
        {
            PartDefinition DigitigradeLeftLeg = partdefinition.addOrReplaceChild("DigitigradeLeftLeg", CubeListBuilder.create(), PartPose.offset(2.0F, 12.0F, 0.0F));

            PartDefinition LeftThigh_r1 = DigitigradeLeftLeg.addOrReplaceChild("LeftThigh_r1", CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 5.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 0.0F, 0.0F));

            PartDefinition LeftLowerLeg = DigitigradeLeftLeg.addOrReplaceChild("LeftLowerLeg", CubeListBuilder.create(), PartPose.offset(0.0F, 5.0F, -2.0F));

            PartDefinition LeftCalf_r1 = LeftLowerLeg.addOrReplaceChild("LeftCalf_r1", CubeListBuilder.create().texOffs(0, 21).addBox(-2.0F, 5.0F, -2.0F, 4.0F, 3.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(0.0F, -5.0F, 2.0F));

            PartDefinition LeftFoot = LeftLowerLeg.addOrReplaceChild("LeftFoot", CubeListBuilder.create(), PartPose.offset(0.0F, 3.0F, 4.0F));

            PartDefinition LeftArch_r1 = LeftFoot.addOrReplaceChild("LeftArch_r1", CubeListBuilder.create().texOffs(0, 24).addBox(-2.0F, -0.5F, -4.0F, 4.0F, 3.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 0.0F, 0.0F));

            PartDefinition LeftPad = LeftFoot.addOrReplaceChild("LeftPad", CubeListBuilder.create().texOffs(0, 26).addBox(-2.0F, 10.0F, -2.0F, 4.0F, 2.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(0.0F, -8.0F, -2.0F));

        }

        // DIGI RIGHT
        {
            PartDefinition DigitigradeRightLeg = partdefinition.addOrReplaceChild("DigitigradeRightLeg", CubeListBuilder.create(), PartPose.offset(-2.0F, 12.0F, 0.0F));

            PartDefinition RightThigh_r1 = DigitigradeRightLeg.addOrReplaceChild("RightThigh_r1", CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 5.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 0.0F, 0.0F));

            PartDefinition RightLowerLeg = DigitigradeRightLeg.addOrReplaceChild("RightLowerLeg", CubeListBuilder.create(), PartPose.offset(0.0F, 5.0F, -2.0F));

            PartDefinition RightCalf_r1 = RightLowerLeg.addOrReplaceChild("RightCalf_r1", CubeListBuilder.create().texOffs(0, 21).addBox(-2.0F, 5.0F, -2.0F, 4.0F, 3.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(0.0F, -5.0F, 2.0F));

            PartDefinition RightFoot = RightLowerLeg.addOrReplaceChild("RightFoot", CubeListBuilder.create(), PartPose.offset(0.0F, 3.0F, 4.0F));

            PartDefinition RightArch_r1 = RightFoot.addOrReplaceChild("RightArch_r1", CubeListBuilder.create().texOffs(0, 24).addBox(-2.0F, -0.5F, -4.0F, 4.0F, 3.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 0.0F, 0.0F));

            PartDefinition RightPad = RightFoot.addOrReplaceChild("RightPad", CubeListBuilder.create().texOffs(0, 26).addBox(-2.0F, 10.0F, -2.0F, 4.0F, 2.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(0.0F, -8.0F, -2.0F));
        }

        // BASIC ARMS
        {
            PartDefinition BasicLeftArm = partdefinition.addOrReplaceChild("BasicLeftArm", CubeListBuilder.create().texOffs(-2, -2).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(5.0F, 2.0F, 0.0F));

            PartDefinition BasicRightArm = partdefinition.addOrReplaceChild("BasicRightArm", CubeListBuilder.create().texOffs(-2, -2).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(-5.0F, 2.0F, 0.0F));
        }

        // TAILED TORSO
        {
            PartDefinition TailedTorso = partdefinition.addOrReplaceChild("TailedTorso", CubeListBuilder.create().texOffs(-2, -2).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 0.0F, 0.0F));

            PartDefinition Tail = TailedTorso.addOrReplaceChild("Tail", CubeListBuilder.create(), PartPose.offset(0.0F, 11.0F, 0.0F));

            PartDefinition TailPrimary = Tail.addOrReplaceChild("TailPrimary", CubeListBuilder.create().texOffs(58, 2).addBox(-1.0F, 0.25F, -0.5F, 2.0F, 1.0F, 2.0F, new CubeDeformation(-0.2F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.1309F, 0.0F, 0.0F));
        }

        // FEMININE TORSO
        {
            PartDefinition FeminineTorso = partdefinition.addOrReplaceChild("FeminineTorso", CubeListBuilder.create().texOffs(-2, -2).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 5.0F, 4.0F, CubeDeformation.NONE)
                    .texOffs(-2, -2).addBox(-4.0F, 5.0F, -2.0F, 8.0F, 4.0F, 4.0F, CubeDeformation.NONE)
                    .texOffs(-2, -2).addBox(-4.0F, 9.0F, -2.0F, 8.0F, 3.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 0.0F, 0.0F));

            PartDefinition Plantoids = FeminineTorso.addOrReplaceChild("Plantoids", CubeListBuilder.create(), PartPose.offset(0.0F, 0.25F, -2.0F));

            PartDefinition RightPlantoid_r1 = Plantoids.addOrReplaceChild("RightPlantoid_r1", CubeListBuilder.create().texOffs(33, 65).addBox(-4.0F, -1.9F, 0.15F, 4.0F, 4.0F, 2.0F, new CubeDeformation(-0.03F))
                    .texOffs(19, 64).addBox(0.0F, -1.9F, 0.15F, 4.0F, 4.0F, 2.0F, new CubeDeformation(-0.03F)), PartPose.offsetAndRotation(0.0F, 2.0F, 0.0F, -0.0017F, 0.0F, 0.0F));

            PartDefinition Center_r1 = Plantoids.addOrReplaceChild("Center_r1", CubeListBuilder.create().texOffs(0, 35).addBox(-0.5F, -1.5F, 0.4F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.2F)), PartPose.offsetAndRotation(0.0F, 2.0F, 0.0F, -0.0009F, 0.0F, 0.0F));

            PartDefinition Tail = FeminineTorso.addOrReplaceChild("Tail", CubeListBuilder.create(), PartPose.offset(0.0F, 11.0F, 0.0F));

            PartDefinition TailPrimary = Tail.addOrReplaceChild("TailPrimary", CubeListBuilder.create().texOffs(58, 2).addBox(-1.0F, 0.25F, -0.5F, 2.0F, 1.0F, 2.0F, new CubeDeformation(-0.2F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.1309F, 0.0F, 0.0F));
        }

        // FEMININE TORSO ALT
        {
            PartDefinition FeminineTorso = partdefinition.addOrReplaceChild("FeminineTorsoAlt", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

            PartDefinition Waist_r1 = FeminineTorso.addOrReplaceChild("Waist_r1", CubeListBuilder.create().texOffs(-2, -2).addBox(-4.0F, -12.0F, -2.0F, 8.0F, 5.0F, 4.0F, CubeDeformation.NONE)
                    .texOffs(-2, -2).addBox(-4.0F, -7.0F, -2.0F, 8.0F, 4.0F, 4.0F, CubeDeformation.NONE)
                    .texOffs(-2, -2).addBox(-4.0F, -3.0F, -2.0F, 8.0F, 3.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 12.0F, 0.0F));

            PartDefinition Plantoids = FeminineTorso.addOrReplaceChild("Plantoids", CubeListBuilder.create(), PartPose.offset(0.0F, 0.25F, -2.0F));

            PartDefinition RightPlantoid_r1 = Plantoids.addOrReplaceChild("RightPlantoid_r1", CubeListBuilder.create().texOffs(33, 65).addBox(-4.0F, -1.9F, 0.15F, 4.0F, 4.0F, 2.0F, new CubeDeformation(-0.03F))
                    .texOffs(19, 64).addBox(0.0F, -1.9F, 0.15F, 4.0F, 4.0F, 2.0F, new CubeDeformation(-0.03F)), PartPose.offsetAndRotation(0.0F, 2.0F, 0.0F, -0.0017F, 0.0F, 0.0F));

            PartDefinition Center_r1 = Plantoids.addOrReplaceChild("Center_r1", CubeListBuilder.create().texOffs(0, 35).addBox(-0.5F, -1.5F, 0.4F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.2F)), PartPose.offsetAndRotation(0.0F, 2.0F, 0.0F, -0.0009F, 0.0F, 0.0F));

            PartDefinition Tail = FeminineTorso.addOrReplaceChild("Tail", CubeListBuilder.create(), PartPose.offset(0.0F, 11.0F, 0.0F));

            PartDefinition TailPrimary = Tail.addOrReplaceChild("TailPrimary", CubeListBuilder.create().texOffs(58, 2).addBox(-1.0F, 0.25F, -0.5F, 2.0F, 1.0F, 2.0F, new CubeDeformation(-0.2F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.1309F, 0.0F, 0.0F));
        }

        // FEMININE TORSO LEGLESS
        {
            PartDefinition LeglessFeminineTorso = partdefinition.addOrReplaceChild("FeminineTorsoLegless", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

            PartDefinition Torso_r1 = LeglessFeminineTorso.addOrReplaceChild("Torso_r1", CubeListBuilder.create().texOffs(16, 16).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 5.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 0.0F, 0.0F));

            PartDefinition TorsoLower_r1 = LeglessFeminineTorso.addOrReplaceChild("TorsoLower_r1", CubeListBuilder.create().texOffs(16, 21).addBox(-4.0F, 5.0F, -2.0F, 8.0F, 7.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 0.0F, 0.0F));

            PartDefinition Plantoids2 = LeglessFeminineTorso.addOrReplaceChild("Plantoids2", CubeListBuilder.create(), PartPose.offset(0.0F, 0.25F, -2.0F));

            PartDefinition RightPlantoid_r2 = Plantoids2.addOrReplaceChild("RightPlantoid_r2", CubeListBuilder.create().texOffs(18, 18).addBox(-4.0F, -1.9F, 0.15F, 4.0F, 4.0F, 2.0F, new CubeDeformation(-0.03F))
                    .texOffs(22, 18).addBox(0.0F, -1.9F, 0.15F, 4.0F, 4.0F, 2.0F, new CubeDeformation(-0.03F)), PartPose.offsetAndRotation(0.0F, 2.0F, 0.0F, -0.0017F, 0.0F, 0.0F));

            PartDefinition Center_r2 = Plantoids2.addOrReplaceChild("Center_r2", CubeListBuilder.create().texOffs(22, 19).addBox(-0.5F, -1.5F, 0.4F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.2F)), PartPose.offsetAndRotation(0.0F, 2.0F, 0.0F, -0.0009F, 0.0F, 0.0F));
        }

        // SNOUTED HEAD
        {
            PartDefinition SnoutedHead = partdefinition.addOrReplaceChild("SnoutedHead", CubeListBuilder.create().texOffs(-6, -6).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, CubeDeformation.NONE)
                    .texOffs(-1, -1).addBox(-2.0F, -3.0F, -4.0F, 4.0F, 2.0F, 3.0F, CubeDeformation.NONE)
                    .texOffs(-1, -1).addBox(-2.0F, -1.0F, -4.0F, 4.0F, 1.0F, 3.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 0.0F, 0.0F));

        }
        
        // LEGLESS
        {
            PartDefinition Legless = partdefinition.addOrReplaceChild("Legless", CubeListBuilder.create().texOffs(-2, -2).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 4.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 12.0F, 0.0F));

            PartDefinition LowerAbdomen = Legless.addOrReplaceChild("LowerAbdomen", CubeListBuilder.create().texOffs(-2, -2).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 4.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 4.0F, 0.0F));

            PartDefinition Tail = LowerAbdomen.addOrReplaceChild("Tail", CubeListBuilder.create().texOffs(-2, -2).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 4.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 4.0F, 0.0F));
        }

        // TAUR
        {
            PartDefinition TaurTorso = partdefinition.addOrReplaceChild("TaurTorso", CubeListBuilder.create().texOffs(-2, -2).addBox(-4.0F, -2.0F, -2.0F, 8.0F, 2.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 12.0F, 0.0F));

            PartDefinition LeftLeg = TaurTorso.addOrReplaceChild("LeftLeg", CubeListBuilder.create(), PartPose.offset(2.0F, 0.0F, 0.0F));

            PartDefinition LeftUpperLeg_r1 = LeftLeg.addOrReplaceChild("LeftUpperLeg_r1", CubeListBuilder.create().texOffs(0, 59).addBox(-4.0F, -12.0F, -2.0F, 4.0F, 6.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(2.0F, 12.0F, 0.0F));

            PartDefinition LeftLowerLeg = LeftLeg.addOrReplaceChild("LeftLowerLeg", CubeListBuilder.create(), PartPose.offset(0.0F, 6.0F, 0.0F));

            PartDefinition LeftLowerLeg_r1 = LeftLowerLeg.addOrReplaceChild("LeftLowerLeg_r1", CubeListBuilder.create().texOffs(67, 0).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 4.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 0.0F, 0.0F));

            PartDefinition LeftFoot = LeftLowerLeg.addOrReplaceChild("LeftFoot", CubeListBuilder.create().texOffs(29, 72).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 2.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 4.0F, 0.0F));

            PartDefinition RightLeg = TaurTorso.addOrReplaceChild("RightLeg", CubeListBuilder.create(), PartPose.offset(-2.0F, 0.0F, 0.0F));

            PartDefinition RightUpperLeg_r1 = RightLeg.addOrReplaceChild("RightUpperLeg_r1", CubeListBuilder.create().texOffs(0, 59).addBox(-4.0F, -12.0F, -2.0F, 4.0F, 6.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(2.0F, 12.0F, 0.0F));

            PartDefinition RightLowerLeg3 = RightLeg.addOrReplaceChild("RightLowerLeg", CubeListBuilder.create(), PartPose.offset(0.0F, 6.0F, 0.0F));

            PartDefinition RightLowerLeg_r1 = RightLowerLeg3.addOrReplaceChild("RightLowerLeg_r1", CubeListBuilder.create().texOffs(67, 0).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 4.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 0.0F, 0.0F));

            PartDefinition RightFoot3 = RightLowerLeg3.addOrReplaceChild("RightFoot", CubeListBuilder.create().texOffs(29, 72).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 2.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 4.0F, 0.0F));

            PartDefinition LeftLeg2 = TaurTorso.addOrReplaceChild("LeftLeg2", CubeListBuilder.create(), PartPose.offset(2.0F, 0.0F, 0.0F));

            PartDefinition LeftThigh_r2 = LeftLeg2.addOrReplaceChild("LeftThigh_r1", CubeListBuilder.create().texOffs(-2, -2).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 5.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 0.0F, 0.0F));

            PartDefinition LeftLowerLeg2 = LeftLeg2.addOrReplaceChild("LeftLowerLeg2", CubeListBuilder.create(), PartPose.offset(0.0F, 5.0F, -2.0F));

            PartDefinition LeftCalf_r2 = LeftLowerLeg2.addOrReplaceChild("LeftCalf_r1", CubeListBuilder.create().texOffs(-2, -2).addBox(-2.0F, 5.0F, -2.0F, 4.0F, 3.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(0.0F, -5.0F, 2.0F));

            PartDefinition LeftFoot2 = LeftLowerLeg2.addOrReplaceChild("LeftFoot2", CubeListBuilder.create(), PartPose.offset(0.0F, 3.0F, 4.0F));

            PartDefinition LeftArch_r2 = LeftFoot2.addOrReplaceChild("LeftArch_r1", CubeListBuilder.create().texOffs(-2, -2).addBox(-2.0F, -0.5F, -4.0F, 4.0F, 3.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 0.0F, 0.0F));

            PartDefinition LeftPad2 = LeftFoot2.addOrReplaceChild("LeftPad2", CubeListBuilder.create().texOffs(-2, -2).addBox(-2.0F, 10.0F, -2.0F, 4.0F, 2.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(0.0F, -8.0F, -2.0F));

            PartDefinition RightLeg2 = TaurTorso.addOrReplaceChild("RightLeg2", CubeListBuilder.create(), PartPose.offset(-2.0F, 0.0F, 0.0F));

            PartDefinition RightThigh_r2 = RightLeg2.addOrReplaceChild("RightThigh_r1", CubeListBuilder.create().texOffs(-2, -2).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 5.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 0.0F, 0.0F));

            PartDefinition RightLowerLeg2 = RightLeg2.addOrReplaceChild("RightLowerLeg2", CubeListBuilder.create(), PartPose.offset(0.0F, 5.0F, -2.0F));

            PartDefinition RightCalf_r2 = RightLowerLeg2.addOrReplaceChild("RightCalf_r1", CubeListBuilder.create().texOffs(-2, -2).addBox(-2.0F, 5.0F, -2.0F, 4.0F, 3.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(0.0F, -5.0F, 2.0F));

            PartDefinition RightFoot2 = RightLowerLeg2.addOrReplaceChild("RightFoot2", CubeListBuilder.create(), PartPose.offset(0.0F, 3.0F, 4.0F));

            PartDefinition RightArch_r2 = RightFoot2.addOrReplaceChild("RightArch_r1", CubeListBuilder.create().texOffs(-2, -2).addBox(-2.0F, -0.5F, -4.0F, 4.0F, 3.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 0.0F, 0.0F));

            PartDefinition RightPad2 = RightFoot2.addOrReplaceChild("RightPad2", CubeListBuilder.create().texOffs(-2, -2).addBox(-2.0F, 10.0F, -2.0F, 4.0F, 2.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(0.0F, -8.0F, -2.0F));

            PartDefinition Tail4 = TaurTorso.addOrReplaceChild("Tail", CubeListBuilder.create(), PartPose.offset(0.0F, -1.75F, 0.0F));

            PartDefinition TailPrimary3 = Tail4.addOrReplaceChild("TailPrimary", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.1309F, 0.0F, 0.0F));

            PartDefinition Base_r1 = TailPrimary3.addOrReplaceChild("Base_r1", CubeListBuilder.create().texOffs(58, 2).addBox(-1.0F, 0.5F, -0.5F, 2.0F, 1.0F, 2.0F, new CubeDeformation(-0.2F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 1.1781F, 0.0F, 0.0F));
        }

        // PUP
        {
            PartDefinition PupTorso = partdefinition.addOrReplaceChild("PupTorso", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

            PartDefinition Torso_r1 = PupTorso.addOrReplaceChild("Torso_r1", CubeListBuilder.create().texOffs(-2, -2).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 5.0F, 4.0F, CubeDeformation.NONE)
                    .texOffs(-2, -2).addBox(-4.0F, 5.0F, -2.0F, 8.0F, 7.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 0.0F, 0.0F));

            PartDefinition Tail = PupTorso.addOrReplaceChild("Tail", CubeListBuilder.create(), PartPose.offset(0.0F, 10.25F, 0.0F));

            PartDefinition TailPrimary = Tail.addOrReplaceChild("TailPrimary", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.1309F, 0.0F, 0.0F));

            PartDefinition Base_r1 = TailPrimary.addOrReplaceChild("Base_r1", CubeListBuilder.create().texOffs(58, 2).addBox(-1.0F, 0.5F, -0.5F, 2.0F, 1.0F, 2.0F, new CubeDeformation(-0.2F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 1.1781F, 0.0F, 0.0F));

        }

        // ARMOR
        {
            Arrays.stream(ArmorModel.values()).forEach(armorModel -> {
                PartDefinition layer = partdefinition.addOrReplaceChild(armorModel.identifier, CubeListBuilder.create(), PartPose.ZERO);
                createArmorLayer(layer, armorModel);
            });
        }

        return LayerDefinition.create(meshdefinition, 64, 32);
    }

    private static final Cacheable<TransfurHelper> INSTANCE = Cacheable.of(() -> new TransfurHelper(getModelSet().bakeLayer(TRANSFUR_HELPER)));

    public static HelperModel getDigitigradeLeftLeg() {
        return INSTANCE.get().DigitigradeLeftLeg;
    }

    public static HelperModel getDigitigradeRightLeg() {
        return INSTANCE.get().DigitigradeRightLeg;
    }

    public static HelperModel getDigitigradeLeftLeg(ArmorModel model) {
        return INSTANCE.get().ArmorMap.get(model).DigitigradeLeftLeg;
    }

    public static HelperModel getDigitigradeRightLeg(ArmorModel model) {
        return INSTANCE.get().ArmorMap.get(model).DigitigradeRightLeg;
    }

    public static HelperModel getFeminineTorso(ArmorModel model) {
        return INSTANCE.get().ArmorMap.get(model).FeminineTorso;
    }

    public static HelperModel getBasicLeftArm() {
        return INSTANCE.get().BasicLeftArm;
    }

    public static HelperModel getBasicRightArm() {
        return INSTANCE.get().BasicRightArm;
    }

    public static HelperModel getTailedTorso() {
        return INSTANCE.get().TailedTorso;
    }

    public static HelperModel getFeminineTorso() {
        return INSTANCE.get().FeminineTorso;
    }

    public static HelperModel getFeminineTorsoAlt() {
        return INSTANCE.get().FeminineTorsoAlt;
    }

    public static HelperModel getFeminineTorsoLegless() {
        return INSTANCE.get().FeminineTorsoLegless;
    }

    public static HelperModel getSnoutedHead() {
        return INSTANCE.get().SnoutedHead;
    }

    public static HelperModel getLegless() {
        return INSTANCE.get().Legless;
    }

    public static HelperModel getTaurTorso() {
        return INSTANCE.get().TaurTorso;
    }

    public static HelperModel getPupTorso() {
        return INSTANCE.get().PupTorso;
    }
}
