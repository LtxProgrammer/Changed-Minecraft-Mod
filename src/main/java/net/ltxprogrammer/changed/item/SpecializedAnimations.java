package net.ltxprogrammer.changed.item;

import com.mojang.blaze3d.vertex.PoseStack;
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
    // Will only be called on Render Thread
    static final Map<Item, AnimationHandler> ANIMATION_CACHE = new HashMap<>();

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
        }

        public AnimationHandler(Item item) {
            this.item = item;
        }

        public void setupAttackAnimation(ItemStack itemStack, EntityStateContext entity, UpperModelContext model) {
            if (!(entity.attackTime <= 0.0F)) {
                HumanoidArm humanoidarm = entity.getAttackArm();
                ModelPart modelpart = model.getArm(humanoidarm);
                float f = entity.attackTime;
                model.body.yRot = Mth.sin(Mth.sqrt(f) * ((float)Math.PI * 2F)) * 0.2F;
                if (humanoidarm == HumanoidArm.LEFT) {
                    model.body.yRot *= -1.0F;
                }

                model.rightArm.z = Mth.sin(model.body.yRot) * 5.0F;
                model.rightArm.x = -Mth.cos(model.body.yRot) * 5.0F;
                model.leftArm.z = -Mth.sin(model.body.yRot) * 5.0F;
                model.leftArm.x = Mth.cos(model.body.yRot) * 5.0F;
                model.rightArm.yRot += model.body.yRot;
                model.leftArm.yRot += model.body.yRot;
                model.leftArm.xRot += model.body.yRot;
                f = 1.0F - entity.attackTime;
                f *= f;
                f *= f;
                f = 1.0F - f;
                float f1 = Mth.sin(f * (float)Math.PI);
                float f2 = Mth.sin(entity.attackTime * (float)Math.PI) * -(model.head.xRot - 0.7F) * 0.75F;
                modelpart.xRot -= f1 * 1.2F + f2;
                modelpart.yRot += model.body.yRot * 2.0F;
                modelpart.zRot += Mth.sin(entity.attackTime * (float)Math.PI) * -0.4F;
            }
        }

        public final void adjustGrip(LivingEntity entity, ItemStack item, ItemTransforms.TransformType type, PoseStack pose) {
            this.adjustGrip(EntityStateContext.simpleOf(entity), item, type, pose);
        }

        public void adjustGrip(EntityStateContext entity, ItemStack item, ItemTransforms.TransformType type, PoseStack pose) {}
    }
}
