package net.ltxprogrammer.changed.client.renderer.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.animations.Limb;
import net.ltxprogrammer.changed.client.renderer.animate.HumanoidAnimator;
import net.ltxprogrammer.changed.client.tfanimations.HelperModel;
import net.ltxprogrammer.changed.client.tfanimations.TransfurHelper;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

@OnlyIn(Dist.CLIENT)
public class SharkModel<T extends ChangedEntity> extends AdvancedFeralModel<T> implements LeglessModel {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(Changed.modResource("shark"), "main");
    private final ModelPart Torso;
    private final ModelPart Head;
    private final ModelPart RightArm;
    private final ModelPart LeftArm;
    private final ModelPart Abdomen;
    private final ModelPart LowerAbdomen;
    private final ModelPart Tail;
    private final ModelPart TailPrimary;
    private final HumanoidAnimator<T, SharkModel<T>> animator;

    public SharkModel(ModelPart root) {
        super(root);
        this.Torso = root.getChild("Torso");
        this.Head = root.getChild("Head");
        this.RightArm = root.getChild("RightArm");
        this.LeftArm = root.getChild("LeftArm");
        this.Abdomen = root.getChild("Abdomen");
        this.LowerAbdomen = this.Abdomen.getChild("LowerAbdomen");
        this.Tail = this.LowerAbdomen.getChild("Tail");
        this.TailPrimary = this.Tail.getChild("TailPrimary");

        this.animator = new HumanoidAnimator<>(this)
                .legLength(4.0f)
                .torsoLength(0.0f);
    }

    @Override
    public void setupHand(T entity) {

    }

    @Override
    public HumanoidAnimator<T, SharkModel<T>> getAnimator(T entity) {
        return animator;
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition Torso = partdefinition.addOrReplaceChild("Torso", CubeListBuilder.create().texOffs(0, 0).addBox(-6.0F, 0.0F, -3.0F, 12.0F, 13.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 21.0F, -8.0F, 1.5708F, 0.0F, 0.0F));

        PartDefinition back_fin = Torso.addOrReplaceChild("back_fin", CubeListBuilder.create().texOffs(22, 38).addBox(-0.5F, -0.75F, -3.0F, 1.0F, 4.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 4.0F, 5.0F, -0.5236F, 0.0F, 0.0F));

        PartDefinition Head = partdefinition.addOrReplaceChild("Head", CubeListBuilder.create().texOffs(30, 19).addBox(-5.0F, -2.5F, -5.0F, 10.0F, 5.0F, 5.0F, new CubeDeformation(0.1F)), PartPose.offset(0.0F, 21.0F, -8.0F));

        PartDefinition RightArm = partdefinition.addOrReplaceChild("RightArm", CubeListBuilder.create().texOffs(0, 41).addBox(-1.5F, -1.0F, -0.25F, 5.0F, 8.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.75F, 21.5F, -3.0F, 1.693F, 0.9599F, -3.1416F));

        PartDefinition LeftArm = partdefinition.addOrReplaceChild("LeftArm", CubeListBuilder.create().texOffs(40, 38).addBox(-3.5F, -1.0F, -0.25F, 5.0F, 8.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.75F, 21.5F, -3.0F, 1.693F, -0.9599F, -3.1416F));

        PartDefinition Abdomen = partdefinition.addOrReplaceChild("Abdomen", CubeListBuilder.create().texOffs(0, 19).addBox(-5.0F, 0.0F, -2.5F, 10.0F, 8.0F, 5.0F, new CubeDeformation(0.1F)), PartPose.offsetAndRotation(0.0F, 21.0F, 5.0F, 1.5708F, 0.0F, 0.0F));

        PartDefinition LowerAbdomen = Abdomen.addOrReplaceChild("LowerAbdomen", CubeListBuilder.create().texOffs(30, 29).addBox(-4.0F, 0.0F, -2.5F, 8.0F, 4.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 8.0F, 0.0F));

        PartDefinition left_fin_r1 = LowerAbdomen.addOrReplaceChild("left_fin_r1", CubeListBuilder.create().texOffs(40, 47).addBox(-4.75F, 13.0F, 12.5F, 1.0F, 3.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -18.0F, -4.0F, 2.5307F, 1.2741F, 3.1416F));

        PartDefinition right_fin_r1 = LowerAbdomen.addOrReplaceChild("right_fin_r1", CubeListBuilder.create().texOffs(0, 50).addBox(3.75F, 13.0F, 12.5F, 1.0F, 3.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -18.0F, -4.0F, 2.5307F, -1.2741F, -3.1416F));

        PartDefinition Tail = LowerAbdomen.addOrReplaceChild("Tail", CubeListBuilder.create().texOffs(0, 32).addBox(-3.0F, -0.25F, -2.5F, 6.0F, 4.0F, 5.0F, new CubeDeformation(-0.2F)), PartPose.offset(0.0F, 4.0F, 0.0F));

        PartDefinition TailPrimary = Tail.addOrReplaceChild("TailPrimary", CubeListBuilder.create().texOffs(36, 0).addBox(-2.0F, -0.25F, -2.5F, 4.0F, 7.0F, 5.0F, new CubeDeformation(-0.3F)), PartPose.offset(0.0F, 3.5F, 0.0F));

        PartDefinition Fin = TailPrimary.addOrReplaceChild("Fin", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 5.5F, -0.25F, -1.7104F, 0.0F, 0.0F));

