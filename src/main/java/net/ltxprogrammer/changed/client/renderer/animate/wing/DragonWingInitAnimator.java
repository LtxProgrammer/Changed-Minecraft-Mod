package net.ltxprogrammer.changed.client.renderer.animate.wing;

import net.ltxprogrammer.changed.client.renderer.animate.LatexAnimator;
import net.ltxprogrammer.changed.entity.LatexEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

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
        float wingRootYAgeLerp = Mth.lerp(core.ageLerp, 0.174532f, 0.3490659f);
        float wingRootZAgeLerp = Mth.lerp(core.ageLerp, 0.174532f, 0.2617994f);

        leftWingRoot.xRot = 0.0f;
        leftWingRoot.yRot = -wingRootYAgeLerp;
        leftWingRoot.zRot = -wingRootZAgeLerp;
        rightWingRoot.xRot = 0.0f;
        rightWingRoot.yRot = wingRootYAgeLerp;
        rightWingRoot.zRot = wingRootZAgeLerp;

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
