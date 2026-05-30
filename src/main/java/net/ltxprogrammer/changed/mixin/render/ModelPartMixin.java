package net.ltxprogrammer.changed.mixin.render;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.ltxprogrammer.changed.client.CubeExtender;
import net.ltxprogrammer.changed.client.ModelPartExtender;
import net.ltxprogrammer.changed.client.Triangle;
import net.ltxprogrammer.changed.client.renderer.layers.LatexParticlesLayer;
import net.ltxprogrammer.changed.client.tfanimations.TransfurAnimator;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.RandomSource;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Mixin(ModelPart.class)
public abstract class ModelPartMixin implements ModelPartExtender {
    @Shadow @Final public Map<String, ModelPart> children;

    @Shadow public boolean visible;

    @Shadow public abstract void translateAndRotate(PoseStack p_104300_);

    @Shadow @Final public List<ModelPart.Cube> cubes;
    @Unique
    public final List<Triangle> triangles = new ArrayList<>();

    @Inject(method = "compile", at = @At("HEAD"))
    public void andCompileTriangles(PoseStack.Pose pose, VertexConsumer consumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha, CallbackInfo ci) {
        triangles.forEach(triangle -> triangle.compile(pose, consumer, packedLight, packedOverlay, red, green, blue, alpha));
    }

    @WrapOperation(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;IIFFFF)V", at = @At(value = "INVOKE", target = "Ljava/util/List;isEmpty()Z"))
    public boolean hasNoCubesOrTriangles(List<ModelPart.Cube> instance, Operation<Boolean> original) {
        return original.call(instance) && triangles.isEmpty();
    }

    @Override
    public void addTriangle(Triangle triangle) {
        this.triangles.add(triangle);
    }

    @Override
    public ModelPart.Cube getRandomCubeWeighted(RandomSource random) {
        float[] weights = new float[cubes.size()];
        float totalWeight = 0.0f;

        for (int i = 0; i < cubes.size(); ++i) {
            var cube = cubes.get(i);
            Vector3f min = ((CubeExtender)cube).getVisualMin();
            Vector3f max = ((CubeExtender)cube).getVisualMax();

            if (min == null) {
                weights[i] = 0.0f;
                continue;
            }

            if (max.x == min.x)
                weights[i] = Math.abs((max.y - min.y) * (max.z - min.z));
            else if (max.y == min.y)
                weights[i] = Math.abs((max.x - min.x) * (max.z - min.z));
            else if (max.z == min.z)
                weights[i] = Math.abs((max.x - min.x) * (max.y - min.y));
            else
                weights[i] = Math.abs((max.x - min.x) * (max.y - min.y) * (max.z - min.z));
            totalWeight += weights[i];
        }

        float chosenPolygon = random.nextFloat() * totalWeight;
        for (int i = 0; i < cubes.size(); ++i) {
            if (weights[i] <= 0.0f)
                continue;
            if ((chosenPolygon -= weights[i]) > 0.0f)
                continue;

            return cubes.get(i);
        }

        throw new IndexOutOfBoundsException(); // Match behavior of calling `getRandomCube()` on part with no cubes
    }

    @WrapMethod(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;IIFFFF)V")
    public void orCapturePartPose(PoseStack pose, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha, Operation<Void> original) {
        if (TransfurAnimator.isCapturing()) {
            pose.pushPose();

            if (TransfurAnimator.capture((ModelPart)(Object)this, pose)) {
                this.translateAndRotate(pose);
                for(ModelPart modelpart : this.children.values()) {
                    modelpart.render(pose, buffer, packedLight, packedOverlay, red, green, blue, alpha);
                }
            }

            pose.popPose();
            return;
        }

        if (LatexParticlesLayer.isCapturing()) {
            pose.pushPose();

            this.translateAndRotate(pose);
            if (LatexParticlesLayer.capture((ModelPart)(Object)this, pose)) {
                for(ModelPart modelpart : this.children.values()) {
                    modelpart.render(pose, buffer, packedLight, packedOverlay, red, green, blue, alpha);
                }
            }

            pose.popPose();
            return;
        }

        original.call(pose, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
