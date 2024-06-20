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

        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-6.0F, -6.0F, 0.0F, 12.0F, 6.0F, 13.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 24.0F, -3.0F));

        PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(31, 27).addBox(-5.0F, -5.5F, -5.0F, 10.0F, 5.0F, 5.0F, new CubeDeformation(0.1F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition tail = body.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(0, 19).addBox(-5.0F, -2.5F, -3.0F, 10.0F, 5.0F, 8.0F, new CubeDeformation(0.1F))
                .texOffs(37, 0).addBox(-4.0F, -2.5F, 5.0F, 8.0F, 5.0F, 4.0F, CubeDeformation.NONE)
                .texOffs(0, 32).addBox(-2.0F, -2.5F, 11.0F, 4.0F, 5.0F, 7.0F, new CubeDeformation(-0.3F))
                .texOffs(18, 47).addBox(-3.0F, -2.5F, 8.0F, 6.0F, 5.0F, 4.0F, new CubeDeformation(-0.2F)), PartPose.offset(0.0F, -3.0F, 16.0F));

        PartDefinition right_fin_r1 = tail.addOrReplaceChild("right_fin_r1", CubeListBuilder.create().texOffs(0, 0).addBox(3.75F, 13.0F, 12.5F, 1.0F, 3.0F, 5.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 3.0F, -13.0F, 0.9599F, 0.0F, -1.8675F));

        PartDefinition left_fin_r1 = tail.addOrReplaceChild("left_fin_r1", CubeListBuilder.create().texOffs(44, 37).addBox(-4.75F, 13.0F, 12.5F, 1.0F, 3.0F, 5.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 3.0F, -13.0F, 0.9599F, 0.0F, 1.8675F));

        PartDefinition tail_fin = tail.addOrReplaceChild("tail_fin", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 0.0F, 16.75F, -0.1396F, 0.0F, 0.0F));

        PartDefinition Base_r1 = tail_fin.addOrReplaceChild("Base_r1", CubeListBuilder.create().texOffs(0, 8).addBox(-0.5F, -8.1668F, -2.1179F, 1.0F, 2.0F, 3.0F, CubeDeformation.NONE)
                .texOffs(0, 19).addBox(-0.5F, -6.1668F, 0.8821F, 1.0F, 7.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(49, 47).addBox(-0.5F, -6.1668F, -2.1179F, 1.0F, 8.0F, 3.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, -0.75F, 2.0F, -0.6109F, 0.0F, 0.0F));

        PartDefinition Base_r2 = tail_fin.addOrReplaceChild("Base_r2", CubeListBuilder.create().texOffs(38, 50).addBox(-0.5F, -2.5538F, -1.8296F, 1.0F, 8.0F, 3.0F, new CubeDeformation(-0.05F))
                .texOffs(0, 0).addBox(-0.5F, 5.3462F, -1.8296F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.05F)), PartPose.offsetAndRotation(0.0F, 0.25F, 2.0F, 1.3963F, 0.0F, 0.0F));

        PartDefinition back_fin = body.addOrReplaceChild("back_fin", CubeListBuilder.create().texOffs(26, 37).addBox(-0.5F, 2.25F, -1.5F, 1.0F, 1.0F, 5.0F, CubeDeformation.NONE)
                .texOffs(0, 44).addBox(-0.5F, -1.75F, -2.5F, 1.0F, 4.0F, 8.0F, CubeDeformation.NONE)
                .texOffs(28, 19).addBox(-1.0F, 1.75F, -2.0F, 2.0F, 1.0F, 5.0F, CubeDeformation.NONE)
                .texOffs(14, 36).addBox(-1.0F, -1.25F, -3.5F, 2.0F, 3.0F, 8.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, -7.0F, 5.0F, 1.0472F, 0.0F, 0.0F));

        PartDefinition left_fin = body.addOrReplaceChild("left_fin", CubeListBuilder.create().texOffs(42, 11).addBox(-1.25F, -4.0F, 0.5F, 1.0F, 5.0F, 8.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(3.0F, -2.0F, 5.0F, 0.9599F, 0.0F, 1.8675F));

        PartDefinition right_fin = body.addOrReplaceChild("right_fin", CubeListBuilder.create().texOffs(34, 37).addBox(0.25F, -4.0F, 0.5F, 1.0F, 5.0F, 8.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-3.0F, -2.0F, 5.0F, 0.9599F, 0.0F, -1.8675F));

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