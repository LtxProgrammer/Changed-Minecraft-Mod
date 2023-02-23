package net.ltxprogrammer.changed.client.renderer.model.armor;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.entity.beast.LatexMermaidShark;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

import java.util.List;
import java.util.Map;

public class ArmorMermaidSharkAbdomenModel<T extends LatexMermaidShark> extends LatexHumanoidArmorModel<T> {
    public static final ModelLayerLocation INNER_ARMOR = ArmorModelLayerLocation.createInnerArmorLocation(Changed.modResource("armor_mermaid_shark_abdomen")).get();
    public static final ModelLayerLocation OUTER_ARMOR = ArmorModelLayerLocation.createOuterArmorLocation(Changed.modResource("armor_mermaid_shark_abdomen")).get();
    public static final ModelPart EMPTY_PART = new ModelPart(List.of(), Map.of());

    protected ArmorMermaidSharkAbdomenModel(ModelPart root, ModelPart abdomen, ModelPart lowerAbdomen, ModelPart tail) {
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
                            tail.getChild("Joint"))).legLengthOffset(0.0F).tailAidsInSwim();
        });
    }
    public ArmorMermaidSharkAbdomenModel(ModelPart modelPart) {
        this(modelPart, modelPart.getChild("Abdomen"), modelPart.getChild("Abdomen").getChild("LowerAbdomen"),
                modelPart.getChild("Abdomen").getChild("LowerAbdomen").getChild("Tail"));
    }

    public static LayerDefinition createArmorLayer(ArmorModel layer) {
        final float deformationOffset = -0.25F;
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition Abdomen = partdefinition.addOrReplaceChild("Abdomen", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 10.0F, 0.0F, 0.0F, 0.0F, 0.0F));

        PartDefinition Base_r1 = Abdomen.addOrReplaceChild("Base_r1", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -0.3201F, -1.9097F, 8.0F, 2.0F, 5.0F, layer.deformation.extend(0.35F + deformationOffset))
                .texOffs(0, 7).addBox(-4.5F, 2.6799F, -2.3597F, 9.0F, 6.0F, 6.0F, layer.deformation.extend(0.35F + deformationOffset)), PartPose.offsetAndRotation(0.0F, 0.0478F, -0.6297F, 0.0F, 0.0F, 0.0F));

        PartDefinition LowerAbdomen = Abdomen.addOrReplaceChild("LowerAbdomen", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 9.0F, -1.0F, 0.0F, 0.0F, 0.0F));

        PartDefinition Base_r2 = LowerAbdomen.addOrReplaceChild("Base_r2", CubeListBuilder.create().texOffs(0, 20).addBox(-4.5F, 0.3384F, -3.0281F, 9.0F, 6.0F, 6.0F, layer.dualDeformation.extend(deformationOffset)), PartPose.offsetAndRotation(0.0F, -1.0025F, 1.0529F, 0.0F, 0.0F, 0.0F));

        PartDefinition Tail = LowerAbdomen.addOrReplaceChild("Tail", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 4.0F, 0.0F, 0.0F, 0.0F, 0.0F));

        PartDefinition Base_r3 = Tail.addOrReplaceChild("Base_r3", CubeListBuilder.create().texOffs(30, 0).addBox(-3.5F, -9.132F, -4.9203F, 7.0F, 4.0F, 5.0F, layer.altDeformation.extend(deformationOffset)), PartPose.offsetAndRotation(0.0F, 8.5418F, 4.7984F, 0.1745F, 0.0F, 0.0F));

        PartDefinition Joint = Tail.addOrReplaceChild("Joint", CubeListBuilder.create(), PartPose.offset(0.0F, 4.0F, 1.5F));

        PartDefinition Base_r4 = Joint.addOrReplaceChild("Base_r4", CubeListBuilder.create().texOffs(30, 12).addBox(-2.5F, -5.132F, -4.4203F, 5.0F, 5.0F, 4.0F, layer.altDeformation.extend(0.25F + deformationOffset)), PartPose.offsetAndRotation(0.0F, 4.5418F, 3.2984F, 0.1745F, 0.0F, 0.0F));

        PartDefinition Torso = partdefinition.addOrReplaceChild("Torso", CubeListBuilder.create(), PartPose.offset(0.0F, -2.0F, 0.0F));

        PartDefinition Torso_r1 = Torso.addOrReplaceChild("Torso_r1", CubeListBuilder.create().texOffs(40, 0).addBox(-4.0F, -12.0F, -2.0F, 8.0F, 12.0F, 4.0F, layer.deformation.extend(deformationOffset)), PartPose.offsetAndRotation(0.0F, 13.0F, 0.0F, 0.0F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 32);
    }

    @Override
    public void prepareForShorts() {}
}
