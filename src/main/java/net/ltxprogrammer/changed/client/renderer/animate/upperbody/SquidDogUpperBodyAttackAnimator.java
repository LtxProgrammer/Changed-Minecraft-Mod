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

public class SquidDogUpperBodyAttackAnimator<T extends LatexEntity, M extends EntityModel<T>> extends AbstractUpperBodyAnimator<T, M> {
    public final ModelPart upperLeftArm;
    public final ModelPart upperRightArm;
    public final ModelPart lowerLeftArm;
    public final ModelPart lowerRightArm;

    public SquidDogUpperBodyAttackAnimator(ModelPart head, ModelPart torso, ModelPart upperLeftArm, ModelPart upperRightArm, ModelPart lowerLeftArm, ModelPart lowerRightArm) {
        super(head, torso, upperLeftArm, upperRightArm);
        this.upperLeftArm = upperLeftArm;
        this.upperRightArm = upperRightArm;
        this.lowerLeftArm = lowerLeftArm;
        this.lowerRightArm = lowerRightArm;
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

    public void poseRightArm(T entity) {
        switch(core.rightArmPose) {
            case EMPTY:
                upperRightArm.yRot = 0.0F;
                break;
            case BLOCK:
                upperRightArm.xRot = upperRightArm.xRot * 0.5F - 0.9424779F;
                upperRightArm.yRot = (-(float)Math.PI / 6F);
                break;
            case ITEM:
                upperRightArm.xRot = upperRightArm.xRot * 0.5F - ((float)Math.PI / 10F);
                upperRightArm.yRot = 0.0F;
                break;
            case THROW_SPEAR:
                upperRightArm.xRot = upperRightArm.xRot * 0.5F - (float)Math.PI;
                upperRightArm.yRot = 0.0F;
                break;
            case BOW_AND_ARROW:
                upperRightArm.yRot = -0.1F + head.yRot;
                upperLeftArm.yRot = 0.1F + head.yRot + 0.4F;
                upperRightArm.xRot = (-(float)Math.PI / 2F) + head.xRot;
                upperLeftArm.xRot = (-(float)Math.PI / 2F) + head.xRot;
                break;
            case CROSSBOW_CHARGE:
                AnimationUtils.animateCrossbowCharge(upperRightArm, upperLeftArm, entity, true);
                break;
            case CROSSBOW_HOLD:
                AnimationUtils.animateCrossbowHold(upperRightArm, upperLeftArm, head, true);
                break;
            case SPYGLASS:
                upperRightArm.xRot = Mth.clamp(head.xRot - 1.9198622F - (entity.isCrouching() ? 0.2617994F : 0.0F), -2.4F, 3.3F);
                upperRightArm.yRot = head.yRot - 0.2617994F;
        }

    }

    public void poseLeftArm(T entity) {
        switch(core.leftArmPose) {
            case EMPTY:
                upperLeftArm.yRot = 0.0F;
                break;
            case BLOCK:
                upperLeftArm.xRot = upperLeftArm.xRot * 0.5F - 0.9424779F;
                upperLeftArm.yRot = ((float)Math.PI / 6F);
                break;
            case ITEM:
                upperLeftArm.xRot = upperLeftArm.xRot * 0.5F - ((float)Math.PI / 10F);
                upperLeftArm.yRot = 0.0F;
                break;
            case THROW_SPEAR:
                upperLeftArm.xRot = upperLeftArm.xRot * 0.5F - (float)Math.PI;
                upperLeftArm.yRot = 0.0F;
                break;
            case BOW_AND_ARROW:
                upperRightArm.yRot = -0.1F + head.yRot - 0.4F;
                upperLeftArm.yRot = 0.1F + head.yRot;
                upperRightArm.xRot = (-(float)Math.PI / 2F) + head.xRot;
                upperLeftArm.xRot = (-(float)Math.PI / 2F) + head.xRot;
                break;
            case CROSSBOW_CHARGE:
                AnimationUtils.animateCrossbowCharge(upperRightArm, upperLeftArm, entity, true);
                break;
            case CROSSBOW_HOLD:
                AnimationUtils.animateCrossbowHold(upperRightArm, upperLeftArm, head, true);
                break;
            case SPYGLASS:
                upperLeftArm.xRot = Mth.clamp(head.xRot - 1.9198622F - (entity.isCrouching() ? 0.2617994F : 0.0F), -2.4F, 3.3F);
                upperLeftArm.yRot = head.yRot + 0.2617994F;
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

            upperRightArm.z = Mth.sin(torso.yRot) * core.torsoWidth + core.forwardOffset;
            upperRightArm.x = -Mth.cos(torso.yRot) * core.torsoWidth;
            upperLeftArm.z = -Mth.sin(torso.yRot) * core.torsoWidth + core.forwardOffset;
            upperLeftArm.x = Mth.cos(torso.yRot) * core.torsoWidth;
            upperRightArm.yRot += torso.yRot;
            upperLeftArm.yRot += torso.yRot;
            upperLeftArm.xRot += torso.yRot;
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