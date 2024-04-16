package net.ltxprogrammer.changed.mixin.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.ltxprogrammer.changed.client.ModelPartExtender;
import net.ltxprogrammer.changed.client.Triangle;
import net.ltxprogrammer.changed.client.tfanimations.TransfurAnimator;
import net.minecraft.client.model.geom.ModelPart;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Mixin(ModelPart.class)
public abstract class ModelPartMixin implements ModelPartExtender {
    @Shadow @Final public Map<String, ModelPart> children;

    @Shadow public boolean visible;

    @Shadow public abstract void translateAndRotate(PoseStack p_104300_);

    @Unique
    public final List<Triangle> triangles = new ArrayList<>();

    @Inject(method = "compile", at = @At("HEAD"))
    public void andCompileTriangles(PoseStack.Pose pose, VertexConsumer consumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha, CallbackInfo ci) {
        triangles.forEach(triangle -> triangle.compile(pose, consumer, packedLight, packedOverlay, red, green, blue, alpha));
    }

    @Redirect(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;IIFFFF)V", at = @At(value = "INVOKE", target = "Ljava/util/List;isEmpty()Z"))
    public boolean hasNoCubesOrTriangles(List<ModelPart.Cube> cubes) {
        return cubes.isEmpty() && triangles.isEmpty();
    }

    @Override
    public void addTriangle(Triangle triangle) {
        this.triangles.add(triangle);
    }
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
