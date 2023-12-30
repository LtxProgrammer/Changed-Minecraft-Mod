package net.ltxprogrammer.changed.client.renderer.animate.upperbody;

import net.ltxprogrammer.changed.client.renderer.animate.LatexAnimator;
import net.ltxprogrammer.changed.entity.LatexEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class SharkUpperBodySwimAnimator<T extends LatexEntity, M extends EntityModel<T>> extends AbstractUpperBodyAnimator<T, M> {
    public static final float SWIM_RATE = 0.333333334f * 0.75f;
    public static final float SWIM_SCALE = 1.5f;
    public static final float TORSO_SWAY_SCALE = 0.2f;

    public SharkUpperBodySwimAnimator(ModelPart head, ModelPart torso, ModelPart leftArm, ModelPart rightArm) {
        super(head, torso, leftArm, rightArm);
    }

    @Override
    public LatexAnimator.AnimateStage preferredStage() {
        return LatexAnimator.AnimateStage.SWIM;
    }

    @Override
    public void setupAnim(@NotNull T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        float midSegmentSway = -TORSO_SWAY_SCALE * Mth.cos(limbSwing * SWIM_RATE -
                (((float)Math.PI / 3.0F) * 0.75f));
        float armOffset = Mth.map(midSegmentSway, -0.35f, 0.35f, 2.0f, -2.0f);

        torso.x += Mth.lerp(core.swimAmount, 0.0f, armOffset * SWIM_SCALE);
        torso.zRot = Mth.lerp(core.swimAmount, torso.zRot, midSegmentSway);
        leftArm.zRot = Mth.lerp(core.swimAmount, leftArm.zRot, midSegmentSway - 0.075f);
        rightArm.zRot = Mth.lerp(core.swimAmount, rightArm.zRot, midSegmentSway + 0.075f);
        leftArm.y += Mth.lerp(core.swimAmount, 0.0f, -armOffset);
        rightArm.y += Mth.lerp(core.swimAmount, 0.0f, armOffset);
        leftArm.x += Mth.lerp(core.swimAmount, 0.0f, armOffset * SWIM_SCALE);
        rightArm.x += Mth.lerp(core.swimAmount, 0.0f, armOffset * SWIM_SCALE);
    }
}