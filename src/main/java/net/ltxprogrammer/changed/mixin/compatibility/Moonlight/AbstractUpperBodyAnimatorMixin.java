package net.ltxprogrammer.changed.mixin.compatibility.Moonlight;

import net.ltxprogrammer.changed.client.renderer.animate.HumanoidAnimator;
import net.ltxprogrammer.changed.client.renderer.animate.upperbody.AbstractUpperBodyAnimator;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.mehvahdjukaar.selene.api.IThirdPersonAnimationProvider;
import net.mehvahdjukaar.selene.util.TwoHandedAnimation;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = AbstractUpperBodyAnimator.class, remap = false)
public abstract class AbstractUpperBodyAnimatorMixin<T extends ChangedEntity, M extends EntityModel<T>> extends HumanoidAnimator.Animator<T, M> {
    @Unique
    public TwoHandedAnimation animationType = new TwoHandedAnimation();

    @Inject(method = "poseRightArmForItem", at = @At("HEAD"), cancellable = true, require = 0)
    public void poseRightArm(T entity, CallbackInfo ci) {
        //cancel off hand animation if two handed so two handed animation always happens last
        if (this.animationType.isTwoHanded()) ci.cancel();
        HumanoidArm handSide = entity.getMainArm();
        ItemStack stack = entity.getItemInHand(handSide == HumanoidArm.RIGHT ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND);
        Item item = stack.getItem();
        if (item instanceof IThirdPersonAnimationProvider thirdPersonAnimationProvider) {
            HumanoidModel propertyModel = this.core.getPropertyModel(null);
            if (thirdPersonAnimationProvider.poseRightArm(stack, propertyModel, entity, handSide, this.animationType)) {
                this.core.applyPropertyModel(propertyModel);
                ci.cancel();
            }
        }
    }

    @Inject(method = "poseLeftArmForItem", at = @At(value = "HEAD"), cancellable = true, require = 0)
    public void poseLeftArm(T entity, CallbackInfo ci) {
        //cancel off hand animation if two handed so two handed animation always happens last
        if (this.animationType.isTwoHanded()) ci.cancel();
        HumanoidArm handSide = entity.getMainArm();
        ItemStack stack = entity.getItemInHand(handSide == HumanoidArm.RIGHT ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND);
        Item item = stack.getItem();
        if (item instanceof IThirdPersonAnimationProvider thirdPersonAnimationProvider) {
            HumanoidModel propertyModel = this.core.getPropertyModel(null);
            if (thirdPersonAnimationProvider.poseLeftArmGeneric(stack, propertyModel, entity, handSide, this.animationType)) {
                this.core.applyPropertyModel(propertyModel);
                ci.cancel();
            }
        }
    }

    @Inject(method = "setupAnim", at = @At(value = "RETURN"), require = 0)
    public void setupAnim(@NotNull T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo ci) {
        this.animationType.setTwoHanded(false);
    }
}
