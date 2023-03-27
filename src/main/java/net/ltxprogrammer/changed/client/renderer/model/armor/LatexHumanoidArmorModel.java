package net.ltxprogrammer.changed.client.renderer.model.armor;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.ltxprogrammer.changed.client.renderer.LatexHumanoidRenderer;
import net.ltxprogrammer.changed.client.renderer.animate.LatexAnimator;
import net.ltxprogrammer.changed.client.renderer.model.LatexHumanoidModelInterface;
import net.ltxprogrammer.changed.entity.LatexEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;

public abstract class LatexHumanoidArmorModel<T extends LatexEntity, M extends EntityModel<T>> extends EntityModel<T> implements LatexHumanoidModelInterface<T, M> {
    public abstract void renderForSlot(T entity, ItemStack stack, EquipmentSlot slot,
            PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha);

    public void prepareMobModel(LatexAnimator<T, M> animator, T entity, float p_102862_, float p_102863_, float partialTicks) {
        super.prepareMobModel(entity, p_102862_, p_102863_, partialTicks);
        animator.swimAmount = entity.getSwimAmount(partialTicks);
        animator.crouching = entity.isCrouching();
        HumanoidModel.ArmPose humanoidmodel$armpose = LatexHumanoidRenderer.getArmPose(entity, InteractionHand.MAIN_HAND);
        HumanoidModel.ArmPose humanoidmodel$armpose1 = LatexHumanoidRenderer.getArmPose(entity, InteractionHand.OFF_HAND);
        if (humanoidmodel$armpose.isTwoHanded()) {
            humanoidmodel$armpose1 = entity.getOffhandItem().isEmpty() ? HumanoidModel.ArmPose.EMPTY : HumanoidModel.ArmPose.ITEM;
        }

        if (entity.getMainArm() == HumanoidArm.RIGHT) {
            animator.rightArmPose = humanoidmodel$armpose;
            animator.leftArmPose = humanoidmodel$armpose1;
        } else {
            animator.rightArmPose = humanoidmodel$armpose1;
            animator.leftArmPose = humanoidmodel$armpose;
        }
    }

    @Override
    public void prepareMobModel(T p_102861_, float p_102862_, float p_102863_, float p_102864_) {
        this.prepareMobModel(getAnimator(), p_102861_, p_102862_, p_102863_, p_102864_);
    }

    @Override
    public void setupAnim(T p_102618_, float p_102619_, float p_102620_, float p_102621_, float p_102622_, float p_102623_) {
        getAnimator().setupAnim(p_102618_, p_102619_, p_102620_, p_102621_, p_102622_, p_102623_);
    }

    public static void setAllPartsVisibility(ModelPart part, boolean visible) {
        part.getAllParts().forEach(modelPart -> modelPart.visible = visible);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {}

    @Override
    public final void setupHand() {

    }

    @Override
    public final ModelPart getArm(HumanoidArm arm) {
        return null;
    }
}
