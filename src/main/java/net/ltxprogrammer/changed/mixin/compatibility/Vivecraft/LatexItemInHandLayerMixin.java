package net.ltxprogrammer.changed.mixin.compatibility.Vivecraft;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import net.ltxprogrammer.changed.client.renderer.layers.LatexItemInHandLayer;
import net.ltxprogrammer.changed.client.renderer.model.AdvancedHumanoidModel;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.extension.RequiredMods;
import net.ltxprogrammer.changed.util.EntityUtil;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.vivecraft.client_vr.ClientDataHolderVR;
import org.vivecraft.client_vr.render.helpers.VREffectsHelper;
import org.vivecraft.client_vr.settings.VRSettings;
import org.vivecraft.data.ViveItems;

@Mixin(value = LatexItemInHandLayer.class)
@RequiredMods("vivecraft")
public abstract class LatexItemInHandLayerMixin<T extends ChangedEntity, M extends AdvancedHumanoidModel<T> & ArmedModel & HeadedModel> extends ItemInHandLayer<T, M> {
    public LatexItemInHandLayerMixin(RenderLayerParent<T, M> p_234846_, ItemInHandRenderer p_234847_) {
        super(p_234846_, p_234847_);
    }

    @Inject(method = "renderArmWithItem", at = @At("HEAD"), cancellable = true)
    private void vivecraft$noItemsInFirstPerson(
            CallbackInfo ci, @Local(argsOnly = true) LivingEntity entity, @Local(argsOnly = true) HumanoidArm arm,
            @Local(argsOnly = true) ItemStack itemStack)
    {
        if (VREffectsHelper.isRenderingFirstPersonPlayer(EntityUtil.maybeGetUnderlying(entity)) &&
                // don't cancel climbing claws, unless menu hand
                (ClientDataHolderVR.getInstance().vrSettings.modelArmsMode != VRSettings.ModelArmsMode.COMPLETE ||
                        ClientDataHolderVR.getInstance().isMenuHand(arm) ||
                        !(ClientDataHolderVR.getInstance().climbTracker.isClimbeyClimb() ||
                                ViveItems.isClimbingClaws(itemStack)
                        )
                ))
        {
            ci.cancel();
        }
    }

    @ModifyExpressionValue(method = "renderArmWithItem", at = {
            @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;is(Lnet/minecraft/world/item/Item;)Z"),
    }, require = 1)
    private boolean vivecraft$noSpyglassInFirstPerson(
            boolean isSpyglass, @Local(argsOnly = true) LivingEntity livingEntity)
    {
        return isSpyglass && !VREffectsHelper.isRenderingFirstPersonPlayer(livingEntity);
    }
}
