package net.ltxprogrammer.changed.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.ltxprogrammer.changed.client.renderer.model.ExoskeletonModel;
import net.ltxprogrammer.changed.entity.robot.Exoskeleton;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class ExoskeletonRenderer extends MobRenderer<Exoskeleton, ExoskeletonModel> {
    public ExoskeletonRenderer(EntityRendererProvider.Context context) {
        super(context, new ExoskeletonModel(context.bakeLayer(ExoskeletonModel.LAYER_LOCATION_SUIT)), 0.4f);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull Exoskeleton entity) {
        return model.getTexture(entity);
    }

    @Override
    public void render(Exoskeleton suit, float yRot, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        if (suit.getWearerEntity() != null) {
            this.shadowStrength = 0f;
            return; // Suit will be rendered as layer instead
        } else {
            this.shadowStrength = 1f;
        };

        poseStack.pushPose();

        float f = (float)suit.getHurtTime() - partialTicks;
        float f1 = suit.getDamage() - partialTicks;
        if (f1 < 0.0F) {
            f1 = 0.0F;
        }

        if (f > 0.0F) {
            poseStack.mulPose(Vector3f.YP.rotationDegrees(Mth.sin(f) * f * f1 / 10.0F * (float)suit.getHurtDir()));
        }

        super.render(suit, yRot, partialTicks, poseStack, bufferSource, packedLight);
        poseStack.popPose();
    }
}
