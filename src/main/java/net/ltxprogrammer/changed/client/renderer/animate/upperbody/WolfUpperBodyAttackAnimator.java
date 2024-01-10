package net.ltxprogrammer.changed.client.renderer.animate.upperbody;

import net.ltxprogrammer.changed.client.renderer.animate.LatexAnimator;
import net.ltxprogrammer.changed.entity.LatexEntity;
import net.ltxprogrammer.changed.entity.UseItemMode;
import net.ltxprogrammer.changed.item.SpecializedAnimations;
import net.minecraft.client.model.AnimationUtils;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import org.jetbrains.annotations.NotNull;

public class WolfUpperBodyAttackAnimator<T extends LatexEntity, M extends EntityModel<T>> extends AbstractUpperBodyAnimator<T, M> {
    public WolfUpperBodyAttackAnimator(ModelPart head, ModelPart torso, ModelPart leftArm, ModelPart rightArm) {
        super(head, torso, leftArm, rightArm);
    }

    @Override
    public LatexAnimator.AnimateStage preferredStage() {
        return LatexAnimator.AnimateStage.ATTACK;
    }

    @Override
    public void setupAnim(@NotNull T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        rightArm.yRot = 0.0F;
        leftArm.yRot = 0.0F;
        var self = entity.getSelfVariant();
        if (self.itemUseMode != UseItemMode.NORMAL)
            return;

        boolean mainHandRight = entity.getMainArm() == HumanoidArm.RIGHT;
        if (entity.isUsingItem()) {
            boolean usingMainHand = entity.getUsedItemHand() == InteractionHand.MAIN_HAND;
            if (usingMainHand == mainHandRight) {
                this.poseRightArm(entity);
            } else {
                this.poseLeftArm(entity);
            }
        } else {
            boolean twoHanded = mainHandRight ? core.leftArmPose.isTwoHanded() : core.rightArmPose.isTwoHanded();
            if (mainHandRight != twoHanded) {
                this.poseLeftArm(entity);
                this.poseRightArm(entity);
            } else {
                this.poseRightArm(entity);
                this.poseLeftArm(entity);
            }
        }

        this.setupAttackAnimation(entity, ageInTicks);
    }

    private void poseRightArm(T entity) {
        switch(core.rightArmPose) {
            case EMPTY:
                rightArm.yRot = 0.0F;
                break;
            case BLOCK:
                rightArm.xRot = rightArm.xRot * 0.5F - 0.9424779F;
                rightArm.yRot = (-(float)Math.PI / 6F);
                break;
            case ITEM:
                rightArm.xRot = rightArm.xRot * 0.5F - ((float)Math.PI / 10F);
                rightArm.yRot = 0.0F;
                break;
            case THROW_SPEAR:
                rightArm.xRot = rightArm.xRot * 0.5F - (float)Math.PI;
                rightArm.yRot = 0.0F;
                break;
            case BOW_AND_ARROW:
                rightArm.yRot = -0.1F + head.yRot;
                leftArm.yRot = 0.1F + head.yRot + 0.4F;
                rightArm.xRot = (-(float)Math.PI / 2F) + head.xRot;
                leftArm.xRot = (-(float)Math.PI / 2F) + head.xRot;
                break;
            case CROSSBOW_CHARGE:
                AnimationUtils.animateCrossbowCharge(rightArm, leftArm, entity, true);
                break;
            case CROSSBOW_HOLD:
                AnimationUtils.animateCrossbowHold(rightArm, leftArm, head, true);
                break;
            case SPYGLASS:
                rightArm.xRot = Mth.clamp(head.xRot - 1.9198622F - (entity.isCrouching() ? 0.2617994F : 0.0F), -2.4F, 3.3F);
                rightArm.yRot = head.yRot - 0.2617994F;
        }

    }

