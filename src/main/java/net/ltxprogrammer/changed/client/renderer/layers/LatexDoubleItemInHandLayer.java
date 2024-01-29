package net.ltxprogrammer.changed.client.renderer.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.ltxprogrammer.changed.client.renderer.model.DoubleArmedModel;
import net.ltxprogrammer.changed.client.renderer.model.LatexHumanoidModel;
import net.ltxprogrammer.changed.entity.LatexEntity;
import net.ltxprogrammer.changed.init.ChangedAbilities;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class LatexDoubleItemInHandLayer<T extends LatexEntity, M extends LatexHumanoidModel<T> & DoubleArmedModel & HeadedModel> extends ItemInHandLayer<T, M> {
    public LatexDoubleItemInHandLayer(RenderLayerParent<T, M> parent) {
        super(parent);
    }

    public void render(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, T entity, float p_117208_, float p_117209_, float p_117210_, float p_117211_, float p_117212_, float p_117213_) {
        boolean flag = entity.getMainArm() == HumanoidArm.RIGHT;
        var abilityInstance = entity.getAbilityInstance(ChangedAbilities.SWITCH_HANDS.get());
        if (abilityInstance == null) return;

        ItemStack leftHandStack = abilityInstance.extraLeftHand;
        ItemStack rightHandStack = abilityInstance.extraRightHand;
        if (!leftHandStack.isEmpty() || !rightHandStack.isEmpty()) {
            poseStack.pushPose();
            if (this.getParentModel().young) {
                float f = 0.5F;
                poseStack.translate(0.0D, 0.75D, 0.0D);
                poseStack.scale(0.5F, 0.5F, 0.5F);
            }

            this.renderArmWithItem(entity, rightHandStack, ItemTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, HumanoidArm.RIGHT, poseStack, bufferSource, packedLight);
            this.renderArmWithItem(entity, leftHandStack, ItemTransforms.TransformType.THIRD_PERSON_LEFT_HAND, HumanoidArm.LEFT, poseStack, bufferSource, packedLight);
            poseStack.popPose();
        }
    }

    protected void renderArmWithItem(LivingEntity entity, ItemStack item, ItemTransforms.TransformType transformType, HumanoidArm arm, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        if (!item.isEmpty()) {
            poseStack.pushPose();
            this.getParentModel().translateToLowerHand(arm, poseStack);
            poseStack.mulPose(Vector3f.XP.rotationDegrees(-90.0F));
            poseStack.mulPose(Vector3f.YP.rotationDegrees(180.0F));
            boolean flag = arm == HumanoidArm.LEFT;
            poseStack.translate((double)((float)(flag ? -1 : 1) / 16.0F), 0.125D, -0.625D);
            Minecraft.getInstance().getItemInHandRenderer().renderItem(entity, item, transformType, flag, poseStack, bufferSource, packedLight);
            poseStack.popPose();
        }
    }
}
