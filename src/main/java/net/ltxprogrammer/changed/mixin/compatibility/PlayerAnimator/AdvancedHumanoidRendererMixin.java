package net.ltxprogrammer.changed.mixin.compatibility.PlayerAnimator;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import dev.kosmx.playerAnim.api.TransformType;
import dev.kosmx.playerAnim.api.firstPerson.FirstPersonMode;
import dev.kosmx.playerAnim.core.util.Vec3f;
import dev.kosmx.playerAnim.impl.IAnimatedPlayer;
import dev.kosmx.playerAnim.impl.animation.AnimationApplier;
import net.ltxprogrammer.changed.client.PoseStackExtender;
import net.ltxprogrammer.changed.client.renderer.AdvancedHumanoidRenderer;
import net.ltxprogrammer.changed.client.renderer.layers.LatexHumanoidArmorLayer;
import net.ltxprogrammer.changed.client.renderer.model.AdvancedHumanoidModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.LatexHumanoidArmorModel;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = AdvancedHumanoidRenderer.class, remap = false)
public abstract class AdvancedHumanoidRendererMixin<T extends ChangedEntity, M extends AdvancedHumanoidModel<T>, A extends LatexHumanoidArmorModel<T, ?>> extends MobRenderer<T, M> {
    @Shadow public abstract AdvancedHumanoidModel<T> getModel(ChangedEntity entity);
    @Shadow public abstract void setModelResetPoseStack(T entity, @Nullable PoseStack.Pose pose);

    private AdvancedHumanoidRendererMixin(EntityRendererProvider.Context p_174304_, M p_174305_, float p_174306_) {
        super(p_174304_, p_174305_, p_174306_);
    }

    @Inject(method = "setupRotations", at = @At("RETURN"))
    private void applyBodyTransforms(T entity, PoseStack matrixStack, float f, float bodyYaw, float tickDelta, CallbackInfo ci) {
        Player player = entity.getUnderlyingPlayer();
        if (player == null) return;

        AnimationApplier animationPlayer = ((IAnimatedPlayer)player).playerAnimator_getAnimation();
        animationPlayer.setTickDelta(tickDelta);
        if (animationPlayer.isActive()) {
            { // Create reset pose so that specialized models can force the poseStack back to pre-animated
                matrixStack.pushPose();
                var extendedStack = (PoseStackExtender) matrixStack;

                matrixStack.scale(-1.0F, -1.0F, 1.0F); // Code here is copied from LivingEntityRenderer
                this.scale(entity, matrixStack, tickDelta);
                matrixStack.translate(0.0D, (double) -1.501F, 0.0D);
                this.setModelResetPoseStack(entity, extendedStack.copyLast());

                matrixStack.popPose();
            }

            Vec3f vec3d = animationPlayer.get3DTransform("body", TransformType.POSITION, Vec3f.ZERO);
            matrixStack.translate(vec3d.getX(), vec3d.getY() + 0.7D, vec3d.getZ());
            Vec3f vec3f = animationPlayer.get3DTransform("body", TransformType.ROTATION, Vec3f.ZERO);
            matrixStack.mulPose(Vector3f.ZP.rotation(vec3f.getZ()));
            matrixStack.mulPose(Vector3f.YP.rotation(vec3f.getY()));
            matrixStack.mulPose(Vector3f.XP.rotation(vec3f.getX()));
            matrixStack.translate(0.0D, -0.7D, 0.0D);
        }
    }

    @Inject(method = "shouldRenderArmor", at = @At("RETURN"), cancellable = true)
    public void shouldRenderArmorOverride(T entity, CallbackInfoReturnable<Boolean> callback) {
        callback.setReturnValue(callback.getReturnValue() && !FirstPersonMode.isFirstPersonPass());
    }
}
