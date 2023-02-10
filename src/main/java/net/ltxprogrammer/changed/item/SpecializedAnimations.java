package net.ltxprogrammer.changed.item;

import com.mojang.blaze3d.vertex.PoseStack;
import net.ltxprogrammer.changed.entity.LatexEntity;
import net.minecraft.client.model.AnimationUtils;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public interface SpecializedAnimations {
    static final Map<Item, AnimationHandler> ANIMATION_CACHE = new HashMap<>();

    // Will only be called on Render Thread
    @Nullable
    AnimationHandler getAnimationHandler();

    abstract class AnimationHandler {
        public final Item item;

        public abstract static class EntityStateContext {
            public final LivingEntity livingEntity;
            public final float attackTime;
            public final float partialTicks;

            protected EntityStateContext(LivingEntity livingEntity, float attackTime, float partialTicks) {
                this.livingEntity = livingEntity;
                this.attackTime = attackTime;
                this.partialTicks = partialTicks;
            }

            public abstract HumanoidArm getAttackArm();
            public boolean fullEquippedItem(Item item) {
                return livingEntity.getItemInHand(InteractionHand.MAIN_HAND).is(item) &&
                        livingEntity.getItemInHand(InteractionHand.OFF_HAND).isEmpty();
            }

            public static class SimpleEntityState extends EntityStateContext {
                protected SimpleEntityState(LivingEntity livingEntity) {
                    super(livingEntity, livingEntity.getAttackAnim(0.5f), 0.5f);
                }

                @Override
                public HumanoidArm getAttackArm() {
                    return HumanoidArm.RIGHT;
                }
            }

            public static EntityStateContext simpleOf(LivingEntity livingEntity) {
                return new SimpleEntityState(livingEntity);
            }
        }
        public abstract static class UpperModelContext {
            public final ModelPart leftArm, rightArm, body, head;

            protected UpperModelContext(ModelPart leftArm, ModelPart rightArm, ModelPart body, ModelPart head) {
                this.leftArm = leftArm;
                this.rightArm = rightArm;
                this.body = body;
                this.head = head;
            }

            public abstract ModelPart getArm(HumanoidArm humanoidArm);
            public ModelPart getMainArm(EntityStateContext entity) {
                return getArm(entity.livingEntity.getMainArm());
            }

            public ModelPart getOffhandArm(EntityStateContext entity) {
                return getArm(entity.livingEntity.getMainArm().getOpposite());
            }

            public final void setupBasicBodyTwitch(EntityStateContext entity) {
                body.yRot = Mth.sin(Mth.sqrt(entity.attackTime) * ((float)Math.PI * 2F)) * 0.2F;
                if (entity.getAttackArm() == HumanoidArm.LEFT) {
                    body.yRot *= -1.0F;
                }

                rightArm.z = Mth.sin(body.yRot) * 5.0F;
                rightArm.x = -Mth.cos(body.yRot) * 5.0F;
                leftArm.z = -Mth.sin(body.yRot) * 5.0F;
                leftArm.x = Mth.cos(body.yRot) * 5.0F;
                rightArm.yRot += body.yRot;
                leftArm.yRot += body.yRot;
                leftArm.xRot += body.yRot;
            }
        }

        public AnimationHandler(Item item) {
            this.item = item;
        }

        public void setupIdleAnimation(ItemStack itemStack, EntityStateContext entity, UpperModelContext model) {}
        public void setupUsingAnimation(ItemStack itemStack, EntityStateContext entity, UpperModelContext model) {}

        public void setupSwingAnimation(ItemStack itemStack, EntityStateContext entity, UpperModelContext model) {
            ModelPart modelPart = model.getArm(entity.getAttackArm());
            model.setupBasicBodyTwitch(entity);
            float f = 1.0F - entity.attackTime;
            f *= f;
            f *= f;
            f = 1.0F - f;
            float f1 = Mth.sin(f * (float)Math.PI);
            float f2 = Mth.sin(entity.attackTime * (float)Math.PI) * -(model.head.xRot - 0.7F) * 0.75F;
            modelPart.xRot -= f1 * 1.2F + f2;
            modelPart.yRot += model.body.yRot * 2.0F;
            modelPart.zRot += Mth.sin(entity.attackTime * (float)Math.PI) * -0.4F;
        }

        public final boolean setupAttackAnimation(ItemStack itemStack, EntityStateContext entity, UpperModelContext model) {
            if (wantBothHands(itemStack) && !entity.fullEquippedItem(item))
                return false;

            if (!(entity.attackTime <= 0.0F)) {
                setupSwingAnimation(itemStack, entity, model);
            } else if (entity.livingEntity.isUsingItem()) {
                setupUsingAnimation(itemStack, entity, model);
            } else {
                setupIdleAnimation(itemStack, entity, model);
            }

            return true;
        }

        public final void adjustGrip(LivingEntity entity, ItemStack item, ItemTransforms.TransformType type, PoseStack pose) {
            this.adjustGrip(EntityStateContext.simpleOf(entity), item, type, pose);
        }

        public void adjustGrip(EntityStateContext entity, ItemStack item, ItemTransforms.TransformType type, PoseStack pose) {}

        public boolean wantBothHands(ItemStack item) { return false; }
    }
}
