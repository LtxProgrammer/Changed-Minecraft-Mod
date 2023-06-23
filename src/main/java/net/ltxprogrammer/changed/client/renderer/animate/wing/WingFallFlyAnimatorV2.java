package net.ltxprogrammer.changed.client.renderer.animate.wing;

import net.ltxprogrammer.changed.client.renderer.animate.LatexAnimator;
import net.ltxprogrammer.changed.entity.LatexEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

/**
 * Animator that handles a slithering entity upright on land
 * @param <T>
 */
public class WingFallFlyAnimatorV2<T extends LatexEntity, M extends EntityModel<T>> extends AbstractWingAnimatorV2<T, M> {
    public WingFallFlyAnimatorV2(
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
        leftWingRoot.yRot = Mth.lerp(fallFlyingAmount, leftWingRoot.yRot, Mth.DEG_TO_RAD * 5.0f);
        leftWingRoot.zRot = Mth.lerp(fallFlyingAmount, leftWingRoot.zRot, 0.0f);
        rightWingRoot.xRot = Mth.lerp(fallFlyingAmount, rightWingRoot.xRot, 0.0f);
        rightWingRoot.yRot = Mth.lerp(fallFlyingAmount, rightWingRoot.yRot, Mth.DEG_TO_RAD * -5.0f);
        rightWingRoot.zRot = Mth.lerp(fallFlyingAmount, rightWingRoot.zRot, 0.0f);

        leftWingBone1.zRot = Mth.lerp(fallFlyingAmount, leftWingBone1.zRot, Mth.DEG_TO_RAD * 30.0f);
        leftWingBone2.zRot = Mth.lerp(fallFlyingAmount, leftWingBone2.zRot, Mth.DEG_TO_RAD * 55.0f);

        rightWingBone1.zRot = Mth.lerp(fallFlyingAmount, rightWingBone1.zRot, Mth.DEG_TO_RAD * 30.0f);
        rightWingBone2.zRot = Mth.lerp(fallFlyingAmount, rightWingBone2.zRot, Mth.DEG_TO_RAD * 55.0f);
    }
}
