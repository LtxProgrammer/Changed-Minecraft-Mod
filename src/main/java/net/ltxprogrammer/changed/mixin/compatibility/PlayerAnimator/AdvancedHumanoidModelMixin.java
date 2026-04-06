package net.ltxprogrammer.changed.mixin.compatibility.PlayerAnimator;

import dev.kosmx.playerAnim.api.firstPerson.FirstPersonConfiguration;
import dev.kosmx.playerAnim.api.firstPerson.FirstPersonMode;
import dev.kosmx.playerAnim.impl.IAnimatedPlayer;
import dev.kosmx.playerAnim.impl.animation.AnimationApplier;
import net.ltxprogrammer.changed.client.renderer.model.AdvancedHumanoidModel;
import net.ltxprogrammer.changed.client.renderer.model.TorsoedModel;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.extension.RequiredMods;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(value = AdvancedHumanoidModel.class, remap = false)
@RequiredMods("playeranimator")
public abstract class AdvancedHumanoidModelMixin<T extends ChangedEntity> extends PlayerModel<T> implements ArmedModel, HeadedModel, TorsoedModel {
    public AdvancedHumanoidModelMixin(ModelPart p_170821_, boolean p_170822_) {
        super(p_170821_, p_170822_);
    }

    @Shadow public abstract void setAllLimbsVisible(T entity, boolean visible);
    @Shadow public abstract @Nullable ModelPart getArm(HumanoidArm arm);

    @Inject(method = "prepareMobModel(Lnet/ltxprogrammer/changed/entity/ChangedEntity;FFF)V", at = @At("RETURN"))
    private void hideBonesInFirstPerson(T entity, float limbSwing, float limbSwingAmount, float partialTicks, CallbackInfo ci) {
        if (FirstPersonMode.isFirstPersonPass() && entity instanceof IAnimatedPlayer animatedEntity) {
            AnimationApplier animationApplier = animatedEntity.playerAnimator_getAnimation();
            FirstPersonConfiguration config = animationApplier.getFirstPersonConfiguration();
            if (entity.getUnderlyingPlayer() == Minecraft.getInstance().getCameraEntity() || entity == Minecraft.getInstance().getCameraEntity()) {
                this.setAllLimbsVisible(entity, false);

                final ModelPart rightArm = getArm(HumanoidArm.RIGHT);
                final ModelPart leftArm = getArm(HumanoidArm.LEFT);
                if (rightArm != null)
                    rightArm.visible = config.isShowRightArm();
                if (leftArm != null)
                    leftArm.visible = config.isShowLeftArm();
            }
        }
    }
}
