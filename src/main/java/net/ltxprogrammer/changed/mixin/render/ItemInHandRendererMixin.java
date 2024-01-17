package net.ltxprogrammer.changed.mixin.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.ltxprogrammer.changed.entity.UseItemMode;
import net.ltxprogrammer.changed.item.SpecializedAnimations;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandRenderer.class)
public abstract class ItemInHandRendererMixin {
    @Shadow private ItemStack mainHandItem;

    @Shadow private float mainHandHeight;

    @Shadow private float oMainHandHeight;

    @Shadow public abstract void renderItem(LivingEntity p_109323_, ItemStack p_109324_, ItemTransforms.TransformType p_109325_, boolean p_109326_, PoseStack p_109327_, MultiBufferSource p_109328_, int p_109329_);

    @Inject(method = "renderItem", at = @At("HEAD"))
    public void renderItem(LivingEntity entity, ItemStack item, ItemTransforms.TransformType type, boolean leftHand, PoseStack pose, MultiBufferSource buffers, int packedLight, CallbackInfo callback) {
        if (!item.isEmpty() && item.getItem() instanceof SpecializedAnimations specialized) {
            var handler = specialized.getAnimationHandler();
            if (handler != null && (!type.firstPerson() || handler.changesFirstPersonAnimation())) {
                handler.adjustGrip(item, entity, type, pose);
            }
        }
    }

    @Inject(method = "renderArmWithItem", at = @At("HEAD"))
    private void renderArmWithItemPre(AbstractClientPlayer player, float partialTicks, float p_109374_, InteractionHand hand, float p_109376_, ItemStack item, float p_109378_, PoseStack pose, MultiBufferSource buffers, int p_109381_, CallbackInfo callback) {
        if (!item.isEmpty() && item.getItem() instanceof SpecializedAnimations specialized) {
            if (!player.isUsingItem())
                return;
            if (player.getUsedItemHand() != hand)
                return;

            var handler = specialized.getAnimationHandler();
            if (handler != null && handler.changesFirstPersonAnimation()) {
                pose.pushPose();
                float progress = 1.0F - (((float)player.useItemRemaining - partialTicks + 1.0F) / (float)item.getUseDuration());
                handler.setupFirstPersonUseAnimation(
                        item,
                        SpecializedAnimations.AnimationHandler.EntityStateContext.simpleOf(player, partialTicks),
                        hand == InteractionHand.MAIN_HAND ? player.getMainArm() : player.getMainArm().getOpposite(),
                        pose,
                        progress);
            }
        }
    }

    @Inject(method = "renderArmWithItem", at = @At("RETURN"))
    private void renderArmWithItemPost(AbstractClientPlayer player, float p_109373_, float p_109374_, InteractionHand hand, float p_109376_, ItemStack item, float partialTicks, PoseStack pose, MultiBufferSource buffers, int p_109381_, CallbackInfo callback) {
        if (!item.isEmpty() && item.getItem() instanceof SpecializedAnimations specialized) {
            if (!player.isUsingItem())
                return;
            if (player.getUsedItemHand() != hand)
                return;

            var handler = specialized.getAnimationHandler();
            if (handler != null && handler.changesFirstPersonAnimation()) {
                pose.popPose();
            }
        }
    }

    @Inject(method = "renderHandsWithItems", at = @At("HEAD"), cancellable = true)
    public void renderHandsWithItems(float partialTicks, PoseStack pose, MultiBufferSource.BufferSource bufferSource, LocalPlayer player, int color, CallbackInfo callback) {
        ProcessTransfur.ifPlayerLatex(player, variant -> {
            var itemUseMode = variant.getItemUseMode();
            if (itemUseMode == UseItemMode.NONE)
                callback.cancel();
            else if (itemUseMode == UseItemMode.MOUTH && player.getMainHandItem().isEmpty())
                callback.cancel();
            else
                return;

            bufferSource.endBatch();
        });
    }
}
