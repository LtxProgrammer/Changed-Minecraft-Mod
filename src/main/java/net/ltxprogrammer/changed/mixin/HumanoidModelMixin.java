package net.ltxprogrammer.changed.mixin;

import net.ltxprogrammer.changed.item.SpecializedAnimations;
import net.minecraft.client.model.AgeableListModel;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HumanoidModel.class)
public abstract class HumanoidModelMixin<T extends LivingEntity> extends AgeableListModel<T> implements ArmedModel, HeadedModel {
    protected SpecializedAnimations.AnimationHandler.EntityStateContext entityContextOf(T entity, float partialTicks) {
        return new SpecializedAnimations.AnimationHandler.EntityStateContext(entity, this.attackTime, partialTicks) {
            @Override
            public HumanoidArm getAttackArm() {
                HumanoidArm humanoidarm = entity.getMainArm();
                return entity.swingingArm == InteractionHand.MAIN_HAND ? humanoidarm : humanoidarm.getOpposite();
            }
        };
    }

    protected SpecializedAnimations.AnimationHandler.UpperModelContext upperModelContext() {
        HumanoidModel<T> self = (HumanoidModel<T>)(Object)this;
        return new SpecializedAnimations.AnimationHandler.UpperModelContext(self.leftArm, self.rightArm, self.body, self.head) {
            @Override
            public ModelPart getArm(HumanoidArm humanoidArm) {
                return humanoidArm == HumanoidArm.LEFT ? this.leftArm : this.rightArm;
            }
        };
    }

    @Inject(method = "setupAttackAnimation", at = @At("HEAD"), cancellable = true)
    protected void setupAttackAnimation(T entity, float ageInTicks, CallbackInfo callback) {
        var entityContext = entityContextOf(entity, ageInTicks - entity.tickCount);
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
    }
}
