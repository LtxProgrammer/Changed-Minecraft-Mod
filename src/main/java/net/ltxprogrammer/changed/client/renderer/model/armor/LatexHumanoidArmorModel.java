package net.ltxprogrammer.changed.client.renderer.model.armor;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.ltxprogrammer.changed.client.renderer.LatexHumanoidRenderer;
import net.ltxprogrammer.changed.client.renderer.model.LatexHumanoidModelController;
import net.ltxprogrammer.changed.data.DeferredModelLayerLocation;
import net.ltxprogrammer.changed.entity.LatexEntity;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;

import java.util.function.Consumer;

public class LatexHumanoidArmorModel<T extends LatexEntity> extends EntityModel<T> implements ArmedModel, HeadedModel {
    public final ModelPart head;
    public final ModelPart body;
    public final ModelPart tail;
    public final ModelPart leftLeg;
    public final ModelPart rightLeg;
    public final ModelPart leftArm;
    public final ModelPart rightArm;

    public final ModelPart leftLeg2;
    public final ModelPart rightLeg2;

    public final LatexHumanoidModelController controller;

    public LatexHumanoidArmorModel(ModelPart head, ModelPart body, ModelPart tail, ModelPart leftLeg, ModelPart rightLeg, ModelPart leftArm, ModelPart rightArm,
                                   Consumer<LatexHumanoidModelController.Builder> consumer) {
        this.head = head;
        this.body = body;
        this.tail = tail;
        this.leftLeg = leftLeg;
        this.rightLeg = rightLeg;
        this.leftArm = leftArm;
        this.rightArm = rightArm;

        this.leftLeg2 = null;
        this.rightLeg2 = null;

        var builder = LatexHumanoidModelController.Builder.of(this, head, body, tail, rightArm, leftArm, rightLeg, leftLeg);
        if (consumer != null)
            consumer.accept(builder);
        this.controller = builder.build();
    }

    public LatexHumanoidArmorModel(ModelPart head, ModelPart body, ModelPart tail, ModelPart leftLeg, ModelPart rightLeg, ModelPart leftArm, ModelPart rightArm,
                                   ModelPart lowerTorso, ModelPart leftLeg2, ModelPart rightLeg2, Consumer<LatexHumanoidModelController.Builder> consumer) {
        this.head = head;
        this.body = body;
        this.tail = tail;
        this.leftLeg = leftLeg;
        this.rightLeg = rightLeg;
        this.leftArm = leftArm;
        this.rightArm = rightArm;

        this.leftLeg2 = leftLeg2;
        this.rightLeg2 = rightLeg2;

        var builder = LatexHumanoidModelController.Builder.of(this, head, body, tail, rightArm, leftArm, rightLeg, leftLeg);
        builder.legs2(lowerTorso, rightLeg2, leftLeg2);
        if (consumer != null)
            consumer.accept(builder);
        this.controller = builder.build();
    }

    public void setAllVisible(boolean b) {
        head.visible = b;
        body.visible = b;
        leftLeg.visible = b;
        rightLeg.visible = b;
        leftArm.visible = b;
        rightArm.visible = b;
    }

    public void prepareMobModel(LatexHumanoidModelController controller, T p_102861_, float p_102862_, float p_102863_, float p_102864_) {
        super.prepareMobModel(p_102861_, p_102862_, p_102863_, p_102864_);
        controller.swimAmount = p_102861_.getSwimAmount(p_102864_);
        controller.crouching = p_102861_.isCrouching();
        HumanoidModel.ArmPose humanoidmodel$armpose = LatexHumanoidRenderer.getArmPose(p_102861_, InteractionHand.MAIN_HAND);
        HumanoidModel.ArmPose humanoidmodel$armpose1 = LatexHumanoidRenderer.getArmPose(p_102861_, InteractionHand.OFF_HAND);
        if (humanoidmodel$armpose.isTwoHanded()) {
            humanoidmodel$armpose1 = p_102861_.getOffhandItem().isEmpty() ? HumanoidModel.ArmPose.EMPTY : HumanoidModel.ArmPose.ITEM;
        }

        if (p_102861_.getMainArm() == HumanoidArm.RIGHT) {
            controller.rightArmPose = humanoidmodel$armpose;
            controller.leftArmPose = humanoidmodel$armpose1;
        } else {
            controller.rightArmPose = humanoidmodel$armpose1;
            controller.leftArmPose = humanoidmodel$armpose;
        }
    }

    @Override
    public void prepareMobModel(T p_102861_, float p_102862_, float p_102863_, float p_102864_) {
        this.prepareMobModel(controller, p_102861_, p_102862_, p_102863_, p_102864_);
    }

    public ModelPart getArm(HumanoidArm p_102852_) {
        return p_102852_ == HumanoidArm.LEFT ? this.leftArm : this.rightArm;
    }

    public void translateToHand(HumanoidArm p_102854_, PoseStack p_102855_) {
        this.getArm(p_102854_).translateAndRotate(p_102855_);
    }

    @Override
    public void setupAnim(T p_102618_, float p_102619_, float p_102620_, float p_102621_, float p_102622_, float p_102623_) {
        controller.setupAnim(p_102618_, p_102619_, p_102620_, p_102621_, p_102622_, p_102623_);
    }

    @Override
    public ModelPart getHead() {
        return head;
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        rightLeg.render(poseStack, buffer, packedLight, packedOverlay);
        leftLeg.render(poseStack, buffer, packedLight, packedOverlay);
        head.render(poseStack, buffer, packedLight, packedOverlay);
        body.render(poseStack, buffer, packedLight, packedOverlay);
        rightArm.render(poseStack, buffer, packedLight, packedOverlay);
        leftArm.render(poseStack, buffer, packedLight, packedOverlay);
        if (rightLeg2 != null)
            rightLeg2.render(poseStack, buffer, packedLight, packedOverlay);
        if (leftLeg2 != null)
            leftLeg2.render(poseStack, buffer, packedLight, packedOverlay);
    }
}
