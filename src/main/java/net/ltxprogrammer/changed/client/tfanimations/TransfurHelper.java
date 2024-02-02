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

    protected final ModelPart DigitigradeLeftLeg;
    protected final ModelPart DigitigradeRightLeg;
    protected final ModelPart TailedTorso;
    protected final ModelPart FeminineTorso;
    protected final ModelPart FeminineTorsoAlt;

    protected TransfurHelper(ModelPart root) {
        this.DigitigradeLeftLeg = root.getChild("DigitigradeLeftLeg");
        this.DigitigradeRightLeg = root.getChild("DigitigradeRightLeg");
        this.TailedTorso = root.getChild("TailedTorso");
        this.FeminineTorso = root.getChild("FeminineTorso");
        this.FeminineTorsoAlt = root.getChild("FeminineTorsoAlt");
    }

    private static EntityModelSet getModelSet() {
        return Minecraft.getInstance().getEntityRenderDispatcher().entityModels;
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        // DIGI LEFT
        {
            PartDefinition DigitigradeLeftLeg = partdefinition.addOrReplaceChild("DigitigradeLeftLeg", CubeListBuilder.create().texOffs(-2, -2).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 5.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(2.0F, 12.0F, 0.0F));

            PartDefinition LeftLowerLeg = DigitigradeLeftLeg.addOrReplaceChild("LeftLowerLeg", CubeListBuilder.create(), PartPose.offset(0.0F, 5.0F, -2.0F));

            PartDefinition LeftCalf_r1 = LeftLowerLeg.addOrReplaceChild("LeftCalf_r1", CubeListBuilder.create().texOffs(-2, -2).addBox(-2.0F, 5.0F, -2.0F, 4.0F, 3.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(0.0F, -5.0F, 2.0F));

            PartDefinition LeftFoot = LeftLowerLeg.addOrReplaceChild("LeftFoot", CubeListBuilder.create(), PartPose.offset(0.0F, 3.0F, 4.0F));

            PartDefinition LeftArch_r1 = LeftFoot.addOrReplaceChild("LeftArch_r1", CubeListBuilder.create().texOffs(-2, -2).addBox(-2.0F, 0.0F, -4.0F, 4.0F, 2.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 0.0F, 0.0F));

            PartDefinition LeftPad = LeftFoot.addOrReplaceChild("LeftPad", CubeListBuilder.create().texOffs(-2, -2).addBox(-2.0F, 10.0F, -2.0F, 4.0F, 2.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(0.0F, -8.0F, -2.0F));
        }

        // DIGI RIGHT
        {
            PartDefinition DigitigradeRightLeg = partdefinition.addOrReplaceChild("DigitigradeRightLeg", CubeListBuilder.create().texOffs(-2, -2).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 5.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(-2.0F, 12.0F, 0.0F));

            PartDefinition RightLowerLeg = DigitigradeRightLeg.addOrReplaceChild("RightLowerLeg", CubeListBuilder.create(), PartPose.offset(0.0F, 5.0F, -2.0F));

            PartDefinition RightCalf_r1 = RightLowerLeg.addOrReplaceChild("RightCalf_r1", CubeListBuilder.create().texOffs(-2, -2).addBox(-2.0F, 5.0F, -2.0F, 4.0F, 3.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(0.0F, -5.0F, 2.0F));

            PartDefinition RightFoot = RightLowerLeg.addOrReplaceChild("RightFoot", CubeListBuilder.create(), PartPose.offset(0.0F, 3.0F, 4.0F));

            PartDefinition RightArch_r1 = RightFoot.addOrReplaceChild("RightArch_r1", CubeListBuilder.create().texOffs(-2, -2).addBox(-2.0F, 0.0F, -4.0F, 4.0F, 2.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 0.0F, 0.0F));

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
        return LayerDefinition.create(meshdefinition, 16, 16);
    }

    private static final Cacheable<TransfurHelper> INSTANCE = Cacheable.of(() -> new TransfurHelper(getModelSet().bakeLayer(TRANSFUR_HELPER)));

    public static ModelPart getDigitigradeLeftLeg() {
        return INSTANCE.get().DigitigradeLeftLeg;
    }

    public static ModelPart getDigitigradeRightLeg() {
        return INSTANCE.get().DigitigradeRightLeg;
    }

    public static ModelPart getTailedTorso() {
        return INSTANCE.get().TailedTorso;
    }

    public static ModelPart getFeminineTorsoAlt() {
        return INSTANCE.get().FeminineTorsoAlt;
    }

    public static ModelPart getFeminineTorso() {
        return INSTANCE.get().FeminineTorso;
    }
}