    private void poseLeftArm(T entity) {
        switch(core.leftArmPose) {
            case EMPTY:
                leftArm.yRot = 0.0F;
                break;
            case BLOCK:
                leftArm.xRot = leftArm.xRot * 0.5F - 0.9424779F;
                leftArm.yRot = ((float)Math.PI / 6F);
                break;
            case ITEM:
                leftArm.xRot = leftArm.xRot * 0.5F - ((float)Math.PI / 10F);
                leftArm.yRot = 0.0F;
                break;
            case THROW_SPEAR:
                leftArm.xRot = leftArm.xRot * 0.5F - (float)Math.PI;
                leftArm.yRot = 0.0F;
                break;
            case BOW_AND_ARROW:
                rightArm.yRot = -0.1F + head.yRot - 0.4F;
                leftArm.yRot = 0.1F + head.yRot;
                rightArm.xRot = (-(float)Math.PI / 2F) + head.xRot;
                leftArm.xRot = (-(float)Math.PI / 2F) + head.xRot;
                break;
            case CROSSBOW_CHARGE:
                AnimationUtils.animateCrossbowCharge(rightArm, leftArm, entity, true);
                break;
            case CROSSBOW_HOLD:
                AnimationUtils.animateCrossbowHold(rightArm, leftArm, head, true);
                break;
            case SPYGLASS:
                leftArm.xRot = Mth.clamp(head.xRot - 1.9198622F - (entity.isCrouching() ? 0.2617994F : 0.0F), -2.4F, 3.3F);
                leftArm.yRot = head.yRot + 0.2617994F;
        }
    }

    protected void setupAttackAnimation(T entity, float ageInTicks) {
        var self = entity.getSelfVariant();
        if (self.itemUseMode != UseItemMode.NORMAL)
            return;

        var entityContext = core.entityContextOf(entity, ageInTicks - entity.tickCount);
        var upperModelContext = upperModelContext();

        var mainHandItem = entity.getItemBySlot(EquipmentSlot.MAINHAND);
        var offHandItem = entity.getItemBySlot(EquipmentSlot.OFFHAND);
        if ((!entity.isUsingItem() || entity.getUsedItemHand() == InteractionHand.MAIN_HAND) &&
                !mainHandItem.isEmpty() && mainHandItem.getItem() instanceof SpecializedAnimations specialized) {
            var handler = specialized.getAnimationHandler();
            if (handler != null && handler.setupAnimation(mainHandItem, entityContext, upperModelContext, InteractionHand.MAIN_HAND)) {
                return;
            }
        }

        else if ((!entity.isUsingItem() || entity.getUsedItemHand() == InteractionHand.OFF_HAND) &&
                !offHandItem.isEmpty() && offHandItem.getItem() instanceof SpecializedAnimations specialized) {
            var handler = specialized.getAnimationHandler();
            if (handler != null && handler.setupAnimation(offHandItem, entityContext, upperModelContext, InteractionHand.OFF_HAND)) {
                return;
            }
        }

        if (!(core.entityModel.attackTime <= 0.0F)) {
            HumanoidArm humanoidarm = core.getAttackArm(entity);
            ModelPart arm = upperModelContext.getArm(humanoidarm);
            float f = core.entityModel.attackTime;
            torso.yRot = Mth.sin(Mth.sqrt(f) * ((float)Math.PI * 2F)) * 0.2F;
            if (humanoidarm == HumanoidArm.LEFT)
                torso.yRot *= -1.0F;

            rightArm.z = Mth.sin(torso.yRot) * core.torsoWidth + core.forwardOffset;
            rightArm.x = -Mth.cos(torso.yRot) * core.torsoWidth;
            leftArm.z = -Mth.sin(torso.yRot) * core.torsoWidth + core.forwardOffset;
            leftArm.x = Mth.cos(torso.yRot) * core.torsoWidth;
            rightArm.yRot += torso.yRot;
            leftArm.yRot += torso.yRot;
            leftArm.xRot += torso.yRot;
            f = 1.0F - core.entityModel.attackTime;
            f *= f;
            f *= f;
            f = 1.0F - f;
            float f1 = Mth.sin(f * (float)Math.PI);
            float f2 = Mth.sin(core.entityModel.attackTime * (float)Math.PI) * -(head.xRot - 0.7F) * 0.75F;
            arm.xRot = (float)((double)arm.xRot - ((double)f1 * 1.2D + (double)f2));
            arm.yRot += torso.yRot * 2.0F;
            arm.zRot += Mth.sin(core.entityModel.attackTime * (float)Math.PI) * -0.4F;
        }
    }
}
