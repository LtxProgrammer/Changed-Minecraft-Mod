package net.ltxprogrammer.changed.client.renderer.model.armor;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.entity.beast.LightLatexCentaur;
import net.ltxprogrammer.changed.entity.beast.LightLatexKnightFusion;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

import static net.ltxprogrammer.changed.client.renderer.model.LatexHumanoidModel.process;

public class ArmorLightLatexCentaurModel extends LatexHumanoidArmorModel<LightLatexCentaur> {
    public static final ModelLayerLocation INNER_ARMOR = ArmorModelLayerLocation.createInnerArmorLocation(Changed.modResource("armor_light_latex_centaur")).get();
    public static final ModelLayerLocation OUTER_ARMOR = ArmorModelLayerLocation.createOuterArmorLocation(Changed.modResource("armor_light_latex_centaur")).get();

    public ArmorLightLatexCentaurModel(ModelPart modelPart) {
        super(new Builder(
                modelPart.getChild("Head"),
                modelPart.getChild("Torso"),
                modelPart.getChild("Torso").getChild("LowerTorso").getChild("Tail"),
                modelPart.getChild("LeftLeg"),
                modelPart.getChild("RightLeg"),
                modelPart.getChild("LeftArm"),
                modelPart.getChild("RightArm")).legs2(
                    modelPart.getChild("Torso").getChild("LowerTorso"),
                    modelPart.getChild("LeftLeg2"),
                    modelPart.getChild("RightLeg2")),
                builder -> builder.forewardOffset(-7.0f).legs2(
                        modelPart.getChild("Torso").getChild("LowerTorso"),
                        modelPart.getChild("RightLeg2"),
                        modelPart.getChild("LeftLeg2")));
    }

    public static LayerDefinition createArmorLayer(ArmorModel layer) {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition RightLeg = partdefinition.addOrReplaceChild("RightLeg", CubeListBuilder.create().texOffs(0, 26).addBox(-1.75F, 12.0F, -1.8F, 4.0F, 2.0F, 4.0F, layer.deformation)
                .texOffs(3, 29).addBox(-1.75F, 12.0F, -2.8F, 4.0F, 2.0F, 1.0F, layer.deformation), PartPose.offset(-3.5F, 10.0F, -7.0F));

        PartDefinition RightLowerLeg_r1 = RightLeg.addOrReplaceChild("RightLowerLeg_r1", CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, 9.8638F, 2.7342F, 4.0F, 1.0F, 4.0F, layer.slightAltDeformation)
                .texOffs(0, 16).addBox(-2.0F, 2.8638F, 2.7342F, 4.0F, 7.0F, 4.0F, layer.slightAltDeformation), PartPose.offsetAndRotation(0.25F, 1.0348F, -2.0472F, -0.2182F, 0.0F, 0.0F));

