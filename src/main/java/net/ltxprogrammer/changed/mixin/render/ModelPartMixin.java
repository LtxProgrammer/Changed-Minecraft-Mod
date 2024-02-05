package net.ltxprogrammer.changed.mixin.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.ltxprogrammer.changed.client.tfanimations.TransfurAnimator;
import net.minecraft.client.model.geom.ModelPart;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(ModelPart.class)
public abstract class ModelPartMixin {
    @Shadow @Final public Map<String, ModelPart> children;

    @Shadow public boolean visible;

    @Shadow public abstract void translateAndRotate(PoseStack p_104300_);

    @Inject(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;IIFFFF)V", at = @At("HEAD"), cancellable = true)
    public void orCapture(PoseStack pose, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha, CallbackInfo ci) {
        if (TransfurAnimator.isCapturing()) {
            pose.pushPose();

            if (TransfurAnimator.capture((ModelPart)(Object)this, pose)) {
                this.translateAndRotate(pose);
                for(ModelPart modelpart : this.children.values()) {
                    modelpart.render(pose, buffer, packedLight, packedOverlay, red, green, blue, alpha);
                }
            }

            pose.popPose();
            ci.cancel();
        }
    }
}
