package net.ltxprogrammer.changed.mixin.render;

import com.google.common.base.MoreObjects;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import net.ltxprogrammer.changed.ability.AbstractAbility;
import net.ltxprogrammer.changed.ability.active.multiarm.AutotoolAbilityInstance;
import net.ltxprogrammer.changed.entity.LivingEntityDataExtension;
import net.ltxprogrammer.changed.entity.UseItemMode;
import net.ltxprogrammer.changed.init.ChangedAbilities;
import net.ltxprogrammer.changed.item.SpecializedAnimations;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
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

    @Shadow public abstract void renderItem(LivingEntity p_109323_, ItemStack p_109324_, ItemDisplayContext p_109325_, boolean p_109326_, PoseStack p_109327_, MultiBufferSource p_109328_, int p_109329_);

    @Shadow protected abstract void renderArmWithItem(AbstractClientPlayer p_109372_, float p_109373_, float p_109374_, InteractionHand p_109375_, float p_109376_, ItemStack p_109377_, float p_109378_, PoseStack p_109379_, MultiBufferSource p_109380_, int p_109381_);

    @Inject(method = "renderItem", at = @At("HEAD"))
    public void renderItem(LivingEntity entity, ItemStack item, ItemDisplayContext type, boolean leftHand, PoseStack pose, MultiBufferSource buffers, int packedLight, CallbackInfo callback) {
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
        ProcessTransfur.ifPlayerTransfurred(player, variant -> {
            var itemUseMode = variant.getItemUseMode();
            if (itemUseMode == UseItemMode.NONE)
                callback.cancel();
            else if (itemUseMode == UseItemMode.MOUTH && player.getMainHandItem().isEmpty())
                callback.cancel();
            else
                return;

            bufferSource.endBatch();
        });

        if (!(player instanceof LivingEntityDataExtension ext)) return;
        AbstractAbility.getAbilityInstanceSafe(ext.getGrabbedBy(), ChangedAbilities.GRAB_ENTITY_ABILITY.get())
                .ifPresent(ability -> {
                    if (ability.grabbedHasControl) return;

                    callback.cancel();
                    bufferSource.endBatch();
                });
    }
    
    @WrapOperation(method = "renderHandsWithItems", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/ItemInHandRenderer;renderArmWithItem(Lnet/minecraft/client/player/AbstractClientPlayer;FFLnet/minecraft/world/InteractionHand;FLnet/minecraft/world/item/ItemStack;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V"))
    private void changed$renderAutotoolHandWithNormalHand(ItemInHandRenderer instance, AbstractClientPlayer player, float partialTicks, float interpPitch, InteractionHand hand, float swingProgress, ItemStack heldItem, float equipProgress, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, Operation<Void> original) {
        var autotool = AbstractAbility.getAbilityInstance(player, ChangedAbilities.AUTOTOOL.get());
        if (autotool == null || hand != autotool.getRenderingSide() || autotool.getRenderingRaiseTicks(partialTicks) >= AutotoolAbilityInstance.ITEM_TRANSITION_TICKS) {
            original.call(instance, player, partialTicks, interpPitch, hand, swingProgress, heldItem, equipProgress, poseStack, bufferSource, packedLight);
            return;
        }

        if (hand == autotool.getRenderingSide()) {
            // Shift arm over
            float otherEquipPercent = 1.0f - (autotool.getRenderingRaiseTicks(partialTicks) / (float) AutotoolAbilityInstance.ITEM_TRANSITION_TICKS);
            float otherEquipProgress = autotool.getRenderingRaiseTicks(partialTicks) * 0.4F;

            float scaledDir = 0.2f * ((hand == InteractionHand.MAIN_HAND) == (player.getMainArm() == HumanoidArm.RIGHT) ? 1.0f : -1.0f);

            poseStack.translate(otherEquipPercent * 2.0f * scaledDir, 0.0f, 0.0f);
            original.call(instance, player, partialTicks, interpPitch, hand, autotool.shouldSwing() ? 0.0f : swingProgress, heldItem, equipProgress, poseStack, bufferSource, packedLight);
            poseStack.translate(otherEquipPercent * -2.0f * scaledDir, 0.0f, 0.0f);
            original.call(instance, player, partialTicks, interpPitch, hand, autotool.shouldSwing() ? swingProgress : 0.0f, autotool.getRenderingActiveItem(), otherEquipProgress, poseStack, bufferSource, packedLight);
        }
    }

    @WrapOperation(method = "renderHandsWithItems", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/MultiBufferSource$BufferSource;endBatch()V"))
    private void changed$renderAutotoolHandWithoutNormalHand(MultiBufferSource.BufferSource instance, Operation<Void> original,
                                                             @Local(argsOnly = true) float partialTicks,
                                                             @Local(argsOnly = true) PoseStack poseStack,
                                                             @Local(argsOnly = true) MultiBufferSource.BufferSource bufferSource,
                                                             @Local(argsOnly = true) LocalPlayer player,
                                                             @Local(argsOnly = true) int packedLight,
                                                             @Local ItemInHandRenderer.HandRenderSelection renderSelection) {
        var autotool = AbstractAbility.getAbilityInstance(player, ChangedAbilities.AUTOTOOL.get());
        if (autotool == null || autotool.getRenderingRaiseTicks(partialTicks) >= AutotoolAbilityInstance.ITEM_TRANSITION_TICKS) {
            original.call(instance);
            return; // autotool doesn't exist, or isn't active
        }

        if (renderSelection == ItemInHandRenderer.HandRenderSelection.RENDER_BOTH_HANDS) {
            original.call(instance);
            return; // autotool already rendered
        }

        float attackProgress = player.getAttackAnim(partialTicks);
        InteractionHand interactionhand = MoreObjects.firstNonNull(player.swingingArm, InteractionHand.MAIN_HAND);
        float swingProgress = interactionhand == autotool.getRenderingSide() ? attackProgress : 0.0F;
        float interpPitch = Mth.lerp(partialTicks, player.xRotO, player.getXRot());

        float otherEquipProgress = autotool.getRenderingRaiseTicks(partialTicks) * 0.4F;
        if ((renderSelection == ItemInHandRenderer.HandRenderSelection.RENDER_MAIN_HAND_ONLY && autotool.getRenderingSide() == InteractionHand.OFF_HAND) ||
                (renderSelection == ItemInHandRenderer.HandRenderSelection.RENDER_OFF_HAND_ONLY && autotool.getRenderingSide() == InteractionHand.MAIN_HAND)) {
            renderArmWithItem(player, partialTicks, interpPitch, autotool.getRenderingSide(), autotool.shouldSwing() ? swingProgress : 0.0f, autotool.getRenderingActiveItem(), otherEquipProgress, poseStack, bufferSource, packedLight);
        }

        original.call(instance);
    }
}