        PartDefinition RightUpperLeg_r1 = RightLeg.addOrReplaceChild("RightUpperLeg_r1", CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, -0.3172F, -0.0274F, 4.0F, 6.0F, 4.0F, layer.inverseDeformation), PartPose.offsetAndRotation(0.25F, 1.0348F, -2.0472F, 0.3054F, 0.0F, 0.0F));

        PartDefinition LeftLeg = partdefinition.addOrReplaceChild("LeftLeg", CubeListBuilder.create().texOffs(0, 26).mirror().addBox(-1.75F, 12.0F, -1.8F, 4.0F, 2.0F, 4.0F, layer.deformation).mirror(false)
                .texOffs(3, 29).mirror().addBox(-1.75F, 12.0F, -2.8F, 4.0F, 2.0F, 1.0F, layer.deformation).mirror(false), PartPose.offset(3.0F, 10.0F, -7.0F));

        PartDefinition LeftLowerLeg_r1 = LeftLeg.addOrReplaceChild("LeftLowerLeg_r1", CubeListBuilder.create().texOffs(16, 38).mirror().addBox(-2.0F, 9.8638F, 2.7342F, 4.0F, 1.0F, 4.0F, layer.slightAltDeformation)
                .texOffs(0, 16).addBox(-2.0F, 2.8638F, 2.7342F, 4.0F, 7.0F, 4.0F, layer.slightAltDeformation).mirror(false), PartPose.offsetAndRotation(0.25F, 1.0348F, -2.0472F, -0.2182F, 0.0F, 0.0F));

        PartDefinition LeftUpperLeg_r1 = LeftLeg.addOrReplaceChild("LeftUpperLeg_r1", CubeListBuilder.create().texOffs(0, 16).mirror().addBox(-2.0F, -0.3172F, -0.0274F, 4.0F, 6.0F, 4.0F, layer.inverseDeformation).mirror(false), PartPose.offsetAndRotation(0.25F, 1.0348F, -2.0472F, 0.3054F, 0.0F, 0.0F));

        PartDefinition Head = partdefinition.addOrReplaceChild("Head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -9.0F, -4.0F, 8.0F, 8.0F, 8.0F, layer.deformation), PartPose.offset(0.0F, -2.0F, -7.0F));

        PartDefinition Torso = partdefinition.addOrReplaceChild("Torso", CubeListBuilder.create().texOffs(16, 16).addBox(-4.0F, -1.0F, -9.0F, 8.0F, 12.0F, 4.0F, layer.dualDeformation), PartPose.offset(0.0F, -2.0F, 0.0F));

        PartDefinition LowerTorso = Torso.addOrReplaceChild("LowerTorso", CubeListBuilder.create(), PartPose.offset(0.0F, 9.0F, -7.0F));

        PartDefinition Tail = LowerTorso.addOrReplaceChild("Tail", CubeListBuilder.create(), PartPose.offset(0.0F, 1.0F, 15.25F));

        PartDefinition Base_r1 = Tail.addOrReplaceChild("Base_r1", CubeListBuilder.create().texOffs(2, 17).addBox(-1.5F, 2.1914F, -2.1983F, 3.0F, 7.0F, 3.0F, layer.altDeformation), PartPose.offsetAndRotation(0.0F, 2.0F, 5.0F, 1.4835F, 0.0F, 0.0F));

        PartDefinition Base_r2 = Tail.addOrReplaceChild("Base_r2", CubeListBuilder.create().texOffs(1, 19).addBox(-1.5F, 2.0F, -1.0F, 3.0F, 7.0F, 3.0F, layer.altDeformation), PartPose.offsetAndRotation(0.0F, 0.0F, -1.0F, 1.1781F, 0.0F, 0.0F));

        PartDefinition RightArm = partdefinition.addOrReplaceChild("RightArm", CubeListBuilder.create().texOffs(40, 16).addBox(-3.0F, -3.0F, -2.0F, 4.0F, 12.0F, 4.0F, layer.deformation), PartPose.offset(-5.0F, 0.0F, -7.0F));

        PartDefinition LeftArm = partdefinition.addOrReplaceChild("LeftArm", CubeListBuilder.create().texOffs(40, 16).mirror().addBox(-1.0F, -3.0F, -2.0F, 4.0F, 12.0F, 4.0F, layer.deformation).mirror(false), PartPose.offset(5.0F, 0.0F, -7.0F));

        PartDefinition LeftLeg2 = partdefinition.addOrReplaceChild("LeftLeg2", CubeListBuilder.create().texOffs(0, 26).mirror().addBox(-2.0F, 12.0F, 0.25F, 4.0F, 2.0F, 4.0F, layer.deformation).mirror(false), PartPose.offset(3.25F, 10.0F, 8.0F));

        PartDefinition LeftLowerLeg_r2 = LeftLeg2.addOrReplaceChild("LeftLowerLeg_r2", CubeListBuilder.create().texOffs(1, 17).mirror().addBox(-2.0F, 3.5131F, 2.8162F, 4.0F, 6.0F, 3.0F, layer.altDeformation).mirror(false), PartPose.offsetAndRotation(0.0F, 2.1238F, 0.5226F, -0.2182F, 0.0F, 0.0F));

        PartDefinition LeftMidLeg_r1 = LeftLeg2.addOrReplaceChild("LeftMidLeg_r1", CubeListBuilder.create().texOffs(0, 16).mirror().addBox(-2.0F, -0.098F, -4.8445F, 4.0F, 7.0F, 4.0F, layer.slightAltDeformation).mirror(false), PartPose.offsetAndRotation(0.0F, 2.1238F, 0.5226F, 0.8727F, 0.0F, 0.0F));

        PartDefinition LeftUpperLeg_r2 = LeftLeg2.addOrReplaceChild("LeftUpperLeg_r2", CubeListBuilder.create().texOffs(0, 16).mirror().addBox(-2.0F, -2.0208F, -2.9291F, 4.0F, 6.0F, 4.0F, layer.altDeformation).mirror(false), PartPose.offsetAndRotation(0.0F, 2.1238F, 0.5226F, -0.0873F, 0.0F, 0.0F));

        PartDefinition RightLeg2 = partdefinition.addOrReplaceChild("RightLeg2", CubeListBuilder.create().texOffs(0, 26).addBox(-2.0F, 12.0F, 0.25F, 4.0F, 2.0F, 4.0F, layer.deformation), PartPose.offset(-3.25F, 10.0F, 8.0F));

        PartDefinition RightLowerLeg_r2 = RightLeg2.addOrReplaceChild("RightLowerLeg_r2", CubeListBuilder.create().texOffs(1, 17).addBox(-2.0F, 3.5131F, 2.8162F, 4.0F, 6.0F, 3.0F, layer.altDeformation), PartPose.offsetAndRotation(0.0F, 2.1238F, 0.5226F, -0.2182F, 0.0F, 0.0F));

        PartDefinition RightMidLeg_r1 = RightLeg2.addOrReplaceChild("RightMidLeg_r1", CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, -0.098F, -4.8445F, 4.0F, 7.0F, 4.0F, layer.slightAltDeformation), PartPose.offsetAndRotation(0.0F, 2.1238F, 0.5226F, 0.8727F, 0.0F, 0.0F));

        PartDefinition RightUpperLeg_r2 = RightLeg2.addOrReplaceChild("RightUpperLeg_r2", CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, -2.0208F, -2.9291F, 4.0F, 6.0F, 4.0F, layer.altDeformation), PartPose.offsetAndRotation(0.0F, 2.1238F, 0.5226F, -0.0873F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 32);
    }
}
