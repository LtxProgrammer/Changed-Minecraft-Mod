package net.ltxprogrammer.changed.mixin.compatibility.Moonlight;

import com.mojang.blaze3d.vertex.PoseStack;
import net.ltxprogrammer.changed.client.renderer.layers.LatexItemInHandLayer;
import net.ltxprogrammer.changed.client.renderer.model.AdvancedHumanoidModel;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.mehvahdjukaar.selene.api.IThirdPersonSpecialItemRenderer;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = LatexItemInHandLayer.class)
public abstract class LatexItemInHandLayerMixin<T extends ChangedEntity, M extends AdvancedHumanoidModel<T> & ArmedModel & HeadedModel> extends ItemInHandLayer<T, M> {
    public LatexItemInHandLayerMixin(RenderLayerParent<T, M> parent) {
        super(parent);
    }

    @Inject(
            method = {"renderArmWithItem"},
            at = {@At("HEAD")},
            cancellable = true
    )
    public void poseRightArm(LivingEntity entity, ItemStack stack, ItemTransforms.TransformType transformType, HumanoidArm humanoidArm, PoseStack poseStack, MultiBufferSource bufferSource, int light, CallbackInfo ci) {
        Item var10 = stack.getItem();
        if (var10 instanceof IThirdPersonSpecialItemRenderer) {
            IThirdPersonSpecialItemRenderer item = (IThirdPersonSpecialItemRenderer)var10;
            item.renderThirdPersonItem((AdvancedHumanoidModel)this.getParentModel(), entity, stack, humanoidArm, poseStack, bufferSource, light);
            ci.cancel();
        }

    }
}
