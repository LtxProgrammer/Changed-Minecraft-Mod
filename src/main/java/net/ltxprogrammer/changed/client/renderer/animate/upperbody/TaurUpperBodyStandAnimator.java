package net.ltxprogrammer.changed.client.renderer.animate.upperbody;

import net.ltxprogrammer.changed.client.renderer.animate.HumanoidAnimator;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class TaurUpperBodyStandAnimator<T extends ChangedEntity, M extends EntityModel<T>> extends AbstractUpperBodyAnimator<T, M> {
    public TaurUpperBodyStandAnimator(ModelPart head, ModelPart torso, ModelPart leftArm, ModelPart rightArm) {
        super(head, torso, leftArm, rightArm);
    }

    @Override
    public HumanoidAnimator.AnimateStage preferredStage() {
        return HumanoidAnimator.AnimateStage.STAND;
    }

    @Override
    public void setupAnim(@NotNull T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        torso.z = Mth.lerp(core.ageLerp, -0.2f, -0.8f) + core.forwardOffset;
        torso.z = Mth.lerp(limbSwingAmount, torso.z, core.forwardOffset);
        head.z = torso.z;
        leftArm.z = torso.z;
        rightArm.z = torso.z;

        torso.xRot = Mth.lerp(core.ageLerp, (float)Math.PI / 50.0f, (float)Math.PI / 42.0f);
        torso.xRot = Mth.lerp(limbSwingAmount, torso.xRot, 0.0f);

        torso.y = Mth.lerp(core.ageLerp, 0.0f, Mth.lerp(limbSwingAmount, 1.0f, 0.25f)) + core.calculateTorsoPositionY();
        head.y = torso.y + Mth.lerp(limbSwingAmount, 0.15f, 0.025f);
        leftArm.y = torso.y + 2.0f;
        rightArm.y = torso.y + 2.0f;
    }
}
