package net.ltxprogrammer.changed.mixin.compatibility.PlayerAnimator;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import dev.kosmx.playerAnim.api.TransformType;
import dev.kosmx.playerAnim.core.util.Vec3f;
import dev.kosmx.playerAnim.impl.IAnimatedPlayer;
import dev.kosmx.playerAnim.impl.animation.AnimationApplier;
import net.ltxprogrammer.changed.client.renderer.AdvancedHumanoidRenderer;
import net.ltxprogrammer.changed.client.renderer.model.AdvancedHumanoidModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.LatexHumanoidArmorModel;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = AdvancedHumanoidRenderer.class, remap = false)
public abstract class AdvancedHumanoidRendererMixin<T extends ChangedEntity, M extends AdvancedHumanoidModel<T>, A extends LatexHumanoidArmorModel<T, ?>> extends MobRenderer<T, M> {
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
            Vec3f vec3d = animationPlayer.get3DTransform("body", TransformType.POSITION, Vec3f.ZERO);
            matrixStack.translate((double)(Float)vec3d.getX(), (double)(Float)vec3d.getY() + 0.7D, (double)(Float)vec3d.getZ());
            Vec3f vec3f = animationPlayer.get3DTransform("body", TransformType.ROTATION, Vec3f.ZERO);
            matrixStack.mulPose(Vector3f.ZP.rotation((Float)vec3f.getZ()));
            matrixStack.mulPose(Vector3f.YP.rotation((Float)vec3f.getY()));
            matrixStack.mulPose(Vector3f.XP.rotation((Float)vec3f.getX()));
            matrixStack.translate(0.0D, -0.7D, 0.0D);
        }

    }
}
