package net.ltxprogrammer.changed.client.renderer.model.armor;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.entity.LatexEntity;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public class ArmorLatexSquidDogModel<T extends LatexEntity> extends LatexHumanoidArmorModel<T> {
    public static final ModelLayerLocation INNER_ARMOR = ArmorModelLayerLocation.createInnerArmorLocation(Changed.modResource("armor_latex_squid_dog")).get();
    public static final ModelLayerLocation OUTER_ARMOR = ArmorModelLayerLocation.createOuterArmorLocation(Changed.modResource("armor_latex_squid_dog")).get();

    public ArmorLatexSquidDogModel(ModelPart modelPart) {
        super(new LatexHumanoidArmorModel.Builder(
                modelPart.getChild("Head"),
                modelPart.getChild("Torso"),
                modelPart.getChild("Torso").getChild("Tail"),
                modelPart.getChild("LeftLeg"),
                modelPart.getChild("RightLeg"),
                modelPart.getChild("LeftArm"),
                modelPart.getChild("RightArm")).arms2(
                        modelPart.getChild("LeftArm2"),
                        modelPart.getChild("RightArm2")
        ), builder -> builder.hipOffset(-4.0f).arms2(modelPart.getChild("RightArm2"), modelPart.getChild("LeftArm2")));
    }

    public static LayerDefinition createArmorLayer(ArmorModel layer) {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition rightLeg = partdefinition.addOrReplaceChild("RightLeg", CubeListBuilder.create().texOffs(0, 26).addBox(-2.0F, 14.2F, -2.4F, 4.0F, 2.0F, 4.0F, layer.deformation), PartPose.offset(-2.45F, 7.5F, -0.65F));

        PartDefinition RightLowerLeg_r1 = rightLeg.addOrReplaceChild("RightLowerLeg_r1", CubeListBuilder.create().texOffs(1, 17).addBox(-1.99F, -8.6F, -1.0F, 4.0F, 6.0F, 3.0F, layer.altDeformation), PartPose.offsetAndRotation(0.0F, 16.75F, -1.25F, -0.5236F, 0.0F, 0.0F));

        PartDefinition RightMidLeg_r1 = rightLeg.addOrReplaceChild("RightMidLeg_r1", CubeListBuilder.create().texOffs(0, 16).addBox(-2.02F, -1.1F, -0.75F, 4.0F, 7.0F, 4.0F, layer.slightAltDeformation), PartPose.offsetAndRotation(0.0F, 6.75F, -1.5F, 0.5672F, 0.0F, 0.0F));

        PartDefinition RightUpperLeg_r1 = rightLeg.addOrReplaceChild("RightUpperLeg_r1", CubeListBuilder.create().texOffs(0, 16).addBox(-2.05F, -2.0F, -2.0F, 4.0F, 6.0F, 4.0F, layer.altDeformation), PartPose.offsetAndRotation(0.0F, 2.5F, 0.0F, -0.2182F, 0.0F, 0.0F));

        PartDefinition leftLeg = partdefinition.addOrReplaceChild("LeftLeg", CubeListBuilder.create().texOffs(0, 26).mirror().addBox(-2.0F, 14.2F, -2.4F, 4.0F, 2.0F, 4.0F, layer.deformation).mirror(false), PartPose.offset(2.55F, 7.5F, -0.65F));

        PartDefinition LeftLowerLeg_r1 = leftLeg.addOrReplaceChild("LeftLowerLeg_r1", CubeListBuilder.create().texOffs(1, 17).mirror().addBox(-2.01F, -8.6F, -1.0F, 4.0F, 6.0F, 3.0F, layer.altDeformation).mirror(false), PartPose.offsetAndRotation(0.0F, 16.75F, -1.25F, -0.5236F, 0.0F, 0.0F));

        PartDefinition LeftMidLeg_r1 = leftLeg.addOrReplaceChild("LeftMidLeg_r1", CubeListBuilder.create().texOffs(0, 16).mirror().addBox(-1.99F, -1.1F, -0.75F, 4.0F, 7.0F, 4.0F, layer.slightAltDeformation).mirror(false), PartPose.offsetAndRotation(0.0F, 6.75F, -1.5F, 0.5672F, 0.0F, 0.0F));

        PartDefinition LeftUpperLeg_r1 = leftLeg.addOrReplaceChild("LeftUpperLeg_r1", CubeListBuilder.create().texOffs(0, 16).mirror().addBox(-1.98F, -2.0F, -2.0F, 4.0F, 6.0F, 4.0F, layer.altDeformation).mirror(false), PartPose.offsetAndRotation(0.0F, 2.5F, 0.0F, -0.2182F, 0.0F, 0.0F));

        PartDefinition head = partdefinition.addOrReplaceChild("Head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.75F, -4.0F, 8.0F, 8.0F, 8.0F, layer.deformation.extend(0.4f)), PartPose.offset(0.0F, -5.5F, 0.0F));

        PartDefinition body = partdefinition.addOrReplaceChild("Torso", CubeListBuilder.create().texOffs(16, 16).addBox(-4.0F, 0.4F, -2.0F, 8.0F, 12.0F, 4.0F, layer.dualDeformation.extend(0.2f)), PartPose.offset(0.0F, -6.0F, 0.0F));

        PartDefinition Tail = body.addOrReplaceChild("Tail", CubeListBuilder.create(), PartPose.offset(0.0F, 12.0F, 1.0F));

        PartDefinition Base_r1 = Tail.addOrReplaceChild("Base_r1", CubeListBuilder.create().texOffs(1, 20).addBox(-1.5F, 1.1914F, -1.7983F, 3.0F, 6.0F, 3.0F, layer.altDeformation), PartPose.offsetAndRotation(0.0F, 3.0F, 6.0F, 1.4835F, 0.0F, 0.0F));

        PartDefinition Base_r2 = Tail.addOrReplaceChild("Base_r2", CubeListBuilder.create().texOffs(1, 19).addBox(-1.5F, 0.5F, -1.0F, 3.0F, 7.0F, 3.0F, layer.altDeformation), PartPose.offsetAndRotation(0.0F, 1.0F, 0.0F, 1.1781F, 0.0F, 0.0F));

        PartDefinition rightArm = partdefinition.addOrReplaceChild("RightArm", CubeListBuilder.create().texOffs(40, 16).addBox(-4.0872F, -1.9981F, -2.0F, 4.0F, 12.0F, 4.0F, layer.deformation), PartPose.offsetAndRotation(-5.0F, -1.0F, -0.15F, 0.0F, 0.0F, 0.0436F));

        PartDefinition rightArm2 = partdefinition.addOrReplaceChild("RightArm2", CubeListBuilder.create().texOffs(40, 16).addBox(-4.3473F, -1.6696F, -2.0F, 4.0F, 12.0F, 4.0F, layer.deformation), PartPose.offsetAndRotation(-5.0F, -4.0F, 0.05F, 0.0F, 0.0F, 0.1745F));

        PartDefinition leftArm = partdefinition.addOrReplaceChild("LeftArm", CubeListBuilder.create().texOffs(40, 16).mirror().addBox(0.0872F, -1.9981F, -2.0F, 4.0F, 12.0F, 4.0F, layer.deformation).mirror(false), PartPose.offsetAndRotation(5.0F, -1.0F, -0.15F, 0.0F, 0.0F, -0.0436F));

        PartDefinition leftArm2 = partdefinition.addOrReplaceChild("LeftArm2", CubeListBuilder.create().texOffs(40, 16).mirror().addBox(0.3473F, -1.6696F, -2.0F, 4.0F, 12.0F, 4.0F, layer.deformation).mirror(false), PartPose.offsetAndRotation(5.0F, -4.0F, 0.05F, 0.0F, 0.0F, -0.1745F));

        return LayerDefinition.create(meshdefinition, 64, 32);
    }

    @Override
    public void prepareForShorts() {
        body.visible = true;
        leftLeg.getChild("LeftUpperLeg_r1").visible = true;
        rightLeg.getChild("RightUpperLeg_r1").visible = true;
    }
}
