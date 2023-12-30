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
public class DragonWingInitAnimator<T extends LatexEntity, M extends EntityModel<T>> extends AbstractWingAnimatorV2<T, M> {
    public DragonWingInitAnimator(
            ModelPart leftWingRoot, ModelPart leftWingBone1, ModelPart leftWingBone2,
            ModelPart rightWingRoot, ModelPart rightWingBone1, ModelPart rightWingBone2) {
        super(leftWingRoot, leftWingBone1, leftWingBone2, rightWingRoot, rightWingBone1, rightWingBone2);
    }

    @Override
    public LatexAnimator.AnimateStage preferredStage() {
        return LatexAnimator.AnimateStage.INIT;
    }

    @Override
    public void setupAnim(@NotNull T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        leftWingRoot.xRot = Mth.DEG_TO_RAD * 7.1228f;
        leftWingRoot.yRot = Mth.DEG_TO_RAD * -29.2189f;
        leftWingRoot.zRot = Mth.DEG_TO_RAD * 16.8588f;
        rightWingRoot.xRot = Mth.DEG_TO_RAD * -7.1228f;
        rightWingRoot.yRot = Mth.DEG_TO_RAD * 29.2189f;
        rightWingRoot.zRot = Mth.DEG_TO_RAD * 16.8588f;

        leftWingBone1.xRot = 0.0f;
        leftWingBone1.yRot = 0.0f;
        leftWingBone1.zRot = 0.0f;
        leftWingBone2.xRot = 0.0f;
        leftWingBone2.yRot = 0.0f;
        leftWingBone2.zRot = Mth.DEG_TO_RAD * 25.0f;

        rightWingBone1.xRot = 0.0f;
        rightWingBone1.yRot = 0.0f;
        rightWingBone1.zRot = 0.0f;
        rightWingBone2.xRot = 0.0f;
        rightWingBone2.yRot = 0.0f;
        rightWingBone2.zRot = Mth.DEG_TO_RAD * 25.0f;
    }
}
