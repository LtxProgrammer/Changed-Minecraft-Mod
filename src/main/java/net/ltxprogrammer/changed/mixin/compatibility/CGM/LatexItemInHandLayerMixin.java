package net.ltxprogrammer.changed.mixin.compatibility.CGM;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import com.mrcrayfish.guns.client.handler.AimingHandler;
import com.mrcrayfish.guns.client.handler.GunRenderingHandler;
import com.mrcrayfish.guns.common.Gun;
import com.mrcrayfish.guns.item.GunItem;
import net.ltxprogrammer.changed.client.renderer.layers.LatexItemInHandLayer;
import net.ltxprogrammer.changed.client.renderer.model.AdvancedHumanoidModel;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.renderer.entity.layers.PlayerItemInHandLayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = LatexItemInHandLayer.class, remap = false)
public abstract class LatexItemInHandLayerMixin<T extends ChangedEntity, M extends AdvancedHumanoidModel<T> & ArmedModel & HeadedModel> extends ItemInHandLayer<T, M> {
    private LatexItemInHandLayerMixin(RenderLayerParent<T, M> p_117183_) {
        super(p_117183_);
    }

    @Inject(
            method = {"renderArmWithItem"},
            at = {@At("HEAD")},
            cancellable = true,
            remap = true
    )
    private void renderArmWithItemHead(LivingEntity entity, ItemStack stack, ItemTransforms.TransformType transformType, HumanoidArm arm, PoseStack poseStack, MultiBufferSource source, int light, CallbackInfo ci) {
        InteractionHand hand = Minecraft.getInstance().options.mainHand == arm ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
        GunItem gunItem;
        Item var11;
        if (hand == InteractionHand.OFF_HAND) {
            if (stack.getItem() instanceof GunItem) {
                ci.cancel();
                return;
            }

            var11 = entity.getMainHandItem().getItem();
            if (var11 instanceof GunItem) {
                gunItem = (GunItem)var11;
                Gun modifiedGun = gunItem.getModifiedGun(entity.getMainHandItem());
                if (!modifiedGun.getGeneral().getGripType().getHeldAnimation().canRenderOffhandItem()) {
                    ci.cancel();
                    return;
                }
            }
        }

        var11 = stack.getItem();
        if (var11 instanceof GunItem) {
            gunItem = (GunItem)var11;
            ci.cancel();
            LatexItemInHandLayer<?, ?> layer = (LatexItemInHandLayer<?,?>)(Object)this;
            renderArmWithGun(layer, (ChangedEntity)entity, stack, gunItem, transformType, hand, arm, poseStack, source, light, Minecraft.getInstance().getFrameTime());
        }

    }

    private static void renderArmWithGun(LatexItemInHandLayer<?, ?> layer, ChangedEntity latex, ItemStack stack, GunItem item, ItemTransforms.TransformType transformType, InteractionHand hand, HumanoidArm arm, PoseStack poseStack, MultiBufferSource source, int light, float deltaTicks) {
        Player player = latex.getUnderlyingPlayer();
        if (player == null) return;

        poseStack.pushPose();
        ((ArmedModel)layer.getParentModel()).translateToHand(arm, poseStack);
        poseStack.mulPose(Vector3f.XP.rotationDegrees(-90.0F));
        poseStack.mulPose(Vector3f.YP.rotationDegrees(180.0F));
        poseStack.translate((double)((float)(arm == HumanoidArm.LEFT ? -1 : 1) / 16.0F), 0.125D, -0.625D);
        GunRenderingHandler.get().applyWeaponScale(stack, poseStack);
        Gun gun = item.getModifiedGun(stack);
        gun.getGeneral().getGripType().getHeldAnimation().applyHeldItemTransforms(player, hand, AimingHandler.get().getAimProgress(player, deltaTicks), poseStack, source);
        GunRenderingHandler.get().renderWeapon(latex, stack, transformType, poseStack, source, light, deltaTicks);
        poseStack.popPose();
    }
}
