package net.ltxprogrammer.changed.mixin.compatibility.Parcool;

import com.alrex.parcool.client.animation.system.AnimatableModelPart;
import com.alrex.parcool.client.animation.system.BlendingModelTransform;
import com.alrex.parcool.client.animation.system.IPlayerAnimatorHolder;
import com.alrex.parcool.client.animation.system.PlayerAnimator;
import com.alrex.parcool.client.animation.system.data.Transform;
import com.alrex.parcool.client.animation.system.math.MathUtil;
import com.alrex.parcool.client.animation.system.math.Vec3f;
import com.mojang.blaze3d.vertex.PoseStack;
import net.ltxprogrammer.changed.client.renderer.AdvancedHumanoidRenderer;
import net.ltxprogrammer.changed.client.renderer.model.AdvancedHumanoidModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.LatexHumanoidArmorModel;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.extension.RequiredMods;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = AdvancedHumanoidRenderer.class, remap = false)
@RequiredMods("parcool")
public abstract class AdvancedHumanoidRendererMixin<T extends ChangedEntity, M extends AdvancedHumanoidModel<T>, A extends LatexHumanoidArmorModel<T, ?>> extends MobRenderer<T, M> {
    private AdvancedHumanoidRendererMixin(EntityRendererProvider.Context p_174304_, M p_174305_, float p_174306_) {
        super(p_174304_, p_174305_, p_174306_);
    }

    @Inject(method = "setupRotations(Lnet/ltxprogrammer/changed/entity/ChangedEntity;Lcom/mojang/blaze3d/vertex/PoseStack;FFF)V", at = @At("HEAD"), cancellable = true)
    protected void onSetupRotationsHead(ChangedEntity entity, PoseStack stack, float bob, float bodyYRot, float partialTicks, CallbackInfo ci) {
        var player = entity.getUnderlyingPlayer();
        if (player == null)
            return;

        if (!player.isFallFlying()) {
            if (player instanceof IPlayerAnimatorHolder holder) {
                PlayerAnimator animator = holder.getParCoolPlayerAnimator();
                if (animator.cancelPlayerModelRotation()) {
                    return;
                }

                BlendingModelTransform transform = animator.getCurrentTransformation();
                if (transform == null) {
                    return;
                }

                Transform bodyTransformation = transform.transformation().transforms().get(AnimatableModelPart.BODY);
                if (bodyTransformation == null) {
                    return;
                }

                if (!transform.isOverwriting()) {
                    bodyTransformation = Transform.NO_TRANSFORMATION.morph(bodyTransformation, transform.blendFactor(), true);
                }

                Vec3f translation = bodyTransformation.translation();
                stack.mulPose(MathUtil.rotation(Vec3f.YP, (float)Math.toRadians(180.0F - bodyYRot)));
                player.getSwimAmount(partialTicks);
                stack.translate(translation.x(), translation.y(), translation.z());
                stack.translate(0.0F, 0.9, 0.0F);
                stack.mulPose((new Quaternionf(bodyTransformation.rotation())).normalize());
                stack.translate(0.0F, -0.9, 0.0F);
                ci.cancel();
            }

        }
    }
}
