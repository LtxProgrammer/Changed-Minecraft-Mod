package net.ltxprogrammer.changed.mixin.render;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import net.ltxprogrammer.changed.client.renderer.model.AdvancedArmedModel;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ItemInHandLayer.class)
public abstract class ItemInHandLayerMixin {
    @WrapOperation(method = "renderArmWithItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/ArmedModel;translateToHand(Lnet/minecraft/world/entity/HumanoidArm;Lcom/mojang/blaze3d/vertex/PoseStack;)V"))
    public void changed$translateToAdvancedModelHand(ArmedModel instance, HumanoidArm side, PoseStack poseStack,
                                                     Operation<Void> original, @Local(argsOnly = true) LivingEntity entity) {
        if (instance instanceof AdvancedArmedModel advancedArmedModel && entity instanceof ChangedEntity)
            advancedArmedModel.translateToHand((ChangedEntity)entity, side, poseStack);
        else
            original.call(instance, side, poseStack);
    }
}
