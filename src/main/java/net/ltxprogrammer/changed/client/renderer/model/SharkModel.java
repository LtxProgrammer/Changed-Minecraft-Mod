package net.ltxprogrammer.changed.client.renderer.model;

import net.ltxprogrammer.changed.Changed;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SharkModel<T extends Entity> extends HierarchicalModel<T> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(Changed.modResource("shark"), "main");
    private final ModelPart root;
    private final ModelPart body;
    private final ModelPart tail;
    private final ModelPart tailFin;

    public SharkModel(ModelPart p_170530_) {
        this.root = p_170530_;
        this.body = p_170530_.getChild("body");
        this.tail = this.body.getChild("tail");
        this.tailFin = this.tail.getChild("tail_fin");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -7.0F, 0.0F, 8.0F, 7.0F, 13.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 24.0F, -3.0F));

        PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(29, 0).addBox(-4.0F, -7.0F, -6.0F, 8.0F, 7.0F, 6.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition nose = head.addOrReplaceChild("nose", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, -10.0F));

        PartDefinition Snout_r1 = nose.addOrReplaceChild("Snout_r1", CubeListBuilder.create().texOffs(8, 36).addBox(0.0F, 0.0F, 0.0F, 1.0F, 2.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-1.5F, -2.0F, 1.0F, 0.0F, -0.2182F, 0.0F));

        PartDefinition Snout_r2 = nose.addOrReplaceChild("Snout_r2", CubeListBuilder.create().texOffs(18, 36).addBox(-1.0F, 0.0F, 0.0F, 1.0F, 2.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(1.5F, -2.0F, 1.0F, 0.0F, 0.2182F, 0.0F));

        PartDefinition Snout_r3 = nose.addOrReplaceChild("Snout_r3", CubeListBuilder.create().texOffs(28, 20).addBox(-1.5F, -29.0F, -7.0F, 3.0F, 2.0F, 3.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(0.0F, 27.0F, 8.0F, 0.0F, 0.0F, 0.0F));

        PartDefinition tail = body.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(0, 20).addBox(-2.0F, -2.5F, -1.0F, 4.0F, 5.0F, 11.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, -2.5F, 14.0F, -0.0873F, 0.0F, 0.0F));

        PartDefinition tail_fin = tail.addOrReplaceChild("tail_fin", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 0.0F, 9.0F, -0.1396F, 0.0F, 0.0F));

        PartDefinition Base_r1 = tail_fin.addOrReplaceChild("Base_r1", CubeListBuilder.create().texOffs(37, 22).addBox(-0.5F, -8.1668F, -2.1179F, 1.0F, 2.0F, 3.0F, CubeDeformation.NONE)
                .texOffs(37, 27).addBox(-0.5F, -6.1668F, 0.8821F, 1.0F, 7.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(0, 20).addBox(-0.5F, -6.1668F, -2.1179F, 1.0F, 8.0F, 3.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, -0.75F, 2.0F, -0.6109F, 0.0F, 0.0F));

        PartDefinition Base_r2 = tail_fin.addOrReplaceChild("Base_r2", CubeListBuilder.create().texOffs(0, 36).addBox(-0.5F, -2.5538F, -1.8296F, 1.0F, 8.0F, 3.0F, new CubeDeformation(-0.05F))
                .texOffs(0, 0).addBox(-0.5F, 5.3462F, -1.8296F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.05F)), PartPose.offsetAndRotation(0.0F, 0.25F, 2.0F, 1.3963F, 0.0F, 0.0F));

        PartDefinition back_fin = body.addOrReplaceChild("back_fin", CubeListBuilder.create().texOffs(0, 0).addBox(-0.5F, -0.75F, -0.5F, 1.0F, 4.0F, 5.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, -7.0F, 5.0F, 1.0472F, 0.0F, 0.0F));

        PartDefinition left_fin = body.addOrReplaceChild("left_fin", CubeListBuilder.create().texOffs(28, 29).addBox(0.0F, -4.0F, -1.5F, 1.0F, 4.0F, 7.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(3.0F, -2.0F, 5.0F, 0.9599F, 0.0F, 1.8675F));

        PartDefinition right_fin = body.addOrReplaceChild("right_fin", CubeListBuilder.create().texOffs(19, 20).addBox(-1.0F, -4.0F, -1.5F, 1.0F, 4.0F, 7.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-3.0F, -2.0F, 5.0F, 0.9599F, 0.0F, -1.8675F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    public ModelPart root() {
        return this.root;
    }

    public void setupAnim(T p_102475_, float p_102476_, float p_102477_, float p_102478_, float p_102479_, float p_102480_) {
        this.body.xRot = p_102480_ * ((float)Math.PI / 180F);
        this.body.yRot = p_102479_ * ((float)Math.PI / 180F);
        if (p_102475_.getDeltaMovement().horizontalDistanceSqr() > 1.0E-7D) {
            this.body.yRot += -0.05F - 0.05F * Mth.cos(p_102478_ * 0.3F);
            this.tail.yRot = -0.1F * Mth.cos(p_102478_ * 0.3F);
            this.tailFin.yRot = -0.2F * Mth.cos(p_102478_ * 0.3F);
        }
    }
}