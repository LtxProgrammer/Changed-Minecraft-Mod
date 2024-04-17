package net.ltxprogrammer.changed.client.tfanimations;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.util.Cacheable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;

/**
 * Provides player limb shaped parts, that are pre-divided into their joints. So the animator has an easier time lerping them
 */
public class TransfurHelper {
    public static final ModelLayerLocation TRANSFUR_HELPER = new ModelLayerLocation(Changed.modResource("transfur_helper"), "main");

    protected final HelperModel DigitigradeLeftLeg;
    protected final HelperModel DigitigradeRightLeg;
    protected final HelperModel TailedTorso;
    protected final HelperModel FeminineTorso;
    protected final HelperModel FeminineTorsoAlt;
    protected final HelperModel SnoutedHead;
    protected final HelperModel Legless;
    protected final HelperModel TaurTorso;

    protected static void copyRotations(ModelPart from, ModelPart to) {
        to.xRot = from.xRot;
        to.yRot = from.yRot;
        to.zRot = from.zRot;
    }

    protected TransfurHelper(ModelPart root) {
        this.DigitigradeLeftLeg = HelperModel.fixed(root.getChild("DigitigradeLeftLeg"));
        this.DigitigradeRightLeg = HelperModel.fixed(root.getChild("DigitigradeRightLeg"));
        this.TailedTorso = HelperModel.fixed(root.getChild("TailedTorso"));
        this.FeminineTorso = HelperModel.fixed(root.getChild("FeminineTorso"));
        this.FeminineTorsoAlt = HelperModel.fixed(root.getChild("FeminineTorsoAlt"));
        this.SnoutedHead = HelperModel.fixed(root.getChild("SnoutedHead"));
        this.Legless = HelperModel.fixed(root.getChild("Legless"));
        this.TaurTorso = HelperModel.withPrepare(root.getChild("TaurTorso"), (part, model) -> {
            copyRotations(Limb.RIGHT_LEG.getModelPart(model), part.getChild("RightLeg"));
            copyRotations(Limb.LEFT_LEG.getModelPart(model), part.getChild("LeftLeg"));
            copyRotations(Limb.RIGHT_LEG.getModelPart(model), part.getChild("RightLeg2"));
            copyRotations(Limb.LEFT_LEG.getModelPart(model), part.getChild("LeftLeg2"));
        });
    }

