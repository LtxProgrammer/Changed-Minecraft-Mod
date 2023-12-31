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
public class DragonWingFallFlyAnimator<T extends LatexEntity, M extends EntityModel<T>> extends AbstractWingAnimatorV2<T, M> {
    public DragonWingFallFlyAnimator(
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
        leftWingRoot.xRot = Mth.lerp(core.fallFlyingAmount, leftWingRoot.xRot, 0.0f);
        leftWingRoot.yRot = Mth.lerp(core.fallFlyingAmount, leftWingRoot.yRot, Mth.DEG_TO_RAD * 5.0f);
        leftWingRoot.zRot = Mth.lerp(core.fallFlyingAmount, leftWingRoot.zRot, 0.0f);
        rightWingRoot.xRot = Mth.lerp(core.fallFlyingAmount, rightWingRoot.xRot, 0.0f);
        rightWingRoot.yRot = Mth.lerp(core.fallFlyingAmount, rightWingRoot.yRot, Mth.DEG_TO_RAD * -5.0f);
        rightWingRoot.zRot = Mth.lerp(core.fallFlyingAmount, rightWingRoot.zRot, 0.0f);

        leftWingBone1.zRot = Mth.lerp(core.fallFlyingAmount, leftWingBone1.zRot, Mth.DEG_TO_RAD * 30.0f);
        leftWingBone2.zRot = Mth.lerp(core.fallFlyingAmount, leftWingBone2.zRot, Mth.DEG_TO_RAD * 55.0f);

        rightWingBone1.zRot = Mth.lerp(core.fallFlyingAmount, rightWingBone1.zRot, Mth.DEG_TO_RAD * 30.0f);
        rightWingBone2.zRot = Mth.lerp(core.fallFlyingAmount, rightWingBone2.zRot, Mth.DEG_TO_RAD * 55.0f);
    }
}
