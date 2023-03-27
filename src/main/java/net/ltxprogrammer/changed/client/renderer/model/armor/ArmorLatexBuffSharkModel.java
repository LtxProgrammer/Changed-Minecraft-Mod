package net.ltxprogrammer.changed.client.renderer.model.armor;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.animate.AnimatorPresets;
import net.ltxprogrammer.changed.client.renderer.animate.LatexAnimator;
import net.ltxprogrammer.changed.entity.LatexEntity;
import net.ltxprogrammer.changed.item.Shorts;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class ArmorLatexBuffSharkModel<T extends LatexEntity> extends LatexHumanoidArmorModel<T, ArmorLatexBuffSharkModel<T>> {
    public static final ModelLayerLocation INNER_ARMOR = ArmorModelLayerLocation.createInnerArmorLocation(Changed.modResource("armor_latex_buff_shark")).get();
    public static final ModelLayerLocation OUTER_ARMOR = ArmorModelLayerLocation.createOuterArmorLocation(Changed.modResource("armor_latex_buff_shark")).get();

    private final ModelPart Head;
    private final ModelPart Torso;
    private final ModelPart Tail;
    private final ModelPart LeftLeg;
    private final ModelPart RightLeg;
    private final ModelPart LeftArm;
    private final ModelPart RightArm;
    private final LatexAnimator<T, ArmorLatexBuffSharkModel<T>> animator;

    public ArmorLatexBuffSharkModel(ModelPart modelPart) {
        this.Head = modelPart.getChild("Head");
        this.Torso = modelPart.getChild("Torso");
        this.Tail = Torso.getChild("Tail");
        this.LeftLeg = modelPart.getChild("LeftLeg");
        this.RightLeg = modelPart.getChild("RightLeg");
        this.LeftArm = modelPart.getChild("LeftArm");
        this.RightArm = modelPart.getChild("RightArm");

        animator = LatexAnimator.of(this).addPreset(AnimatorPresets.sharkLike(Head, Torso, LeftArm, RightArm, Tail, List.of(), LeftLeg, RightLeg))
                .hipOffset(-5.0f).legLength(16.0f).armLength(16.0f).torsoLength(18.0f);
    }

    public static LayerDefinition createArmorLayer(ArmorModel layer) {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition RightLeg = partdefinition.addOrReplaceChild("RightLeg", CubeListBuilder.create().texOffs(0, 26).addBox(-2.4F, 15.0F, -2.75F, 4.0F, 2.0F, 4.0F, layer.deformation), PartPose.offset(-2.5F, 7.0F, 0.0F));

        PartDefinition RightLowerLeg_r1 = RightLeg.addOrReplaceChild("RightLowerLeg_r1", CubeListBuilder.create().texOffs(1, 17).addBox(-2.0F, -8.5F, -0.8F, 4.0F, 7.0F, 3.0F, layer.altDeformation), PartPose.offsetAndRotation(-0.4F, 17.25F, -1.25F, -0.5236F, 0.0F, 0.0F));

        PartDefinition RightMidLeg_r1 = RightLeg.addOrReplaceChild("RightMidLeg_r1", CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, -0.25F, -3.2F, 4.0F, 8.0F, 4.0F, layer.slightAltDeformation), PartPose.offsetAndRotation(-0.4F, 5.25F, -1.5F, 0.5672F, 0.0F, 0.0F));

        PartDefinition RightUpperLeg_r1 = RightLeg.addOrReplaceChild("RightUpperLeg_r1", CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, -1.0F, -2.1F, 4.0F, 7.0F, 4.0F, layer.altDeformation.extend(0.27F)), PartPose.offsetAndRotation(-0.4F, 1.0F, 0.0F, -0.2182F, 0.0F, 0.0F));

        PartDefinition LeftLeg = partdefinition.addOrReplaceChild("LeftLeg", CubeListBuilder.create().texOffs(0, 26).addBox(-1.6F, 15.0F, -2.75F, 4.0F, 2.0F, 4.0F, layer.deformation), PartPose.offset(2.5F, 7.0F, 0.0F));

        PartDefinition LeftLowerLeg_r1 = LeftLeg.addOrReplaceChild("LeftLowerLeg_r1", CubeListBuilder.create().texOffs(1, 17).mirror().addBox(-2.0F, -8.5F, -0.8F, 4.0F, 7.0F, 3.0F, layer.altDeformation), PartPose.offsetAndRotation(0.4F, 17.25F, -1.25F, -0.5236F, 0.0F, 0.0F));

        PartDefinition LeftMidLeg_r1 = LeftLeg.addOrReplaceChild("LeftMidLeg_r1", CubeListBuilder.create().texOffs(0, 16).mirror().addBox(-2.0F, -0.25F, -3.2F, 4.0F, 8.0F, 4.0F, layer.slightAltDeformation), PartPose.offsetAndRotation(0.4F, 5.25F, -1.5F, 0.5672F, 0.0F, 0.0F));

        PartDefinition LeftUpperLeg_r1 = LeftLeg.addOrReplaceChild("LeftUpperLeg_r1", CubeListBuilder.create().texOffs(0, 16).mirror().addBox(-2.0F, -1.0F, -2.1F, 4.0F, 7.0F, 4.0F, layer.altDeformation.extend(0.27F)), PartPose.offsetAndRotation(0.4F, 1.0F, 0.0F, -0.2182F, 0.0F, 0.0F));

        PartDefinition Head = partdefinition.addOrReplaceChild("Head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -7.5F, -4.0F, 8.0F, 8.0F, 8.0F, layer.deformation.extend(0.25F)), PartPose.offset(0.0F, -10.0F, 0.0F));

        PartDefinition Torso = partdefinition.addOrReplaceChild("Torso", CubeListBuilder.create().texOffs(16, 16).addBox(-4.0F, 1.0F, -2.0F, 8.0F, 12.0F, 4.0F, layer.dualDeformation.extend(0.2F))
                .texOffs(34, 4).addBox(-5.0F, 12.0F, -2.1F, 10.0F, 4.0F, 5.0F, layer.deformation), PartPose.offset(0.0F, -8.0F, 0.0F));

        PartDefinition Tail = Torso.addOrReplaceChild("Tail", CubeListBuilder.create(), PartPose.offset(0.0F, 13.75F, 0.0F));

        PartDefinition Base_r1 = Tail.addOrReplaceChild("Base_r1", CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, 0.8F, -5.2F, 4.0F, 7.0F, 4.0F, layer.altDeformation.extend(0.4F)), PartPose.offsetAndRotation(0.0F, 2.0F, 6.5F, 1.2654F, 0.0F, 0.0F));

        PartDefinition Base_r2 = Tail.addOrReplaceChild("Base_r2", CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, -0.9F, -2.7F, 4.0F, 8.0F, 4.0F, layer.altDeformation.extend(0.9F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.5F, 1.0036F, 0.0F, 0.0F));

        PartDefinition RightArm = partdefinition.addOrReplaceChild("RightArm", CubeListBuilder.create().texOffs(40, 16).addBox(-3.25F, -1.0F, -2.0F, 4.0F, 12.0F, 4.0F, layer.deformation), PartPose.offset(-6.0F, -6.0F, 0.0F));

        PartDefinition LeftArm = partdefinition.addOrReplaceChild("LeftArm", CubeListBuilder.create().texOffs(40, 16).mirror().addBox(-0.75F, -1.0F, -2.0F, 4.0F, 12.0F, 4.0F, layer.deformation).mirror(false), PartPose.offset(6.0F, -6.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 32);
    }

    @Override
    public void renderForSlot(T entity, ItemStack stack, EquipmentSlot slot, PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        switch (slot) {
            case HEAD -> Head.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
            case CHEST -> {
                Torso.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
                LeftArm.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
                RightArm.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
            }
            case LEGS -> {
                if (stack.getItem() instanceof Shorts) {
                    setAllPartsVisibility(LeftLeg, false);
                    setAllPartsVisibility(RightLeg, false);
                    LeftLeg.getChild("LeftUpperLeg_r1").visible = true;
                    RightLeg.getChild("RightUpperLeg_r1").visible = true;
                }

                Torso.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
                LeftLeg.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
                RightLeg.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);

                if (stack.getItem() instanceof Shorts) {
                    setAllPartsVisibility(LeftLeg, true);
                    setAllPartsVisibility(RightLeg, true);
                }
            }
            case FEET -> {
                LeftLeg.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
                RightLeg.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
            }
        }
    }

    @Override
    public LatexAnimator<T, ArmorLatexBuffSharkModel<T>> getAnimator() {
        return animator;
    }
}


