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

    public final ModelPart leftArm2;
    public final ModelPart rightArm2;
    public final ModelPart lowerTorso;
    public final ModelPart leftLeg2;
    public final ModelPart rightLeg2;

    public final LatexHumanoidModelController controller;

    public static class Builder {
        public ModelPart head;
        public ModelPart body;
        public ModelPart tail;
        public ModelPart leftLeg;
        public ModelPart rightLeg;
        public ModelPart leftArm;
        public ModelPart rightArm;

        public ModelPart leftArm2 = null;
        public ModelPart rightArm2 = null;
        public ModelPart lowerTorso = null;
        public ModelPart leftLeg2 = null;
        public ModelPart rightLeg2 = null;

        public Builder(ModelPart head, ModelPart body, ModelPart tail, ModelPart leftLeg, ModelPart rightLeg, ModelPart leftArm, ModelPart rightArm) {
            this.head = head;
            this.body = body;
            this.tail = tail;
            this.leftLeg = leftLeg;
            this.rightLeg = rightLeg;
            this.leftArm = leftArm;
            this.rightArm = rightArm;
        }

        public Builder arms2(ModelPart leftArm2, ModelPart rightArm2) {
            this.leftArm2 = leftArm2;
            this.rightArm2 = rightArm2;
            return this;
        }

        public Builder legs2(ModelPart lowerTorso, ModelPart leftLeg2, ModelPart rightLeg2) {
            this.lowerTorso = lowerTorso;
            this.leftLeg2 = leftLeg2;
            this.rightLeg2 = rightLeg2;
            return this;
        }
    }

    public LatexHumanoidArmorModel(Builder builder, Consumer<LatexHumanoidModelController.Builder> consumer) {
        this.head = builder.head;
        this.body = builder.body;
        this.tail = builder.tail;
        this.leftLeg = builder.leftLeg;
        this.rightLeg = builder.rightLeg;
        this.leftArm = builder.leftArm;
        this.rightArm = builder.rightArm;

        this.leftLeg2 = builder.leftLeg2;
        this.rightLeg2 = builder.rightLeg2;
        this.lowerTorso = builder.lowerTorso;
        this.leftArm2 = builder.leftArm2;
        this.rightArm2 = builder.rightArm2;

        var controllerBuilder = LatexHumanoidModelController.Builder.of(this, head, body, tail, rightArm, leftArm, rightLeg, leftLeg);
        if (consumer != null)
            consumer.accept(controllerBuilder);
        this.controller = controllerBuilder.build();
    }

    public void setAllVisible(boolean b) {
        head.visible = b;
        body.visible = b;
        leftLeg.visible = b;
        rightLeg.visible = b;
        leftArm.visible = b;
        rightArm.visible = b;
        if (lowerTorso != null)
            lowerTorso.visible = b;
        if (leftLeg2 != null)
            leftLeg2.visible = b;
        if (leftLeg2 != null)
            rightLeg2.visible = b;
        if (leftLeg2 != null)
            leftArm2.visible = b;
        if (leftLeg2 != null)
            rightArm2.visible = b;
    }

    public void translateToHand(HumanoidArm arm, PoseStack stack) {

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
        rightLeg.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        leftLeg.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        head.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        body.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        rightArm.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        leftArm.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        if (lowerTorso != null)
            lowerTorso.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        if (rightLeg2 != null)
            rightLeg2.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        if (leftLeg2 != null)
            leftLeg2.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        if (rightArm2 != null)
            rightArm2.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        if (leftArm2 != null)
            leftArm2.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
