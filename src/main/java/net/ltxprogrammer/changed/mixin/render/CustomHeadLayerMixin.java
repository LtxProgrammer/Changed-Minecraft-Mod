package net.ltxprogrammer.changed.mixin.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.ltxprogrammer.changed.item.WearableItem;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CustomHeadLayer.class)
public abstract class CustomHeadLayerMixin<T extends LivingEntity, M extends EntityModel<T> & HeadedModel> extends RenderLayer<T, M> {
    public CustomHeadLayerMixin(RenderLayerParent<T, M> p_117346_) {
        super(p_117346_);
    }

    @Inject(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/entity/LivingEntity;FFFFFF)V", at = @At("HEAD"), cancellable = true)
    public void render(PoseStack stack, MultiBufferSource bufferSource, int p_116733_, T entity, float p_116735_, float p_116736_, float p_116737_, float p_116738_, float p_116739_, float p_116740_,
                       CallbackInfo callbackInfo) {
        if (entity.getItemBySlot(EquipmentSlot.HEAD).getItem() instanceof WearableItem wearableItem && wearableItem.customWearRenderer())
            callbackInfo.cancel();
    }
}
