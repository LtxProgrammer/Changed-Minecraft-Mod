package net.ltxprogrammer.changed.client.renderer.animate;

import net.ltxprogrammer.changed.entity.LatexEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import org.jetbrains.annotations.NotNull;

/**
 * Animator that handles wing idling for legacy models
 * @param <T>
 */
public class LegacyWingInitAnimator<T extends LatexEntity, M extends EntityModel<T>> extends AbstractLegacyWingAnimator<T, M> {
    public LegacyWingInitAnimator(
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
        leftWingRoot.xRot = 0.0f;
        leftWingRoot.yRot = -0.174532f;
        leftWingRoot.zRot = -0.174532f;
        rightWingRoot.xRot = 0.0f;
        rightWingRoot.yRot = 0.174532f;
        rightWingRoot.zRot = 0.174532f;

        leftWingBone1.xRot = 0.0f;
        leftWingBone1.yRot = 0.0f;
        leftWingBone1.zRot = -0.087266f;
        leftWingBone2.xRot = 0.0f;
        leftWingBone2.yRot = 0.0f;
        leftWingBone2.zRot = -0.481710f;

        rightWingBone1.xRot = 0.0f;
        rightWingBone1.yRot = 0.0f;
        rightWingBone1.zRot = 0.087266f;
        rightWingBone2.xRot = 0.0f;
        rightWingBone2.yRot = 0.0f;
        rightWingBone2.zRot = 0.481710f;
    }
}
