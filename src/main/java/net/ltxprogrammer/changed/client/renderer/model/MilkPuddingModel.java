package net.ltxprogrammer.changed.client.renderer.model;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.entity.beast.MilkPudding;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;

public class MilkPuddingModel extends HierarchicalModel<MilkPudding> {
    // This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(Changed.modResource("milk_pudding"), "main");
    private final ModelPart Root;
    private final ModelPart Middle;
    private final ModelPart Top;

    public MilkPuddingModel(ModelPart root) {
        this.Root = root.getChild("Root");
        this.Middle = Root.getChild("Middle");
        this.Top = Middle.getChild("Top");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition Root = partdefinition.addOrReplaceChild("Root", CubeListBuilder.create().texOffs(0, 0).addBox(-5.0F, -4.0F, -5.0F, 10.0F, 4.0F, 10.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition Middle = Root.addOrReplaceChild("Middle", CubeListBuilder.create().texOffs(0, 14).addBox(-4.0F, -5.5F, -4.0F, 8.0F, 3.0F, 8.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition Top = Middle.addOrReplaceChild("Top", CubeListBuilder.create().texOffs(24, 14).addBox(-2.0F, -7.0F, -2.0F, 4.0F, 3.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 32);
    }

    @Override
    public void setupAnim(MilkPudding entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        Middle.y = Mth.cos(limbSwing * 0.6662F) * limbSwingAmount;
        Top.y = Middle.y;
    }

    @Override
    public ModelPart root() {
        return Root;
    }
}
