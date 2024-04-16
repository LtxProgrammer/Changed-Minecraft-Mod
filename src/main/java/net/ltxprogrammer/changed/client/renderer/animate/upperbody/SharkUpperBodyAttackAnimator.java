package net.ltxprogrammer.changed.client.renderer.animate.upperbody;

import net.ltxprogrammer.changed.client.renderer.animate.HumanoidAnimator;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.entity.UseItemMode;
import net.ltxprogrammer.changed.item.SpecializedAnimations;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import org.jetbrains.annotations.NotNull;

public class SharkUpperBodyAttackAnimator<T extends ChangedEntity, M extends EntityModel<T>> extends AbstractUpperBodyAnimator<T, M> {
    public SharkUpperBodyAttackAnimator(ModelPart head, ModelPart torso, ModelPart leftArm, ModelPart rightArm) {
        super(head, torso, leftArm, rightArm);
    }

    @Override
    public HumanoidAnimator.AnimateStage preferredStage() {
        return HumanoidAnimator.AnimateStage.ATTACK;
    }

    @Override
    public void setupAnim(@NotNull T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        rightArm.yRot = 0.0F;
        leftArm.yRot = 0.0F;
        if (entity.getItemUseMode() != UseItemMode.NORMAL)
            return;

        boolean mainHandRight = entity.getMainArm() == HumanoidArm.RIGHT;
        if (entity.isUsingItem()) {
            boolean usingMainHand = entity.getUsedItemHand() == InteractionHand.MAIN_HAND;
            if (usingMainHand == mainHandRight) {
                this.poseRightArmForItem(entity);
            } else {
                this.poseLeftArmForItem(entity);
            }
        } else {
            boolean twoHanded = mainHandRight ? core.leftArmPose.isTwoHanded() : core.rightArmPose.isTwoHanded();
            if (mainHandRight != twoHanded) {
                this.poseLeftArmForItem(entity);
                this.poseRightArmForItem(entity);
            } else {
                this.poseRightArmForItem(entity);
                this.poseLeftArmForItem(entity);
            }
        }

        this.setupAttackAnimation(entity, ageInTicks);
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
