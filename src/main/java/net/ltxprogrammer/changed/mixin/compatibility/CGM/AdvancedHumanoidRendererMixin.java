package net.ltxprogrammer.changed.mixin.compatibility.CGM;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mrcrayfish.guns.client.handler.AimingHandler;
import com.mrcrayfish.guns.common.Gun;
import com.mrcrayfish.guns.item.GunItem;
import net.ltxprogrammer.changed.client.renderer.AdvancedHumanoidRenderer;
import net.ltxprogrammer.changed.client.renderer.model.AdvancedHumanoidModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.LatexHumanoidArmorModel;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.entity.variant.TransfurVariantInstance;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = AdvancedHumanoidRenderer.class, remap = false)
public abstract class AdvancedHumanoidRendererMixin<T extends ChangedEntity, M extends AdvancedHumanoidModel<T>, A extends LatexHumanoidArmorModel<T, ?>> extends MobRenderer<T, M> {
    private AdvancedHumanoidRendererMixin(EntityRendererProvider.Context p_174304_, M p_174305_, float p_174306_) {
        super(p_174304_, p_174305_, p_174306_);
    }

    @Inject(method = "render(Lnet/ltxprogrammer/changed/entity/ChangedEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/MobRenderer;render(Lnet/minecraft/world/entity/Mob;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", remap = true))
    public void preRender(T entity, float yRot, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, CallbackInfo ci) {
        Player player = entity.getUnderlyingPlayer();
        if (player == null) return;

        ItemStack heldItem = player.getMainHandItem();
        if (!heldItem.isEmpty() && heldItem.getItem() instanceof GunItem) {
            Gun gun = ((GunItem)heldItem.getItem()).getModifiedGun(heldItem);
            gun.getGeneral().getGripType().getHeldAnimation().applyPlayerPreRender(player, InteractionHand.MAIN_HAND, AimingHandler.get().getAimProgress(player, partialTicks), poseStack, bufferSource);

            TransfurVariantInstance.syncEntityPosRotWithEntity(entity, player);
        }
    }
}
