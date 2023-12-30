package net.ltxprogrammer.changed.client.renderer.animate.wing;

import net.ltxprogrammer.changed.client.renderer.animate.LatexAnimator;
import net.ltxprogrammer.changed.entity.LatexEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class DragonWingCreativeFlyAnimator<T extends LatexEntity, M extends EntityModel<T>> extends AbstractWingAnimatorV2<T, M> {
    public static final float WING_FLAP_RATE = 0.2f;
    public static final float BODY_FLY_SCALE = 0.5f;

    public DragonWingCreativeFlyAnimator(
            ModelPart leftWingRoot, ModelPart leftWingBone1, ModelPart leftWingBone2,
            ModelPart rightWingRoot, ModelPart rightWingBone1, ModelPart rightWingBone2) {
        super(leftWingRoot, leftWingBone1, leftWingBone2, rightWingRoot, rightWingBone1, rightWingBone2);
    }

    @Override
    public LatexAnimator.AnimateStage preferredStage() {
        return LatexAnimator.AnimateStage.CREATIVE_FLY;
    }

    @Override
    public void setupAnim(@NotNull T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        leftWingRoot.xRot = Mth.lerp(core.flyAmount, leftWingRoot.xRot, 0.0f);
        leftWingRoot.yRot = Mth.lerp(core.flyAmount, leftWingRoot.yRot, 0.087266f);
        leftWingRoot.zRot = Mth.lerp(core.flyAmount, leftWingRoot.zRot, 0.0f);
        rightWingRoot.xRot = Mth.lerp(core.flyAmount, rightWingRoot.xRot, 0.0f);
        rightWingRoot.yRot = Mth.lerp(core.flyAmount, rightWingRoot.yRot, -0.087266f);
        rightWingRoot.zRot = Mth.lerp(core.flyAmount, rightWingRoot.zRot, 0.0f);

        leftWingBone1.zRot = Mth.lerp(core.flyAmount, leftWingBone1.zRot, -0.523598f);
        leftWingBone2.zRot = Mth.lerp(core.flyAmount, leftWingBone2.zRot, -0.959931f);

        rightWingBone1.zRot = Mth.lerp(core.flyAmount, rightWingBone1.zRot, 0.523598f);
        rightWingBone2.zRot = Mth.lerp(core.flyAmount, rightWingBone2.zRot, 0.959931f);

        float flapAmount = Mth.cos(ageInTicks * WING_FLAP_RATE);
        flapAmount = flapAmount * flapAmount;
        float flapRotate = Mth.map(flapAmount, 0.0f, 1.0f, Mth.DEG_TO_RAD * -20.0f, Mth.DEG_TO_RAD * 32.0f);

        leftWingRoot.yRot = Mth.lerp(core.flyAmount, leftWingRoot.yRot, -flapRotate);
        rightWingRoot.yRot = Mth.lerp(core.flyAmount, rightWingRoot.yRot, flapRotate);
    }
}
