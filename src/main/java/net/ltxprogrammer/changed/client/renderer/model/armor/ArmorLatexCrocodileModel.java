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

import static net.ltxprogrammer.changed.client.renderer.model.LatexHumanoidModel.process;

public class ArmorLatexCrocodileModel<T extends LatexEntity> extends LatexHumanoidArmorModel<T> {
    public static final ModelLayerLocation INNER_ARMOR = ArmorModelLayerLocation.createInnerArmorLocation(Changed.modResource("armor_latex_crocodile")).get();
    public static final ModelLayerLocation OUTER_ARMOR = ArmorModelLayerLocation.createOuterArmorLocation(Changed.modResource("armor_latex_crocodile")).get();

    public ArmorLatexCrocodileModel(ModelPart modelPart) {
        super(new Builder(
                modelPart.getChild("Head"),
                modelPart.getChild("Torso"),
                modelPart.getChild("Torso").getChild("Tail"),
                modelPart.getChild("LeftLeg"),
                modelPart.getChild("RightLeg"),
                modelPart.getChild("LeftArm"),
                modelPart.getChild("RightArm")), builder -> builder.hipOffset(-5.0f).legLengthOffset(-4.0f));
    }

    public static LayerDefinition createArmorLayer(ArmorModel layer) {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition RightLeg = partdefinition.addOrReplaceChild("RightLeg", CubeListBuilder.create().texOffs(0, 26).addBox(-2.4F, 15.0F, -2.75F, 4.0F, 2.0F, 4.0F, layer.deformation), PartPose.offset(-2.5F, 7.0F, 0.0F));

        PartDefinition RightLowerLeg_r1 = RightLeg.addOrReplaceChild("RightLowerLeg_r1", CubeListBuilder.create().texOffs(1, 17).addBox(-2.0F, -8.5F, -1.0F, 4.0F, 7.0F, 3.0F, layer.altDeformation), PartPose.offsetAndRotation(-0.4F, 17.25F, -1.25F, -0.5236F, 0.0F, 0.0F));

        PartDefinition RightMidLeg_r1 = RightLeg.addOrReplaceChild("RightMidLeg_r1", CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 8.0F, 4.0F, layer.slightAltDeformation), PartPose.offsetAndRotation(-0.4F, 5.25F, -1.5F, 0.5672F, 0.0F, 0.0F));

        PartDefinition RightUpperLeg_r1 = RightLeg.addOrReplaceChild("RightUpperLeg_r1", CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, -1.0F, -2.0F, 4.0F, 7.0F, 4.0F, layer.altDeformation), PartPose.offsetAndRotation(-0.4F, 1.0F, 0.0F, -0.2182F, 0.0F, 0.0F));

        PartDefinition LeftLeg = partdefinition.addOrReplaceChild("LeftLeg", CubeListBuilder.create().texOffs(0, 26).addBox(-1.6F, 15.0F, -2.75F, 4.0F, 2.0F, 4.0F, layer.deformation), PartPose.offset(2.5F, 7.0F, 0.0F));

        PartDefinition LeftLowerLeg_r1 = LeftLeg.addOrReplaceChild("LeftLowerLeg_r1", CubeListBuilder.create().texOffs(1, 17).mirror().addBox(-2.0F, -8.5F, -1.0F, 4.0F, 7.0F, 3.0F, layer.altDeformation).mirror(false), PartPose.offsetAndRotation(0.4F, 17.25F, -1.25F, -0.5236F, 0.0F, 0.0F));

        PartDefinition LeftMidLeg_r1 = LeftLeg.addOrReplaceChild("LeftMidLeg_r1", CubeListBuilder.create().texOffs(0, 16).mirror().addBox(-2.0F, 0.0F, -2.0F, 4.0F, 8.0F, 4.0F, layer.slightAltDeformation).mirror(false), PartPose.offsetAndRotation(0.4F, 5.25F, -1.5F, 0.5672F, 0.0F, 0.0F));

        PartDefinition LeftUpperLeg_r1 = LeftLeg.addOrReplaceChild("LeftUpperLeg_r1", CubeListBuilder.create().texOffs(0, 16).mirror().addBox(-2.0F, -1.0F, -2.0F, 4.0F, 7.0F, 4.0F, layer.altDeformation).mirror(false), PartPose.offsetAndRotation(0.4F, 1.0F, 0.0F, -0.2182F, 0.0F, 0.0F));

        PartDefinition Head = partdefinition.addOrReplaceChild("Head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, layer.deformation), PartPose.offset(0.0F, -10.0F, 0.0F));

        PartDefinition Torso = partdefinition.addOrReplaceChild("Torso", CubeListBuilder.create().texOffs(16, 16).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, layer.dualDeformation.extend((0.5f))), PartPose.offset(0.0F, -10.0F, 0.0F));

        PartDefinition Tail = Torso.addOrReplaceChild("Tail", CubeListBuilder.create(), PartPose.offset(0.0F, 15.0F, 0.0F));

        PartDefinition Base_r1 = Tail.addOrReplaceChild("Base_r1", CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, 0.7F, -3.4F, 4.0F, 7.0F, 4.0F, layer.altDeformation.extend(0.5f)), PartPose.offsetAndRotation(0.0F, 2.0F, 6.5F, 1.2654F, 0.0F, 0.0F));

        PartDefinition Base_r2 = Tail.addOrReplaceChild("Base_r2", CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, 0.0F, -1.5F, 4.0F, 8.0F, 4.0F, layer.altDeformation), PartPose.offsetAndRotation(0.0F, 0.0F, 0.5F, 1.0036F, 0.0F, 0.0F));

        PartDefinition RightArm = partdefinition.addOrReplaceChild("RightArm", CubeListBuilder.create().texOffs(40, 16).addBox(-3.25F, -1.0F, -2.0F, 4.0F, 12.0F, 4.0F, layer.deformation), PartPose.offset(-6.0F, -9.0F, 0.0F));

        PartDefinition LeftArm = partdefinition.addOrReplaceChild("LeftArm", CubeListBuilder.create().texOffs(40, 16).mirror().addBox(-0.75F, -1.0F, -2.0F, 4.0F, 12.0F, 4.0F, layer.deformation).mirror(false), PartPose.offset(6.0F, -9.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 32);
    }
}
