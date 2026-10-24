package net.ltxprogrammer.changed.client.renderer.animate.upperbody;

import net.ltxprogrammer.changed.client.renderer.animate.HumanoidAnimator;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.client.renderer.model.AdvancedHumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class OrcaUpperBodySwimAnimator<T extends ChangedEntity, M extends AdvancedHumanoidModel<T>> extends AbstractUpperBodyAnimator<T, M> {
    public static final float SWIM_RATE = 0.333333334f * 0.75f;
    public static final float SWIM_SCALE = 1.5f;
    public static final float TORSO_SWAY_SCALE = 0.2f;

    public OrcaUpperBodySwimAnimator(ModelPart head, ModelPart torso, ModelPart leftArm, ModelPart rightArm) {
        super(head, torso, leftArm, rightArm);
    }

    @Override
    public HumanoidAnimator.AnimateStage preferredStage() {
        return HumanoidAnimator.AnimateStage.SWIM;
    }

    @Override
    public void setupAnim(@NotNull T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        float midSegmentSway = -TORSO_SWAY_SCALE * Mth.cos(limbSwing * SWIM_RATE -
                (((float)Math.PI / 3.0F) * -0.75f));
        float armOffset = Mth.map(midSegmentSway, -0.35f, 0.35f, 2.0f, -2.0f);

        torso.z += Mth.lerp(core.swimAmount, 0.0f, armOffset * SWIM_SCALE);
        torso.xRot = Mth.lerp(core.swimAmount, torso.xRot, midSegmentSway);
        leftArm.xRot += Mth.lerp(core.swimAmount, 0.0f, (midSegmentSway - 0.17f));
        rightArm.xRot += Mth.lerp(core.swimAmount, 0.0f, (midSegmentSway - 0.17f));

        positionLeftArmByTorso((x, y, z) -> {
            leftArm.x = Mth.lerp(core.swimAmount, leftArm.x, x);
            leftArm.y = Mth.lerp(core.swimAmount, leftArm.y, y);
            leftArm.z = Mth.lerp(core.swimAmount, leftArm.z, z);
        });
        positionRightArmByTorso((x, y, z) -> {
            rightArm.x = Mth.lerp(core.swimAmount, rightArm.x, x);
            rightArm.y = Mth.lerp(core.swimAmount, rightArm.y, y);
            rightArm.z = Mth.lerp(core.swimAmount, rightArm.z, z);
        });
    }
}