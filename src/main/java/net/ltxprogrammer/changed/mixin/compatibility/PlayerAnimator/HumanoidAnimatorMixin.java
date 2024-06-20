package net.ltxprogrammer.changed.mixin.compatibility.PlayerAnimator;

import dev.kosmx.playerAnim.core.impl.AnimationProcessor;
import dev.kosmx.playerAnim.core.util.SetableSupplier;
import dev.kosmx.playerAnim.impl.IAnimatedPlayer;
import dev.kosmx.playerAnim.impl.IPlayerModel;
import dev.kosmx.playerAnim.impl.animation.AnimationApplier;
import net.ltxprogrammer.changed.client.renderer.animate.HumanoidAnimator;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = HumanoidAnimator.class, remap = false)
public abstract class HumanoidAnimatorMixin<T extends ChangedEntity> implements IPlayerModel {
    @Shadow public abstract HumanoidModel<?> getPropertyModel(@Nullable EquipmentSlot slot);

    @Shadow public abstract void applyPropertyModel(HumanoidModel<?> propertyModel);

    @Shadow public float forwardOffset;
    @Shadow public float hipOffset;

    @Shadow public abstract float calculateTorsoPositionY();

    @Unique private final SetableSupplier<AnimationProcessor> emoteSupplier = new SetableSupplier();
    @Unique private boolean firstPersonNext;

    @Inject(method = "setupAnim", at = @At("RETURN"))
    public void setupAnimEND(@NotNull T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo info) {
        Player player = entity.getUnderlyingPlayer();
        if (player == null) return;

        if (!this.firstPersonNext && player instanceof AbstractClientPlayer && ((IAnimatedPlayer)player).playerAnimator_getAnimation().isActive()) {
            AnimationApplier emote = ((IAnimatedPlayer)player).playerAnimator_getAnimation();
            this.emoteSupplier.set(emote);
            var propertyModel = this.getPropertyModel(null);
            propertyModel.body.y -= this.calculateTorsoPositionY();
            propertyModel.head.y -= this.calculateTorsoPositionY();
            propertyModel.rightArm.y -= this.calculateTorsoPositionY();
            propertyModel.leftArm.y -= this.calculateTorsoPositionY();
            propertyModel.rightLeg.y -= hipOffset;
            propertyModel.leftLeg.y -= hipOffset;

            emote.updatePart("head", propertyModel.head);
            propertyModel.hat.copyFrom(propertyModel.head);
            emote.updatePart("leftArm", propertyModel.leftArm);
            emote.updatePart("rightArm", propertyModel.rightArm);
            emote.updatePart("leftLeg", propertyModel.leftLeg);
            emote.updatePart("rightLeg", propertyModel.rightLeg);
            emote.updatePart("torso", propertyModel.body);

            propertyModel.body.y += this.calculateTorsoPositionY();
            propertyModel.head.y += this.calculateTorsoPositionY();
            propertyModel.rightArm.y += this.calculateTorsoPositionY();
            propertyModel.leftArm.y += this.calculateTorsoPositionY();
            propertyModel.rightLeg.y += hipOffset;
            propertyModel.leftLeg.y += hipOffset;

            this.applyPropertyModel(propertyModel);
        } else {
            this.firstPersonNext = false;
            this.emoteSupplier.set(null);
        }
    }

    public void playerAnimator_prepForFirstPersonRender() {
        this.firstPersonNext = true;
    }
}