    private static EntityModelSet getModelSet() {
        return Minecraft.getInstance().getEntityRenderDispatcher().entityModels;
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        // DIGI LEFT
        {
            PartDefinition DigitigradeLeftLeg = partdefinition.addOrReplaceChild("DigitigradeLeftLeg", CubeListBuilder.create(), PartPose.offset(2.0F, 12.0F, 0.0F));

            PartDefinition LeftThigh_r1 = DigitigradeLeftLeg.addOrReplaceChild("LeftThigh_r1", CubeListBuilder.create().texOffs(-2, -2).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 5.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 0.0F, 0.0F));

            PartDefinition LeftLowerLeg = DigitigradeLeftLeg.addOrReplaceChild("LeftLowerLeg", CubeListBuilder.create(), PartPose.offset(0.0F, 5.0F, -2.0F));

            PartDefinition LeftCalf_r1 = LeftLowerLeg.addOrReplaceChild("LeftCalf_r1", CubeListBuilder.create().texOffs(-2, -2).addBox(-2.0F, 5.0F, -2.0F, 4.0F, 3.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(0.0F, -5.0F, 2.0F));

            PartDefinition LeftFoot = LeftLowerLeg.addOrReplaceChild("LeftFoot", CubeListBuilder.create(), PartPose.offset(0.0F, 3.0F, 4.0F));

            PartDefinition LeftArch_r1 = LeftFoot.addOrReplaceChild("LeftArch_r1", CubeListBuilder.create().texOffs(-2, -2).addBox(-2.0F, -0.5F, -4.0F, 4.0F, 3.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 0.0F, 0.0F));

            PartDefinition LeftPad = LeftFoot.addOrReplaceChild("LeftPad", CubeListBuilder.create().texOffs(-2, -2).addBox(-2.0F, 10.0F, -2.0F, 4.0F, 2.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(0.0F, -8.0F, -2.0F));
        }

        // DIGI RIGHT
        {
            PartDefinition DigitigradeRightLeg = partdefinition.addOrReplaceChild("DigitigradeRightLeg", CubeListBuilder.create(), PartPose.offset(-2.0F, 12.0F, 0.0F));

            PartDefinition RightThigh_r1 = DigitigradeRightLeg.addOrReplaceChild("RightThigh_r1", CubeListBuilder.create().texOffs(-2, -2).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 5.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 0.0F, 0.0F));

            PartDefinition RightLowerLeg = DigitigradeRightLeg.addOrReplaceChild("RightLowerLeg", CubeListBuilder.create(), PartPose.offset(0.0F, 5.0F, -2.0F));

            PartDefinition RightCalf_r1 = RightLowerLeg.addOrReplaceChild("RightCalf_r1", CubeListBuilder.create().texOffs(-2, -2).addBox(-2.0F, 5.0F, -2.0F, 4.0F, 3.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(0.0F, -5.0F, 2.0F));

            PartDefinition RightFoot = RightLowerLeg.addOrReplaceChild("RightFoot", CubeListBuilder.create(), PartPose.offset(0.0F, 3.0F, 4.0F));

            PartDefinition RightArch_r1 = RightFoot.addOrReplaceChild("RightArch_r1", CubeListBuilder.create().texOffs(-2, -2).addBox(-2.0F, -0.5F, -4.0F, 4.0F, 3.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 0.0F, 0.0F));

            PartDefinition RightPad = RightFoot.addOrReplaceChild("RightPad", CubeListBuilder.create().texOffs(-2, -2).addBox(-2.0F, 10.0F, -2.0F, 4.0F, 2.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(0.0F, -8.0F, -2.0F));
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
            PartDefinition TaurTorso = partdefinition.addOrReplaceChild("TaurTorso", CubeListBuilder.create().texOffs(-2, -2).addBox(-4.0F, -2.0F, -2.0F, 8.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 12.0F, 0.0F));

            PartDefinition LeftLeg = TaurTorso.addOrReplaceChild("LeftLeg", CubeListBuilder.create(), PartPose.offset(2.0F, 0.0F, 0.0F));

            PartDefinition LeftUpperLeg_r1 = LeftLeg.addOrReplaceChild("LeftUpperLeg_r1", CubeListBuilder.create().texOffs(0, 59).addBox(-4.0F, -12.0F, -2.0F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(2.0F, 12.0F, 0.0F));

            PartDefinition LeftLowerLeg = LeftLeg.addOrReplaceChild("LeftLowerLeg", CubeListBuilder.create(), PartPose.offset(0.0F, 6.0F, 0.0F));

            PartDefinition LeftLowerLeg_r1 = LeftLowerLeg.addOrReplaceChild("LeftLowerLeg_r1", CubeListBuilder.create().texOffs(67, 0).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

            PartDefinition LeftFoot = LeftLowerLeg.addOrReplaceChild("LeftFoot", CubeListBuilder.create().texOffs(29, 72).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 4.0F, 0.0F));

            PartDefinition RightLeg = TaurTorso.addOrReplaceChild("RightLeg", CubeListBuilder.create(), PartPose.offset(-2.0F, 0.0F, 0.0F));

            PartDefinition RightUpperLeg_r1 = RightLeg.addOrReplaceChild("RightUpperLeg_r1", CubeListBuilder.create().texOffs(0, 59).addBox(-4.0F, -12.0F, -2.0F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(2.0F, 12.0F, 0.0F));

            PartDefinition RightLowerLeg3 = RightLeg.addOrReplaceChild("RightLowerLeg", CubeListBuilder.create(), PartPose.offset(0.0F, 6.0F, 0.0F));

            PartDefinition RightLowerLeg_r1 = RightLowerLeg3.addOrReplaceChild("RightLowerLeg_r1", CubeListBuilder.create().texOffs(67, 0).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

            PartDefinition RightFoot3 = RightLowerLeg3.addOrReplaceChild("RightFoot", CubeListBuilder.create().texOffs(29, 72).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 4.0F, 0.0F));

            PartDefinition LeftLeg2 = TaurTorso.addOrReplaceChild("LeftLeg2", CubeListBuilder.create(), PartPose.offset(2.0F, 0.0F, 0.0F));

            PartDefinition LeftThigh_r2 = LeftLeg2.addOrReplaceChild("LeftThigh_r1", CubeListBuilder.create().texOffs(-2, -2).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 5.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

            PartDefinition LeftLowerLeg2 = LeftLeg2.addOrReplaceChild("LeftLowerLeg2", CubeListBuilder.create(), PartPose.offset(0.0F, 5.0F, -2.0F));

            PartDefinition LeftCalf_r2 = LeftLowerLeg2.addOrReplaceChild("LeftCalf_r1", CubeListBuilder.create().texOffs(-2, -2).addBox(-2.0F, 5.0F, -2.0F, 4.0F, 3.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -5.0F, 2.0F));

            PartDefinition LeftFoot2 = LeftLowerLeg2.addOrReplaceChild("LeftFoot2", CubeListBuilder.create(), PartPose.offset(0.0F, 3.0F, 4.0F));

            PartDefinition LeftArch_r2 = LeftFoot2.addOrReplaceChild("LeftArch_r1", CubeListBuilder.create().texOffs(-2, -2).addBox(-2.0F, -0.5F, -4.0F, 4.0F, 3.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

            PartDefinition LeftPad2 = LeftFoot2.addOrReplaceChild("LeftPad2", CubeListBuilder.create().texOffs(-2, -2).addBox(-2.0F, 10.0F, -2.0F, 4.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -8.0F, -2.0F));

            PartDefinition RightLeg2 = TaurTorso.addOrReplaceChild("RightLeg2", CubeListBuilder.create(), PartPose.offset(-2.0F, 0.0F, 0.0F));

            PartDefinition RightThigh_r2 = RightLeg2.addOrReplaceChild("RightThigh_r1", CubeListBuilder.create().texOffs(-2, -2).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 5.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

            PartDefinition RightLowerLeg2 = RightLeg2.addOrReplaceChild("RightLowerLeg2", CubeListBuilder.create(), PartPose.offset(0.0F, 5.0F, -2.0F));

            PartDefinition RightCalf_r2 = RightLowerLeg2.addOrReplaceChild("RightCalf_r1", CubeListBuilder.create().texOffs(-2, -2).addBox(-2.0F, 5.0F, -2.0F, 4.0F, 3.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -5.0F, 2.0F));

            PartDefinition RightFoot2 = RightLowerLeg2.addOrReplaceChild("RightFoot2", CubeListBuilder.create(), PartPose.offset(0.0F, 3.0F, 4.0F));

            PartDefinition RightArch_r2 = RightFoot2.addOrReplaceChild("RightArch_r1", CubeListBuilder.create().texOffs(-2, -2).addBox(-2.0F, -0.5F, -4.0F, 4.0F, 3.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

            PartDefinition RightPad2 = RightFoot2.addOrReplaceChild("RightPad2", CubeListBuilder.create().texOffs(-2, -2).addBox(-2.0F, 10.0F, -2.0F, 4.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -8.0F, -2.0F));

            PartDefinition Tail4 = TaurTorso.addOrReplaceChild("Tail", CubeListBuilder.create(), PartPose.offset(0.0F, -1.75F, 0.0F));

            PartDefinition TailPrimary3 = Tail4.addOrReplaceChild("TailPrimary", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.1309F, 0.0F, 0.0F));

            PartDefinition Base_r1 = TailPrimary3.addOrReplaceChild("Base_r1", CubeListBuilder.create().texOffs(58, 2).addBox(-1.0F, 0.5F, -0.5F, 2.0F, 1.0F, 2.0F, new CubeDeformation(-0.2F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 1.1781F, 0.0F, 0.0F));
        }

        return LayerDefinition.create(meshdefinition, 16, 16);
    }

    private static final Cacheable<TransfurHelper> INSTANCE = Cacheable.of(() -> new TransfurHelper(getModelSet().bakeLayer(TRANSFUR_HELPER)));

    public static HelperModel getDigitigradeLeftLeg() {
        return INSTANCE.get().DigitigradeLeftLeg;
    }

    public static HelperModel getDigitigradeRightLeg() {
        return INSTANCE.get().DigitigradeRightLeg;
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

    public static HelperModel getSnoutedHead() {
        return INSTANCE.get().SnoutedHead;
    }

    public static HelperModel getLegless() {
        return INSTANCE.get().Legless;
    }

    public static HelperModel getTaurTorso() {
        return INSTANCE.get().TaurTorso;
    }
}
