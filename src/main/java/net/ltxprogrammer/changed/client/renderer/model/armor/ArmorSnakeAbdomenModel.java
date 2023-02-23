package net.ltxprogrammer.changed.client.renderer.model.armor;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.entity.beast.LatexSnake;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

import java.util.List;
import java.util.Map;

public class ArmorSnakeAbdomenModel<T extends LatexSnake> extends LatexHumanoidArmorModel<T> {
    public static final ModelLayerLocation INNER_ARMOR = ArmorModelLayerLocation.createInnerArmorLocation(Changed.modResource("armor_snake_abdomen")).get();
    public static final ModelLayerLocation OUTER_ARMOR = ArmorModelLayerLocation.createOuterArmorLocation(Changed.modResource("armor_snake_abdomen")).get();
    public static final ModelPart EMPTY_PART = new ModelPart(List.of(), Map.of());

    protected ArmorSnakeAbdomenModel(ModelPart root, ModelPart abdomen, ModelPart lowerAbdomen, ModelPart tail) {
        super(new Builder(
                EMPTY_PART,
                root.getChild("Torso"),
                tail,
                EMPTY_PART,
                EMPTY_PART,
                EMPTY_PART,
                EMPTY_PART).noLegs(abdomen, lowerAbdomen), builder -> {
            builder.noLegs(abdomen, lowerAbdomen)
                    .tailJoints(List.of(
                            tail.getChild("Joint"),
                            tail.getChild("Joint").getChild("Joint2"))).legLengthOffset(0.0F).tailAidsInSwim();
        });
    }
    public ArmorSnakeAbdomenModel(ModelPart modelPart) {
        this(modelPart, modelPart.getChild("Abdomen"), modelPart.getChild("Abdomen").getChild("LowerAbdomen"),
                modelPart.getChild("Abdomen").getChild("LowerAbdomen").getChild("Tail"));
    }

    public static LayerDefinition createArmorLayer(ArmorModel layer) {
        final float deformationOffset = -0.25F;
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition Abdomen = partdefinition.addOrReplaceChild("Abdomen", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 10.0F, 0.0F, -0.2182F, 0.0F, 0.0F));

        PartDefinition Base_r1 = Abdomen.addOrReplaceChild("Base_r1", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, 0.734F, -1.8537F, 8.0F, 2.0F, 5.0F, layer.deformation.extend(0.3F + deformationOffset))
                .texOffs(0, 7).addBox(-4.5F, 2.6799F, -2.3597F, 9.0F, 7.0F, 6.0F, layer.deformation.extend(0.15F + deformationOffset)), PartPose.offsetAndRotation(0.0F, 0.0478F, -0.6297F, 0.0F, 0.0F, 0.0F));

        PartDefinition LowerAbdomen = Abdomen.addOrReplaceChild("LowerAbdomen", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 9.0F, -1.0F, 0.8727F, 0.0F, 0.0F));

        PartDefinition Base_r2 = LowerAbdomen.addOrReplaceChild("Base_r2", CubeListBuilder.create().texOffs(0, 20).addBox(-4.5F, 0.3384F, -3.0281F, 9.0F, 6.0F, 6.0F, layer.dualDeformation.extend(deformationOffset)), PartPose.offsetAndRotation(0.0F, -1.0025F, 1.0529F, 0.0F, 0.0F, 0.0F));

        PartDefinition Tail = LowerAbdomen.addOrReplaceChild("Tail", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 5.0F, 0.0F, 0.6981F, 0.0F, 0.0F));

        PartDefinition Base_r3 = Tail.addOrReplaceChild("Base_r3", CubeListBuilder.create().texOffs(30, 0).addBox(-3.5F, 3.8569F, -3.4336F, 7.0F, 7.0F, 5.0F, layer.altDeformation.extend(0.1F + deformationOffset)), PartPose.offsetAndRotation(0.0F, -5.0025F, 1.0529F, 0.2007F, 0.0F, 0.0F));

        PartDefinition Joint = Tail.addOrReplaceChild("Joint", CubeListBuilder.create(), PartPose.offset(0.0F, 5.8854F, 2.329F));

        PartDefinition Base_r4 = Joint.addOrReplaceChild("Base_r4", CubeListBuilder.create().texOffs(30, 12).addBox(-2.5F, 10.8541F, -2.9527F, 5.0F, 6.0F, 4.0F, layer.altDeformation.extend(0.35F + deformationOffset)), PartPose.offsetAndRotation(0.0F, -10.8879F, -1.2762F, 0.2007F, 0.0F, 0.0F));

        PartDefinition Joint2 = Joint.addOrReplaceChild("Joint2", CubeListBuilder.create(), PartPose.offset(0.0F, 6.2157F, 1.3003F));

        PartDefinition Base_r5 = Joint2.addOrReplaceChild("Base_r5", CubeListBuilder.create().texOffs(46, 20).addBox(-2.5F, 16.8531F, -2.848F, 5.0F, 8.0F, 4.0F, layer.altDeformation.extend(-0.15F + deformationOffset)), PartPose.offsetAndRotation(0.0F, -17.1036F, -2.5765F, 0.2007F, 0.0F, 0.0F));

        PartDefinition Torso = partdefinition.addOrReplaceChild("Torso", CubeListBuilder.create(), PartPose.offset(0.0F, -2.0F, 0.0F));

        PartDefinition Torso_r1 = Torso.addOrReplaceChild("Torso_r1", CubeListBuilder.create().texOffs(40, 0).addBox(-4.0F, -12.0F, -2.0F, 8.0F, 12.0F, 4.0F, layer.deformation.extend(deformationOffset)), PartPose.offsetAndRotation(0.0F, 13.0F, 0.0F, 0.0F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 32);
    }

    @Override
    public void prepareForShorts() {}
}
