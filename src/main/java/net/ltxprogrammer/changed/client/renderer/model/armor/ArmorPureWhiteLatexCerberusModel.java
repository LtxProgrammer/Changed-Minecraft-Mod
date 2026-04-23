package net.ltxprogrammer.changed.client.renderer.model.armor;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.CubeListBuilderExtender;
import net.ltxprogrammer.changed.client.renderer.animate.AnimatorPresets;
import net.ltxprogrammer.changed.client.renderer.animate.HumanoidAnimator;
import net.ltxprogrammer.changed.client.renderer.animate.arm.ArmRideAnimator;
import net.ltxprogrammer.changed.client.renderer.animate.arm.ArmSwimAnimator;
import net.ltxprogrammer.changed.client.renderer.animate.arm.DoubleArmBobAnimator;
import net.ltxprogrammer.changed.client.renderer.animate.multihead.TripleHeadAnimator;
import net.ltxprogrammer.changed.client.renderer.animate.upperbody.WolfHeadInitAnimator;
import net.ltxprogrammer.changed.client.renderer.model.DoubleArmedModel;
import net.ltxprogrammer.changed.client.renderer.model.TripleHeadedModel;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.entity.beast.TripleHeadedEntity;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class ArmorPureWhiteLatexCerberusModel<T extends ChangedEntity & TripleHeadedEntity> extends LatexHumanoidArmorModel<T, ArmorPureWhiteLatexCerberusModel<T>> implements TripleHeadedModel<T>, DoubleArmedModel<T> {
    public static final ArmorModelSet<ChangedEntity, ?> MODEL_SET =
            ArmorModelSet.ofUnspecified(Changed.modResource("armor_pure_white_latex_cerberus_unified"), ArmorPureWhiteLatexCerberusModel::createArmorLayer);

    private final ModelPart RightLeg;
    private final ModelPart LeftLeg;
    private final ModelPart RightArm;
    private final ModelPart LeftArm;
    private final ModelPart UpperRightArm;
    private final ModelPart UpperLeftArm;
    private final ModelPart HeadRow;
    private final ModelPart HeadMiddle;
    private final ModelPart HeadLeft;
    private final ModelPart HeadRight;
    private final ModelPart Torso;
    private final HumanoidAnimator<T, ArmorPureWhiteLatexCerberusModel<T>> animator;

    public ArmorPureWhiteLatexCerberusModel(ModelPart modelPart, ArmorModel model) {
        super(modelPart, model);
        this.RightLeg = modelPart.getChild("RightLeg");
        this.LeftLeg = modelPart.getChild("LeftLeg");
        this.HeadRow = modelPart.getChild("HeadRow");
        this.HeadMiddle = HeadRow.getChild("Middle");
        this.HeadLeft = HeadRow.getChild("LeftOffset").getChild("Left");
        this.HeadRight = HeadRow.getChild("RightOffset").getChild("Right");
        this.Torso = modelPart.getChild("Torso");
        this.RightArm = modelPart.getChild("RightArm");
        this.UpperRightArm = modelPart.getChild("RightArm2");
        this.LeftArm = modelPart.getChild("LeftArm");
        this.UpperLeftArm = modelPart.getChild("LeftArm2");

        var leftLowerLeg = LeftLeg.getChild("LeftLowerLeg");
        var leftFoot = leftLowerLeg.getChild("LeftFoot");
        var rightLowerLeg = RightLeg.getChild("RightLowerLeg");
        var rightFoot = rightLowerLeg.getChild("RightFoot");

        animator = HumanoidAnimator.of(this).hipOffset(-1.5f)
                .addPreset(AnimatorPresets.wolfBipedal(LeftLeg, leftLowerLeg, leftFoot, leftFoot.getChild("LeftPad"), RightLeg, rightLowerLeg, rightFoot, rightFoot.getChild("RightPad")))
                .addPreset(AnimatorPresets.doubleArmUpperBody(HeadMiddle, Torso, UpperLeftArm, UpperRightArm, LeftArm, RightArm))
                .addAnimator(new WolfHeadInitAnimator<>(HeadMiddle))
                .addAnimator(new ArmSwimAnimator<>(UpperLeftArm, UpperRightArm))
                .addAnimator(new DoubleArmBobAnimator<>(UpperLeftArm, UpperRightArm, LeftArm, RightArm))
                .addAnimator(new ArmRideAnimator<>(UpperLeftArm, UpperRightArm))
                .addAnimator(new TripleHeadAnimator<>(HeadLeft, HeadMiddle, HeadRight));
    }

    public static LayerDefinition createArmorLayer(ArmorModel layer) {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        addUnifiedLegs(partdefinition, layer);

        PartDefinition Torso = partdefinition.addOrReplaceChild("Torso", CubeListBuilder.create().texOffs(16, 16).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, layer.dualDeformation), PartPose.offset(0.0F, -0.5F, 0.0F));

        PartDefinition HeadRow = partdefinition.addOrReplaceChild("HeadRow", CubeListBuilder.create(), PartPose.offset(0.0F, -0.5F, 0.0F));

        PartDefinition Middle = HeadRow.addOrReplaceChild("Middle", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, layer.dualDeformation), PartPose.offset(0.0F, 0.0F, -1.0F));

        PartDefinition LeftOffset = HeadRow.addOrReplaceChild("LeftOffset", CubeListBuilder.create(), PartPose.offsetAndRotation(6.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.2618F));

        PartDefinition Left = LeftOffset.addOrReplaceChild("Left", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, layer.dualDeformation), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.1309F, -0.6981F, 0.0F));

        PartDefinition RightOffset = HeadRow.addOrReplaceChild("RightOffset", CubeListBuilder.create(), PartPose.offsetAndRotation(-6.0F, 0.0F, 1.0F, 0.0F, 0.0F, -0.2618F));

        PartDefinition Right = RightOffset.addOrReplaceChild("Right", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, layer.dualDeformation), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.1309F, 0.6981F, 0.0F));

        PartDefinition RightArm = partdefinition.addOrReplaceChild("RightArm", CubeListBuilder.create().texOffs(40, 16).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, layer.dualDeformation), PartPose.offset(-5.0F, 5.5F, 0.0F));

        PartDefinition RightArm2 = partdefinition.addOrReplaceChild("RightArm2", CubeListBuilder.create().texOffs(40, 16).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, layer.dualDeformation), PartPose.offset(-5.0F, 1.5F, 0.0F));

        PartDefinition LeftArm = partdefinition.addOrReplaceChild("LeftArm", CubeListBuilder.create().texOffs(40, 16).mirror().addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, layer.dualDeformation).mirror(false), PartPose.offset(5.0F, 5.5F, 0.0F));

        PartDefinition LeftArm2 = partdefinition.addOrReplaceChild("LeftArm2", CubeListBuilder.create().texOffs(40, 16).mirror().addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, layer.dualDeformation).mirror(false), PartPose.offset(5.0F, 1.5F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 32);
    }

    @Override
    public void prepareVisibility(EquipmentSlot armorSlot, ItemStack item) {
        super.prepareVisibility(armorSlot, item);
        if (armorSlot == EquipmentSlot.LEGS) {
            prepareUnifiedLegsForArmor(item, LeftLeg, RightLeg);
        }
    }

    @Override
    public void renderForSlot(T entity, RenderLayerParent<? super T, ?> parent, ItemStack stack, EquipmentSlot slot, PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        poseStack.pushPose();
        this.scaleForSlot(parent, slot, poseStack);

        switch (slot) {
            case HEAD -> HeadRow.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
            case CHEST -> {
                Torso.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
                LeftArm.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
                RightArm.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
                UpperLeftArm.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
                UpperRightArm.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
            }
            case LEGS -> {
                Torso.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
                LeftLeg.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
                RightLeg.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
            }
            case FEET -> {
                LeftLeg.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
                RightLeg.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
            }
        }

        poseStack.popPose();
    }

    @Override
    public HumanoidAnimator<T, ArmorPureWhiteLatexCerberusModel<T>> getAnimator(T entity) {
        return animator;
    }

    public ModelPart getArm(HumanoidArm arm) {
        return arm == HumanoidArm.LEFT ? this.UpperLeftArm : this.UpperRightArm;
    }

    public ModelPart getLeg(HumanoidArm leg) {
        return leg == HumanoidArm.LEFT ? this.LeftLeg : this.RightLeg;
    }

    public ModelPart getHead() {
        return this.HeadLeft;
    }

    @Override
    public ModelPart getCenterHead() {
        return this.HeadMiddle;
    }

    @Override
    public ModelPart getOtherHead() {
        return this.HeadRight;
    }

    public ModelPart getTorso() {
        return Torso;
    }

    @Override
    public void translateToUpperHand(ChangedEntity entity, HumanoidArm arm, PoseStack poseStack) {

    }

    @Override
    public void translateToLowerHand(ChangedEntity entity, HumanoidArm arm, PoseStack poseStack) {

    }

    @Override
    public ModelPart getOtherArm(HumanoidArm arm) {
        return arm == HumanoidArm.LEFT ? this.LeftArm : this.RightArm;
    }
}
