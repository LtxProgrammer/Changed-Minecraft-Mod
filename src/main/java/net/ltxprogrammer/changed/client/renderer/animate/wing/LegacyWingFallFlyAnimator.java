package net.ltxprogrammer.changed.client.renderer.animate.wing;

import net.ltxprogrammer.changed.client.renderer.animate.LatexAnimator;
import net.ltxprogrammer.changed.entity.LatexEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

/**
 * Animator that handles wing gliding for legacy models
 * @param <T>
 */
public class LegacyWingFallFlyAnimator<T extends LatexEntity, M extends EntityModel<T>> extends AbstractLegacyWingAnimator<T, M> {
    public LegacyWingFallFlyAnimator(
            ModelPart leftWingRoot, ModelPart leftWingBone1, ModelPart leftWingBone2,
            ModelPart rightWingRoot, ModelPart rightWingBone1, ModelPart rightWingBone2) {
        super(leftWingRoot, leftWingBone1, leftWingBone2, rightWingRoot, rightWingBone1, rightWingBone2);
    }

    @Override
    public LatexAnimator.AnimateStage preferredStage() {
        return LatexAnimator.AnimateStage.FALL_FLY;
    }

    @Override
    public void setupAnim(@NotNull T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        float fallFlyingTicks = (float)entity.getFallFlyingTicks();
        float fallFlyingAmount = Mth.clamp(fallFlyingTicks * fallFlyingTicks / 100.0F, 0.0F, 1.0F);

        leftWingRoot.xRot = Mth.lerp(fallFlyingAmount, leftWingRoot.xRot, 0.0f);
        leftWingRoot.yRot = Mth.lerp(fallFlyingAmount, leftWingRoot.yRot, 0.087266f);
        leftWingRoot.zRot = Mth.lerp(fallFlyingAmount, leftWingRoot.zRot, 0.0f);
        rightWingRoot.xRot = Mth.lerp(fallFlyingAmount, rightWingRoot.xRot, 0.0f);
        rightWingRoot.yRot = Mth.lerp(fallFlyingAmount, rightWingRoot.yRot, -0.087266f);
        rightWingRoot.zRot = Mth.lerp(fallFlyingAmount, rightWingRoot.zRot, 0.0f);

        leftWingBone1.zRot = Mth.lerp(fallFlyingAmount, leftWingBone1.zRot, -0.523598f);
        leftWingBone2.zRot = Mth.lerp(fallFlyingAmount, leftWingBone2.zRot, -0.959931f);

        rightWingBone1.zRot = Mth.lerp(fallFlyingAmount, rightWingBone1.zRot, 0.523598f);
        rightWingBone2.zRot = Mth.lerp(fallFlyingAmount, rightWingBone2.zRot, 0.959931f);
    }
}
