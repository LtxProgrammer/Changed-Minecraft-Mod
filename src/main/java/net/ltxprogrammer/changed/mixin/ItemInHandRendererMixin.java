package net.ltxprogrammer.changed.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.ltxprogrammer.changed.item.SpecializedAnimations;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandRenderer.class)
public abstract class ItemInHandRendererMixin {
    @Inject(method = "renderItem", at = @At("HEAD"))
    public void renderItem(LivingEntity entity, ItemStack item, ItemTransforms.TransformType type, boolean leftHand, PoseStack pose, MultiBufferSource buffers, int packedLight, CallbackInfo callback) {
        if (!item.isEmpty() && item.getItem() instanceof SpecializedAnimations specialized) {
            var handler = specialized.getAnimationHandler();
            if (handler != null) {
                handler.adjustGrip(entity, item, type, pose);
            }
        }
    }

    @Inject(method = "renderArmWithItem", at = @At("HEAD"))
    private void renderArmWithItem(AbstractClientPlayer player, float p_109373_, float p_109374_, InteractionHand hand, float p_109376_, ItemStack item, float partialTicks, PoseStack pose, MultiBufferSource buffers, int p_109381_, CallbackInfo callback) {
        if (!item.isEmpty() && item.getItem() instanceof SpecializedAnimations specialized) {
            if (!player.isUsingItem())
                return;

            var handler = specialized.getAnimationHandler();
            if (handler != null) {
                float progress = 1.0F - ((float)player.useItemRemaining / (float)player.getUseItem().getUseDuration());
                handler.setupFirstPersonUseAnimation(
                        item,
                        SpecializedAnimations.AnimationHandler.EntityStateContext.simpleOf(player, partialTicks),
                        hand == InteractionHand.MAIN_HAND ? player.getMainArm() : player.getMainArm().getOpposite(),
                        pose,
                        progress);
            }
        }
    }
}