        PartDefinition Base_r1 = Fin.addOrReplaceChild("Base_r1", CubeListBuilder.create().texOffs(22, 32).addBox(-0.5F, -8.1668F, -2.1179F, 1.0F, 2.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(28, 50).addBox(-0.5F, -6.1668F, 0.8821F, 1.0F, 7.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(12, 41).addBox(-0.5F, -6.1668F, -2.1179F, 1.0F, 8.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.75F, 2.0F, -0.6109F, 0.0F, 0.0F));

        PartDefinition Base_r2 = Fin.addOrReplaceChild("Base_r2", CubeListBuilder.create().texOffs(20, 50).addBox(-0.5F, -2.5538F, -1.8296F, 1.0F, 8.0F, 3.0F, new CubeDeformation(-0.05F))
                .texOffs(36, 12).addBox(-0.5F, 5.3462F, -1.8296F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.05F)), PartPose.offsetAndRotation(0.0F, 0.25F, 2.0F, 1.3963F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        Head.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        Torso.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        Abdomen.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        RightArm.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        LeftArm.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }

    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.Head.loadPose(PartPose.offset(0.0F, 21.0F, -8.0F));
        this.Torso.loadPose(PartPose.offsetAndRotation(0.0F, 21.0F, -8.0F, 1.5708F, 0.0F, 0.0F));
        this.RightArm.loadPose(PartPose.offsetAndRotation(-4.75F, 21.5F, -3.0F, 1.693F, 0.9599F, -3.1416F));
        this.LeftArm.loadPose(PartPose.offsetAndRotation(4.75F, 21.5F, -3.0F, 1.693F, -0.9599F, -3.1416F));
        this.Abdomen.loadPose(PartPose.offsetAndRotation(0.0F, 21.0F, 5.0F, 1.5708F, 0.0F, 0.0F));

        this.Head.xRot = headPitch * ((float)Math.PI / 180F) * 0.125f;
        this.Head.yRot = netHeadYaw * ((float)Math.PI / 180F) * 0.125f;
        if (entity.getDeltaMovement().horizontalDistanceSqr() > 1.0E-7D) {
            this.Abdomen.yRot += 0.1F * Mth.cos(ageInTicks * 0.3F) * limbSwingAmount;
            this.LowerAbdomen.zRot = -0.2F * Mth.cos(ageInTicks * 0.3F) * limbSwingAmount;
            this.Tail.zRot = -0.2F * Mth.cos(ageInTicks * 0.3F) * limbSwingAmount;
            this.TailPrimary.zRot = -0.2F * Mth.cos(ageInTicks * 0.3F) * limbSwingAmount;
        }
    }

    @Override
    public @Nullable HelperModel getTransfurHelperModel(Limb limb) {
        if (limb == Limb.ABDOMEN)
            return TransfurHelper.getLegless();
        return super.getTransfurHelperModel(limb);
    }

    @Override
    public ModelPart getArm(HumanoidArm arm) {
        return arm == HumanoidArm.LEFT ? this.LeftArm : this.RightArm;
    }

    @Override
    public ModelPart getLeg(HumanoidArm leg) {
        return null;
    }

    @Override
    public ModelPart getHead() {
        return Head;
    }

    @Override
    public ModelPart getAbdomen() {
        return Abdomen;
    }

    @Override
    public ModelPart getTorso() {
        return Torso;
    }
}